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

class TagViewModel (private val tokenRepository: TokenRepository) : ViewModel() {
    private val networkRepository = NetworkRepository()

    private val _tagList = MutableLiveData<List<TagListData.Data>>()
    val tagList : LiveData<List<TagListData.Data>> = _tagList

    private var offset : Int? = null
    private var isLoadEnd : Boolean = false


    fun getTagList() = viewModelScope.launch {
        try {
            val response = networkRepository.getTagListData(0)
            if (response.isSuccessful) {
                _tagList.value = response.body()!!.data
                offset = 20
            } else {
                Log.d("tag_list", response.errorBody()!!.string())
            }
        } catch (e : Exception) {
            Log.d("tag_list", e.toString())
        }
    }

    fun getNewTagList() = viewModelScope.launch {
        var tempData = tagList.value?.toMutableList() ?: mutableListOf()
        if (!isLoadEnd) {
            try {
                val response = networkRepository.getTagListData(offset!!)
                if (response.isSuccessful) {
                    if (response.body()!!.data.isEmpty()) {
                        isLoadEnd = true
                    } else {
                        Log.d("tag_list", response.body()!!.data.toString())
                        for (data in response.body()!!.data) {
                            tempData.add(data)
                        }
                        offset = offset!! + 20
                    }
                } else {
                    Log.d("tag_list", response.errorBody()!!.string())
                    isLoadEnd = true
                }
            } catch (e: Exception) {
                Log.d("tag_list", e.toString())
                isLoadEnd = true
            }
            _tagList.value = tempData
        }
    }

    fun resetScrollData() {
        offset = null
        isLoadEnd = false
    }
}

class TagViewModelFactory(private val tokenRepository: TokenRepository, ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TagViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TagViewModel(tokenRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}