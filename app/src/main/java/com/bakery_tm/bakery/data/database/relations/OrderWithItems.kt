package com.bakery_tm.bakery.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.bakery_tm.bakery.data.database.entity.OrderEntity
import com.bakery_tm.bakery.data.database.entity.OrderItemEntity

data class OrderWithItems(
    @Embedded val order: OrderEntity,
    @Relation(
        parentColumn = "orderId",
        entityColumn = "orderId",
        entity = OrderItemEntity::class
    )
    val items: List<OrderItemWithProduct>
)