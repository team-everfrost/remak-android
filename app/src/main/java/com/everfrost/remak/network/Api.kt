package com.everfrost.remak.network

import com.everfrost.remak.network.model.AddDataInCollectionData
import com.everfrost.remak.network.model.CollectionListData
import com.everfrost.remak.network.model.CreateCollectionData
import com.everfrost.remak.network.model.CreateData
import com.everfrost.remak.network.model.DeleteData
import com.everfrost.remak.network.model.DownloadData
import com.everfrost.remak.network.model.MainListData
import com.everfrost.remak.network.model.SignInData
import com.everfrost.remak.network.model.SignUpData
import com.everfrost.remak.network.model.TagListData
import com.everfrost.remak.network.model.UpdateData
import com.everfrost.remak.network.model.UploadFileData
import com.everfrost.remak.network.model.UserData
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
    //로그인
    @POST("auth/local/login")
    suspend fun signIn(@Body body: SignInData.RequestBody): retrofit2.Response<SignInData.ResponseBody>

    //회원가입
    @POST("auth/signup-code")
    suspend fun getVerifyCode(@Body body: SignUpData.GetVerifyRequestBody): retrofit2.Response<SignUpData.GetVerifyResponseBody>

    //인증번호 확인
    @POST("auth/verify-code")
    suspend fun checkVerifyCode(@Body body: SignUpData.CheckVerifyRequestBody): retrofit2.Response<SignUpData.CheckVerifyResponseBody>

    //회원가입
    @POST("auth/local/signup")
    suspend fun signUp(@Body body: SignUpData.SignUpRequestBody): retrofit2.Response<SignUpData.SignUpResponseBody>

    //메모 생성
    @POST("document/memo")
    suspend fun createMemo(@Body body: CreateData.MemoRequestBody): retrofit2.Response<CreateData.MemoResponseBody>

    //메인 리스트
    @GET("document")
    suspend fun getMainList(
        @Query("cursor") cursor: String?,
        @Query("doc-id") docID: String?,
        @Query("limit") limit: Int? = 20
    ): retrofit2.Response<MainListData.Response>

    //자료 상세
    @GET("document/{docId}")
    suspend fun getDetailData(@Path("docId") docId: String): retrofit2.Response<MainListData.DetailResponse>

    @PATCH("document/memo/{docId}")
    suspend fun updateMemo(
        @Path("docId") docId: String,
        @Body body: UpdateData.MemoRequestBody
    ): retrofit2.Response<UpdateData.MemoResponseBody>

    @DELETE("document/{docId}")
    suspend fun deleteMemo(@Path("docId") docId: String): retrofit2.Response<DeleteData.ResponseBody>

    @Multipart
    @POST("document/file")
    suspend fun uploadFile(@Part files: List<MultipartBody.Part>): retrofit2.Response<UploadFileData.ResponseBody>

    @GET("document/file/{docId}")
    suspend fun downloadFile(@Path("docId") docId: String): retrofit2.Response<DownloadData.ResponseBody>

    @POST("document/webpage")
    suspend fun createWebPage(@Body body: CreateData.WebPageRequestBody): retrofit2.Response<CreateData.WebPageResponseBody>

    @GET("search/hybrid")
    suspend fun getEmbeddingData(
        @Query("query") query: String?,
    ): retrofit2.Response<MainListData.Response>

    @GET("search/text")
    suspend fun getTextSearchData(
        @Query("query") query: String?,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int?
    ): retrofit2.Response<MainListData.Response>

    @GET("document/search/tag")
    suspend fun getTagDetailData(
        @Query("tagName") tagName: String?,
        @Query("cursor") cursor: String?,
        @Query("doc-id") docID: String?,
        @Query("limit") limit: Int? = 20
    ): retrofit2.Response<MainListData.Response>

    @GET("tag")
    suspend fun getTagListData(
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int?
    ): retrofit2.Response<TagListData.Response>

    @POST("auth/check-email")
    suspend fun checkEmail(@Body body: SignInData.CheckEmailRequest): retrofit2.Response<SignInData.CheckEmailResponse>

    @GET("collection")
    suspend fun getCollectionListData(
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int?
    ): retrofit2.Response<CollectionListData.Response>

    @POST("collection")
    suspend fun createCollection(@Body body: CreateCollectionData.RequestBody): retrofit2.Response<CreateCollectionData.ResponseBody>

    @GET("tag")
    suspend fun getTagSearchData(
        @Query("query") query: String?,
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int?
    ): retrofit2.Response<TagListData.Response>

    @GET("document/search/collection")
    suspend fun getCollectionDetailData(
        @Query("collectionName") collectionName: String?,
        @Query("cursor") cursor: String?,
        @Query("doc-id") docID: String?,
        @Query("limit") limit: Int? = 20
    ): retrofit2.Response<MainListData.Response>

    @PATCH("collection/{name}")
    suspend fun addDataInCollection(
        @Path("name") name: String,
        @Body body: AddDataInCollectionData.AddRequestBody
    ): retrofit2.Response<AddDataInCollectionData.AddResponse>

    @PATCH("collection/{name}")
    suspend fun removeDataInCollection(
        @Path("name") name: String,
        @Body body: AddDataInCollectionData.RemoveRequestBody
    ): retrofit2.Response<AddDataInCollectionData.RemoveResponse>

    @DELETE("collection/{name}")
    suspend fun deleteCollection(@Path("name") name: String): retrofit2.Response<DeleteData.ResponseBody>

    @PATCH("collection/{name}")
    suspend fun updateCollection(
        @Path("name") name: String,
        @Body body: AddDataInCollectionData.UpdateCollectionRequestBody
    ): retrofit2.Response<AddDataInCollectionData.RemoveResponse>

    @GET("user")
    suspend fun getUserData(): retrofit2.Response<UserData.Response>

    @GET("user/storage/size")
    suspend fun getStorageSize(): retrofit2.Response<UserData.StorageData>

    @GET("user/storage/usage")
    suspend fun getStorageUsage(): retrofit2.Response<UserData.StorageData>

    @POST("auth/reset-code")
    suspend fun resetPasswordCode(@Body body: SignUpData.GetVerifyRequestBody): retrofit2.Response<SignUpData.GetVerifyResponseBody>

    @POST("auth/verify-reset-code")
    suspend fun checkVerifyResetCode(@Body body: SignUpData.CheckVerifyRequestBody): retrofit2.Response<SignUpData.CheckVerifyResponseBody>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body body: SignUpData.SignUpRequestBody): retrofit2.Response<SignUpData.CheckVerifyResponseBody>

    @POST("auth/withdraw-code")
    suspend fun withdrawCode(): retrofit2.Response<SignUpData.WithdrawVerifyResponseBody>

    @POST("auth/verify-withdraw-code")
    suspend fun verifyWithdrawCode(@Body body: SignUpData.WithdrawVerifyRequestBody): retrofit2.Response<SignUpData.WithdrawVerifyResponseBody>

    @POST("auth/withdraw")
    suspend fun withdraw(): retrofit2.Response<SignUpData.WithdrawVerifyResponseBody>
}