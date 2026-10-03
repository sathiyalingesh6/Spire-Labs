package com.example.spire_labs.domain.model

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val rating: Double,
    val category: String,
    val brand: String?,
    val stock: Int,
    val thumbnail: String,
    val images: List<String> = emptyList()
)
