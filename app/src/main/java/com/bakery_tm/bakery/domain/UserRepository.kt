package com.bakery_tm.bakery.domain

import com.bakery_tm.bakery.data.database.entity.UserEntity

interface UserRepository {

    suspend fun isUserLoggedIn(): Boolean

    suspend fun getLoggedInUser(): UserEntity?

    suspend fun getUserByEmail(email: String): UserEntity?

    suspend fun updateIsLoggedIn(isLoggedIn: Boolean, email: String)

    suspend fun registerUser(data: UserEntity)
}