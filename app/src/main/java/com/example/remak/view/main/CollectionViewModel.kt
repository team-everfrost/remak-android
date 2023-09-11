package com.example.remak.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.remak.dataStore.TokenRepository
import com.example.remak.model.ErrorResponse
import com.example.remak.network.model.CollectionListData
import com.example.remak.repository.NetworkRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch

class CollectionViewModel(private val tokenRepository: TokenRepository) : ViewModel() {
    private val networkRepository = NetworkRepository()
    private val _isCollectionEmpty = MutableLiveData<Boolean>()
    val isCollectionEmpty: LiveData<Boolean> = _isCollectionEmpty
    private val _collectionList = MutableLiveData<List<CollectionListData.Data>>()
    val collectionList: LiveData<List<CollectionListData.Data>> = _collectionList
    private var offset: Int? = null
    private val _isDuplicateName = MutableLiveData<Boolean?>()
    val isDuplicateName: LiveData<Boolean?> = _isDuplicateName

    fun getCollectionList() = viewModelScope.launch {
        try {
            val response = networkRepository.getCollectionListData(0)
            if (response.isSuccessful) {
                _collectionList.value = response.body()!!.data
                //빈 배열인지 감지
                _isCollectionEmpty.value = response.body()!!.data.isEmpty()
                Log.d("collection_list", response.body()!!.data.toString())
                offset = 20
            } else {

            }
        } catch (e: Exception) {
            _isCollectionEmpty.value = true
        }
    }

    fun createCollection(name: String, description: String?) = viewModelScope.launch {
        try {
            val response = networkRepository.createCollection(name, description)
            if (response.isSuccessful) {
                Log.d("createCollection", response.body().toString())
                _isDuplicateName.value = false
            } else {
                val errorBodyString = response.errorBody()!!.string()
                val gson = Gson()
                val errorResponse = gson.fromJson(errorBodyString, ErrorResponse::class.java)
                val message = errorResponse.message
                if (message == "error collection already exists") {
                    _isDuplicateName.value = true
                }
            }
        } catch (e: Exception) {
            Log.d("createCollection", e.toString())
        }
    }

    fun resetDuplicateName() {
        _isDuplicateName.value = null
    }

}

class CollectionViewModelFactory(private val tokenRepository: TokenRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CollectionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CollectionViewModel(tokenRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}