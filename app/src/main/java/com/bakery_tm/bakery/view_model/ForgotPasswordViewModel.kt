package com.bakery_tm.bakery.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakery_tm.bakery.data.database.entity.toModel
import com.bakery_tm.bakery.domain.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class ForgotPasswordViewModel(
    private val userRepository: UserRepository
): ViewModel() {

    private val _state = MutableStateFlow("")
    val state: StateFlow<String> = _state

    private val _userNotExistsEvent = MutableSharedFlow<Boolean>()
    val userNotExistsEvent: SharedFlow<Boolean> = _userNotExistsEvent

    private val _sendNotification = MutableSharedFlow<String>()
    val sendNotification: SharedFlow<String> = _sendNotification

    fun onEmailChanged(email: String) {
        _state.value = email
    }

    fun isEmailExists() {
        viewModelScope.launch {
//            val user = userRepository.getUserByEmail(_state.value)
//            if (user != null) {
//                _userNotExistsEvent.emit(false)
//                val newPassword = generatePassword()
////                userRepository.updateProfileData(
////                    model = user.toModel().copy(password = newPassword),
////                    userId = user.userId
////                )
//                _sendNotification.emit(newPassword)
//            } else {
//                _userNotExistsEvent.emit(true)
//            }
        }
    }

    private fun generatePassword(length: Int = 6): String {
        val chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        return buildString {
            repeat(length) {
                append(chars[Random.nextInt(chars.length)])
            }
        }
    }
}