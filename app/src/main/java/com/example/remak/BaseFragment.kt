package com.example.remak

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
    var passwordValid = false
    var passwordRepeatValid = false

    fun hideKeyboard(){
        if (activity != null && requireActivity().currentFocus != null){
            val inputManager : InputMethodManager = requireActivity().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(requireActivity().currentFocus!!.windowToken, 0)
        }
    }

    //이메일 유효성 체크
    fun isEmailValid(email: String) : Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    }

    fun emailCheck(email: AppCompatEditText, btn: AppCompatButton, emailErrorMessage: TextView) {
        email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (isEmailValid(email.text.toString())){
                    btn.isEnabled = true
                    val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.custom_ripple_effect)
                    email.background = ContextCompat.getDrawable(requireContext(), R.drawable.edit_text_round)
                    btn.background = drawable
                    emailErrorMessage.visibility = View.INVISIBLE
                } else {
                    email.background = ContextCompat.getDrawable(requireContext(), R.drawable.edit_text_round_red)
                    btn.isEnabled = false
                    emailErrorMessage.visibility = View.VISIBLE

                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //do nothing
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //do nothing
            }
        })
    }

    //영문 숫자를 조합하여 8자리 이상을 확인하는 함수
    fun isPasswordValid(password : String) : Boolean {
        val passwordRegex = Regex("^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9]{8,}\$")
        return passwordRegex.matches(password)
    }


    //두개의 비밀번호 같은지 확인
    fun passwordCheck(password : AppCompatEditText, passwordRepeat : AppCompatEditText, btn : AppCompatButton) {

        password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (isPasswordValid(password.text.toString()) && password.text.toString() == passwordRepeat.text.toString()){
                    btn.isEnabled = true
                } else {
                    btn.isEnabled = false
                }

            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //do nothing
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //do nothing
            }

        })

        passwordRepeat.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (isPasswordValid(passwordRepeat.text.toString()) && password.text.toString() == passwordRepeat.text.toString()){
                    val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.custom_ripple_effect)
                    passwordRepeat.background = ContextCompat.getDrawable(requireContext(), R.drawable.edit_text_round)
                    btn.background = drawable
                    btn.isEnabled = true
                } else {
                    passwordRepeat.background = ContextCompat.getDrawable(requireContext(), R.drawable.edit_text_round_red)
                    btn.isEnabled = false
                }

            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //do nothing
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //do nothing
            }
        })



    }

    fun btnEnabled(btn: AppCompatButton) {
        btn.isEnabled = passwordValid && passwordRepeatValid
    }






}