package com.bakery_tm.bakery.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakery_tm.bakery.data.database.entity.toModel
import com.bakery_tm.bakery.domain.UserRepository
import com.bakery_tm.bakery.models.NavigationEvent
import com.bakery_tm.bakery.models.UserState
import com.bakery_tm.bakery.screen.AccountFieldType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn = _isLoggedIn.asStateFlow()
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
                getUserData(true)
            }
        }
    }

    fun onEditClicked(type: AccountFieldType) {
        viewModelScope.launch {
            _events.emit(NavigationEvent.NavigateToEdit(type))
        }
    }

    private fun getUserData(isLogout: Boolean = false) {
        viewModelScope.launch {
            try {
                _state.update { state -> state.copy(isLoading = true) }
                val user = userRepository.getLoggedInUser(isLogout)
                _state.update { state -> state.copy(userStateModel = user?.toModel()) }
            } catch (e: Exception) {
                _isLoggedIn.value = false
            } finally {
                _isLoggedIn.value = userRepository.isUserLoggedIn()
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
                val model = userRepository.getLoggedInUser()?.toModel()
                _state.update { state -> state.copy(userStateModel = model) }
            }
        }
    }

    fun getOrders(
        userId: Int
    ): List<String> {
        //getLocalsFromSharedPrefs
        //updateLocalsWithServer
        //date, location, cost, description (состав)

        return emptyList()
    }

    fun registrate(
        model: String
    ) {
        //repoToServer -> if success save data locally
    }

    fun updateName() {
        //repo Update Name
        //sharedPref Update Name
    }

    fun updateSurname() {
        //repo Update Surname
        //sharedPref Update Surname
    }

    fun forgetPassword() {
        //repoToServer try Update -> if success save pass encrypted locally
    }
}