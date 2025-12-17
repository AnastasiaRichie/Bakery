package com.bakery_tm.bakery.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakery_tm.bakery.data.database.entity.CartItemEntity
import com.bakery_tm.bakery.data.database.entity.UserEntity
import com.bakery_tm.bakery.data.database.relations.CartItemWithProduct
import com.bakery_tm.bakery.data.database.relations.OrderWithItems
import com.bakery_tm.bakery.domain.ShoppingCartRepository
import com.bakery_tm.bakery.domain.UserRepository
import com.bakery_tm.bakery.models.Address
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ShoppingCartViewModel(
    private val shoppingCartRepository: ShoppingCartRepository,
    private val userRepository: UserRepository,
): ViewModel() {

    private var user: UserEntity? = null
    private var localOrderId: Long? = null

    private val _cartItems = MutableStateFlow<List<CartItemWithProduct>>(listOf())
    val cartItems: StateFlow<List<CartItemWithProduct>> = _cartItems

    private val _cartItem = MutableStateFlow<CartItemEntity?>(null)
    val cartItem: StateFlow<CartItemEntity?> = _cartItem

    private val _order = MutableStateFlow<OrderWithItems?>(null)
    val order: StateFlow<OrderWithItems?> = _order

    private val _orders = MutableStateFlow<List<OrderWithItems>>(emptyList())
    val orders: StateFlow<List<OrderWithItems>> = _orders

    init {
        viewModelScope.launch {
            user = userRepository.getLoggedInUser()
            launch {
                user?.userId?.let {
                    shoppingCartRepository.getCartFull(it).collect { _cartItems.value = it }
                }
            }
            launch {
                user?.userId?.let { getAllOrders(it) }
            }
        }
    }

    fun getCartInfoByProductId(productId: Long) {
        viewModelScope.launch {
            shoppingCartRepository.getCart(productId).collect { _cartItem.value = it }
        }
    }

    fun updateQuantity(add: Boolean, productId: Long) {
        viewModelScope.launch {
            user?.userId?.let { userId ->
                if (add) {
                    shoppingCartRepository.addProduct(userId, productId)
                } else {
                    shoppingCartRepository.minusProduct(productId)
                }
            }
        }
    }

    fun addProduct(productId: Long) {
        viewModelScope.launch {
            user?.userId?.let { userId ->
                shoppingCartRepository.addProductToCart(userId, productId)
            }
        }
    }

    fun deleteProduct(itemId: Long) {
        viewModelScope.launch {
            shoppingCartRepository.removeProductFromCart(itemId)
        }
    }

    fun createOrder(address: Address) {
        viewModelScope.launch {
            user?.userId?.let {
                shoppingCartRepository.createOrder(it, address)
            }
        }
    }

    fun getDetailedOrder(orderId: Long) {
        updateSelectedOrderId(orderId)
        viewModelScope.launch {
            _order.value = shoppingCartRepository.getOrderDetails(orderId)
        }
    }

    private fun updateSelectedOrderId(orderId: Long) {
        if (localOrderId != orderId) {
            _order.value = null
            localOrderId = orderId
        }
    }

    private suspend fun getAllOrders(userId: Int) {
        shoppingCartRepository.getAllOrders(userId).collect {
            _orders.value = it
        }
    }
}