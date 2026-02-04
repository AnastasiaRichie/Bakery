package com.bakery_tm.bakery.domain

import com.bakery_tm.bakery.data.database.entity.UserEntity
import com.bakery_tm.bakery.models.UserStateModel
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getUser(): Flow<UserEntity?>

    suspend fun insertUser(user: UserEntity)

    suspend fun deleteUser()

    suspend fun register(model: UserStateModel)

    suspend fun login(email: String, password: String)

    suspend fun updateUser(name: String?, lastName: String?, email: String?, password: String?)
}