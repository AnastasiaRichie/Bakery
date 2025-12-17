package com.bakery_tm.bakery.models

data class UserStateModel(
    val userId: Int = -1,
    val name: String = "",
    val surname: String? = null,
    val email: String = "",
    val password: String = "",
)