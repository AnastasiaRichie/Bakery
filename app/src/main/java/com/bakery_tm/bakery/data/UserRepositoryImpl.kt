package com.bakery_tm.bakery.data

import com.bakery_tm.bakery.data.database.UserDao
import com.bakery_tm.bakery.data.database.entity.UserEntity
import com.bakery_tm.bakery.domain.AuthState
import com.bakery_tm.bakery.domain.UserRepository
import com.bakery_tm.bakery.models.UserStateModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class UserRepositoryImpl(private val userDao: UserDao): UserRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)

    override val authState: StateFlow<AuthState> = _authState

    override suspend fun setAuthState(state: AuthState) {
        _authState.emit(state)
    }

    override suspend fun isUserLoggedIn(): Boolean {
        val isLoggedIn = userDao.getLoggedInUser().firstOrNull() != null
        _authState.emit(if (isLoggedIn) AuthState.Authenticated else AuthState.Unauthenticated)
        return isLoggedIn
    }

    override fun getLoggedInUser(): Flow<UserEntity?> {
        return userDao.getLoggedInUser()
    }

    override suspend fun getUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }

    override suspend fun updateIsLoggedIn(isLoggedIn: Boolean, email: String) {
        withContext(Dispatchers.IO) {
            _authState.emit(if (isLoggedIn) AuthState.Authenticated else AuthState.Unauthenticated)
            userDao.updateIsLoggedIn(isLoggedIn = isLoggedIn, email = email)
        }
    }

    override suspend fun registerUser(data: UserEntity) {
        userDao.insert(data)
    }

    override suspend fun updateProfileData(model: UserStateModel, userId: Int) {
        withContext(Dispatchers.IO) {
            userDao.updateUserName(model.name, userId)
            userDao.updateUserSurname(model.surname.orEmpty(), userId)
            userDao.updateUserEmail(model.email, userId)
            if (model.password.isNotEmpty()) {
                userDao.updateUserPassword(model.password.hashCode().toString(), userId)
            }
        }
    }
}