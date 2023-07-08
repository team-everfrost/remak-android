package com.example.remak.view.account.signUp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.remak.dataModel.TokenData
import com.example.remak.dataStore.TokenRepository
import com.example.remak.network.model.SignUpData
import com.example.remak.repository.NetworkRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.lang.Exception

class SignUpViewModel(private val signInRepository: TokenRepository) : ViewModel() {

    private val networkRepository = NetworkRepository()
    private val _userEmail = MutableLiveData<String>()
    val userEmail : LiveData<String> = _userEmail

    private val _userPassword = MutableLiveData<String>()
    val userPassword : LiveData<String> = _userPassword

    private val _verifyCodeResult = MutableLiveData<Boolean>()
    val verifyCodeResult : LiveData<Boolean> = _verifyCodeResult



    init {
        resetData()
    }


    //이메일 전송 후 확인코드 받는 로직
    fun getVerifyCode(email : String) = viewModelScope.launch {
        try {
            val response = networkRepository.getVerifyCode(email)
            _verifyCodeResult.value = response.isSuccessful
        } catch (e : Exception) {
            _verifyCodeResult.value = false
            e.printStackTrace()
        }
    }

    fun doneVerifyCodeResult() {
        _verifyCodeResult.value = false
    }

    //확인코드 입력 후 검증받는 로직
    fun checkVerifyCode (signupCode : String, email : String) = viewModelScope.launch {
        try {
            val response = networkRepository.checkVerifyCode(signupCode, email)

            if (response.isSuccessful) {
                //response내용을 각각 log로 출력
                _verifyCodeResult.value = true

            } else {
                _verifyCodeResult.value = false
                Log.d("fail", Gson().fromJson(response.errorBody()?.charStream(), SignUpData.CheckVerifyResponseBody::class.java).message)

            }
        } catch (e : Exception) {
            _verifyCodeResult.value = false
            Log.d("networkError", "Exception: ", e)
            e.printStackTrace()
        }
    }

    //비밀번호 입력 후 토큰 얻고 회원가입 완료하는 로직
    fun signup (email : String, password : String) = viewModelScope.launch {
        try {
            val response = networkRepository.signUp(email, password)

            if (response.isSuccessful) {
                Log.d("success", response.body().toString())


                Log.d("token", response.body()?.data!!.accessToken)
                //토큰 저장
                val token = TokenData(response.body()?.data!!.accessToken)
                signInRepository.saveUser(token)
                //확인값 true로 변경
                _verifyCodeResult.value = true

            } else {
                _verifyCodeResult.value = false
                Log.d("fail", Gson().fromJson(response.errorBody()?.charStream(), SignUpData.SignUpResponseBody::class.java).message)

            }
        } catch (e : Exception) {
            _verifyCodeResult.value = false
            Log.d("networkError", "Exception: ", e)
            e.printStackTrace()
        }
    }





    private fun resetData() {
        _userEmail.value = ""
        _userPassword.value = ""
        _verifyCodeResult.value = false
    }

    fun setUserEmail(email : String) {
        _userEmail.value = email
    }

    fun setUserPassword(password : String) {
        _userPassword.value = password
    }


}

class SignUpViewModelFactory(private val signInRepository: TokenRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignUpViewModel(signInRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}