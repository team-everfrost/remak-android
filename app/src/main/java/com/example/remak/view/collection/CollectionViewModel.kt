package com.example.remak.view.collection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.remak.dataStore.TokenRepository
import com.example.remak.model.ErrorResponse
import com.example.remak.network.model.CollectionListData
import com.example.remak.network.model.MainListData
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
    private val _isUpdateComplete = MutableLiveData<Boolean>()
    val isUpdateComplete: LiveData<Boolean> = _isUpdateComplete
    private val _collectionDetailData = MutableLiveData<List<MainListData.Data>>()
    val collectionDetailData: LiveData<List<MainListData.Data>> = _collectionDetailData
    private val _isActionComplete = MutableLiveData<Boolean>()
    val isActionComplete: LiveData<Boolean> = _isActionComplete
    private var cursor: String? = null
    private var docId: String? = null
    private val _selectedItemsCount = MutableLiveData<Int>()
    val selectedItemsCount: LiveData<Int> = _selectedItemsCount
    fun getCollectionList() = viewModelScope.launch {
        try {
            val response = networkRepository.getCollectionListData(0)
            if (response.isSuccessful) {
                _collectionList.value = response.body()!!.data
                //빈 배열인지 감지
                _isCollectionEmpty.value = response.body()!!.data.isEmpty()
                offset = 20
            } else {

            }
        } catch (e: Exception) {
            _isCollectionEmpty.value = true
        }
    }

    fun getCollectionDetailData(collectionName: String) = viewModelScope.launch {
        val response = networkRepository.getCollectionDetailData(collectionName, null, null)
        try {
            if (response.isSuccessful) {
                _collectionDetailData.value = response.body()!!.data
                _isCollectionEmpty.value = response.body()!!.data.isEmpty()
                response.body()!!.data.let {
                    cursor = it.last().updatedAt
                    docId = it.last().docId
                }
            } else {
            }
        } catch (e: Exception) {

        }
    }

    fun deleteCollection(name: String) = viewModelScope.launch {
        val response = networkRepository.deleteCollection(name)
        try {
            if (response.isSuccessful) {
            } else {
            }
            _isActionComplete.value = true
        } catch (e: Exception) {
        }
    }

    fun createCollection(name: String, description: String?) = viewModelScope.launch {
        try {
            val response = networkRepository.createCollection(name, description)
            if (response.isSuccessful) {
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
        }
    }

    fun addDataInCollection(collectionName: List<String>, docIds: List<String>) =
        viewModelScope.launch {
            for (item in collectionName) {
                val response = networkRepository.addDataInCollection(item, docIds)
                try {
                    if (response.isSuccessful) {
                    } else {
                    }
                } catch (e: Exception) {
                }
            }
            _isUpdateComplete.value = true
        }

    fun removeDataInCollection(collectionName: String, docIds: List<String>) =
        viewModelScope.launch {
            val response = networkRepository.removeDataInCollection(collectionName, docIds)
            try {
                if (response.isSuccessful) {
                    getCollectionDetailData(collectionName)
                } else {
                }
            } catch (e: Exception) {
            }
        }

    fun resetSelectionCount() {
        _selectedItemsCount.value = 0
    }

    fun resetDuplicateName() {
        _isDuplicateName.value = null
    }

    fun increaseSelectionCount() {
        _selectedItemsCount.value = (_selectedItemsCount.value ?: 0) + 1
    }

    fun decreaseSelectionCount() {
        _selectedItemsCount.value = (_selectedItemsCount.value ?: 0) - 1
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