package com.bakery_tm.bakery.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakery_tm.bakery.data.database.entity.UserEntity
import com.bakery_tm.bakery.data.database.relations.OrderWithItems
import com.bakery_tm.bakery.domain.OrderRepository
import com.bakery_tm.bakery.domain.UserRepository
import com.bakery_tm.bakery.models.Address
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class OrderViewModel(
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
): ViewModel() {

    private var user: UserEntity? = null
    private var localOrderId: Long? = null

    private val _order = MutableStateFlow<OrderWithItems?>(null)
    val order: StateFlow<OrderWithItems?> = _order

    private val _orders = MutableStateFlow<List<OrderWithItems>>(emptyList())
    val orders: StateFlow<List<OrderWithItems>> = _orders

    init {
        getOrders()
    }

    fun getOrders() {
        viewModelScope.launch {
            user = userRepository.getLoggedInUser().firstOrNull()
            user?.userId?.let { getAllOrders(it) }
        }
    }

    fun onLogoutClicked() {
        user = null
        localOrderId = null
        _order.value = null
        _orders.value = emptyList()
    }

    fun createOrder(address: Address) {
        viewModelScope.launch {
            user?.userId?.let {
                orderRepository.createOrder(it, address)
            }
        }
    }

    fun getDetailedOrder(orderId: Long) {
        updateSelectedOrderId(orderId)
        viewModelScope.launch {
            _order.value = orderRepository.getOrderDetails(orderId)
        }
    }

    private fun updateSelectedOrderId(orderId: Long) {
        if (localOrderId != orderId) {
            _order.value = null
            localOrderId = orderId
        }
    }

    private suspend fun getAllOrders(userId: Int) {
        orderRepository.getAllOrders(userId).collect {
            _orders.value = it
        }
    }
}