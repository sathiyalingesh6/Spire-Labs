package com.example.spire_labs.data.mapper

import com.example.spire_labs.data.remote.dto.ProductDto
import com.example.spire_labs.domain.model.Product

fun ProductDto.toDomain(): Product {
    return Product(
        id = id,
        title = title,
        description = description,
        price = price,
        rating = rating,
        category = category,
        brand = brand,
        stock = stock,
        thumbnail = thumbnail,
        images = images
    )
}

fun List<ProductDto>.toDomain(): List<Product> = map { it.toDomain() }
