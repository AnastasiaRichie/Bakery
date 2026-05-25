package com.bakery_tm.bakery.data

import com.bakery_tm.bakery.common.AuthManager
import com.bakery_tm.bakery.data.api.ErrorHandler
import com.bakery_tm.bakery.data.api.FoodApi
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
import retrofit2.HttpException
import java.io.IOException

class OrderRepositoryImpl(
    private val cartDao: CartDao,
    private val orderApi: OrderApi,
    private val foodApi: FoodApi,
    private val authManager: AuthManager,
    private val errorHandler: ErrorHandler,
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
        } catch (e: IOException) {
            authManager.clearToken()
            authManager.setAuthState(AuthState.Unauthenticated)
        } catch (e: HttpException) {
            try {
                val unavailable = foodApi.getUnavailableProductIds()
                unavailable.forEach { cartDao.deleteByProductId(it) }
            } catch (e: Exception) {
            }
            throw Exception(errorHandler.parseError(e))
        }
    }

    override suspend fun reorder(orderId: Long) {
        try {
            orderApi.reorder(orderId)
        } catch (e: HttpException) {
            try {
                val unavailable = foodApi.getUnavailableProductIds()
                unavailable.forEach { cartDao.deleteByProductId(it) }
            } catch (e: Exception) {
            }
            throw Exception(errorHandler.parseError(e))
        }
    }

    override suspend fun getAllOrders(userId: Int): Flow<List<OrderResponse>> = flow {
        try {
            val orders = orderApi.getOrders().sortedBy { it.date }
            emit(orders)
        } catch (e: IOException) {
            emit(emptyList())
        }
    }

    override suspend fun getOrderDetails(orderId: Long): OrderResponse? {
        return try { orderApi.getOrder(orderId) } catch (e: IOException) { null }
    }

    override suspend fun calculateOrderTotal(orderId: Long, items: List<OrderResponseItem>): Double {
        return items.sumOf { it.quantity * it.product.price.replace(" BYN", "").replace(",", ".").toDouble() }
    }

    override suspend fun getAllOrders(): List<OrderResponse> {
        return orderApi.getAllOrders()
    }

    override suspend fun getOrdersByEmail(email: String): List<OrderResponse> {
        return orderApi.getOrdersByEmail(email)
    }

    override suspend fun markOrderReceived(orderId: Long) {
        orderApi.markOrderReceived(orderId)
    }
}
