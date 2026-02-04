package com.bakery_tm.bakery.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bakery_tm.bakery.data.api.WebSocketManager
import com.bakery_tm.bakery.data.database.entity.UserEntity
import com.bakery_tm.bakery.domain.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MainViewModel(
    userRepository: UserRepository,
    private val webSocketManager: WebSocketManager,
): ViewModel() {

    val user: StateFlow<UserEntity?> =
        userRepository.getUser().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun connect(userId: Int) {
        webSocketManager.connect(userId)
    }

    fun disconnect() {
        webSocketManager.disconnect()
    }
}