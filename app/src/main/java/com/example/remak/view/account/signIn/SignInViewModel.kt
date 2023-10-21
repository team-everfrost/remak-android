package com.example.remak.view.account.signIn

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.remak.dataStore.TokenRepository
import com.example.remak.model.TokenData
import com.example.remak.repository.NetworkRepository
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch

class SignInViewModel(private val signInRepository: TokenRepository) : ViewModel() {

    private val networkRepository = NetworkRepository()
    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean> = _loginResult
    private val _isEmailValid = MutableLiveData<Boolean>()
    val isEmailValid: LiveData<Boolean> = _isEmailValid
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

    fun networkErrorHandled() {
        _isNetworkError.value = false
    }

    fun checkEmail(email: String) = viewModelScope.launch {
        try {
            val response = networkRepository.checkEmail(email)
            if (response.isSuccessful) {
            } else {
            }
            _isEmailValid.value = response.isSuccessful
        } catch (e: Exception) {
            _isNetworkError.value = true
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


    fun kakaoLogin(context: Activity) {
        // 카카오계정으로 로그인 공통 callback 구성
        // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("kakao", "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                Log.i("kakao", "카카오계정으로 로그인 성공 ${token.accessToken}")
            }
        }

        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    Log.e("kakao", "카카오톡으로 로그인 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
                } else if (token != null) {
                    Log.i("kakao", "카카오톡으로 로그인 성공 ${token.accessToken}")
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
            Log.d("kakao", "설치X")
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

    fun resetEmailValid() {
        _isResetEmailValid.value = false
    }

    fun resetVerifyCodeResult() {
        _isVerifyCodeValid.value = null
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