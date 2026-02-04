package com.bakery_tm.bakery.data

import com.bakery_tm.bakery.common.AuthManager
import com.bakery_tm.bakery.data.api.ErrorHandler
import com.bakery_tm.bakery.data.api.LoginRequest
import com.bakery_tm.bakery.data.api.OrderApi
import com.bakery_tm.bakery.data.api.UpdateUserRequest
import com.bakery_tm.bakery.data.api.UserApi
import com.bakery_tm.bakery.data.database.UserDao
import com.bakery_tm.bakery.data.database.entity.UserEntity
import com.bakery_tm.bakery.data.database.entity.toEntity
import com.bakery_tm.bakery.domain.UserRepository
import com.bakery_tm.bakery.models.UserStateModel
import com.bakery_tm.bakery.models.toApi
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class UserRepositoryImpl(
    private val loginApi: UserApi,
    private val orderApi: OrderApi,
    private val userDao: UserDao,
    private val authManager: AuthManager,
    private val errorHandler: ErrorHandler,
): UserRepository {

    override fun getUser(): Flow<UserEntity?> = userDao.getUser()

    override suspend fun insertUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    override suspend fun deleteUser() {
        userDao.deleteUser()
        authManager.clearToken()
    }

    override suspend fun register(model: UserStateModel) {
        try {
            val tokenResponse = loginApi.register(model.toApi())
            userDao.insertUser(model.toEntity().copy(userId = tokenResponse.userId))
            authManager.saveToken(tokenResponse.token)
        } catch (e: HttpException) {
            throw Exception(errorHandler.parseError(e))
        }
    }

    override suspend fun login(email: String, password: String) {
        try {
            val user = loginApi.login(LoginRequest(email, password))
            userDao.insertUser(user.toEntity())
            user.token?.let { authManager.saveToken(it) }
        } catch (e: HttpException) {
            throw Exception(errorHandler.parseError(e))
        }
    }

    override suspend fun updateUser(name: String?, lastName: String?, email: String?, password: String?) {
        try {
            if (name == null && lastName == null && email == null && password == null) return
            orderApi.updateUser(UpdateUserRequest(name, lastName, email, password))
            name?.let { userDao.updateUserName(it) }
            lastName?.let { userDao.updateUserSurname(it) }
            email?.let { userDao.updateUserEmail(it) }
        } catch (e: HttpException) {
            throw Exception(errorHandler.parseError(e))
        }
    }
}