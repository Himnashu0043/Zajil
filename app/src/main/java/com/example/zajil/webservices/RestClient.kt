package com.example.zajil.webservices

import com.example.zajil.util.App
import com.example.zajil.util.Constants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RestClient {

    //private const val BASE_URL = "http://18.223.46.241/api/v1/"
//    private const val BASE_URL = "https://jobyoda.com/api/v1/"
//    private const val BASE_URL_SOCKRT = "https://3.23.145.70:8080"

    fun getInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getHttpClient())
            .build()
    }

    private fun getHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor() // logs HTTP request and response data.

        logging.level = HttpLoggingInterceptor.Level.BODY // set your desired log level

        val httpClient = OkHttpClient.Builder()
        httpClient.connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
        httpClient.addInterceptor(logging)

        httpClient.addInterceptor { chain ->
            val request: Request =
                chain.request().newBuilder().addHeader("token", App.token).build()
            chain.proceed(request)
        }
        return httpClient.build()
    }

    fun getApi() = getInstance().create(Api::class.java)

}