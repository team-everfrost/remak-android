package com.example.remak

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {


    fun hideKeyboard() {
        if (activity != null && requireActivity().currentFocus != null) {
            val inputManager: InputMethodManager =
                requireActivity().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(requireActivity().currentFocus!!.windowToken, 0)
        }
    }

    //이메일 유효성 체크
    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    }

    fun emailCheck(email: AppCompatEditText, btn: AppCompatButton, emailErrorMessage: TextView) {
        email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (isEmailValid(email.text.toString())) {
                    btn.isEnabled = true
                    val drawable =
                        ContextCompat.getDrawable(requireContext(), R.drawable.custom_ripple_effect)
                    email.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.edit_text_round)
                    btn.background = drawable
                    emailErrorMessage.visibility = View.INVISIBLE
                } else {
                    email.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.edit_text_round_red)
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
    fun isPasswordValid(password: String): Boolean {
        val passwordRegex = Regex("^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9]{8,}\$")
        return passwordRegex.matches(password)
    }


    //두개의 비밀번호 같은지 확인
    fun passwordCheck(
        password: AppCompatEditText,
        passwordRepeat: AppCompatEditText,
        btn: AppCompatButton
    ) {

        password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (isPasswordValid(password.text.toString()) && password.text.toString() == passwordRepeat.text.toString()) {
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
                if (isPasswordValid(passwordRepeat.text.toString()) && password.text.toString() == passwordRepeat.text.toString()) {
                    val drawable =
                        ContextCompat.getDrawable(requireContext(), R.drawable.custom_ripple_effect)
                    passwordRepeat.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.edit_text_round)
                    btn.background = drawable
                    btn.isEnabled = true
                } else {
                    passwordRepeat.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.edit_text_round_red)
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

    fun signInCheck(email: AppCompatEditText, password: AppCompatEditText, btn: AppCompatButton) {
        //이메일과 패스워드중 하나라도 비어있으면 버튼 비활성화 또한 이메일 형식에 맞지않으면 버튼 비활성화
        email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (email.text.toString().isNotEmpty() && password.text.toString()
                        .isNotEmpty() && isEmailValid(email.text.toString())
                ) {
                    btn.isEnabled = true
                    val drawable =
                        ContextCompat.getDrawable(requireContext(), R.drawable.custom_ripple_effect)
                    btn.background = drawable
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
        //패스워드 입력도 추적
        password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (email.text.toString().isNotEmpty() && password.text.toString()
                        .isNotEmpty() && isEmailValid(email.text.toString())
                ) {
                    btn.isEnabled = true
                    val drawable =
                        ContextCompat.getDrawable(requireContext(), R.drawable.custom_ripple_effect)
                    btn.background = drawable
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

    }


    //안내창 다이얼로그 생성
    fun showInformDialog(msg: String) {
        val dialog = Dialog(requireContext())
        val windowManager =
            requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog_information)

        if (Build.VERSION.SDK_INT < 30) {
            val display = windowManager.defaultDisplay
            val size = Point()

            display.getSize(size)

            val window = dialog.window
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val x = (size.x * 0.7).toInt()


            window?.setLayout(x, WindowManager.LayoutParams.WRAP_CONTENT)

        } else {
            val rect = windowManager.currentWindowMetrics.bounds

            val window = dialog.window
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val x = (rect.width() * 0.7).toInt()
            window?.setLayout(x, WindowManager.LayoutParams.WRAP_CONTENT)

        }

        val confirmBtn = dialog.findViewById<View>(R.id.confirmBtn)
        confirmBtn.setOnClickListener {
            dialog.dismiss()
        }
        val msgText = dialog.findViewById<TextView>(R.id.msgTextView)
        msgText.text = msg
        dialog.show()


    }


}