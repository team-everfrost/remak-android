package com.everfrost.remak.view.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.everfrost.remak.dataStore.SearchHistoryRepository
import com.everfrost.remak.network.model.MainListData
import com.everfrost.remak.repository.NetworkRepository
import kotlinx.coroutines.launch

class SearchViewModel(private val searchHistoryRepository: SearchHistoryRepository) : ViewModel() {

    private val _searchResult = MutableLiveData<List<MainListData.Data>>()
    val searchResult: LiveData<List<MainListData.Data>> = _searchResult
    private val _searchHistory = MutableLiveData<List<String>>()
    val searchHistory: LiveData<List<String>> = _searchHistory
    private var isLoadEnd: Boolean = false
    private val networkRepository = NetworkRepository()
    var searchCursor: String? = null
    var searchDocID: String? = null
    var embeddingOffset: Int? = null
    val isEmbeddingLoading = MutableLiveData<Boolean>().apply { value = false }
    private var lastQuery: String? = null
    private var offset = 0

    fun getEmbeddingSearchResult(query: String) = viewModelScope.launch {
        lastQuery = query
        val response = networkRepository.getEmbeddingData(query)
        try {
            if (response.isSuccessful) {
                _searchResult.value = response.body()!!.data
            } else {
            }
        } catch (e: Exception) {
        }
    }

    fun getTextSearchResult(query: String) = viewModelScope.launch {
        lastQuery = query
        val response = networkRepository.getTextSearchData(query, null)
        try {
            if (response.isSuccessful) {
                _searchResult.value = response.body()!!.data
                response.body()!!.data.let {
                    searchCursor = it.last().updatedAt
                    searchDocID = it.last().docId
                }
                offset = 20

            } else {
            }
        } catch (e: Exception) {
        }

    }

    fun getNewTextSearchResult() = viewModelScope.launch {
        if (!isLoadEnd) {
            val tempData = searchResult.value?.toMutableList() ?: mutableListOf()
            val response = networkRepository.getTextSearchData(lastQuery, offset)
            try {
                if (response.isSuccessful) {
                    for (data in response.body()!!.data) {
                        tempData.add(data)
                    }
                    response.body()!!.data.let {
                        searchCursor = it.last().createdAt
                        searchDocID = it.last().docId
                    }
                    _searchResult.value = tempData
                    offset += 20

                } else {
                }
            } catch (e: Exception) {
                isLoadEnd = true
            }
        }
    }

    fun getNewEmbeddingSearch() = viewModelScope.launch {
        isEmbeddingLoading.value = true
        var tempData = searchResult.value?.toMutableList() ?: mutableListOf()
        val response = networkRepository.getEmbeddingData(lastQuery)
        try {
            if (response.isSuccessful) {
                for (data in response.body()!!.data) {
                    tempData.add(data)
                }
                embeddingOffset = embeddingOffset?.plus(20)

            } else {
            }
        } catch (e: Exception) {
            isLoadEnd = true
        }

        _searchResult.value = tempData
        isEmbeddingLoading.value = false
    }

    fun resetSearchData() {
        _searchResult.value = listOf()
        isLoadEnd = false
        searchCursor = null
        searchDocID = null
        embeddingOffset = null
    }

    fun resetScrollData() {
        isLoadEnd = false
        searchCursor = null
        searchDocID = null
        offset = 0
        embeddingOffset = null
    }

    fun saveSearchHistory(query: String) = viewModelScope.launch {
        searchHistoryRepository.saveSearchHistory(query)
    }

    fun getSearchHistory() = viewModelScope.launch {
        _searchHistory.value = searchHistoryRepository.fetchSearchHistory()
    }

    fun deleteSearchHistory(query: String) = viewModelScope.launch {
        searchHistoryRepository.deleteSearchQuery(query)
        getSearchHistory()
    }
}

class SearchViewModelFactory(private val searchHistoryRepository: SearchHistoryRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(searchHistoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}