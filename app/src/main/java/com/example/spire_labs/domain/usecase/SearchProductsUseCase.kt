package com.example.spire_labs.domain.usecase

import com.example.spire_labs.core.network.Resource
import com.example.spire_labs.domain.model.Product
import com.example.spire_labs.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke(query: String): Flow<Resource<List<Product>>> {
        return productRepository.searchProducts(query)
    }
}
