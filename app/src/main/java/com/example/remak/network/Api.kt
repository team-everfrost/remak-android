package com.example.remak.network


import com.example.remak.network.model.CreateData
import com.example.remak.network.model.DeleteData
import com.example.remak.network.model.DetailData
import com.example.remak.network.model.DownloadData
import com.example.remak.network.model.MainListData
import com.example.remak.network.model.SignInData
import com.example.remak.network.model.SignUpData
import com.example.remak.network.model.UpdateData
import com.example.remak.network.model.UploadFileData
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query


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

    @GET("document")
    suspend fun getMainList(
        @Query("cursor")  cursor : String?,
        @Query("doc-id")  docID : String?,
        @Query("limit") limit : Int? = 20
    ) : retrofit2.Response<MainListData.Response>

    @GET("document/{docId}")
    suspend fun getDetailData(@Path("docId") docId : String) : retrofit2.Response<DetailData.ResponseBody>

    @PATCH("document/memo/update/{docId}")
    suspend fun updateMemo(@Path("docId") docId : String, @Body body: UpdateData.MemoRequestBody) : retrofit2.Response<UpdateData.MemoResponseBody>

    @DELETE("document/{docId}")
    suspend fun deleteMemo(@Path("docId") docId : String) : retrofit2.Response<DeleteData.ResponseBody>

    @Multipart
    @POST("document/file/upload")
    suspend fun uploadFile(@Part files : List<MultipartBody.Part>) : retrofit2.Response<UploadFileData.ResponseBody>

    @GET("document/file/download/{docId}")
    suspend fun downloadFile(@Path("docId") docId : String) : retrofit2.Response<DownloadData.ResponseBody>

    @POST("document/webpage/create")
    suspend fun createWebPage(@Body body: CreateData.WebPageRequestBody): retrofit2.Response<CreateData.WebPageResponseBody>
}