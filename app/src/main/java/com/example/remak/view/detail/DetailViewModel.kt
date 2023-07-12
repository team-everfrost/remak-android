package com.example.remak.view.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remak.network.model.DetailData
import com.example.remak.repository.NetworkRepository
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {

    private val networkRepository = NetworkRepository()

    private val _detailData = MutableLiveData<DetailData.Data>()
    val detailData : LiveData<DetailData.Data> = _detailData


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



}