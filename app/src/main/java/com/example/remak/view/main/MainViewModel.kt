package com.example.remak.view.main

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.remak.dataStore.TokenRepository
import com.example.remak.network.model.MainListData
import com.example.remak.repository.NetworkRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjuster
import java.time.temporal.TemporalAdjusters

class MainViewModel(private val tokenRepository: TokenRepository) : ViewModel() {

    private val networkRepository = NetworkRepository()

    //메인 리스트
    private val _mainListData = MutableLiveData<List<MainListData.Data>>()
    val mainListData : LiveData<List<MainListData.Data>> = _mainListData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _isMemoCreateSuccess = MutableLiveData<String>()
    val isMemoCreateSuccess : LiveData<String> = _isMemoCreateSuccess

    private val _uploadFileSuccess = MutableLiveData<Boolean>()
    val uploadFileSuccess : LiveData<Boolean> = _uploadFileSuccess

    private val _isLogIn = MutableLiveData<Boolean>()
    val isLogIn : LiveData<Boolean> = _isLogIn

    private val _isWebPageCreateSuccess = MutableLiveData<Boolean>()
    val isWebPageCreateSuccess : LiveData<Boolean> = _isWebPageCreateSuccess


    var cursor : String? = null
    var docID : String? = null


    private fun classifyDate(dateString : String) : String {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val dateTime = ZonedDateTime.parse(dateString, formatter).toLocalDate()
        val today = LocalDate.now()

        return  when {
            dateTime.isEqual(today) -> {
                "오늘"
            }
            //이번주
            dateTime.isAfter(today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))) -> {
                "이번 주"
            }
            //이번달
            dateTime.isAfter(today.withDayOfMonth(1).minusDays(1)) -> {
                "이번 달"
            }
            else -> {
                "그 이전"
            }
        }
    }



    fun deleteToken() = viewModelScope.launch {
        tokenRepository.deleteUser()
    }
    fun getAllMainList() = viewModelScope.launch {
        _isLoading.value = true
        Log.d("token", tokenRepository.fetchTokenData().toString())

        try {
            val response = networkRepository.getMainList(null, null)
            if (response.isSuccessful) {
                val dataWithHeaders = mutableListOf<MainListData.Data>()
                val currentHeader : String? = null
                val newData = mutableListOf<MainListData.Data>()
                var currentDateType : String? = null


                // 임시로 이미지는 https://picsum.photos/200/300로 적용
                for (i in response.body()!!.data){
                    if (i.type == "IMAGE") {
                        response.body()!!.data[response.body()!!.data.indexOf(i)].url = "https://picsum.photos/200/300"
                    }
                }
                _mainListData.value = response.body()?.data

                for (data in response.body()!!.data) {
                    val dateType = classifyDate(data.updatedAt!!)
                    if (dateType != currentDateType) {
                        currentDateType = dateType
                        Log.d("data삽입", currentDateType.toString())

                        newData.add(MainListData.Data(
                            docId = null,
                            title = null,
                            type = "DATE",
                            url = null,
                            content = null,
                            summary = null,
                            status = null,
                            createdAt = null,
                            updatedAt = null,
                            tags = listOf(),
                            isSelected = false,
                            header = dateType
                        ))
                    }
                    newData.add(data)
                }



                _mainListData.value = newData





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

                response.body()?.data?.let {
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
                _uploadFileSuccess.value = true

            } else {
                Log.d("fail", response.errorBody()?.string()!!)
            }
        } catch (e : Exception) {
            Log.d("networkError", e.toString())
        }
    }

    fun isUploadFileSuccess() {
        _uploadFileSuccess.value = false
    }

    fun createWebPage(url : String) = viewModelScope.launch {
        try {
            val response = networkRepository.createWebPage(url)
            if (response.isSuccessful) {
                Log.d("success", response.body().toString())
                getAllMainList()
                _isWebPageCreateSuccess.value = true

            } else {
                Log.d("fail", response.errorBody()?.string()!!)
            }
        } catch (e : Exception) {
            Log.d("networkError", e.toString())
        }
    }

    fun loginCheck() = viewModelScope.launch {
        if (tokenRepository.fetchTokenData() != null) {
            _isLogIn.value = true
        } else {
            _isLogIn.value = false
        }
    }


}

class MainViewModelFactory(private val tokenRepository: TokenRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(tokenRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}