package com.bakery_tm.bakery.data

import com.bakery_tm.bakery.data.database.CartDao
import com.bakery_tm.bakery.data.database.OrderDao
import com.bakery_tm.bakery.data.database.entity.OrderEntity
import com.bakery_tm.bakery.data.database.entity.OrderItemEntity
import com.bakery_tm.bakery.data.database.relations.OrderWithItems
import com.bakery_tm.bakery.domain.OrderRepository
import com.bakery_tm.bakery.models.Address
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class OrderRepositoryImpl(
    private val orderDao: OrderDao,
    private val cartDao: CartDao,
): OrderRepository {
    override suspend fun createOrder(userId: Int, address: Address) {
        val cartItems = cartDao.getCarts(userId).firstOrNull()
        if (cartItems.isNullOrEmpty()) return
        val orderId = orderDao.insertOrder(
            OrderEntity(
                userOwnerId = userId,
                orderAddress = address,
                date = System.currentTimeMillis()
            )
        )

        cartItems.forEach { item ->
            orderDao.insertOrderItem(
                OrderItemEntity(
                    orderId = orderId,
                    productId = item.productId,
                    quantity = item.quantity
                )
            )
        }
        cartDao.clearCart(userId)
    }

    override fun getAllOrders(userId: Int): Flow<List<OrderWithItems>> {
        return orderDao.getOrdersForUser(userId)
    }

    override suspend fun getOrderDetails(orderId: Long): OrderWithItems {
        return orderDao.getOrderDetails(orderId)
    }
}
