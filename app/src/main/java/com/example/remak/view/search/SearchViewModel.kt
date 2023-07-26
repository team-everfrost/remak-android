package com.example.remak.view.search

import android.app.appsearch.SearchResult
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.remak.dataStore.TokenRepository
import com.example.remak.network.model.SearchEmbeddingData
import com.example.remak.repository.NetworkRepository
import com.example.remak.view.detail.DetailViewModel
import kotlinx.coroutines.launch

class SearchViewModel(private val tokenRepository: TokenRepository) : ViewModel() {
    private val networkRepository = NetworkRepository()

    private val _searchResult = MutableLiveData<List<SearchEmbeddingData.Data>>()
    val searchResult : LiveData<List<SearchEmbeddingData.Data>> = _searchResult

    fun getSearchResult(query : String) = viewModelScope.launch {
        val response = networkRepository.getEmbeddingData(query)
        try {
            if (response.isSuccessful) {
                _searchResult.value = response.body()!!.data
                Log.d("search result", _searchResult.value.toString())
            } else {
                println(response.errorBody())
            }
        } catch (e : Exception) {
            println(e)
        }
    }


}

class SearchViewModelFactory(private val tokenRepository: TokenRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(tokenRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}