package com.bakery_tm.bakery.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bakery_tm.bakery.data.database.entity.UserEntity

@Dao
interface UserDao {

    // Добавить пользователя
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    // Получить пользователя по почте
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    // Установить пользователя как залогиненого по почте
    @Query("UPDATE users SET isLoggedIn = :isLoggedIn WHERE email = :email")
    suspend fun updateIsLoggedIn(isLoggedIn: Boolean, email: String)

    // Получить залогиненого пользователя
    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    suspend fun getLoggedInUser(): UserEntity?

    @Query("UPDATE users SET name = :name WHERE userId = :userId")
    suspend fun updateUserName(name: String, userId: Int)

    @Query("UPDATE users SET surname = :surname WHERE userId = :userId")
    suspend fun updateUserSurname(surname: String, userId: Int)

    @Query("UPDATE users SET email = :email WHERE userId = :userId")
    suspend fun updateUserEmail(email: String, userId: Int)

    @Query("UPDATE users SET hashedPassword = :password WHERE userId = :userId")
    suspend fun updateUserPassword(password: String, userId: Int)
}