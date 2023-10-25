package com.everfrost.remak

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.content.ContextCompat

object UtilityLogin {
    fun isEmailValid(email: String): Boolean { //이메일 형식에 맞는지 확인
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPasswordValid(password: String): Boolean { //비밀번호 형식에 맞는지 확인
        val passwordRegex =
            Regex("^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9!@#$%^&*()-_=+\\[\\]{}|;:'\",.<>?/]{9,}\$")//

        return passwordRegex.matches(password)
    }

    //이메일 형식에 맞는지 확인하여 버튼 활성화
    fun emailCheck(
        context: Context,
        email: AppCompatEditText,
        btn: AppCompatButton,
        emailErrorMessage: TextView
    ) {
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
                    btn.background =
                        ContextCompat.getDrawable(context, R.drawable.custom_ripple_effect)
                    btn.setTextColor(ContextCompat.getColor(context, R.color.whiteGray))
                    emailErrorMessage.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    fun passwordLengthCheck(
        context: Context,
        password: String,
        icon: ImageFilterView
    ) {
        if (password.length >= 9) {
            icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.checkbox_checked))
        } else {
            icon.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.add_collection_uncheck
                )
            )
        }
    }

    fun passwordNumberCheck(
        context: Context,
        password: String,
        icon: ImageFilterView
    ) {
        //숫자가 포함되어 있는지 확인
        if (('0'..'9').any { password.contains(it) }) {
            icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.checkbox_checked))
        } else {
            icon.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.add_collection_uncheck
                )
            )
        }
    }

    fun passwordEnglishCheck(
        context: Context,
        password: String,
        icon: ImageFilterView
    ) {
        if (password.matches(".*[a-zA-Z].*".toRegex())) {
            icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.checkbox_checked))
        } else {
            icon.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.add_collection_uncheck
                )
            )
        }
    }

}