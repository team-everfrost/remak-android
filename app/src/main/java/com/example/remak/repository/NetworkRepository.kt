package com.example.remak.repository

import androidx.room.Update
import com.example.remak.network.Api
import com.example.remak.network.RetrofitInstance
import com.example.remak.network.model.CreateData
import com.example.remak.network.model.DetailData
import com.example.remak.network.model.MainListData

import com.example.remak.network.model.SignInData
import com.example.remak.network.model.SignUpData
import com.example.remak.network.model.UpdateData
import retrofit2.Response

class NetworkRepository {

    private val client = RetrofitInstance.getInstance().create(Api::class.java)

    //로그인
    suspend fun signIn(email : String, password : String) : Response<SignInData.ResponseBody> {
        val requestBody = SignInData.RequestBody(email = email, password = password)
        return client.signIn(requestBody)
    }

    suspend fun getVerifyCode(email: String): Response<SignUpData.GetVerifyResponseBody> {
        val requestBody = SignUpData.GetVerifyRequestBody(email = email)
        return client.getVerifyCode(requestBody)
    }

    suspend fun checkVerifyCode(signupCode: String, email: String): Response<SignUpData.CheckVerifyResponseBody> {
        val requestBody = SignUpData.CheckVerifyRequestBody(signupCode = signupCode, email = email)
        return client.checkVerifyCode(requestBody)
    }

    suspend fun signUp(email: String, password: String): Response<SignUpData.SignUpResponseBody> {
        val requestBody = SignUpData.SignUpRequestBody(email = email, password = password)
        return client.signUp(requestBody)
    }

    suspend fun createMemo(content : String) : Response<CreateData.MemoResponseBody> {
        val requestBody = CreateData.MemoRequestBody(content = content)
        return client.createMemo(requestBody)
    }

    suspend fun getMainList(cursor : String?, docID : String?) : Response<MainListData.Response> {
        return client.getMainList(cursor, docID)
    }

    suspend fun getDetailData(docId : String) : Response<DetailData.ResponseBody> {
        return client.getDetailData(docId)
    }

    suspend fun updateMemo(docId : String, content : String) : Response<UpdateData.MemoResponseBody> {
        val requestBody = UpdateData.MemoRequestBody(content = content)
        return client.updateMemo(docId, requestBody)
    }


}