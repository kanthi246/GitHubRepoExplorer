package com.kanthi.githubrepoexplorer.core.di

import com.kanthi.githubrepoexplorer.BuildConfig
import com.kanthi.githubrepoexplorer.data.remote.api.GithubApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Builds the networking stack used to talk to the GitHub REST API: OkHttp (the low-level HTTP
 * client) -> Retrofit (turns HTTP calls into a type-safe Kotlin interface, see GithubApiService)
 * -> the actual API service used by the repository layer.
 *
 * Everything here is a @Singleton so the whole app shares one connection pool instead of each
 * feature creating its own client.
 *
 * Benefit: all networking configuration (timeouts, logging, base URL) lives in exactly one place,
 * and any class that needs to make API calls just asks Hilt for a GithubApiService instead of
 * knowing how to assemble one.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(GithubApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideGithubApiService(retrofit: Retrofit): GithubApiService =
        retrofit.create(GithubApiService::class.java)
}
