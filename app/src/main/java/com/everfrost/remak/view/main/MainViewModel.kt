package com.everfrost.remak.view.main

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.everfrost.remak.dataStore.TokenRepository
import com.everfrost.remak.network.model.MainListData
import com.everfrost.remak.network.model.UserData
import com.everfrost.remak.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val networkRepository: NetworkRepository
) : ViewModel() {
    private var currentDateType: String? = null
    val recyclerState: MutableLiveData<Parcelable> = MutableLiveData()


    //메인 리스트
    private val _mainListData = MutableLiveData<List<MainListData.Data>>()
    val mainListData: LiveData<List<MainListData.Data>> = _mainListData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isMemoCreateSuccess = MutableLiveData<String>()
    val isMemoCreateSuccess: LiveData<String> = _isMemoCreateSuccess

    private val _uploadFileSuccess = MutableLiveData<Boolean>()
    val uploadFileSuccess: LiveData<Boolean> = _uploadFileSuccess

    private val _isLogIn = MutableLiveData<Boolean>()
    val isLogIn: LiveData<Boolean> = _isLogIn

    private val _isWebPageCreateSuccess = MutableLiveData<Boolean>()
    val isWebPageCreateSuccess: LiveData<Boolean> = _isWebPageCreateSuccess

    private val _selectedItemsCount = MutableLiveData<Int>()
    val selectedItemsCount: LiveData<Int> = _selectedItemsCount

    private val _isDataEmpty = MutableLiveData<Boolean>()
    val isDataEmpty: LiveData<Boolean> = _isDataEmpty

    private val _userData = MutableLiveData<UserData.Data>()
    val userData: LiveData<UserData.Data> = _userData

    private var isLoadEnd: Boolean = false

    private var cursor: String? = null
    private var docID: String? = null

    private var searchCursor: String? = null
    private var searchDocID: String? = null
    private var embeddingOffset: Int? = null
    val isEmbeddingLoading = MutableLiveData<Boolean>().apply { value = false }
    private var lastQuery: String? = null

    private fun classifyDate(dateString: String): String {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val dateTime = ZonedDateTime.parse(dateString, formatter).toLocalDate()
        val today = LocalDate.now()
        return when {
            //오늘
            dateTime.isEqual(today) -> {
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
        try {
            val response = networkRepository.getMainList(null, null)
            if (response.isSuccessful) {
                _isDataEmpty.value = response.body()?.data?.isEmpty()
                currentDateType = null
                val newData = mutableListOf<MainListData.Data>()
                _mainListData.value = response.body()?.data
                for (data in response.body()!!.data) {
                    data.updatedAt =
                        convertToUserTimezone(data.updatedAt!!, TimeZone.getDefault().id)
                    val dateType = classifyDate(data.updatedAt!!.toString())
                    if (dateType != currentDateType) {
                        currentDateType = dateType
                        newData.add(
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
                    newData.add(data)
                }
                _mainListData.value = newData
                response.body()?.data?.let {
                    cursor = it.last().createdAt
                    docID = it.last().docId
                }
            } else {
            }
        } catch (e: Exception) {
            Log.d("메인", e.toString())
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
                }
            } catch (e: Exception) {
                isLoadEnd = true
            }
            _mainListData.value = tempData
            _isLoading.value = false
        }
    }

    fun createWebPage(url: String) = viewModelScope.launch {
        try {
            val response = networkRepository.createWebPage(url)
            if (response.isSuccessful) {
                getAllMainList()
                _isWebPageCreateSuccess.value = true

            } else {
            }
        } catch (e: Exception) {
        }
    }

    fun getUserData() = viewModelScope.launch {
        val response = networkRepository.getUserData()
        if (response.isSuccessful) {
            _userData.value = response.body()?.data
        }
    }

    fun loginCheck() = viewModelScope.launch {
        _isLogIn.value = tokenRepository.fetchTokenData() != null
    }

    fun deleteDocument(docId: String) = viewModelScope.launch {
        val response = networkRepository.deleteDocument(docId)
        try {
            if (response.isSuccessful) {
                getAllMainList()
            } else {
            }
        } catch (e: Exception) {
        }
    }

    fun resetSelectionCount() {
        _selectedItemsCount.value = 0
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

    fun increaseSelectionCount() {
        _selectedItemsCount.value = (_selectedItemsCount.value ?: 0) + 1
    }

    fun decreaseSelectionCount() {
        _selectedItemsCount.value = (_selectedItemsCount.value ?: 0) - 1
    }

}

