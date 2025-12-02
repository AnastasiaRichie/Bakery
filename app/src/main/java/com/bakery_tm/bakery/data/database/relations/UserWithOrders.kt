package com.bakery_tm.bakery.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.bakery_tm.bakery.data.database.entity.OrderEntity
import com.bakery_tm.bakery.data.database.entity.UserEntity

data class UserWithOrders(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "userId",
        entityColumn = "userOwnerId",
        entity = OrderEntity::class
    )
    val orders: List<OrderWithItems>
)