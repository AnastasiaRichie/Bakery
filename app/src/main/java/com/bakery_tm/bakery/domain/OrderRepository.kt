package com.bakery_tm.bakery.domain

import com.bakery_tm.bakery.data.database.relations.OrderWithItems
import com.bakery_tm.bakery.models.Address
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    suspend fun createOrder(userId: Int, address: Address)
    suspend fun reorder(orderId: Long)

    fun getAllOrders(userId: Int): Flow<List<OrderWithItems>>

    suspend fun getOrderDetails(orderId: Long): OrderWithItems
    suspend fun calculateOrderTotal(orderId: Long): Double
}
