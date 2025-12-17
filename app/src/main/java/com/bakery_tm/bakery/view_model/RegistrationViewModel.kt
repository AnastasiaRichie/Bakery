package com.bakery_tm.bakery.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakery_tm.bakery.common.SessionManager
import com.bakery_tm.bakery.data.database.entity.toEntity
import com.bakery_tm.bakery.domain.UserRepository
import com.bakery_tm.bakery.models.FieldType
import com.bakery_tm.bakery.models.NavigationEvent
import com.bakery_tm.bakery.models.UserStateModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val sessionManager: SessionManager,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _isLoggedIn = MutableSharedFlow<Boolean>(replay = 0)
    val isLoggedIn: SharedFlow<Boolean> = _isLoggedIn

    private val _state = MutableStateFlow(UserStateModel())
    val state: StateFlow<UserStateModel> = _state

    private val _events = MutableSharedFlow<NavigationEvent>(replay = 0)
    val events: SharedFlow<NavigationEvent> = _events

    init {
        if (sessionManager.isLoggedIn()) {
            viewModelScope.launch {
                _isLoggedIn.emit(sessionManager.isLoggedIn())
            }
        }
    }

    fun onLoginClick(email: String) {
        viewModelScope.launch {
            try {
                _events.emit(NavigationEvent.NavigateToFood)
            } finally {
                userRepository.updateIsLoggedIn(true, email)
            }
        }
    }

    fun onRegisterClick(user: UserStateModel) {
        viewModelScope.launch {
            try {
                userRepository.registerUser(user.toEntity(true))
            } catch (e: Exception) {
                Log.e("RegistrationViewModel", "onRegisterClick e: $e")
            } finally {
                _events.emit(NavigationEvent.NavigateToFood)
            }
        }
    }

    fun onSignUpClick() {
        viewModelScope.launch {
            _events.emit(NavigationEvent.NavigateToRegister)
        }
    }

    fun onGuestClick() {
        viewModelScope.launch {
            _events.emit(NavigationEvent.NavigateToFood)
        }
    }

    fun onLoginValueChanged(valueType: FieldType, value: String) {
        _state.update {
            when (valueType) {
                FieldType.EMAIL -> it.copy(email = value)
                FieldType.PASSWORD -> it.copy(password = value)
                else -> it
            }
        }
    }

    fun onRegistrationValueChanged(valueType: FieldType, value: String) {
        _state.update {
            when (valueType) {
                FieldType.NAME -> it.copy(name = value)
                FieldType.SURNAME -> it.copy(surname = value)
                FieldType.EMAIL -> it.copy(email = value)
                FieldType.PASSWORD -> it.copy(password = value)
            }
        }
    }

    fun login() {
        sessionManager.setLoggedIn(true)
        viewModelScope.launch {
            _isLoggedIn.emit(true)
        }
    }

    fun logout() {
        sessionManager.setLoggedIn(false)
        viewModelScope.launch {
            _isLoggedIn.emit(false)
        }
    }
}