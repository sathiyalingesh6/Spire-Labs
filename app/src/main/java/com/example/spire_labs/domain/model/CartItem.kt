package com.example.spire_labs.domain.model

data class CartItem(
    val id: Int,
    val productId: Int,
    val title: String,
    val price: Double,
    val thumbnail: String,
    val quantity: Int,
    val stock: Int
) {
    val totalPrice: Double
        get() = price * quantity
}
