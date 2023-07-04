package com.example.remak.repository

import com.example.remak.network.Api
import com.example.remak.network.RetrofitInstance
import com.example.remak.network.model.SignInData
import retrofit2.Call
import retrofit2.Response

class NetworkRepository {

    private val client = RetrofitInstance.getInstance().create(Api::class.java)

    suspend fun SingIn(email : String, password : String) : Response<SignInData.ResponseBody> {
        val requestBody = SignInData.RequestBody(email = email, password = password)
        return client.signIn(requestBody)
    }
}