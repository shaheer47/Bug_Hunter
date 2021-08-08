package com.bytesbridge.app.bughunter.activities.di.modules

import android.content.Context
import com.bytesbridge.app.bughunter.activities.`interface`.IBackendApiSearch
import com.bytesbridge.app.bughunter.activities.di.qualifiers.BackendApiWithTokenAuth
import com.bytesbridge.app.bughunter.activities.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RetrofitModuleStackOverflow {
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.apply { logging.level = HttpLoggingInterceptor.Level.BODY }

        return logging
    }

    @Provides
    @Singleton
    fun provideHttpClient(logging: HttpLoggingInterceptor): OkHttpClient {
        val httpClientBuilder = OkHttpClient.Builder()
        httpClientBuilder.addInterceptor(logging)
        httpClientBuilder.readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)

        return httpClientBuilder.build()
    }

    @Provides
    @Singleton
    @BackendApiWithTokenAuth
    fun provideHttpClientWithTokenAuth(
        @ApplicationContext appContext: Context,
        logging: HttpLoggingInterceptor
    ): OkHttpClient {
        val httpClientBuilder = OkHttpClient.Builder()
        httpClientBuilder.addInterceptor(Interceptor { chain: Interceptor.Chain ->
            val original = chain.request()

            // Request customization: add request headers
            val requestBuilder = original.newBuilder()
                .addHeader("content-type", "application/json")
                .addHeader(
                    "Authorization",
                    "Bearer ${Constants.STACK_OVERFLOW_API}"
                )
            val request = requestBuilder.build()
            chain.proceed(request)
        })
        httpClientBuilder.addInterceptor(logging)
        httpClientBuilder.readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)

        return httpClientBuilder.build()
    }



    @Provides
    @Singleton
    fun provideGoogleApiInterfaceWithTokenAuth(
        @BackendApiWithTokenAuth
        httpClient: OkHttpClient
    ): IBackendApiSearch {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.GOOGLE_SEARCH_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
        return retrofit.create(IBackendApiSearch::class.java)
    }
}