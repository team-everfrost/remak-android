package com.example.remak.view.main

import android.provider.MediaStore.Files
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remak.network.model.MainListData
import com.example.remak.repository.NetworkRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File

class MainViewModel : ViewModel() {

    private val networkRepository = NetworkRepository()

    private val _mainListData = MutableLiveData<List<MainListData.Data>>()
    val mainListData : LiveData<List<MainListData.Data>> = _mainListData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _isMemoCreateSuccess = MutableLiveData<String>()
    val isMemoCreateSuccess : LiveData<String> = _isMemoCreateSuccess

    var cursor : String? = null
    var docID : String? = null


    fun getAllMainList() = viewModelScope.launch {
        _isLoading.value = true
        try {
            val response = networkRepository.getMainList(null, null)
            if (response.isSuccessful) {
                _mainListData.value = response.body()?.data

                response.body()?.data?.let {
                    cursor = it.last().createdAt
                    docID = it.last().docId
                }

            } else {
                Log.d("fail", response.errorBody()!!.string())
            }
        } catch (e : Exception) {
        }

        Log.d("cursor", cursor.toString())
        Log.d("docID", docID.toString())
        Log.d("mainListData", mainListData.value.toString())
        _isLoading.value = false
    }

    fun getNewMainList() = viewModelScope.launch {
        _isLoading.value = true
        var tempData = mainListData.value

        try {
            val response = networkRepository.getMainList(cursor, docID)
            if (response.isSuccessful) {
                for (data in response.body()!!.data) {
                    tempData = tempData?.plus(data)
                }

                response.body()?.data?.let {it ->
                    cursor = it.last().createdAt
                    docID = it.last().docId
                }


            } else {
                Log.d("fail", response.errorBody().toString())
            }
        } catch (e : Exception) {
            Log.d("networkError", e.toString())
        }

        Log.d("cursor", cursor.toString())
        Log.d("docID", docID.toString())
        Log.d("mainListData", mainListData.value.toString())
        if(tempData != null) {
            _mainListData.value = tempData!!
        }
        _isLoading.value = false
    }


    fun createMemo(content : String) = viewModelScope.launch {
        try {
            val response = networkRepository.createMemo(content)
            if (response.isSuccessful) {
                Log.d("success", response.body().toString())
                _isMemoCreateSuccess.value = "메모가 생성되었습니다."

            } else {
                Log.d("fail", response.errorBody().toString())
                _isMemoCreateSuccess.value = "메모 생성에 실패했습니다"
            }
        } catch (e : Exception) {
            Log.d("networkError", e.toString())
        }

    }

    /** 파일 업로드 */
    fun uploadFile(files : List<MultipartBody.Part>) = viewModelScope.launch {
        try {
            val response = networkRepository.uploadFile(files)
            Log.d("file", files.toString())
            if (response.isSuccessful) {
                Log.d("success", response.body().toString())
                getAllMainList()

            } else {
                Log.d("fail", response.errorBody()?.string()!!)
            }
        } catch (e : Exception) {
            Log.d("networkError", e.toString())
        }
    }




}