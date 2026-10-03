package com.example.spire_labs.domain.usecase

import com.example.spire_labs.domain.repository.CartRepository
import javax.inject.Inject

class RemoveFromCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(cartItemId: Int) {
        cartRepository.removeFromCart(cartItemId)
    }
}
