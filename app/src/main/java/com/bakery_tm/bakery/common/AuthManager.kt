package com.bakery_tm.bakery.common

import android.content.SharedPreferences
import androidx.core.content.edit
import com.auth0.android.jwt.JWT
import com.bakery_tm.bakery.domain.AuthState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthManager(
    private val prefs: SharedPreferences,
    private val applicationScope: CoroutineScope
) {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)

    val authState: StateFlow<AuthState> = _authState

    fun saveToken(token: String) {
        prefs.edit { putString(KEY_JWT, token) }
        applicationScope.launch {
            _authState.emit(AuthState.Authenticated)
        }
    }

    fun getToken(): String? = prefs.getString(KEY_JWT, null)

    fun clearToken() {
        prefs.edit { remove(KEY_JWT) }
        applicationScope.launch {
            _authState.emit(AuthState.Unauthenticated)
        }
    }

    fun isTokenValid(): Boolean {
        val token = getToken() ?: return false
        return try {
            val jwt = JWT(token)
            applicationScope.launch {
                _authState.emit(AuthState.Authenticated)
            }
            !jwt.isExpired(10)
        } catch (e: Exception) {
            applicationScope.launch {
                _authState.emit(AuthState.Unauthenticated)
            }
            false
        }
    }

    suspend fun setAuthState(state: AuthState) {
        _authState.emit(state)
    }

    companion object {
        private const val KEY_JWT = "JWT_TOKEN"
    }
}