package com.example.remak.network

import com.example.remak.App
import com.example.remak.dataStore.TokenRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



class AuthInterceptor (private val tokenRepository: TokenRepository) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        runBlocking {
            val tokenData = tokenRepository.fetchTokenData()

            if (tokenData != null) {
                request = request.newBuilder()
                    .addHeader("Authorization", "Bearer ${tokenData.accessToken}")
                    .build()
            }
        }
        return chain.proceed(request)
    }

}
object RetrofitInstance {
    private val BASE_URL = "https://api-dev.remak.io/"
    private val dataStore = App.context().provideDataStore()
    private val tokenRepository = TokenRepository(dataStore)
    private val authInterceptor = AuthInterceptor(tokenRepository)

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    private val retrofit = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    fun getInstance() : Retrofit {
        return retrofit
    }

}