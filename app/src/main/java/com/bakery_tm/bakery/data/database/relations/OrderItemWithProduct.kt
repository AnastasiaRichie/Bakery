package com.bakery_tm.bakery.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.bakery_tm.bakery.data.database.entity.OrderItemEntity
import com.bakery_tm.bakery.data.database.entity.ProductEntity

data class OrderItemWithProduct(
    @Embedded val orderItem: OrderItemEntity,
    @Relation(
        parentColumn = "productId",
        entityColumn = "productId"
    )
    val product: ProductEntity
)