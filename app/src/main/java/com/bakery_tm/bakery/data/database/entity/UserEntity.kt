package com.bakery_tm.bakery.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bakery_tm.bakery.data.api.UserResponse
import com.bakery_tm.bakery.models.UserStateModel

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: Int,
    val name: String,
    val surname: String,
    val email: String,
)

fun UserStateModel.toEntity(): UserEntity {
    return UserEntity(
        name = name,
        surname = lastName.orEmpty(),
        email = email,
        userId = userId,
    )
}

fun UserEntity.toModel(): UserStateModel{
    return UserStateModel(
        userId = userId,
        name = name,
        lastName = surname,
        email = email,
        password = "****",
    )
}

fun UserResponse.toEntity(): UserEntity {
    return UserEntity(
        userId = userId,
        name = name,
        surname = lastName,
        email = email,
    )
}