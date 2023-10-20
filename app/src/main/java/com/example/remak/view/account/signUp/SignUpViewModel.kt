package com.example.remak.view.account.signUp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.remak.dataStore.TokenRepository
import com.example.remak.model.TokenData
import com.example.remak.repository.NetworkRepository
import kotlinx.coroutines.launch

class SignUpViewModel(private val tokenRepository: TokenRepository) : ViewModel() {

    private val networkRepository = NetworkRepository()
    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> = _userEmail

    private val _userPassword = MutableLiveData<String>()
    val userPassword: LiveData<String> = _userPassword

    private val _isSignInSuccess = MutableLiveData<Boolean>()
    val isSignInSuccess: LiveData<Boolean> = _isSignInSuccess

    private val _isEmailExist = MutableLiveData<Boolean>()
    val isEmailExist: LiveData<Boolean> = _isEmailExist

    private val _isEmailInvalid = MutableLiveData<Boolean>()
    val isEmailInvalid: LiveData<Boolean> = _isEmailInvalid

    private val _isVerifyCodeValid = MutableLiveData<Boolean?>(null)
    val isVerifyCodeValid: LiveData<Boolean?> = _isVerifyCodeValid

    init {
        resetData()
    }

    fun resetIsEmailExit() {
        _isEmailExist.value = false
    }

    //이메일 전송 후 확인코드 받는 로직
    fun getVerifyCode(email: String) = viewModelScope.launch {
        try {
            val response = networkRepository.getVerifyCode(email)
            if (response.isSuccessful) {
                _userEmail.value = email
                _isSignInSuccess.value = true
            } else {
                _isSignInSuccess.value = false

                if (response.code() == 400) { //이메일 형식이 잘못되었을 때
                    _isEmailInvalid.value = true
                } else if (response.code() == 409) { //이미 존재하는 이메일일 때
                    _isEmailExist.value = true
                }

            }
        } catch (e: Exception) {
            _isSignInSuccess.value = false
            e.printStackTrace()
        }
    }

    fun doneVerifyCodeResult() {
        _isSignInSuccess.value = false
    }

    fun doneEmailCheck() {
        _isEmailExist.value = false
        _isEmailInvalid.value = false
    }

    //확인코드 입력 후 검증받는 로직
    fun checkVerifyCode(signupCode: String) = viewModelScope.launch {
        try {
            val response =
                networkRepository.checkVerifyCode(signupCode, _userEmail.value.toString())

            _isVerifyCodeValid.value = response.isSuccessful
        } catch (e: Exception) {
            _isVerifyCodeValid.value = false
            e.printStackTrace()
        }
    }

    //비밀번호 입력 후 토큰 얻고 회원가입 완료하는 로직
    fun signup(email: String, password: String) = viewModelScope.launch {
        try {
            val response = networkRepository.signUp(email, password)
            if (response.isSuccessful) {
                Log.d("token", response.body()?.data!!.accessToken)
                //토큰 저장
                val token = TokenData(response.body()?.data!!.accessToken)
                tokenRepository.saveUser(token)
                //확인값 true로 변경
                _isSignInSuccess.value = true

            } else {
                Log.d("fail", response.errorBody()!!.string())
                _isSignInSuccess.value = false
            }
        } catch (e: Exception) {
            Log.d("fail", e.toString())
            _isSignInSuccess.value = false
            e.printStackTrace()
        }
    }

    private fun resetData() {
        _userEmail.value = ""
        _userPassword.value = ""
        _isSignInSuccess.value = false
    }

    fun resetVerifyCodeResult() {
        _isVerifyCodeValid.value = null
    }

    fun setUserEmail(email: String) {
        _userEmail.value = email
    }

    fun setUserPassword(password: String) {
        _userPassword.value = password
    }

}

class SignUpViewModelFactory(private val signInRepository: TokenRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignUpViewModel(signInRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}