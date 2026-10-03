package com.example.spire_labs.data.remote.api

import com.example.spire_labs.data.remote.dto.ProductDto
import com.example.spire_labs.data.remote.dto.ProductsResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DummyJsonApi {

    @GET("products")
    suspend fun getProducts(
        @Query("limit") limit: Int = 30,
        @Query("skip") skip: Int = 0
    ): ProductsResponseDto

    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String
    ): ProductsResponseDto

    @GET("products/{id}")
    suspend fun getProductById(
        @Path("id") id: Int
    ): ProductDto
}
