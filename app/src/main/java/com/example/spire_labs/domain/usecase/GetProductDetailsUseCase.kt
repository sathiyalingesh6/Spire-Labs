package com.example.spire_labs.domain.usecase

import com.example.spire_labs.core.network.Resource
import com.example.spire_labs.domain.model.Product
import com.example.spire_labs.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductDetailsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke(productId: Int): Flow<Resource<Product>> {
        return productRepository.getProductById(productId)
    }
}
