package com.bakery_tm.bakery.view_model

import androidx.lifecycle.ViewModel
import com.bakery_tm.bakery.models.UserStateModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel: ViewModel() {

    private val _state = MutableStateFlow<UserStateModel>(UserStateModel())
    val state: StateFlow<UserStateModel> = _state

    private val _isRegistered = MutableStateFlow<Boolean>(false)
    val isRegistered: StateFlow<Boolean> = _isRegistered
}