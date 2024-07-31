package com.everfrost.remak.view.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.everfrost.remak.network.model.UserData
import com.everfrost.remak.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.BigInteger
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class ProfileViewModel @Inject constructor(private val networkRepository: NetworkRepository) :
    ViewModel() {
    private val _userData = MutableLiveData<UserData.Data>()
    val userData: LiveData<UserData.Data> = _userData
    private val _storageSize = MutableLiveData<BigInteger>()
    val storageSize: LiveData<BigInteger> = _storageSize
    private val _usageSize = MutableLiveData<Double>()
    val usageSize: LiveData<Double> = _usageSize
    private val _usagePercent = MutableLiveData<Int>()
    val usagePercent: LiveData<Int> = _usagePercent

    fun getStorageSize() = viewModelScope.launch {
        val response = networkRepository.getStorageSize()
        if (response.isSuccessful) {
            val storageBytesSize = response.body()?.data
            val gbSize = storageBytesSize?.divide(BigInteger.valueOf(1024))
                ?.divide(BigInteger.valueOf(1024))
                ?.divide(BigInteger.valueOf(1024))
            _storageSize.value = gbSize!!
            getUsageSize(storageBytesSize)
        }
    }

    private fun getUsageSize(storageBytesSize: BigInteger) = viewModelScope.launch {
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

