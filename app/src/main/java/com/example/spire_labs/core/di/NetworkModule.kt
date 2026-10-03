package com.example.spire_labs.core.di

import com.example.spire_labs.BuildConfig
import com.example.spire_labs.data.remote.api.DummyJsonApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DummyJsonRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    /**
     * Standard OkHttpClient for public APIs with logging.
     */
    @Provides
    @Singleton
    @DefaultOkHttp
    fun provideDefaultOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Authenticated OkHttpClient (ready for Bearer token / auth interceptors).
     */
    @Provides
    @Singleton
    @AuthOkHttp
    fun provideAuthOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            // Add custom Token / Auth interceptor here when needed
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Retrofit instance specifically configured for DummyJSON API.
     */
    @Provides
    @Singleton
    @DummyJsonRetrofit
    fun provideDummyJsonRetrofit(
        @DefaultOkHttp okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideDummyJsonApi(
        @DummyJsonRetrofit retrofit: Retrofit
    ): DummyJsonApi {
        return retrofit.create(DummyJsonApi::class.java)
    }
}
