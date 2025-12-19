package com.bakery_tm.bakery.models

data class FoodModel(
    val foodId: Long,
    val name: String,
    val weight: String,
    val description: String,
    val fullDescription: String,
    val price: String,
    val foodImageName: String,
    val foodType: FoodType = FoodType.FLOUR,
)