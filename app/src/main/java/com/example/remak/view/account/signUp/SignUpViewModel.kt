package com.example.remak.view.account.signUp

import android.app.Application
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remak.network.model.SignInData
import com.example.remak.network.model.SignUpData
import com.example.remak.repository.NetworkRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.math.log

class SignUpViewModel : ViewModel() {

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
            Log.d("test", response.toString())
            Log.d("test", response.body().toString())

            if (response.isSuccessful) {
                //response내용을 각각 log로 출력
                Log.d("success", response.body().toString())
                _verifyCodeResult.value = true

            } else {
                _verifyCodeResult.value = false
                Log.d("fail", Gson().fromJson(response.errorBody()?.charStream(), SignUpData.GetVerifyResponseBody::class.java).message)

            }
        } catch (e : Exception) {
            _verifyCodeResult.value = false
            Log.d("networkError", "Exception: ", e)
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
                Log.d("success", response.body().toString())
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