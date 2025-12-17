package com.bakery_tm.bakery.data

import com.bakery_tm.bakery.data.database.UserDao
import com.bakery_tm.bakery.data.database.entity.UserEntity
import com.bakery_tm.bakery.domain.UserRepository
import com.bakery_tm.bakery.screen.AccountFieldType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepositoryImpl(private val userDao: UserDao): UserRepository {

    override suspend fun isUserLoggedIn(): Boolean {
        return userDao.getLoggedInUser() != null
    }

    override suspend fun getLoggedInUser(isLogout: Boolean): UserEntity? {
        return withContext(Dispatchers.IO) { userDao.getLoggedInUser() }
    }

    override suspend fun getUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }

    override suspend fun updateIsLoggedIn(isLoggedIn: Boolean, email: String) {
        withContext(Dispatchers.IO) {
            userDao.updateIsLoggedIn(isLoggedIn = isLoggedIn, email = email)
        }
    }

    override suspend fun registerUser(data: UserEntity) {
        userDao.insert(data)
    }

    override suspend fun updateField(fieldType: AccountFieldType, value: String, userId: Int) {
        withContext(Dispatchers.IO) {
            when (fieldType) {
                AccountFieldType.NAME -> userDao.updateUserName(value, userId)
                AccountFieldType.SURNAME -> userDao.updateUserSurname(value, userId)
                AccountFieldType.EMAIL -> userDao.updateUserEmail(value, userId)
                AccountFieldType.PASSWORD -> userDao.updateUserPassword(value.hashCode().toString(), userId)
            }
        }
    }
}