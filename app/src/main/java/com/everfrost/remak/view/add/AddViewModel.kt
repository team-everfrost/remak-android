package com.everfrost.remak.view.add

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.everfrost.remak.dataStore.TokenRepository
import com.everfrost.remak.repository.NetworkRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class AddViewModel(private val tokenRepository: TokenRepository) : ViewModel() {
    private val networkRepository = NetworkRepository()

    private val _isMemoComplete = MutableLiveData<Boolean>()
    val isMemoComplete: LiveData<Boolean> = _isMemoComplete

    private val _uploadState = MutableLiveData<UploadState>()
    val uploadState: LiveData<UploadState> = _uploadState

    private val _isActionComplete = MutableLiveData<Boolean>()
    val isActionComplete: LiveData<Boolean> = _isActionComplete

    private val _isFileTooLarge = MutableLiveData<Boolean>()
    val isFileTooLarge: LiveData<Boolean> = _isFileTooLarge

    fun createWebPage(url: String) = viewModelScope.launch {
        try {
            val response = networkRepository.createWebPage(url)
            _isActionComplete.value = response.isSuccessful

        } catch (e: Exception) {
        }
    }

    /** 파일 업로드 */
    fun uploadFile(files: List<MultipartBody.Part>) = viewModelScope.launch {
        _uploadState.value = UploadState.LOADING
        Log.d("AddViewModel", "uploadFile: $files")
        try {
            val response = networkRepository.uploadFile(files)
            if (response.isSuccessful) {
                _isActionComplete.value = true
                _uploadState.value = UploadState.SUCCESS
            } else {
                _uploadState.value = UploadState.FAIL
                Log.d("AddViewModel", "fail uploadFile: ${response.code()}")
                if (response.code() == 413) _isFileTooLarge.value = true
            }
        } catch (e: Exception) {
            Log.d("AddViewModel", "exception uploadFile: $e")
        }
    }

    fun addMemo(content: String) = viewModelScope.launch {
        val response = networkRepository.createMemo(content)
        try {
            _isMemoComplete.value = response.isSuccessful
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