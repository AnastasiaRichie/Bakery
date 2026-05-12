package com.bakery_tm.bakery.data.api

import com.bakery_tm.bakery.models.UserType
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST

interface UserApi {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): TokenResponse

    @POST("login")
    suspend fun login(@Body request: LoginRequest): UserResponse

    @POST("get-user-by-email")
    suspend fun getUserByEmail(@Body request: EmailRequest): UserResponse

    @PATCH("update-user-pass")
    suspend fun updateUserPassword(@Body request: UpdateUserPassRequest)
}
data class LoginRequest(val email: String, val password: String)
data class EmailRequest(val email: String)
data class RegisterRequest(
    val name: String,
    val lastName: String? = null,
    val email: String,
    val password: String,
)
data class TokenResponse(val token: String, val userId: Int, val userType: UserType)

data class UserResponse(
    val token: String? = null,
    val userId: Int,
    val userType: UserType,
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

data class UpdateUserPassRequest(
    val email: String,
    val password: String
)