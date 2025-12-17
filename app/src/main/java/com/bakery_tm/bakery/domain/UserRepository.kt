package com.bakery_tm.bakery.domain

import com.bakery_tm.bakery.data.database.entity.UserEntity
import com.bakery_tm.bakery.screen.AccountFieldType

interface UserRepository {

    suspend fun isUserLoggedIn(): Boolean

    suspend fun getLoggedInUser(isLogout: Boolean = true): UserEntity?

    suspend fun getUserByEmail(email: String): UserEntity?

    suspend fun updateIsLoggedIn(isLoggedIn: Boolean, email: String)

    suspend fun registerUser(data: UserEntity)

    suspend fun updateField(fieldType: AccountFieldType, value: String, userId: Int)
}