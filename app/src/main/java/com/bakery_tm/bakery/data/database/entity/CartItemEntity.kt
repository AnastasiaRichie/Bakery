package com.bakery_tm.bakery.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bakery_tm.bakery.domain.OrderRequestItem

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val cartItemId: Long = 0,
    val userId: Int,
    val productId: Long,
    val quantity: Int
)

fun CartItemEntity.toModel(): OrderRequestItem = OrderRequestItem(quantity = quantity, productId = productId)