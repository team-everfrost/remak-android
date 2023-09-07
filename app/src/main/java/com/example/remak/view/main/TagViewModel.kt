package com.example.remak.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.remak.dataStore.TokenRepository
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


    fun getTagList() = viewModelScope.launch {
        try {
            var response = networkRepository.getTagListData(0)
            if (response.isSuccessful) {

                _tagList.value = response.body()!!.data


                offset = 20
            } else {
                Log.d("tag_list", response.errorBody()!!.string())
            }
        } catch (e: Exception) {
            Log.d("tag_list", e.toString())
        }
    }

    fun getNewTagList() = viewModelScope.launch {
        var tempData = tagList.value?.toMutableList() ?: mutableListOf()
        if (!isListEnd && !isLoadEnd) {
            isLoadEnd = true
            try {
                val response = networkRepository.getTagListData(offset!!)
                if (response.isSuccessful) {
                    if (response.body()!!.data.isEmpty()) {
                        isListEnd = true
                    } else {
                        Log.d("tag_list", offset.toString())
                        for (data in response.body()!!.data) {
                            data.type = "TAG"
                            tempData.add(data)
                        }
                        offset = offset!! + 20
                    }
                } else {
                    Log.d("tag_list", response.errorBody()!!.string())
                    isListEnd = true
                }
            } catch (e: Exception) {
                Log.d("tag_list", e.toString())
                isListEnd = true
            }
            _tagList.value = tempData
            isLoadEnd = false
        }
    }

    fun resetScrollData() {
        offset = null
        isListEnd = false
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