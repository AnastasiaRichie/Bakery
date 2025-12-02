package com.bakery_tm.bakery.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bakery_tm.bakery.data.database.entity.OrderEntity
import com.bakery_tm.bakery.data.database.entity.OrderItemEntity
import com.bakery_tm.bakery.data.database.entity.ProductEntity
import com.bakery_tm.bakery.data.database.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        ProductEntity::class
    ], version = 1, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BakeryDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun orderDao(): OrderDao
    abstract fun productDao(): ProductDao
}