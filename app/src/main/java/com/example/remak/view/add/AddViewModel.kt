package com.example.remak.view.add

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

    private val _isUploadComplete = MutableLiveData<Boolean>()
    val isUploadComplete: LiveData<Boolean> = _isUploadComplete

    private val _uploadState = MutableLiveData<UploadState>()
    val uploadState: LiveData<UploadState> = _uploadState

    private val _isActionComplete = MutableLiveData<Boolean>()
    val isActionComplete: LiveData<Boolean> = _isActionComplete

    fun createWebPage(url: String) = viewModelScope.launch {
        try {
            val response = networkRepository.createWebPage(url)
            if (response.isSuccessful) {
            } else {
            }
            _isActionComplete.value = true

        } catch (e: Exception) {
        }
    }

    /** 파일 업로드 */
    fun uploadFile(files: List<MultipartBody.Part>) = viewModelScope.launch {
        _uploadState.value = UploadState.LOADING
        try {
            val response = networkRepository.uploadFile(files)
            if (response.isSuccessful) {
                _uploadState.value = UploadState.SUCCESS
            } else {
            }
        } catch (e: Exception) {
        }
    }

    fun addMemo(content: String) = viewModelScope.launch {
        val response = networkRepository.createMemo(content)
        try {
            _isUploadComplete.value = response.isSuccessful
        } catch (e: Exception) {
        }
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