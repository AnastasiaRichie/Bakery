package com.bakery_tm.bakery.data

import com.bakery_tm.bakery.common.AuthManager
import com.bakery_tm.bakery.data.api.NoAuthException
import com.bakery_tm.bakery.data.api.OrderApi
import com.bakery_tm.bakery.data.database.CartDao
import com.bakery_tm.bakery.data.database.entity.toModel
import com.bakery_tm.bakery.domain.AuthState
import com.bakery_tm.bakery.domain.OrderRepository
import com.bakery_tm.bakery.domain.OrderRequestDomainModel
import com.bakery_tm.bakery.domain.OrderResponse
import com.bakery_tm.bakery.domain.OrderResponseItem
import com.bakery_tm.bakery.domain.OrderState
import com.bakery_tm.bakery.models.Address
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class OrderRepositoryImpl(
    private val cartDao: CartDao,
    private val orderApi: OrderApi,
    private val authManager: AuthManager,
): OrderRepository {

    override suspend fun createOrder(userId: Int, address: Address) {
        val cartItems = cartDao.getCarts(userId).firstOrNull()
        if (cartItems.isNullOrEmpty()) return
        try {
            orderApi.createOrder(
                OrderRequestDomainModel(
                    userId = userId,
                    address = address,
                    date = System.currentTimeMillis(),
                    orderState = OrderState.ORDERED,
                    items = cartItems.map { it.toModel() }
                )
            )
            cartDao.clearCart(userId)
        } catch (e: NoAuthException) {
            authManager.clearToken()
            authManager.setAuthState(AuthState.Unauthenticated)
        }
    }

    override suspend fun reorder(orderId: Long) {
        orderApi.reorder(orderId)
    }

    override suspend fun getAllOrders(userId: Int): Flow<List<OrderResponse>> = flow {
        try {
            val orders = orderApi.getOrders().sortedBy { it.date }
            emit(orders)
        } catch (e: NoAuthException) {
            emit(emptyList())
        }
    }

    override suspend fun getOrderDetails(orderId: Long): OrderResponse? {
        return try { orderApi.getOrder(orderId) } catch (e: NoAuthException) { null }
    }

    override suspend fun calculateOrderTotal(orderId: Long, items: List<OrderResponseItem>): Double {
        return items.sumOf { it.quantity * it.product.price.replace(" BYN", "").toDouble() }
    }
}
