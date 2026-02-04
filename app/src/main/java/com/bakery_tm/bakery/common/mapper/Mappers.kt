package com.bakery_tm.bakery.common.mapper

import com.bakery_tm.bakery.data.database.entity.ProductEntity
import com.bakery_tm.bakery.domain.ProductDomainModel
import com.bakery_tm.bakery.models.ProductModel
import com.bakery_tm.bakery.models.ProductType

fun ProductEntity.toDomain(): ProductDomainModel{
    return ProductDomainModel(
        productId = productId,
        name = name,
        weight = weight,
        description = description,
        fullDescription = fullDescription,
        allergens = allergens,
        price = price,
        productType = productType.name.uppercase(),
        productImageName = productImageName
    )
}

fun ProductDomainModel.toEntity(): ProductEntity{
    return ProductEntity(
        productId = productId,
        name = name,
        weight = weight,
        description = description,
        fullDescription = fullDescription,
        allergens = allergens,
        price = price,
        productType = ProductType.valueOf(productType),
        productImageName = productImageName
    )
}

fun ProductDomainModel.toModel(): ProductModel {
    return ProductModel(
        productId = productId,
        name = name,
        weight = weight,
        description = description,
        fullDescription = fullDescription,
        allergens = allergens,
        price = price,
        productType = ProductType.valueOf(productType),
        productImageName = productImageName
    )
}