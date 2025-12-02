package com.bakery_tm.bakery.data

import com.bakery_tm.bakery.domain.OrderRepository
import com.bakery_tm.bakery.models.FoodModel

class OrderRepositoryImpl(
    private val orderStore: OrderStore
): OrderRepository {

    override fun updateOrder(order: FoodModel, count: Int) {
        orderStore.putProduct(order, count)
    }

    override fun getOrder(): Map<FoodModel, Int> {
        return orderStore.getOrder()
    }
}