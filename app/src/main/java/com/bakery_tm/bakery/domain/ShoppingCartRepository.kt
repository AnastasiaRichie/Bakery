package com.bakery_tm.bakery.domain

import com.bakery_tm.bakery.data.database.entity.CartItemEntity
import com.bakery_tm.bakery.data.database.relations.CartItemWithProduct
import kotlinx.coroutines.flow.Flow

interface ShoppingCartRepository {

    suspend fun addProduct(userId: Int, productId: Long)

    suspend fun minusProduct(productId: Long)
    suspend fun addProductToCart(userId: Int, productId: Long)
    suspend fun removeProductFromCart(cartItemId: Long)

    fun getCartFull(userId: Int): Flow<List<CartItemWithProduct>>

    fun calculateCartTotal(userId: Int): Flow<Double>

    fun getCart(productId: Long): Flow<CartItemEntity?>
}