package com.example.remak.view.main

import android.util.Log
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
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone

class MainViewModel(private val tokenRepository: TokenRepository) : ViewModel() {
    private var currentDateType : String? = null

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

    private var isLoadEnd : Boolean = false

    var cursor : String? = null
    var docID : String? = null

    var searchCursor : String? = null
    var searchDocID : String? = null
    var embeddingOffset : Int? = null
    val isEmbeddingLoading = MutableLiveData<Boolean>().apply { value = false }
    private var lastQuery : String? = null

    private fun classifyDate(dateString : String) : String {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val dateTime = ZonedDateTime.parse(dateString, formatter).toLocalDate()
        val today = LocalDate.now()
        return  when {
            //오늘
            dateTime.isEqual(today) -> {
                Log.d("오늘", "오늘")
                "오늘"
            }
            //오늘 제외 7일 전
            dateTime.isAfter(today.minusDays(7)) -> {
                "최근 일주일"
            }
            //오늘 기준 30일 전
            dateTime.isAfter(today.minusDays(30)) -> {
                "최근 한달"
            }
            else -> {
                "그 이전"
            }
        }
    }

    private fun convertToUserTimezone(dateString: String, userTimezone: String): String {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val zonedDateTime = ZonedDateTime.parse(dateString, formatter)
        val userZoneId = ZoneId.of(userTimezone)

        // Convert the datetime to user's timezone
        val userZonedDateTime = zonedDateTime.withZoneSameInstant(userZoneId)
        return userZonedDateTime.format(formatter)
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
                currentDateType = null

                val newData = mutableListOf<MainListData.Data>()

                _mainListData.value = response.body()?.data

                for (data in response.body()!!.data) {
                    data.updatedAt = convertToUserTimezone(data.updatedAt!!, TimeZone.getDefault().id)
                    val dateType = classifyDate(data.updatedAt!!.toString())
                    if (dateType != currentDateType) {
                        currentDateType = dateType
                        newData.add(MainListData.Data(
                            docId = null,
                            title = null,
                            type = "DATE",
                            url = null,
                            content = null,
                            summary = null,
                            status = null,
                            thumbnailUrl = null,
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
                Log.d("mainlistdatas", _mainListData.value.toString())
                response.body()?.data?.let {
                    cursor = it.last().createdAt
                    docID = it.last().docId
                }
            } else {
                Log.d("fail", response.errorBody()!!.string())
            }
        } catch (e : Exception) {
            Log.d("networkError", e.toString())
        }
        _isLoading.value = false
    }

    fun getNewMainList() = viewModelScope.launch {
        if (!isLoadEnd) {
            _isLoading.value = true
            var tempData = mainListData.value?.toMutableList() ?: mutableListOf()
            try {
                val response = networkRepository.getMainList(cursor, docID)
                if (response.isSuccessful) {
                    Log.d("getnewmainlistSuccess", response.body().toString())

                    for (data in response.body()!!.data) {
                        data.updatedAt =
                            convertToUserTimezone(data.updatedAt!!, TimeZone.getDefault().id)
                        val dateType = classifyDate(data.updatedAt!!.toString())
                        if (dateType != currentDateType) {
                            currentDateType = dateType
                            tempData.add(
                                MainListData.Data(
                                    docId = null,
                                    title = null,
                                    type = "DATE",
                                    url = null,
                                    content = null,
                                    summary = null,
                                    status = null,
                                    thumbnailUrl = null,
                                    createdAt = null,
                                    updatedAt = null,
                                    tags = listOf(),
                                    isSelected = false,
                                    header = dateType
                                )
                            )
                        }
                        tempData.add(data)
                    }
                    response.body()?.data?.let {
                        cursor = it.last().createdAt
                        docID = it.last().docId
                    }
                } else {
                    Log.d("getnewmainlistFail", response.errorBody().toString())
                }
            } catch (e: Exception) {
                Log.d("getnewmainlistNetwork", e.toString())
                isLoadEnd = true
            }

            Log.d("cursor", cursor.toString())
            Log.d("docID", docID.toString())
            Log.d("mainListData", mainListData.value.toString())

            _mainListData.value = tempData
            _isLoading.value = false
        }
    }


    fun createMemo(content : String) = viewModelScope.launch {
        try {
            val response = networkRepository.createMemo(content)
            if (response.isSuccessful) {
                _isMemoCreateSuccess.value = "메모가 생성되었습니다."
            } else {
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

    fun resetUploadFileSuccess() {
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
        _isLogIn.value = tokenRepository.fetchTokenData() != null
    }


    fun deleteDocument(docId : String) = viewModelScope.launch {
        val response = networkRepository.deleteDocument(docId)
        try {
            if (response.isSuccessful) {
                Log.d("delete", "success")
                getAllMainList()
            } else {
                Log.d("delete", response.errorBody()!!.string())
            }
        } catch (e : Exception) {
            Log.d("delete", e.toString())
        }
    }


    fun resetMainData() {
        isLoadEnd = false
        cursor = null
        docID = null
        _mainListData.value = listOf()
    }

    fun resetScrollData() {
        isLoadEnd = false
        cursor = null
        docID = null
        searchCursor = null
        searchDocID = null
        embeddingOffset = null
    }


}

class MainViewModelFactory(private val tokenRepository: TokenRepository, ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(tokenRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}