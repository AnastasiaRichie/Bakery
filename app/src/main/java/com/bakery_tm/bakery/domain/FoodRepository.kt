package com.bakery_tm.bakery.domain

import kotlinx.coroutines.flow.Flow

interface FoodRepository {

    fun getProducts(): Flow<List<ProductDomainModel>>

    suspend fun removeProduct(productId: Long): List<ProductDomainModel>

    suspend fun returnBackProduct(productId: Long): List<ProductDomainModel>
}