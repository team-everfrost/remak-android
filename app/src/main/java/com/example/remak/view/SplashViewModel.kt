package com.example.remak.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.remak.dataStore.TokenRepository
import kotlinx.coroutines.launch

class SplashViewModel(private val signInRepository: TokenRepository) : ViewModel() {

    private val _isReady = MutableLiveData<Boolean>()
    val isReady: LiveData<Boolean> get() = _isReady


    private val _isToken = MutableLiveData<Boolean>()
    val isToken: LiveData<Boolean> = _isToken

    init {
        checkToken()
        _isReady.postValue(false)

    }

    private fun checkToken() {
        viewModelScope.launch {
            _isToken.value = signInRepository.checkToken()
        }
    }

    suspend fun isTokenAvailable(): Boolean {
        val result = signInRepository.checkToken()
        // After checking the token, set the isReady to true
        _isReady.postValue(true)
        return result
    }

    fun returnIsReady(): Boolean {
        return _isReady.value ?: false
    }


}

class SplashViewModelFactory(private val signInRepository: TokenRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SplashViewModel(signInRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
