package com.bakery_tm.bakery.data.api

import com.bakery_tm.bakery.domain.ProductDomainModel
import retrofit2.http.GET

interface FoodApi {

    @GET("products")
    suspend fun getProducts(): List<ProductDomainModel>
}