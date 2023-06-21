package com.example.remak

import android.app.Application
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignUpViewModel : ViewModel() {
    private val _userEmail = MutableLiveData<String>()
    val userEmail : LiveData<String> = _userEmail


    init {
        resetData()
    }



    private fun resetData() {
        _userEmail.value = ""
    }


}