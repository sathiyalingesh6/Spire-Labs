package com.example.spire_labs.domain.usecase

import com.example.spire_labs.domain.repository.CartRepository
import javax.inject.Inject

class UpdateCartQuantityUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(cartItemId: Int, newQuantity: Int) {
        if (newQuantity <= 0) {
            cartRepository.removeFromCart(cartItemId)
        } else {
            cartRepository.updateQuantity(cartItemId, newQuantity)
        }
    }
}
