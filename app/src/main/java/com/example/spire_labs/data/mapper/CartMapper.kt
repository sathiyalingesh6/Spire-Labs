package com.example.spire_labs.data.mapper

import com.example.spire_labs.data.local.entity.CartEntity
import com.example.spire_labs.domain.model.CartItem
import com.example.spire_labs.domain.model.Product

fun CartEntity.toDomain(): CartItem {
    return CartItem(
        id = id,
        productId = productId,
        title = title,
        price = price,
        thumbnail = thumbnail,
        quantity = quantity,
        stock = stock
    )
}

fun List<CartEntity>.toDomain(): List<CartItem> = map { it.toDomain() }

fun Product.toCartEntity(quantity: Int = 1): CartEntity {
    return CartEntity(
        productId = id,
        title = title,
        price = price,
        thumbnail = thumbnail,
        quantity = quantity,
        stock = stock
    )
}
