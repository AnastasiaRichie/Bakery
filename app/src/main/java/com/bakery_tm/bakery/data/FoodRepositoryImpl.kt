package com.bakery_tm.bakery.data

import com.bakery_tm.bakery.common.mapper.toDomain
import com.bakery_tm.bakery.common.mapper.toEntity
import com.bakery_tm.bakery.data.api.FoodApi
import com.bakery_tm.bakery.data.api.NoAuthException
import com.bakery_tm.bakery.data.database.ProductDao
import com.bakery_tm.bakery.domain.ProductDomainModel
import com.bakery_tm.bakery.domain.FoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class FoodRepositoryImpl(private val productDao: ProductDao, private val foodApi: FoodApi) : FoodRepository {

    override suspend fun getProducts(): Flow<List<ProductDomainModel>> = flow {
        try {
            if (productDao.getAllProducts().isEmpty()) {
                val products = foodApi.getProducts()
                productDao.insertAllProducts(products.map { it.toEntity() })
                emit(products)
            } else {
                emit(productDao.getAllProducts().map { it.toDomain() })
                val products = foodApi.getProducts()
                productDao.insertAllProducts(products.map { it.toEntity() })
            }
        } catch (e: NoAuthException) {
            emit(emptyList())
        }
    }
}