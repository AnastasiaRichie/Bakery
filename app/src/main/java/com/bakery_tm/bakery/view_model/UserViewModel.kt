package com.bakery_tm.bakery.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakery_tm.bakery.common.AuthManager
import com.bakery_tm.bakery.data.database.entity.toModel
import com.bakery_tm.bakery.domain.AuthState
import com.bakery_tm.bakery.domain.AvatarPreferences
import com.bakery_tm.bakery.domain.UserRepository
import com.bakery_tm.bakery.models.NavigationEvent
import com.bakery_tm.bakery.models.UserState
import com.bakery_tm.bakery.models.UserStateModel
import com.bakery_tm.bakery.screen.ProfileAvatar
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepository,
    private val avatarPrefs: AvatarPreferences,
    private val authManager: AuthManager,
) : ViewModel() {

    val authState = authManager.authState

    private val _state = MutableStateFlow(UserState())
    val state: StateFlow<UserState> = _state
    private val _events = MutableSharedFlow<NavigationEvent>(replay = 0)
    val events: SharedFlow<NavigationEvent> = _events
    private val _selectedAvatar = MutableStateFlow(ProfileAvatar.Calm)
    val selectedAvatar: StateFlow<ProfileAvatar> = _selectedAvatar

    init {
        getUserData()
        loadAvatar()
    }

    fun selectAvatar(avatar: ProfileAvatar) {
        _selectedAvatar.value = avatar
        avatarPrefs.saveAvatar(avatar.name)
    }

    fun onLogOutClicked() {
        viewModelScope.launch {
            try {
                _events.emit(NavigationEvent.NavigateToRegister)
                userRepository.deleteUser()
            } finally {
                getUserData()
            }
        }
    }

    private fun getUserData() {
        viewModelScope.launch {
            try {
                _state.update { state -> state.copy(isLoading = true) }
                userRepository.getUser().collect { user ->
                    _state.update { state -> state.copy(userStateModel = user?.toModel(), isLoading = false) }
                    val isTokenValid = authManager.isTokenValid()
                    authManager.setAuthState(
                        if (isTokenValid) AuthState.Authenticated else AuthState.Unauthenticated
                    )
                }
            } catch (e: Exception) {
                _state.update { state -> state.copy(errorMessage = e.message.orEmpty(), isLoading = false) }
                authManager.setAuthState(AuthState.Unauthenticated)
            } finally {
                _state.update { state -> state.copy(isLoading = false) }
            }
        }
    }

    fun onEditDataSaved(name: String?, lastName: String?, email: String?, password: String?) {
        viewModelScope.launch {
            try {
                userRepository.updateUser(name = name, lastName = lastName, email = email, password = password)
                _events.emit(NavigationEvent.NavigateBack)
            } finally {
                val model = userRepository.getUser().firstOrNull()?.toModel()
                _state.update { state -> state.copy(userStateModel = model) }
            }
        }
    }

    private fun loadAvatar() {
        val name = avatarPrefs.getAvatar()
        _selectedAvatar.value = ProfileAvatar.entries.find { it.name == name } ?: ProfileAvatar.Calm
    }
}