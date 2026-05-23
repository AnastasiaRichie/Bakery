package com.bakery_tm.bakery.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakery_tm.bakery.common.AuthManager
import com.bakery_tm.bakery.domain.UserRepository
import com.bakery_tm.bakery.models.EmptyFieldException
import com.bakery_tm.bakery.models.FieldType
import com.bakery_tm.bakery.models.NavigationEvent
import com.bakery_tm.bakery.models.SimplePasswordException
import com.bakery_tm.bakery.models.UserStateModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.ConnectException

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
                if (email.isEmpty() || password.isEmpty()) throw EmptyFieldException()
                userRepository.login(email, password)
            } catch (e: ConnectException) {
                _events.emit(NavigationEvent.ShowError("Подключите интернет для продолжения работы"))
            } catch (e: Exception) {
                _events.emit(NavigationEvent.ShowError(e.message.orEmpty()))
            }
        }
    }

    fun onRegisterClick(model: UserStateModel) {
        viewModelScope.launch {
            try {
                if (model.email.isEmpty() || model.name.isEmpty() || model.password.isEmpty()) throw EmptyFieldException()
                if (model.password.length < 6) throw SimplePasswordException()
                userRepository.register(model)
            } catch (e: ConnectException) {
                _events.emit(NavigationEvent.ShowError("Подключите интернет для продолжения работы"))
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