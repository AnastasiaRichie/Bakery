package com.bakery_tm.bakery.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakery_tm.bakery.data.database.entity.toModel
import com.bakery_tm.bakery.domain.AuthState
import com.bakery_tm.bakery.domain.UserRepository
import com.bakery_tm.bakery.models.NavigationEvent
import com.bakery_tm.bakery.models.UserState
import com.bakery_tm.bakery.screen.AccountFieldType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {

    val authState = userRepository.authState

    private val _state = MutableStateFlow(UserState())
    val state: StateFlow<UserState> = _state
    private val _events = MutableSharedFlow<NavigationEvent>(replay = 0)
    val events: SharedFlow<NavigationEvent> = _events

    init {
        getUserData()
    }

    fun onLogOutClicked(email: String) {
        viewModelScope.launch {
            try {
                _events.emit(NavigationEvent.NavigateToRegister)
                userRepository.updateIsLoggedIn(false, email)
            } finally {
                getUserData()
            }
        }
    }

    fun onEditClicked(type: AccountFieldType) {
        viewModelScope.launch {
            _events.emit(NavigationEvent.NavigateToEdit(type))
        }
    }

    private fun getUserData() {
        viewModelScope.launch {
            try {
                _state.update { state -> state.copy(isLoading = true) }
                userRepository.getLoggedInUser().collect { user ->
                    _state.update { state -> state.copy(userStateModel = user?.toModel(), isLoading = false) }
                    userRepository.setAuthState(
                        if (user != null) {
                            AuthState.Authenticated
                        } else {
                            AuthState.Unauthenticated
                        }
                    )
                }
            } catch (e: Exception) {
                userRepository.setAuthState(AuthState.Unauthenticated)
            } finally {
                _state.update { state -> state.copy(isLoading = false) }
            }
        }
    }

    fun onEditDataSaved(type: AccountFieldType, enteredValue: String, userId: Int) {
        viewModelScope.launch {
            try {
                userRepository.updateField(type, enteredValue, userId)
                _events.emit(NavigationEvent.NavigateBack)

            } finally {
                val model = userRepository.getLoggedInUser().firstOrNull()?.toModel()
                _state.update { state -> state.copy(userStateModel = model) }
            }
        }
    }
}