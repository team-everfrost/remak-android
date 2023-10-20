package com.example.remak.repository

import com.example.remak.network.Api
import com.example.remak.network.RetrofitInstance
import com.example.remak.network.model.AddDataInCollectionData
import com.example.remak.network.model.CollectionListData
import com.example.remak.network.model.CreateCollectionData
import com.example.remak.network.model.CreateData
import com.example.remak.network.model.DeleteData
import com.example.remak.network.model.DownloadData
import com.example.remak.network.model.MainListData
import com.example.remak.network.model.SignInData
import com.example.remak.network.model.SignUpData
import com.example.remak.network.model.TagListData
import com.example.remak.network.model.UpdateData
import com.example.remak.network.model.UploadFileData
import okhttp3.MultipartBody
import retrofit2.Response

class NetworkRepository {

    private val client = RetrofitInstance.getInstance().create(Api::class.java)

    //로그인
    suspend fun signIn(email: String, password: String): Response<SignInData.ResponseBody> {
        val requestBody = SignInData.RequestBody(email = email, password = password)
        return client.signIn(requestBody)
    }

    suspend fun getVerifyCode(email: String): Response<SignUpData.GetVerifyResponseBody> {
        val requestBody = SignUpData.GetVerifyRequestBody(email = email)
        return client.getVerifyCode(requestBody)
    }

    suspend fun checkVerifyCode(
        signupCode: String,
        email: String
    ): Response<SignUpData.CheckVerifyResponseBody> {
        val requestBody = SignUpData.CheckVerifyRequestBody(code = signupCode, email = email)
        return client.checkVerifyCode(requestBody)
    }

    suspend fun signUp(email: String, password: String): Response<SignUpData.SignUpResponseBody> {
        val requestBody = SignUpData.SignUpRequestBody(email = email, password = password)
        return client.signUp(requestBody)
    }

    suspend fun createMemo(content: String): Response<CreateData.MemoResponseBody> {
        val requestBody = CreateData.MemoRequestBody(content = content)
        return client.createMemo(requestBody)
    }

    suspend fun getMainList(cursor: String?, docID: String?): Response<MainListData.Response> {
        return client.getMainList(cursor, docID)
    }

    suspend fun getDetailData(docId: String): Response<MainListData.DetailResponse> {
        return client.getDetailData(docId)
    }

    suspend fun updateMemo(docId: String, content: String): Response<UpdateData.MemoResponseBody> {
        val requestBody = UpdateData.MemoRequestBody(content = content)
        return client.updateMemo(docId, requestBody)
    }

    suspend fun deleteDocument(docId: String): Response<DeleteData.ResponseBody> {
        return client.deleteMemo(docId)
    }

    suspend fun uploadFile(files: List<MultipartBody.Part>): Response<UploadFileData.ResponseBody> {
        return client.uploadFile(files)
    }

    suspend fun downloadFile(docId: String): Response<DownloadData.ResponseBody> {
        return client.downloadFile(docId)
    }

    suspend fun createWebPage(url: String): Response<CreateData.WebPageResponseBody> {
        val requestBody = CreateData.WebPageRequestBody(" ", url = url, " ")
        return client.createWebPage(requestBody)
    }

    suspend fun getEmbeddingData(
        query: String?
    ): Response<MainListData.Response> {
        return client.getEmbeddingData(query)
    }

    suspend fun getTextSearchData(
        query: String?,
        offset: Int?,
    ): Response<MainListData.Response> {
        return client.getTextSearchData(query, 20, offset)
    }

    suspend fun getTagDetailData(
        tagName: String,
        cursor: String?,
        docId: String?
    ): Response<MainListData.Response> {
        return client.getTagDetailData(tagName, cursor, docId)
    }

    suspend fun getTagListData(offset: Int?): Response<TagListData.Response> {
        return client.getTagListData(20, offset)
    }

    suspend fun checkEmail(email: String): Response<SignInData.CheckEmailResponse> {
        val requestBody = SignInData.CheckEmailRequest(email = email)
        return client.checkEmail(requestBody)
    }

    suspend fun getCollectionListData(offset: Int?): Response<CollectionListData.Response> {
        return client.getCollectionListData(20, offset)
    }

    suspend fun createCollection(
        name: String,
        description: String?
    ): Response<CreateCollectionData.ResponseBody> {
        val requestBody = CreateCollectionData.RequestBody(name = name, description = description)
        return client.createCollection(requestBody)
    }

    suspend fun getTagSearchData(query: String?, offset: Int?): Response<TagListData.Response> {
        return client.getTagSearchData(query, 20, offset)
    }

    suspend fun getCollectionDetailData(
        collectionName: String?,
        cursor: String?,
        docId: String?
    ): Response<MainListData.Response> {
        return client.getCollectionDetailData(collectionName, cursor, docId)
    }

    suspend fun addDataInCollection(
        name: String,
        docIds: List<String>
    ): Response<AddDataInCollectionData.AddResponse> {
        val requestBody = AddDataInCollectionData.AddRequestBody(docIds)
        return client.addDataInCollection(name, requestBody)
    }

    suspend fun removeDataInCollection(
        name: String,
        docIds: List<String>
    ): Response<AddDataInCollectionData.RemoveResponse> {
        val requestBody = AddDataInCollectionData.RemoveRequestBody(docIds)
        return client.removeDataInCollection(name, requestBody)
    }

    suspend fun deleteCollection(name: String): Response<DeleteData.ResponseBody> {
        return client.deleteCollection(name)
    }

    suspend fun getUserData(): Response<com.example.remak.network.model.UserData.Response> {
        return client.getUserData()
    }

    suspend fun updateCollection(
        name: String,
        newName: String,
        description: String?
    ): Response<AddDataInCollectionData.RemoveResponse> {
        val requestBody = AddDataInCollectionData.UpdateCollectionRequestBody(
            newName = newName,
            description = description
        )
        return client.updateCollection(name, requestBody)
    }

    suspend fun getStorageSize(): Response<com.example.remak.network.model.UserData.StorageData> {
        return client.getStorageSize()
    }

    suspend fun getStorageUsage(): Response<com.example.remak.network.model.UserData.StorageData> {
        return client.getStorageUsage()
    }

    suspend fun resetPasswordCode(
        email: String
    ): Response<SignUpData.GetVerifyResponseBody> {
        val requestBody = SignUpData.GetVerifyRequestBody(email)
        return client.resetPasswordCode(requestBody)
    }

    suspend fun checkVerifyResetCode(
        code: String,
        email: String
    ): Response<SignUpData.CheckVerifyResponseBody> {
        val requestBody = SignUpData.CheckVerifyRequestBody(code, email)
        return client.checkVerifyResetCode(requestBody)
    }

    suspend fun resetPassword(
        email: String,
        password: String
    ): Response<SignUpData.CheckVerifyResponseBody> {
        val requestBody = SignUpData.SignUpRequestBody(email, password)
        return client.resetPassword(requestBody)
    }

}