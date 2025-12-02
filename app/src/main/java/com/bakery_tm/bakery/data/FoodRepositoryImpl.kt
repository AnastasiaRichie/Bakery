package com.bakery_tm.bakery.data

import com.bakery_tm.bakery.common.mapper.toDomain
import com.bakery_tm.bakery.data.database.ProductDao
import com.bakery_tm.bakery.domain.FoodRepository
import com.bakery_tm.bakery.models.FoodModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FoodRepositoryImpl(private val productDao: ProductDao) : FoodRepository {

    override suspend fun getProducts(): Flow<List<FoodModel>> = flow {
        emit(productDao.getAllProducts().map { it.toDomain() })
    }
}