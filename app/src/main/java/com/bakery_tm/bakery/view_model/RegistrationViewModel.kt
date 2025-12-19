package com.bakery_tm.bakery.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class RegistrationViewModel(private val userRepository: UserRepository) : ViewModel() {

    val authState = userRepository.authState

    private val _state = MutableStateFlow(UserStateModel())
    val state: StateFlow<UserStateModel> = _state

    private val _events = MutableSharedFlow<NavigationEvent>(replay = 0)
    val events: SharedFlow<NavigationEvent> = _events

    fun onLoginClick(email: String, password: String) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUserByEmail(email)
                when {
                    user == null -> {
                        _events.emit(NavigationEvent.ShowError("Пользователя с таким email не существует!"))
                        return@launch
                    }
                    user.hashedPassword != password.hashCode().toString() -> {
                        _events.emit(NavigationEvent.ShowError("Проверьте корректность введенных данных!"))
                        return@launch
                    }
                    else -> {
                        try {
                            userRepository.updateIsLoggedIn(true, email)
                        } finally {
                            _events.emit(NavigationEvent.NavigateToFood)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("RegistrationViewModel", "onLoginClick e: $e")
            }
        }
    }

    fun onRegisterClick(model: UserStateModel) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUserByEmail(model.email)
                if (user != null) {
                    _events.emit(NavigationEvent.ShowError("Пользователь с таким email уже существует!"))
                    return@launch
                }
                try {
                    userRepository.registerUser(model.toEntity(true))
                } finally {
                    _events.emit(NavigationEvent.NavigateToFood)
                }
            } catch (e: Exception) {
                Log.e("RegistrationViewModel", "onRegisterClick e: $e")
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
}