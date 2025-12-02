package com.bakery_tm.bakery.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.bakery_tm.bakery.data.database.entity.OrderEntity
import com.bakery_tm.bakery.data.database.entity.OrderItemEntity
import com.bakery_tm.bakery.data.database.relations.OrderWithItems
import com.bakery_tm.bakery.data.database.relations.UserWithOrders

@Dao
interface OrderDao {

    // Получение одного пользователя с его заказами и товарами
    @Transaction
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserWithOrders(userId: Long): UserWithOrders

    // Создание нового заказа
    @Insert
    suspend fun insertOrder(order: OrderEntity): Long
    @Insert
    suspend fun insertNewOrder(order: OrderItemEntity): Long

    // Получение одного заказа с товарами
    @Transaction
    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    suspend fun getOrderWithItems(orderId: Long): OrderWithItems?

    // Получение всех заказов пользователя (без деталей товаров)
    @Query("SELECT * FROM orders WHERE userOwnerId = :userId")
    suspend fun getOrdersByUser(userId: Long): List<OrderEntity>

    // Получение всех заказов с их позициями
    @Transaction
    @Query("SELECT * FROM orders")
    suspend fun getAllOrdersWithItems(): List<OrderWithItems>
}