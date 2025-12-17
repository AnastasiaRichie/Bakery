package com.bakery_tm.bakery.models

import com.bakery_tm.bakery.screen.AccountFieldType

sealed interface NavigationEvent {
    data object NavigateBack : NavigationEvent
    data object NavigateToFood : NavigationEvent
    data object NavigateToRegister : NavigationEvent
    data class NavigateToEdit(val type: AccountFieldType) : NavigationEvent
    data class ShowError(val message: String) : NavigationEvent
}