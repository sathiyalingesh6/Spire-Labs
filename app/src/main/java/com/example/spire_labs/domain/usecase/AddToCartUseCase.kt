package com.example.spire_labs.domain.usecase

import com.example.spire_labs.domain.model.Product
import com.example.spire_labs.domain.repository.CartRepository
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(product: Product, quantity: Int = 1) {
        cartRepository.addToCart(product, quantity)
    }
}
