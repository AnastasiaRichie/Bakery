package com.bakery_tm.bakery.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakery_tm.bakery.common.UpdateOrderListener
import com.bakery_tm.bakery.data.api.WebSocketManager
import com.bakery_tm.bakery.domain.OrderRepository
import com.bakery_tm.bakery.domain.OrderResponse
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrdersViewModel(
    private val orderRepository: OrderRepository,
    private val webSocketManager: WebSocketManager,
) : ViewModel(), UpdateOrderListener {

    init {
        webSocketManager.attachOrderListener(this)
    }

    private val _orders = MutableStateFlow<List<OrderResponse>>(emptyList())
    val orders: StateFlow<List<OrderResponse>> = _orders.asStateFlow()

    private val _allOrders = MutableStateFlow<List<OrderResponse>>(emptyList())
    val allOrders: StateFlow<List<OrderResponse>> = _allOrders.asStateFlow()

    private val _onNewOrderCreated = MutableSharedFlow<Unit>()
    val onNewOrderCreated: SharedFlow<Unit> = _onNewOrderCreated

    private var email: String? = null

    override fun onCleared() {
        webSocketManager.detachOrderListener(this)
        super.onCleared()
    }

    override fun requireOrderUpdate(orderId: Long) {
        getAllOrders()
        email?.let { fetchOrdersByEmail(it) }
    }

    fun getAllOrders() {
        viewModelScope.launch {
            try {
                val orders = orderRepository.getAllOrders()
                _allOrders.value = orders.sortedBy { it.orderId }
            } catch (e: Exception) {
                Log.e("OrdersViewModel", "Failed to fetch all orders", e)
            }
        }
    }

    fun fetchOrdersByEmail(email: String) {
        this.email = email
        viewModelScope.launch {
            try {
                val ordersFromApi = orderRepository.getOrdersByEmail(email)
                _orders.value = ordersFromApi
            } catch (e: Exception) {
                Log.e("OrdersViewModel", "Failed to fetch orders", e)
            }
        }
    }

    fun markOrderReceived(orderId: Long) {
        viewModelScope.launch {
            try {
                orderRepository.markOrderReceived(orderId)
                email?.let { fetchOrdersByEmail(it) }
            } catch (e: Exception) {
                Log.e("OrdersViewModel", "Failed to mark order received", e)
            }
        }
    }

    override fun onOrderCreated() {
        viewModelScope.launch { _onNewOrderCreated.emit(Unit) }
        getAllOrders()
        email?.let { fetchOrdersByEmail(it) }
    }
}