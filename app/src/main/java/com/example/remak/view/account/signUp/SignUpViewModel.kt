package com.example.remak.view.account.signUp

import android.app.Application
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remak.repository.NetworkRepository
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

    fun getVerifyCode(email : String) = viewModelScope.launch {
        try {
            val response = networkRepository.getVerifyCode(email)
            if (response.isSuccessful) {
                _verifyCodeResult.value = true
            } else {
                _verifyCodeResult.value = false
            }
        } catch (e : Exception) {
            _verifyCodeResult.value = false
        }


    }



    private fun resetData() {
        _userEmail.value = ""
        _userPassword.value = ""
    }

    fun setUserEmail(email : String) {
        _userEmail.value = email
    }

    fun setUserPassword(password : String) {
        _userPassword.value = password
    }


}