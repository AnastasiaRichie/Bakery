package com.bakery_tm.bakery.models

data class UserStateModel(
    val name: String = "",
    val surname: String? = null,
    val email: String = "",
    val password: String = "",
)