package com.example.remak.repository

import com.example.remak.network.Api
import com.example.remak.network.RetrofitInstance
import com.example.remak.network.model.AuthEmailErrorResponseBody
import com.example.remak.network.model.AuthEmailRequestBody
import com.example.remak.network.model.SignInData
import retrofit2.Response

class NetworkRepository {

    private val client = RetrofitInstance.getInstance().create(Api::class.java)

    suspend fun signIn(email : String, password : String) : Response<SignInData.ResponseBody> {
        val requestBody = SignInData.RequestBody(email = email, password = password)
        return client.signIn(requestBody)
    }

    suspend fun getVerifyCode(email: String): Response<AuthEmailErrorResponseBody> {
        val requestBody = AuthEmailRequestBody(email = email)
        return client.getVerifyCode(requestBody)
    }
}