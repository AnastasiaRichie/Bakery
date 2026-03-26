package com.bakery_tm.bakery.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bakery_tm.bakery.data.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    fun getUser(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("UPDATE users SET name = :name")
    suspend fun updateUserName(name: String)

    @Query("UPDATE users SET surname = :surname")
    suspend fun updateUserSurname(surname: String)

    @Query("UPDATE users SET email = :email")
    suspend fun updateUserEmail(email: String)

    @Query("DELETE FROM users")
    suspend fun deleteUser()
}