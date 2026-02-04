package com.bakery_tm.bakery.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakery_tm.bakery.data.database.entity.CartItemEntity
import com.bakery_tm.bakery.data.database.entity.UserEntity
import com.bakery_tm.bakery.data.database.relations.CartItemWithProduct
import com.bakery_tm.bakery.domain.ShoppingCartRepository
import com.bakery_tm.bakery.domain.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ShoppingCartViewModel(
    private val shoppingCartRepository: ShoppingCartRepository,
    private val userRepository: UserRepository,
): ViewModel() {

    private var localUser: UserEntity? = null

    private val _cartItems = MutableStateFlow<List<CartItemWithProduct>>(emptyList())
    val cartItems: StateFlow<List<CartItemWithProduct>> = _cartItems

    private val _state = MutableStateFlow(CartState())
    val state: StateFlow<CartState> = _state

    private val _cartSum = MutableStateFlow(0.0)
    val cartSum: StateFlow<Double> = _cartSum

    init {
        getShoppingCart()
    }

    fun getShoppingCart() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getUser().collect { user ->
                localUser = user
                user?.userId?.let { userId ->
                    launch {
                        shoppingCartRepository.getCartFull(userId).collect { _cartItems.value = it }
                    }
                    launch {
                        shoppingCartRepository.calculateCartTotal(userId).collect {
                            _cartSum.value = it
                        }
                    }
                }
            }
        }
    }

    fun onLogoutClicked() {
        _state.value = CartState()
        _cartItems.value = emptyList()
        localUser = null
    }

    fun getCartInfoByProductId(productId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            shoppingCartRepository.getCart(productId).collect { _state.value = CartState(false, it) }
        }
    }

    fun updateQuantity(add: Boolean, productId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            localUser?.userId?.let { userId ->
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
            Log.e("qwe", "addProduct user?.userId: " + localUser?.userId)
            localUser?.userId?.let { userId ->
                shoppingCartRepository.addProductToCart(userId, productId)
            }
        }
    }

    fun deleteProduct(cartItemId: Long) {
        viewModelScope.launch {
            shoppingCartRepository.removeProductFromCart(cartItemId)
        }
    }

    fun updateSelectedState() {
        viewModelScope.launch {
            _state.emit(CartState())
        }
    }

    fun addOrDeleteCartItem(productId: Long) {
        val model = _cartItems.value.find { it.product.productId == productId }
        if (model != null) {
            deleteProduct(model.item.cartItemId)
        } else {
            addProduct(productId)
        }
    }
}

data class CartState(
    val isLoading: Boolean = true,
    val cartItem: CartItemEntity? = null
)