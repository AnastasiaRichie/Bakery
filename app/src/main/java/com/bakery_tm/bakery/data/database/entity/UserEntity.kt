package com.bakery_tm.bakery.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,
    val name: String,
    val surname: String,
    val dateOfBirth: String,
    val email: String,
    val hashedPassword: String,
    val isLoggedIn: Boolean,
)