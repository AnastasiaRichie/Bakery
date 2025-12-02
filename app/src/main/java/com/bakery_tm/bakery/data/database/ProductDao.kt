package com.bakery_tm.bakery.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.bakery_tm.bakery.data.database.entity.ProductEntity

@Dao
interface ProductDao {

    // Добавить все мок-позиции пекарни
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProducts(users: List<ProductEntity>)

    // Получить доступные позиции пекарни
    @Transaction
    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<ProductEntity>
}