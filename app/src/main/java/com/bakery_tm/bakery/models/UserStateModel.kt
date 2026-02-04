package com.bakery_tm.bakery.models

import com.bakery_tm.bakery.data.api.RegisterRequest

data class UserStateModel(
    val userId: Int = -1,
    val name: String = "",
    val lastName: String? = null,
    val email: String = "",
    val password: String = "",
)

fun UserStateModel.toApi(): RegisterRequest {
    return RegisterRequest(
        name = name,
        lastName = lastName.orEmpty(),
        email = email,
        password = password
    )
}