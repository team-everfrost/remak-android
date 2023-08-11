package com.example.remak.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.remak.dataStore.SearchHistoryRepository
import com.example.remak.network.model.SearchEmbeddingData
import com.example.remak.repository.NetworkRepository
import kotlinx.coroutines.launch

class SearchViewModel(private val searchHistoryRepository: SearchHistoryRepository) : ViewModel() {

    private val _searchResult = MutableLiveData<List<SearchEmbeddingData.Data>>()
    val searchResult : LiveData<List<SearchEmbeddingData.Data>> = _searchResult

    private val _searchHistory = MutableLiveData<List<String>>()
    val searchHistory : LiveData<List<String>> = _searchHistory

    private var isLoadEnd : Boolean = false


    private val networkRepository = NetworkRepository()
    var searchCursor : String? = null
    var searchDocID : String? = null
    var embeddingOffset : Int? = null
    val isEmbeddingLoading = MutableLiveData<Boolean>().apply { value = false }
    private var lastQuery : String? = null


    fun getEmbeddingSearchResult(query : String) = viewModelScope.launch {
        lastQuery = query
        val response = networkRepository.getEmbeddingData(query, null)
        try {
            if (response.isSuccessful) {
                _searchResult.value = response.body()!!.data
                Log.d("search_result", _searchResult.value.toString())
                embeddingOffset = 20
            } else {
                Log.d("search_result", response.errorBody()!!.string())
            }
        } catch (e : Exception) {
            Log.d("search_result", e.toString())
        }
    }

    fun getTextSearchResult(query: String) = viewModelScope.launch {
        lastQuery = query
        val response = networkRepository.getTextSearchData(query, null, null)
        try {
            if (response.isSuccessful) {
                _searchResult.value = response.body()!!.data
                Log.d("search_result", _searchResult.value.toString())
                response.body()!!.data.let {
                    searchCursor = it.last().updatedAt
                    searchDocID = it.last().docId
                }
            } else {
                Log.d("search_result", response.errorBody()!!.string())
            }
        } catch (e : Exception) {
            Log.d("search_result", e.toString())
        }

    }

    fun getNewTextSearchResult() = viewModelScope.launch {
        if (!isLoadEnd) {
            val tempData = searchResult.value?.toMutableList() ?: mutableListOf()
            val response = networkRepository.getTextSearchData(lastQuery, searchCursor, searchDocID)
            try {
                Log.d("search_result", response.body()!!.data.toString())
                if (response.isSuccessful) {
                    for (data in response.body()!!.data) {
                        tempData.add(data)
                    }
                    response.body()!!.data.let {
                        searchCursor = it.last().createdAt
                        searchDocID = it.last().docId
                    }
                    _searchResult.value = tempData

                } else {
                    Log.d("search_result", response.errorBody()!!.string())
                }
            } catch (e : Exception) {
                Log.d("search_result", e.toString())
                isLoadEnd = true
            }
        }
    }

    fun getNewEmbeddingSearch() = viewModelScope.launch {
        isEmbeddingLoading.value = true
        var tempData = searchResult.value?.toMutableList() ?: mutableListOf()

        Log.d("embeddingOffset", embeddingOffset.toString())
        val response = networkRepository.getEmbeddingData(lastQuery, embeddingOffset)
        try {
            if (response.isSuccessful) {
                for (data in response.body()!!.data) {
                    tempData.add(data)
                }
                embeddingOffset = embeddingOffset?.plus(20)

            } else {
                Log.d("search_result", response.errorBody()!!.string())
            }
        } catch (e : Exception) {
            Log.d("search_result", e.toString())
            isLoadEnd = true
        }

        _searchResult.value = tempData
        isEmbeddingLoading.value = false
    }


    fun resetSearchData() {
        _searchResult.value = listOf()
    }



    fun resetScrollData() {
        isLoadEnd = false
        searchCursor = null
        searchDocID = null
        embeddingOffset = null
    }

    fun saveSearchHistory(query : String) = viewModelScope.launch {
        searchHistoryRepository.saveSearchHistory(query)
    }

    fun getSearchHistory()  = viewModelScope.launch {
        _searchHistory.value = searchHistoryRepository.fetchSearchHistory()
    }

    fun deleteSearchHistory(query : String) = viewModelScope.launch {
        searchHistoryRepository.deleteSearchQuery(query)
        getSearchHistory()
    }
}

class SearchViewModelFactory(private val searchHistoryRepository: SearchHistoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(searchHistoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}