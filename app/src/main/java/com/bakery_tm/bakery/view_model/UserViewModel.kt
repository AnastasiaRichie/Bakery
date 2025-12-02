package com.bakery_tm.bakery.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakery_tm.bakery.domain.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val repository: UserRepository,
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    init {
        getUserData()
    }

    private fun getUserData() {
        viewModelScope.launch {
            try {
                _isLoggedIn.value = repository.isUserLoggedIn()
            } catch (e: Exception) {
                _isLoggedIn.value = false
            }
        }
    }
}