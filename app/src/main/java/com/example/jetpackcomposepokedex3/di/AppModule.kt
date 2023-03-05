package com.example.jetpackcomposepokedex3.di

import com.example.jetpackcomposepokedex3.data.remote.PokemonServices
import com.example.jetpackcomposepokedex3.repository.PokemonRepository
import com.example.jetpackcomposepokedex3.util.Constants.BASE_URL
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

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val TIMEOUT = 30L

    @Provides
    @Singleton
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor {
        // to log the request and response with tag (okhttp.OkHttpClient)
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return httpLoggingInterceptor
    }

    @Provides
    @Singleton
    fun providesOkHttp(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder().apply {
            addInterceptor(httpLoggingInterceptor)
            connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            readTimeout(TIMEOUT, TimeUnit.SECONDS)
        }.build()

    @Singleton
    @Provides
    fun providePokemonServices(okHttpClient: OkHttpClient): PokemonServices =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL)
            . client(okHttpClient)
            .build()
            .create(PokemonServices::class.java)

    @Singleton
    @Provides
    fun providePokemonRepo(
        services: PokemonServices
    ) = PokemonRepository(services)

}