package com.bakery_tm.bakery.models

sealed interface LoginEvent {
    data object NavigateToFood : LoginEvent
    data object NavigateToRegister : LoginEvent
    data class ShowError(val message: String) : LoginEvent
}