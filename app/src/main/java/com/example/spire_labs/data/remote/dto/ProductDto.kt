package com.example.spire_labs.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ProductDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("rating") val rating: Double,
    @SerializedName("category") val category: String,
    @SerializedName("brand") val brand: String? = null,
    @SerializedName("stock") val stock: Int,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("images") val images: List<String> = emptyList()
)
