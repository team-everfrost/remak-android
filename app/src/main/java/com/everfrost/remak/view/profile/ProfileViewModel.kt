package com.everfrost.remak.view.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.everfrost.remak.dataStore.TokenRepository
import com.everfrost.remak.network.model.UserData
import com.everfrost.remak.repository.NetworkRepository
import kotlinx.coroutines.launch
import kotlin.math.round

class ProfileViewModel(private val tokenRepository: TokenRepository) : ViewModel() {
    private val networkRepository = NetworkRepository()
    private val _userData = MutableLiveData<UserData.Data>()
    val userData: LiveData<UserData.Data> = _userData
    private val _storageSize = MutableLiveData<Int>()
    val storageSize: LiveData<Int> = _storageSize
    private val _usageSize = MutableLiveData<Double>()
    val usageSize: LiveData<Double> = _usageSize
    private val _usagePercent = MutableLiveData<Int>()
    val usagePercent: LiveData<Int> = _usagePercent

    fun getUserData() = viewModelScope.launch {
        val response = networkRepository.getUserData()
        if (response.isSuccessful) {
            _userData.value = response.body()?.data
        }
    }

    fun getStorageSize() = viewModelScope.launch {
        val response = networkRepository.getStorageSize()
        if (response.isSuccessful) {
            val storageBytesSize = response.body()?.data
            val gbSize = storageBytesSize?.div(1024)?.div(1024)?.div(1024)
            _storageSize.value = gbSize!!
            getUsageSize(storageBytesSize)
        }
    }

    private fun getUsageSize(storageBytesSize: Int) = viewModelScope.launch {
        val response = networkRepository.getStorageUsage()
        if (response.isSuccessful) {
            val usageByte = response.body()?.data
            //useageByte를 소수점 1자리수 GB단위로 변경
            _usageSize.value =
                round((usageByte?.toDouble()!!.div(1024).div(1024).div(1024)) * 100) / 100

            //소수점 한자리까지 계산
            _usagePercent.value =
                round(usageByte.toDouble() / storageBytesSize.toDouble() * 100).toInt()


        }
    }


}

class ProfileViewModelFactory(private val tokenRepository: TokenRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(tokenRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}