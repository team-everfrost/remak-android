package com.everfrost.remak.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.everfrost.remak.dataStore.TokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(private val signInRepository: TokenRepository) :
    ViewModel() {
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
