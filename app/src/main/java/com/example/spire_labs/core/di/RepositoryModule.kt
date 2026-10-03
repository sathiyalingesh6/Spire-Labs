package com.example.spire_labs.core.di

import com.example.spire_labs.data.repository.CartRepositoryImpl
import com.example.spire_labs.data.repository.ProductRepositoryImpl
import com.example.spire_labs.domain.repository.CartRepository
import com.example.spire_labs.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        cartRepositoryImpl: CartRepositoryImpl
    ): CartRepository
}
