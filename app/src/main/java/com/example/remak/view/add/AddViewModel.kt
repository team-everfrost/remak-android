package com.example.remak.view.add

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.remak.dataStore.TokenRepository
import com.example.remak.repository.NetworkRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class AddViewModel(private val tokenRepository: TokenRepository) : ViewModel() {
    private val networkRepository = NetworkRepository()

    private val _uploadFileSuccess = MutableLiveData<Boolean>()
    val uploadFileSuccess: LiveData<Boolean> = _uploadFileSuccess

    private val _uploadState = MutableLiveData<UploadState>()
    val uploadState: LiveData<UploadState> = _uploadState

    fun createWebPage(url: String) = viewModelScope.launch {
        try {
            val response = networkRepository.createWebPage(url)
            if (response.isSuccessful) {
                Log.d("success", response.body().toString())

            } else {
                Log.d("fail", response.errorBody()?.string()!!)
            }
        } catch (e: Exception) {
            Log.d("networkError", e.toString())
        }
    }

    /** 파일 업로드 */
    fun uploadFile(files: List<MultipartBody.Part>) = viewModelScope.launch {
        _uploadState.value = UploadState.LOADING
        try {
            val response = networkRepository.uploadFile(files)
            Log.d("file", files.toString())
            if (response.isSuccessful) {
                Log.d("success", response.body().toString())
                _uploadFileSuccess.value = true
                _uploadState.value = UploadState.SUCCESS

            } else {
                Log.d("fail", response.errorBody()?.string()!!)
            }
        } catch (e: Exception) {
            Log.d("networkError", e.toString())
        }
    }

    fun resetUploadFileSuccess() {
        _uploadFileSuccess.value = false
    }

}

class AddViewModelFactory(private val tokenRepository: TokenRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddViewModel(tokenRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

enum class UploadState {
    LOADING, SUCCESS, FAIL
}