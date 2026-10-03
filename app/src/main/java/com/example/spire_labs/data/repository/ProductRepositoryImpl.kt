package com.example.spire_labs.data.repository

import com.example.spire_labs.core.di.IoDispatcher
import com.example.spire_labs.core.network.Resource
import com.example.spire_labs.data.mapper.toDomain
import com.example.spire_labs.data.remote.api.DummyJsonApi
import com.example.spire_labs.domain.model.Product
import com.example.spire_labs.domain.repository.ProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val api: DummyJsonApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ProductRepository {

    override fun getProducts(limit: Int, skip: Int): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading)
        try {
            val response = api.getProducts(limit, skip)
            val products = response.products.toDomain()
            emit(Resource.Success(products))
        } catch (e: IOException) {
            emit(Resource.Error(message = "No internet connection or network error", cause = e))
        } catch (e: HttpException) {
            emit(Resource.Error(message = "Server error: ${e.code()}", cause = e))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.localizedMessage ?: "An unexpected error occurred", cause = e))
        }
    }.flowOn(ioDispatcher)

    override fun searchProducts(query: String): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading)
        try {
            val response = api.searchProducts(query)
            val products = response.products.toDomain()
            emit(Resource.Success(products))
        } catch (e: IOException) {
            emit(Resource.Error(message = "No internet connection or network error", cause = e))
        } catch (e: HttpException) {
            emit(Resource.Error(message = "Server error: ${e.code()}", cause = e))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.localizedMessage ?: "An unexpected error occurred", cause = e))
        }
    }.flowOn(ioDispatcher)

    override fun getProductById(productId: Int): Flow<Resource<Product>> = flow {
        emit(Resource.Loading)
        try {
            val response = api.getProductById(productId)
            emit(Resource.Success(response.toDomain()))
        } catch (e: IOException) {
            emit(Resource.Error(message = "No internet connection or network error", cause = e))
        } catch (e: HttpException) {
            emit(Resource.Error(message = "Server error: ${e.code()}", cause = e))
        } catch (e: Exception) {
            emit(Resource.Error(message = e.localizedMessage ?: "An unexpected error occurred", cause = e))
        }
    }.flowOn(ioDispatcher)
}
