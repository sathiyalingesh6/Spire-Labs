package com.example.spire_labs.domain.repository

import com.example.spire_labs.core.network.Resource
import com.example.spire_labs.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(limit: Int = 30, skip: Int = 0): Flow<Resource<List<Product>>>
    fun searchProducts(query: String): Flow<Resource<List<Product>>>
    fun getProductById(productId: Int): Flow<Resource<Product>>
}
