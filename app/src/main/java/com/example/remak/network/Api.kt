package com.example.remak.network

import com.example.remak.network.model.AuthEmailErrorResponseBody
import com.example.remak.network.model.AuthEmailRequestBody
import com.example.remak.network.model.SignInData
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface Api {
    @POST("auth/local/login")
    suspend fun signIn(@Body body: SignInData.RequestBody): retrofit2.Response<SignInData.ResponseBody>

    @POST("auth/signup-code")
    suspend fun getVerifyCode(@Body body: AuthEmailRequestBody): retrofit2.Response<AuthEmailErrorResponseBody>


}