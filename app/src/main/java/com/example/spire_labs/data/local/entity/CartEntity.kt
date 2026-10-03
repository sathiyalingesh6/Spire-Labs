package com.example.spire_labs.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: Int,
    val title: String,
    val price: Double,
    val thumbnail: String,
    val quantity: Int,
    val stock: Int
)
