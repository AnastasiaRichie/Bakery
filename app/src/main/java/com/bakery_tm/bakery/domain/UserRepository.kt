package com.bakery_tm.bakery.domain

import com.bakery_tm.bakery.data.database.entity.UserEntity
import com.bakery_tm.bakery.models.UserStateModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val authState: StateFlow<AuthState>

    suspend fun setAuthState(state: AuthState)

    suspend fun isUserLoggedIn(): Boolean

    fun getLoggedInUser(): Flow<UserEntity?>

    suspend fun getUserByEmail(email: String): UserEntity?

    suspend fun updateIsLoggedIn(isLoggedIn: Boolean, email: String)

    suspend fun registerUser(data: UserEntity)

    suspend fun updateProfileData(model: UserStateModel, userId: Int)
}