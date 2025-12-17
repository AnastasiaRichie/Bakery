package com.bakery_tm.bakery.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.bakery_tm.bakery.data.database.entity.OrderEntity
import com.bakery_tm.bakery.data.database.entity.OrderItemEntity
import com.bakery_tm.bakery.data.database.relations.OrderWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    //Оформить заказ
    @Insert suspend fun insertOrder(order: OrderEntity): Long

    //Добавляет позицию товара в заказ
    @Insert suspend fun insertOrderItem(item: OrderItemEntity): Long

    //все заказы конкретного пользователя
    @Transaction
    @Query("SELECT * FROM orders WHERE userOwnerId = :userId")
    fun getOrdersForUser(userId: Int): Flow<List<OrderWithItems>>

    //Возвращает детальную информацию
    @Transaction
    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    suspend fun getOrderDetails(orderId: Long): OrderWithItems
}