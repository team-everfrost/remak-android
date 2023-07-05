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
import com.example.remak.repository.NetworkRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.lang.Exception

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
            Log.d("viewMeodel에선 response", response.toString())
            if (response.isSuccessful) {
                _verifyCodeResult.value = true
            Log.d("viewMeodel에선 success", response.body().toString())

            } else {
                _verifyCodeResult.value = false
                Log.d("fail", Gson().fromJson(response.errorBody()?.string(), SignInData.ErrorResponse::class.java).toString())

            }
        } catch (e : Exception) {
            _verifyCodeResult.value = false
            Log.d("networkError", "Exception: ", e)
            e.printStackTrace()
        }
    }

    //확인코드 입력 후 검증받는 로직





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