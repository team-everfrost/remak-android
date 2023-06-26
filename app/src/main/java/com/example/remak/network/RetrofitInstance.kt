package com.example.remak.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {
    private val BASE_URL = "sever url"

    private val client = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getInstance() : Retrofit {
        return client
    }
}