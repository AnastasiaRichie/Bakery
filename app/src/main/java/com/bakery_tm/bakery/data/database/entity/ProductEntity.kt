package com.bakery_tm.bakery.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bakery_tm.bakery.models.ProductType

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val productId: Long = 0,
    val name: String,
    val price: String,
    val weight: String,
    val description: String,
    val fullDescription: String,
    val allergens: List<String>,
    val productType: ProductType,
    val productImageName: String,
)