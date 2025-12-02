package com.bakery_tm.bakery.data

import com.bakery_tm.bakery.models.FoodModel

object OrderStore {

    //Store data for saving order while work with it

    private var order = mutableMapOf<FoodModel, Int>()

    fun putProduct(productType: FoodModel, count: Int = 1) {
        order.put(productType, count)
        //TODO save to shared
    }

    fun deleteProduct(productType: FoodModel) {
        order.remove(productType)
        //TODO save to shared
    }

    fun getOrder() = order

    fun cleanOrder() {
        order.clear()
    }
}