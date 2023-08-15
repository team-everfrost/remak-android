package com.example.remak

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat

object UtilityLogin {
    fun isEmailValid(email: String): Boolean { //이메일 형식에 맞는지 확인
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPasswordValid(password: String): Boolean { //비밀번호 형식에 맞는지 확인
        val passwordRegex = Regex("^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9]{8,}\$")
        return passwordRegex.matches(password)
    }

    //이메일 형식에 맞는지 확인하여 버튼 활성화
    fun emailCheck(context: Context, email: AppCompatEditText, btn: AppCompatButton, emailErrorMessage: TextView) {
        email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (isEmailValid(email.text.toString())) {
                    btn.isEnabled = true
                    val drawable =
                        ContextCompat.getDrawable(context, R.drawable.custom_ripple_effect_blue_rec)
                    email.background =
                        ContextCompat.getDrawable(context, R.drawable.edit_text_round)
                    btn.setTextColor(ContextCompat.getColor(context, R.color.white))
                    btn.background = drawable
                    emailErrorMessage.visibility = View.INVISIBLE
                } else {
                    email.background =
                        ContextCompat.getDrawable(context, R.drawable.edit_text_round_red)
                    btn.isEnabled = false
                    btn.background = ContextCompat.getDrawable(context, R.drawable.custom_ripple_effect)
                    btn.setTextColor(ContextCompat.getColor(context, R.color.whiteGray))
                    emailErrorMessage.visibility = View.VISIBLE
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    //두개의 비밀번호 같은지 확인
    fun passwordCheck(
        context: Context,
        password: AppCompatEditText,
        passwordRepeat: AppCompatEditText,
        btn: AppCompatButton
    ) {
        password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                btn.isEnabled = isPasswordValid(password.text.toString()) && password.text.toString() == passwordRepeat.text.toString()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        passwordRepeat.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (isPasswordValid(passwordRepeat.text.toString()) && password.text.toString() == passwordRepeat.text.toString()) {
                    val drawable =
                        ContextCompat.getDrawable(context, R.drawable.custom_ripple_effect)
                    passwordRepeat.background =
                        ContextCompat.getDrawable(context, R.drawable.edit_text_round)
                    btn.background = drawable
                    btn.isEnabled = true
                } else {
                    passwordRepeat.background =
                        ContextCompat.getDrawable(context, R.drawable.edit_text_round_red)
                    btn.isEnabled = false
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    fun signInCheck(context: Context, email: AppCompatEditText, password: AppCompatEditText, btn: AppCompatButton) {
        //이메일과 패스워드중 하나라도 비어있으면 버튼 비활성화 또한 이메일 형식에 맞지않으면 버튼 비활성화
        email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (email.text.toString().isNotEmpty() && password.text.toString()
                        .isNotEmpty() && isEmailValid(email.text.toString())
                ) {
                    btn.isEnabled = true
                    val drawable =
                        ContextCompat.getDrawable(context, R.drawable.custom_ripple_effect)
                    btn.background = drawable
                } else {
                    btn.isEnabled = false
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        //패스워드 입력도 추적
        password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (email.text.toString().isNotEmpty() && password.text.toString()
                        .isNotEmpty() && isEmailValid(email.text.toString())
                ) {
                    btn.isEnabled = true
                    val drawable =
                        ContextCompat.getDrawable(context, R.drawable.custom_ripple_effect)
                    btn.background = drawable
                } else {
                    btn.isEnabled = false
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}