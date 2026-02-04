package com.bakery_tm.bakery.models

data class UserState(
    val userStateModel: UserStateModel? = null,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
)