package com.example.spire_labs.domain.repository

import com.example.spire_labs.domain.model.CartItem
import com.example.spire_labs.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartItems(): Flow<List<CartItem>>
    suspend fun addToCart(product: Product, quantity: Int = 1)
    suspend fun updateQuantity(cartItemId: Int, newQuantity: Int)
    suspend fun removeFromCart(cartItemId: Int)
    suspend fun clearCart()
}
