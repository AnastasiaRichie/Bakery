package com.bakery_tm.bakery.data.api

import com.bakery_tm.bakery.domain.ProductDomainModel
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FoodApi {

    @GET("products")
    suspend fun getProducts(): List<ProductDomainModel>

    @POST("product/{id}")
    suspend fun removeProduct(@Path("id") productId: Long): List<ProductDomainModel>
}