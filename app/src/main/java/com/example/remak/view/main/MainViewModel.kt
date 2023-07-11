package com.example.remak.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remak.network.model.MainListData
import com.example.remak.repository.NetworkRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val networkRepository = NetworkRepository()

    //_mainListData는 mutablelivedata형식에 MainListData.Data list를 가지고있다.
    private val _mainListData = MutableLiveData<List<MainListData.Data>>()
    val mainListData : LiveData<List<MainListData.Data>> = _mainListData

    var cursor : String? = null
    var docID : String? = null


    fun getAllMainList() = viewModelScope.launch {
        try {
            val response = networkRepository.getMainList(null, null)
            if (response.isSuccessful) {
                _mainListData.value = response.body()?.data

                response.body()?.data?.let {it ->
                    cursor = it.last().createdAt
                    docID = it.last().docId
                }

                Log.d("success", response.body().toString())
            } else {
                Log.d("fail", response.errorBody().toString())
            }
        } catch (e : Exception) {
            Log.d("networkError", e.toString())
        }

        Log.d("cursor", cursor.toString())
        Log.d("docID", docID.toString())
        Log.d("mainListData", mainListData.value.toString())
    }

    fun getNewMainList() = viewModelScope.launch {
        try {
            val response = networkRepository.getMainList(cursor, docID)
            if (response.isSuccessful) {
                _mainListData.value = response.body()?.data

                response.body()?.data?.let {it ->
                    cursor = it.last().createdAt
                    docID = it.last().docId
                }

                Log.d("success", response.body().toString())
            } else {
                Log.d("fail", response.errorBody().toString())
            }
        } catch (e : Exception) {
            Log.d("networkError", e.toString())
        }

        Log.d("cursor", cursor.toString())
        Log.d("docID", docID.toString())
        Log.d("mainListData", mainListData.value.toString())
    }


    fun createMemo(content : String) = viewModelScope.launch {
        try {
            val response = networkRepository.createMemo(content)
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