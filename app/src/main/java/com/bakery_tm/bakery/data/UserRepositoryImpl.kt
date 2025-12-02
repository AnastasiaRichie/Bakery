package com.bakery_tm.bakery.data

import com.bakery_tm.bakery.data.database.UserDao
import com.bakery_tm.bakery.data.database.entity.UserEntity
import com.bakery_tm.bakery.domain.UserRepository

class UserRepositoryImpl(private val userDao: UserDao): UserRepository {

    override suspend fun isUserLoggedIn(): Boolean {
        return userDao.getLoggedInUser() != null
    }

    override suspend fun getLoggedInUser(): UserEntity? {
        return userDao.getLoggedInUser()
    }

    override suspend fun getUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }

    override suspend fun updateIsLoggedIn(isLoggedIn: Boolean, email: String) {
        userDao.updateIsLoggedIn(isLoggedIn = isLoggedIn, email = email)
    }

    override suspend fun registerUser(data: UserEntity) {
        userDao.insert(data)
    }
}