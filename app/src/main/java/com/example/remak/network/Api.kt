package com.example.remak.network


import androidx.room.Delete
import com.example.remak.network.model.CreateData
import com.example.remak.network.model.SignInData
import com.example.remak.network.model.SignUpData
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface Api {
    @POST("auth/local/login")
    suspend fun signIn(@Body body: SignInData.RequestBody): retrofit2.Response<SignInData.ResponseBody>

    @POST("auth/signup-code")
    suspend fun getVerifyCode(@Body body: SignUpData.GetVerifyRequestBody): retrofit2.Response<SignUpData.GetVerifyResponseBody>

    @POST("auth/verify-code")
    suspend fun checkVerifyCode(@Body body: SignUpData.CheckVerifyRequestBody): retrofit2.Response<SignUpData.CheckVerifyResponseBody>

    @POST("auth/local/signup")
    suspend fun signUp(@Body body: SignUpData.SignUpRequestBody): retrofit2.Response<SignUpData.SignUpResponseBody>

    @POST("document/memo/create")
    suspend fun createMemo(@Body body: CreateData.MemoRequestBody): retrofit2.Response<CreateData.MemoResponseBody>






}