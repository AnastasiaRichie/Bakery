package com.bakery_tm.bakery.domain

import com.bakery_tm.bakery.models.FoodModel

interface OrderRepository {

    fun updateOrder(order: FoodModel, count: Int)
    fun getOrder(): Map<FoodModel, Int>
}