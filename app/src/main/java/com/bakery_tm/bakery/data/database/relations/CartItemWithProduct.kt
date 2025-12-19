package com.bakery_tm.bakery.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.bakery_tm.bakery.data.database.entity.CartItemEntity
import com.bakery_tm.bakery.data.database.entity.ProductEntity

data class CartItemWithProduct(
    @Embedded val item: CartItemEntity,
    @Relation(
        parentColumn = "productId",
        entityColumn = "productId"
    )
    val product: ProductEntity
)