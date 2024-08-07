package com.everfrost.remak.view.account.signIn

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.everfrost.remak.R
import com.everfrost.remak.UtilitySystem
import com.everfrost.remak.databinding.AccountResetPassword2FragmentBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint

class AccountResetPassword2Fragment : Fragment() {
    private lateinit var binding: AccountResetPassword2FragmentBinding
    private val viewModel: SignInViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = AccountResetPassword2FragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener {
            UtilitySystem.hideKeyboard(requireActivity())
        }

        //상태바 높이만큼 margin적용
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val statusBarHeight: Int = if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else {
            // 기본값 또는 예상되는 높이
            24 * resources.displayMetrics.density.toInt()
        }
        val layoutParams = binding.rootLayout.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(0, statusBarHeight, 0, 0)
        binding.rootLayout.layoutParams = layoutParams

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val verifyEditTexts = arrayOf(
            binding.verifyCodeEditText1,
            binding.verifyCodeEditText2,
            binding.verifyCodeEditText3,
            binding.verifyCodeEditText4,
            binding.verifyCodeEditText5,
            binding.verifyCodeEditText6
        )
        binding.emailVerifyCodeText.text =
            getString(R.string.verification_text, viewModel.userEmail.value)

        binding.verifyCodeEditText1.requestFocus()
        UtilitySystem.showKeyboard(requireActivity())

        verifyEditTexts.forEach { editText ->
            editText.setOnClickListener {
                for (i in verifyEditTexts.indices) {
                    if (verifyEditTexts[i].text.toString().isEmpty()) {
                        verifyEditTexts[i].isFocusableInTouchMode = true
                        verifyEditTexts[i].isFocusable = true
                        verifyEditTexts[i].requestFocus()
                        UtilitySystem.showKeyboard(requireActivity())
                        for (j in verifyEditTexts.indices) {
                            if (j != i) {
                                verifyEditTexts[j].isFocusableInTouchMode = false
                                verifyEditTexts[j].isFocusable = false
                            }
                        }
                        return@setOnClickListener
                    }
                }
            }
        }

        for (i in verifyEditTexts.indices) {
            verifyEditTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1 && i < verifyEditTexts.lastIndex) {
                        verifyEditTexts[i + 1].isFocusable = true
                        verifyEditTexts[i + 1].isFocusableInTouchMode = true
                        verifyEditTexts[i + 1].requestFocus()

                        for (j in verifyEditTexts.indices) {
                            if (j != i + 1) {
                                verifyEditTexts[j].isFocusableInTouchMode = false
                                verifyEditTexts[j].isFocusable = false
                            }
                        }

                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    if (verifyEditTexts.all { it.text.length == 1 }) {
                        val verifyCode = verifyEditTexts.joinToString("") { it.text.toString() }
                        viewModel.checkVerifyResetCode(verifyCode)
                    }
                }
            })
            verifyEditTexts[i].setOnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (i > 0 && verifyEditTexts[i].text.toString().isEmpty()) {
                        verifyEditTexts[i - 1].text.clear()
                        verifyEditTexts[i - 1].isFocusable = true
                        verifyEditTexts[i - 1].isFocusableInTouchMode = true
                        verifyEditTexts[i - 1].requestFocus()

                        for (j in verifyEditTexts.indices) {
                            if (j != i - 1) {
                                verifyEditTexts[j].isFocusableInTouchMode = false
                                verifyEditTexts[j].isFocusable = false
                            }
                        }
                        return@setOnKeyListener true
                    }
                }
                return@setOnKeyListener false
            }
        }
        //이메일 인증 성공 시 다음 화면으로 이동
        viewModel.isVerifyCodeValid.observe(viewLifecycleOwner) { isSuccessful ->
            if (isSuccessful == true) {
                findNavController().navigate(R.id.action_accountResetPassword2Fragment_to_accountResetPassword3Fragment)
                viewModel.resetVerifyCodeResult()
                verifyEditTexts.forEach { it.text.clear() }
            } else if (isSuccessful == false) {
                //Todo : 에러메시지 출력 및 edittext 색 변경
                binding.emailVerifyCodeText.text = "인증번호가 일치하지 않습니다\n다시확인해주세요"
                binding.emailVerifyCodeText.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red
                    )
                )
                verifyEditTexts.forEach {
                    it.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.edit_text_verify_code_error
                    )
                }

            }
        }
    }

}