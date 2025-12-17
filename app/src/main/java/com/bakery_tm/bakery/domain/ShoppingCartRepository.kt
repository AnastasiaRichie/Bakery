package com.bakery_tm.bakery.domain

import com.bakery_tm.bakery.data.database.entity.CartItemEntity
import com.bakery_tm.bakery.data.database.relations.CartItemWithProduct
import com.bakery_tm.bakery.data.database.relations.OrderWithItems
import com.bakery_tm.bakery.models.Address
import kotlinx.coroutines.flow.Flow

interface ShoppingCartRepository {

    // Cart
    suspend fun addProduct(userId: Int, productId: Long)

    suspend fun minusProduct(productId: Long)
    suspend fun addProductToCart(userId: Int, productId: Long)
    suspend fun removeProductFromCart(id: Long)

    fun getCartFull(userId: Int): Flow<List<CartItemWithProduct>>

    fun calculateCartTotal(userId: Int): Flow<Double>

    fun getCart(productId: Long): Flow<CartItemEntity?>

    // Order
    suspend fun createOrder(userId: Int, address: Address)

    fun getAllOrders(userId: Int): Flow<List<OrderWithItems>>

    suspend fun getOrderDetails(orderId: Long): OrderWithItems
}