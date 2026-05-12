package com.bakery_tm.bakery.data.api

import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST

interface UserApi {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): TokenResponse

    @POST("login")
    suspend fun login(@Body request: LoginRequest): UserResponse


}
data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(
    val name: String,
    val lastName: String? = null,
    val email: String,
    val password: String,
)
data class TokenResponse(val token: String, val userId: Int)

data class UserResponse(
    val token: String? = null,
    val userId: Int,
    val email: String,
    val name: String,
    val lastName: String
)

data class UpdateUserRequest(
    val name: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val password: String? = null
)