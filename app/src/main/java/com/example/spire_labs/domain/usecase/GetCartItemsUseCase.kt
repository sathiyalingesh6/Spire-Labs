package com.example.spire_labs.domain.usecase

import com.example.spire_labs.domain.model.CartItem
import com.example.spire_labs.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCartItemsUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    operator fun invoke(): Flow<List<CartItem>> {
        return cartRepository.getCartItems()
    }
}
