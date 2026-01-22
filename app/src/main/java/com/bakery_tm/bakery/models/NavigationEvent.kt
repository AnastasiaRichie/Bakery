package com.bakery_tm.bakery.models


sealed interface NavigationEvent {
    data object NavigateBack : NavigationEvent
    data object NavigateToFood : NavigationEvent
    data object NavigateToRegister : NavigationEvent
    data class ShowError(val message: String) : NavigationEvent
}