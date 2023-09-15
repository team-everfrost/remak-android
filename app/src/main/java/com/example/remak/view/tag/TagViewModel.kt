package com.example.remak.view.tag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.remak.dataStore.TokenRepository
import com.example.remak.network.model.MainListData
import com.example.remak.network.model.TagListData
import com.example.remak.repository.NetworkRepository
import kotlinx.coroutines.launch

class TagViewModel(private val tokenRepository: TokenRepository) : ViewModel() {
    private val networkRepository = NetworkRepository()
    private val _tagList = MutableLiveData<List<TagListData.Data>>()
    val tagList: LiveData<List<TagListData.Data>> = _tagList
    private var offset: Int? = null
    private var isListEnd: Boolean = false
    private var isLoadEnd: Boolean = false
    private lateinit var lastQuery: String
    private val _tagDetailData = MutableLiveData<List<MainListData.Data>>()
    val tagDetailData: LiveData<List<MainListData.Data>> = _tagDetailData

    private var cursor: String? = null
    private var docId: String? = null
    fun getTagList() = viewModelScope.launch {
        try {
            var response = networkRepository.getTagListData(0)
            if (response.isSuccessful) {
                _tagList.value = response.body()!!.data
                offset = 20
            } else {
            }
        } catch (e: Exception) {
        }
    }

    fun getNewTagList() = viewModelScope.launch {
        val tempData = tagList.value?.toMutableList() ?: mutableListOf()
        if (!isListEnd && !isLoadEnd) {
            isLoadEnd = true
            try {
                val response = networkRepository.getTagListData(offset!!)
                if (response.isSuccessful) {
                    if (response.body()!!.data.isEmpty()) {
                        isListEnd = true
                    } else {
                        for (data in response.body()!!.data) {
                            tempData.add(data)
                        }
                        offset = offset!! + 20
                    }
                } else {
                    isListEnd = true
                }
            } catch (e: Exception) {
                isListEnd = true
            }
            _tagList.value = tempData
            isLoadEnd = false
        }
    }

    fun getTagDetailData(tagName: String) = viewModelScope.launch {
        try {
            val response = networkRepository.getTagDetailData(tagName, null, null)
            if (response.isSuccessful) {
                _tagDetailData.value = response.body()!!.data
                response.body()!!.data.let {
                    cursor = it.last().updatedAt
                    docId = it.last().docId
                }
            } else {
            }
        } catch (e: Exception) {
        }
    }

    fun getNewTagDetailData(tagName: String) = viewModelScope.launch {
        if (!isLoadEnd) {
            val tempData = tagDetailData.value?.toMutableList() ?: mutableListOf()
            try {
                val response = networkRepository.getTagDetailData(tagName, cursor, docId)
                if (response.isSuccessful) {
                    response.body()!!.data.let {
                        if (it.isNotEmpty()) {
                            tempData.addAll(it)
                            _tagDetailData.value = tempData
                            cursor = it.last().updatedAt
                            docId = it.last().docId
                        } else {
                            isLoadEnd = true
                        }
                    }
                    _tagDetailData.value = tempData
                } else {
                }
            } catch (e: Exception) {
            }
        }
    }

    fun getTagSearchResult(query: String) = viewModelScope.launch {
        lastQuery = query
        val response = networkRepository.getTagSearchData(query, null)
        try {
            if (response.isSuccessful) {
                _tagList.value = response.body()!!.data
                offset = 20
            } else {
            }
        } catch (e: Exception) {
        }
    }

    fun getNewTagSearchResult() = viewModelScope.launch {
        val tempDat = tagList.value?.toMutableList() ?: mutableListOf()
        if (!isListEnd && !isLoadEnd) {
            isLoadEnd = true
        }
        val response = networkRepository.getTagSearchData(lastQuery, offset)
        try {
            if (response.isSuccessful) {
                if (response.body()!!.data.isEmpty()) {
                    isListEnd = true
                } else {
                    for (data in response.body()!!.data) {
                        tempDat.add(data)
                    }
                    offset = offset!! + 20
                }
            } else {
                isListEnd = true
            }
        } catch (e: Exception) {
            isListEnd = true
        }
        _tagList.value = tempDat
        isLoadEnd = false
    }

    fun resetScrollData() {
        offset = null
        isListEnd = false
        isLoadEnd = false
    }

    fun resetTagDetailData() {
        _tagDetailData.value = listOf()
    }
}

class TagViewModelFactory(private val tokenRepository: TokenRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TagViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TagViewModel(tokenRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}