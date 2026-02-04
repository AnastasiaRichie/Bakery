package com.bakery_tm.bakery.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakery_tm.bakery.common.UpdateOrderListener
import com.bakery_tm.bakery.data.api.NoAuthException
import com.bakery_tm.bakery.data.api.WebSocketManager
import com.bakery_tm.bakery.data.database.entity.UserEntity
import com.bakery_tm.bakery.domain.OrderRepository
import com.bakery_tm.bakery.domain.OrderResponse
import com.bakery_tm.bakery.domain.OrderState
import com.bakery_tm.bakery.domain.UserRepository
import com.bakery_tm.bakery.models.Address
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrderViewModel(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val webSocketManager: WebSocketManager,
): ViewModel(), UpdateOrderListener {

    private var localUser: UserEntity? = null
    private var localOrderId: Long? = null

    private val _order = MutableStateFlow<OrderResponse?>(null)
    val order: StateFlow<OrderResponse?> = _order

    private val _orders = MutableStateFlow<List<Pair<OrderResponse, Double>>>(emptyList())
    val orders: StateFlow<List<Pair<OrderResponse, Double>>> = _orders

    private val _onBack = MutableSharedFlow<Unit>()
    val onBack: SharedFlow<Unit> = _onBack

    init {
        webSocketManager.attachOrderListener(this)
        viewModelScope.launch {
            userRepository.getUser().collect { user ->
                localUser = user
                user?.userId?.let { getAllOrders(it) }
            }
        }
    }

    override fun onCleared() {
        webSocketManager.detachOrderListener()
        super.onCleared()
    }

    override fun requireOrderUpdate(orderId: Long) {
        _orders.update { orders ->
            orders.map { pair ->
                val order = pair.first
                val sum = pair.second
                if (order.orderId == orderId) {
                    Pair(order.copy(orderState = OrderState.RECEIVED), sum)
                } else {
                    pair
                }
            }
        }
    }

    fun getOrders() {
        viewModelScope.launch {
            localUser?.userId?.let { getAllOrders(it) }
        }
    }

    fun onLogoutClicked() {
        localUser = null
        localOrderId = null
        _order.value = null
        _orders.value = emptyList()
    }

    fun createOrder(address: Address) {
        viewModelScope.launch {
            localUser?.userId?.let {
                try {
                    orderRepository.createOrder(it, address)
                } finally {
                    getAllOrders(it)
                }
            }
        }
    }

    fun getDetailedOrder(orderId: Long) {
        updateSelectedOrderId(orderId)
        viewModelScope.launch {
            _order.value = orderRepository.getOrderDetails(orderId)
        }
    }

    fun reorder(orderId: Long) {
        viewModelScope.launch {
            try {
                orderRepository.reorder(orderId)
            } finally {
                localUser?.userId?.let { getAllOrders(it) }
                _onBack.emit(Unit)
            }
        }
    }

    private fun updateSelectedOrderId(orderId: Long) {
        if (localOrderId != orderId) {
            _order.value = null
            localOrderId = orderId
        }
    }

    private suspend fun getAllOrders(userId: Int) {
        try {
            orderRepository.getAllOrders(userId).collect {
                _orders.value = it.map { it to orderRepository.calculateOrderTotal(it.orderId, it.items) }
            }
        } catch (e: NoAuthException) {
            _orders.value = emptyList()
        }
    }
}