package com.bakery_tm.bakery.domain

import com.bakery_tm.bakery.models.ProductType

data class ProductDomainModel(
    val productId: Long,
    val name: String,
    val weight: String,
    val description: String,
    val fullDescription: String,
    val allergens: List<String>,
    val price: String,
    val productImageName: String,
    val productType: String = ProductType.FLOUR.name,
)