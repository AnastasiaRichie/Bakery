package com.bakery_tm.bakery.common.mapper

import com.bakery_tm.bakery.data.database.entity.ProductEntity
import com.bakery_tm.bakery.models.FoodModel

fun ProductEntity.toDomain(): FoodModel{
    return FoodModel(
        id = productId,
        name = name,
        weight = weight,
        description = description,
        fullDescription = fullDescription,
        price = price,
        foodType = foodType,
        foodImageName = foodImageName
    )
}