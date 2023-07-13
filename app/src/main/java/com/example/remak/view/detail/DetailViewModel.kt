package com.example.remak.view.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.remak.dataStore.TokenRepository
import com.example.remak.network.model.DetailData
import com.example.remak.repository.NetworkRepository
import kotlinx.coroutines.launch

class DetailViewModel(private val tokenRepository: TokenRepository) : ViewModel() {

    private val networkRepository = NetworkRepository()

    private val _detailData = MutableLiveData<DetailData.Data>()
    val detailData : LiveData<DetailData.Data> = _detailData

   fun deleteDocument(docId : String) = viewModelScope.launch {
        try {
            val response = networkRepository.deleteDocument(docId)
            if (response.isSuccessful) {
                Log.d("success", response.body().toString())
            } else {
                Log.d("fail", response.errorBody().toString())
            }
        } catch (e : Exception) {
            Log.d("networkError", e.toString())
        }
    }


    fun getDetailData(docId : String) = viewModelScope.launch {
        try {
            val response = networkRepository.getDetailData(docId)
            if (response.isSuccessful) {
                _detailData.value = response.body()!!.data
                Log.d("success", response.body().toString())
            } else {
                Log.d("fail", response.errorBody().toString())
            }
        } catch (e : Exception) {
            Log.d("networkError", e.toString())
        }
    }

    fun updateMemo(docId : String, content : String) = viewModelScope.launch {
        try {
            val response = networkRepository.updateMemo(docId, content)
            if (response.isSuccessful) {
                Log.d("success", response.body().toString())
            } else {
                Log.d("fail", response.errorBody().toString())
            }
        } catch (e : Exception) {
            Log.d("networkError", e.toString())
        }
    }



}

class DetailViewModelFactory(private val tokenRepository: TokenRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(tokenRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}