package com.bakery_tm.bakery.domain

import kotlinx.coroutines.flow.Flow

interface FoodRepository {

    suspend fun getProducts(): Flow<List<ProductDomainModel>>
}