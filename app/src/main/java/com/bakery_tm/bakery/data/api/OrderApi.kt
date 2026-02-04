package com.bakery_tm.bakery.data.api

import com.bakery_tm.bakery.domain.OrderIdModel
import com.bakery_tm.bakery.domain.OrderRequestDomainModel
import com.bakery_tm.bakery.domain.OrderResponse
import com.bakery_tm.bakery.domain.ReorderResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderApi {
    @GET("orders")
    suspend fun getOrders(): List<OrderResponse>

    @POST("order")
    suspend fun createOrder(@Body order: OrderRequestDomainModel): OrderIdModel

    @GET("orders/{id}")
    suspend fun getOrder(@Path("id") orderId: Long): OrderResponse

    @POST("orders/{id}/reorder")
    suspend fun reorder(@Path("id") orderId: Long): ReorderResponse

    @PATCH("update-user")
    suspend fun updateUser(@Body request: UpdateUserRequest): UserResponse
}

