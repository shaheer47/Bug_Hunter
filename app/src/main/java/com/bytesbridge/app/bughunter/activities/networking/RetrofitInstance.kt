package com.bytesbridge.app.bughunter.activities.networking

import com.bytesbridge.app.bughunter.activities.`interface`.IBackendApiSearch
import com.bytesbridge.app.bughunter.activities.`interface`.IBackendApiStackOverflow
import com.bytesbridge.app.bughunter.activities.utils.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitInstance {
    companion object {

        //client for stackoverflow
      private val stackOverflowRetrofit by lazy {
          val logging = HttpLoggingInterceptor()
          logging.setLevel(HttpLoggingInterceptor.Level.BODY)
          val client = OkHttpClient.Builder()
              .addInterceptor(logging)

          client.addInterceptor(logging)
          client.readTimeout(30, TimeUnit.SECONDS)
              .connectTimeout(10, TimeUnit.SECONDS)

          Retrofit.Builder()
              .baseUrl(Constants.STACK_OVERFLOW_BASE_URL)
              .addConverterFactory(GsonConverterFactory.create())
              .client(client.build())
              .build()
      }

        //client for search
        private val searchClientRetrofit by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)

            client.addInterceptor(Interceptor { chain: Interceptor.Chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .addHeader("content-type", "application/json")
                    .addHeader(
                        "x-rapidapi-host",
                        "google-search3.p.rapidapi.com"
                    )
                    .addHeader(
                        "X-RapidAPI-Key",
                        "26ff3ebae9msh6b3c34e5e764764p15fb17jsncf0df0f2f8cd"
                    )
                val request = requestBuilder.build()
                chain.proceed(request)
            })
            client.addInterceptor(logging)
            client.readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)

            Retrofit.Builder()
                .baseUrl(Constants.GOOGLE_SEARCH_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build()
        }

        val searchApi by lazy {
            searchClientRetrofit.create(IBackendApiSearch::class.java)
        }
        val stackOverflowApi by lazy {
            stackOverflowRetrofit.create(IBackendApiStackOverflow::class.java)
        }
    }
}