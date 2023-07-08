package com.example.remak.view.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remak.repository.NetworkRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val networkRepository = NetworkRepository()


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