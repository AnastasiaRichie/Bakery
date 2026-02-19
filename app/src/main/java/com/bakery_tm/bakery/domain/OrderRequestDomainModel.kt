package com.bakery_tm.bakery.domain

import com.bakery_tm.bakery.models.Address
import com.bakery_tm.bakery.models.ProductModel

data class OrderRequestDomainModel(
    val userId: Int,
    val address: Address,
    val date: Long,
    val orderState: OrderState,
    val items: List<OrderRequestItem>
)

data class OrderRequestItem(
    val quantity: Int,
    val productId: Long
)

data class OrderResponse(
    val orderId: Long,
    val userId: Int,
    val address: Address,
    val orderState: OrderState,
    val date: Long,
    val items: List<OrderResponseItem>
)

data class OrderResponseItem(
    val id: Long,
    val quantity: Int,
    val product: ProductModel
)

data class OrderIdModel(val orderId: String)

data class ReorderResponse(val orderId: Long)

enum class OrderState {
    ORDERED,
    RECEIVED,
}