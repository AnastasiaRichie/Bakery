package com.bakery_tm.bakery.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakery_tm.bakery.common.SessionManager
import com.bakery_tm.bakery.models.FieldType
import com.bakery_tm.bakery.models.LoginEvent
import com.bakery_tm.bakery.models.UserStateModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _isLoggedIn = MutableSharedFlow<Boolean>(replay = 0)
    val isLoggedIn: SharedFlow<Boolean> = _isLoggedIn

    private val _state = MutableStateFlow(UserStateModel())
    val state: StateFlow<UserStateModel> = _state

    private val _events = MutableSharedFlow<LoginEvent>(replay = 0)
    val events: SharedFlow<LoginEvent> = _events

    init {
        if (sessionManager.isLoggedIn()) {
            viewModelScope.launch {
                _isLoggedIn.emit(sessionManager.isLoggedIn())
            }
        }
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _events.emit(LoginEvent.NavigateToFood)
        }
    }

    fun onSignUpClick() {
        viewModelScope.launch {
            _events.emit(LoginEvent.NavigateToRegister)
        }
    }

    fun onGuestClick() {
        viewModelScope.launch {
            _events.emit(LoginEvent.NavigateToFood)
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