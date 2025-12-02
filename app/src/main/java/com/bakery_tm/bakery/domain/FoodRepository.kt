package com.bakery_tm.bakery.domain

import com.bakery_tm.bakery.models.FoodModel
import kotlinx.coroutines.flow.Flow

interface FoodRepository {

    suspend fun getProducts(): Flow<List<FoodModel>>
}