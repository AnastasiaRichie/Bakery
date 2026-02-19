package com.bakery_tm.bakery.domain

import com.bakery_tm.bakery.models.Address
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    suspend fun createOrder(userId: Int, address: Address)
    suspend fun reorder(orderId: Long)

    suspend fun getAllOrders(userId: Int): Flow<List<OrderResponse>>

    suspend fun getOrderDetails(orderId: Long): OrderResponse?

    suspend fun calculateOrderTotal(orderId: Long, items: List<OrderResponseItem>): Double
}
