package com.bakery_tm.bakery.models

data class ProductModel(
    val productId: Long,
    val name: String,
    val weight: String,
    val description: String,
    val fullDescription: String,
    val allergens: List<String>,
    val price: String,
    val productImageName: String,
    val productType: ProductType,
)