package com.bakery_tm.bakery.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakery_tm.bakery.common.AuthManager
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
    private val userRepository: UserRepository,
    private val authManager: AuthManager,
) : ViewModel() {

    val authState = authManager.authState

    private val _state = MutableStateFlow(UserStateModel())
    val state: StateFlow<UserStateModel> = _state

    private val _events = MutableSharedFlow<NavigationEvent>(replay = 0)
    val events: SharedFlow<NavigationEvent> = _events

    fun onLoginClick(email: String, password: String) {
        viewModelScope.launch {
            try {
                userRepository.login(email, password)
            } catch (e: Exception) {
                Log.e("qwe", "onLoginClick e: " + e)
                _events.emit(NavigationEvent.ShowError(e.message.orEmpty()))
            }
        }
    }

    fun onRegisterClick(model: UserStateModel) {
        viewModelScope.launch {
            try {
                userRepository.register(model)
            } catch (e: Exception) {
                _events.emit(NavigationEvent.ShowError(e.message.orEmpty()))
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
                FieldType.SURNAME -> it.copy(lastName = value)
                FieldType.EMAIL -> it.copy(email = value)
                FieldType.PASSWORD -> it.copy(password = value)
            }
        }
    }
}