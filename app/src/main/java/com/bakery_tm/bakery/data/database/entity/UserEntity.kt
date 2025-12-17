package com.bakery_tm.bakery.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bakery_tm.bakery.models.UserStateModel

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,
    val name: String,
    val surname: String,
    val email: String,
    val hashedPassword: String,
    val isLoggedIn: Boolean,
)

fun UserStateModel.toEntity(isLoggedIn: Boolean): UserEntity {
    return UserEntity(
        name = name,
        surname = surname.orEmpty(),
        email = email,
        hashedPassword = password.hashCode().toString(),
        isLoggedIn = isLoggedIn
    )
}

fun UserEntity.toModel(): UserStateModel{
    return UserStateModel(
        userId = userId,
        name = name,
        surname = surname,
        email = email,
        password = "****",
    )
}
