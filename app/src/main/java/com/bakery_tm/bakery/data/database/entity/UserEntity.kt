package com.bakery_tm.bakery.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bakery_tm.bakery.data.api.UserResponse
import com.bakery_tm.bakery.models.UserStateModel
import com.bakery_tm.bakery.models.UserType

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: Int,
    val userType: UserType,
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
        userType = userType,
    )
}

fun UserEntity.toModel(): UserStateModel{
    return UserStateModel(
        userId = userId,
        userType = userType,
        name = name,
        lastName = surname,
        email = email,
        password = "****",
    )
}

fun UserResponse.toEntity(): UserEntity {
    return UserEntity(
        userId = userId,
        userType = userType,
        name = name,
        surname = lastName,
        email = email,
    )
}