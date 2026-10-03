package com.example.spire_labs.data.repository

import com.example.spire_labs.core.di.IoDispatcher
import com.example.spire_labs.data.local.dao.CartDao
import com.example.spire_labs.data.mapper.toCartEntity
import com.example.spire_labs.data.mapper.toDomain
import com.example.spire_labs.domain.model.CartItem
import com.example.spire_labs.domain.model.Product
import com.example.spire_labs.domain.repository.CartRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CartRepository {

    override fun getCartItems(): Flow<List<CartItem>> {
        return cartDao.getAllCartItems()
            .map { it.toDomain() }
            .flowOn(ioDispatcher)
    }

    override suspend fun addToCart(product: Product, quantity: Int) = withContext(ioDispatcher) {
        val existingItem = cartDao.getCartItemByProductId(product.id)
        if (existingItem != null) {
            val newQuantity = (existingItem.quantity + quantity).coerceAtMost(product.stock)
            cartDao.updateQuantity(existingItem.id, newQuantity)
        } else {
            cartDao.insertOrUpdate(product.toCartEntity(quantity))
        }
    }

    override suspend fun updateQuantity(cartItemId: Int, newQuantity: Int) = withContext(ioDispatcher) {
        if (newQuantity <= 0) {
            cartDao.deleteById(cartItemId)
        } else {
            cartDao.updateQuantity(cartItemId, newQuantity)
        }
    }

    override suspend fun removeFromCart(cartItemId: Int) = withContext(ioDispatcher) {
        cartDao.deleteById(cartItemId)
    }

    override suspend fun clearCart() = withContext(ioDispatcher) {
        cartDao.clearCart()
    }
}
