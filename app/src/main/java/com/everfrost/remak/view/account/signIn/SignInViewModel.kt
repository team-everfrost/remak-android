package com.everfrost.remak.view.account.signIn

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.everfrost.remak.dataStore.TokenRepository
import com.everfrost.remak.model.TokenData
import com.everfrost.remak.repository.NetworkRepository
import kotlinx.coroutines.launch

class SignInViewModel(private val signInRepository: TokenRepository) : ViewModel() {

    private val networkRepository = NetworkRepository()
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult
    private val _isEmailValid = MutableLiveData<Boolean?>()
    val isEmailValid: LiveData<Boolean?> = _isEmailValid
    private val _isNetworkError = MutableLiveData<Boolean>()
    val isNetworkError: LiveData<Boolean> = _isNetworkError

    private val _isResetEmailValid = MutableLiveData<Boolean>()
    val isResetEmailValid: LiveData<Boolean> = _isResetEmailValid

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> = _userEmail

    private val _isVerifyCodeValid = MutableLiveData<Boolean?>(null)
    val isVerifyCodeValid: LiveData<Boolean?> = _isVerifyCodeValid

    private val _userNewPassword = MutableLiveData<String>()
    val userNewPassword: LiveData<String> = _userNewPassword

    private val _isResetPasswordSuccess = MutableLiveData<Boolean>()
    val isResetPasswordSuccess: LiveData<Boolean> = _isResetPasswordSuccess

    private val _isWithdrawCodeValid = MutableLiveData<Boolean?>(null)
    val isWithdrawCodeValid: LiveData<Boolean?> = _isWithdrawCodeValid

    private val _isWithdrawSuccess = MutableLiveData<Boolean>()
    val isWithdrawSuccess: LiveData<Boolean> = _isWithdrawSuccess

    fun networkErrorHandled() {
        _isNetworkError.value = false
    }

    fun checkEmail(email: String) = viewModelScope.launch {
        Log.d("이메일 중복 확인", email)
        try {
            val response = networkRepository.checkEmail(email)
            if (response.isSuccessful) {
                Log.d("이메일 중복 확인", "성공")
            } else {
                Log.d("이메일 중복 확인", "실패")
            }
            _isEmailValid.value = response.isSuccessful
        } catch (e: Exception) {
            Log.d("이메일 중복 확인", e.toString())
        }
    }

    //이메일 로그인 로직
    fun emailLogin(email: String, password: String) = viewModelScope.launch {
        try {
            val response = networkRepository.signIn(email, password)
            if (response.isSuccessful) {
                val token = TokenData(response.body()?.data!!.accessToken)
                signInRepository.saveUser(token)
                _loginResult.value = true
                Log.d("로그인", "성공")
            } else {
                _loginResult.value = false
                Log.d(response.code().toString(), response.message())
            }
        } catch (e: Exception) {
            _isNetworkError.value = true
            Log.d("로그인", e.toString())
        }
    }

    fun getResetPasswordCode(email: String) = viewModelScope.launch {
        try {
            val response = networkRepository.resetPasswordCode(email)
            if (response.isSuccessful) {
                _isResetEmailValid.value = true
                _userEmail.value = email
            } else {
                if (response.code() == 404) {
                    _isResetEmailValid.value = false
                }
            }
        } catch (e: Exception) {
            _isNetworkError.value = true
        }
    }


    fun checkVerifyResetCode(code: String) = viewModelScope.launch {
        try {
            Log.d("코드", code)
            Log.d("이메일", userEmail.value!!)
            val response = networkRepository.checkVerifyResetCode(code, userEmail.value!!)
            _isVerifyCodeValid.value = response.isSuccessful
            if (response.isSuccessful) {
                Log.d("코드", "성공")
            } else {
                Log.d("코드", "실패")
            }
        } catch (e: Exception) {
            _isNetworkError.value = true
        }
    }

    fun resetPassword() = viewModelScope.launch {
        try {
            val response =
                networkRepository.resetPassword(userEmail.value!!, userNewPassword.value!!)
            _isResetPasswordSuccess.value = response.isSuccessful
        } catch (e: Exception) {
            _isNetworkError.value = true
        }
    }

    fun getWithdrawVerifyCode() = viewModelScope.launch {
        try {
            val response = networkRepository.withdrawCode()
        } catch (e: Exception) {
            _isNetworkError.value = true
        }
    }

    fun verifyWithdrawCode(code: String) = viewModelScope.launch {
        try {
            val response = networkRepository.verifyWithdrawCode(code)
            _isWithdrawCodeValid.value = response.isSuccessful
        } catch (e: Exception) {
            _isNetworkError.value = true
        }
    }

    fun withdraw() = viewModelScope.launch {
        try {
            val response = networkRepository.withdraw()
            _isWithdrawSuccess.value = response.isSuccessful
        } catch (e: Exception) {
            _isNetworkError.value = true
        }
    }

    fun resetEmailValid() {
        _isResetEmailValid.value = false
    }

    fun resetIsEmailValid() {
        _isEmailValid.value = null
    }

    fun resetVerifyCodeResult() {
        _isVerifyCodeValid.value = null
    }

    fun resetWithdrawCodeResult() {
        _isWithdrawCodeValid.value = null
    }

    fun setUserNewPassword(password: String) {
        _userNewPassword.value = password
        Log.d("비밀번호", userNewPassword.value.toString())
    }

    fun resetAllValue() {
        _isEmailValid.value = false
        _isResetEmailValid.value = false
        _isVerifyCodeValid.value = false
        _isResetPasswordSuccess.value = false
        _isNetworkError.value = false
    }
}

class SignInViewModelFactory(private val signInRepository: TokenRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignInViewModel(signInRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}