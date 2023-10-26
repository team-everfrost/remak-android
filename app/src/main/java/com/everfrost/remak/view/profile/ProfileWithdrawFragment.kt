package com.everfrost.remak.view.profile

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.everfrost.remak.R
import com.everfrost.remak.UtilityDialog
import com.everfrost.remak.UtilitySystem
import com.everfrost.remak.dataStore.TokenRepository
import com.everfrost.remak.databinding.AccountWithdraw1FragmentBinding
import com.everfrost.remak.view.account.signIn.SignInViewModel
import com.everfrost.remak.view.account.signIn.SignInViewModelFactory
import com.everfrost.remak.view.main.MainViewModel
import com.everfrost.remak.view.main.MainViewModelFactory

class ProfileWithdrawFragment : Fragment() {
    private lateinit var binding: AccountWithdraw1FragmentBinding
    private val viewModel: SignInViewModel by activityViewModels {
        SignInViewModelFactory(
            tokenRepository
        )
    }
    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            tokenRepository
        )
    }
    lateinit var tokenRepository: TokenRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tokenRepository =
            TokenRepository((requireActivity().application as com.everfrost.remak.App).dataStore)
        binding = AccountWithdraw1FragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener {
            UtilitySystem.hideKeyboard(requireActivity())
        }
        viewModel.getWithdrawVerifyCode()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mainViewModel.userData.observe(viewLifecycleOwner) {
            binding.emailVerifyCodeText.text = getString(R.string.verification_text, it.email)
        }

        val verifyEditTexts = arrayOf(
            binding.verifyCodeEditText1,
            binding.verifyCodeEditText2,
            binding.verifyCodeEditText3,
            binding.verifyCodeEditText4,
            binding.verifyCodeEditText5,
            binding.verifyCodeEditText6
        )

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
                        viewModel.verifyWithdrawCode(verifyCode)
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

        viewModel.isWithdrawCodeValid.observe(viewLifecycleOwner) {
            if (it == true) {
                UtilityDialog.showWarnDialog(
                    requireContext(),
                    "정말로 탈퇴하시겠어요?",
                    "회원탈퇴를 하시면 데이터가 모두 삭제돼요\n" +
                            "또한, 모든 데이터는 복구가 불가능해요",
                    confirmBtnText = "네",
                    cancelBtnText = "아니오",
                    confirmClick = {
                        viewModel.withdraw()
                    },
                    cancelClick = {}
                )
            }
        }

        viewModel.isWithdrawSuccess.observe(viewLifecycleOwner) {
            if (it) {
                UtilityDialog.showInformDialog(
                    "탈퇴가 완료되었어요",
                    "그동안 이용해 주셔서 감사했어요",
                    requireContext(),
                    confirmClick = {
                        mainViewModel.deleteToken()
                        val intent = Intent(
                            requireContext(),
                            com.everfrost.remak.view.account.AccountActivity::class.java
                        )
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        requireActivity().finish()
                        startActivity(intent)
                    }
                )
            }
        }


    }
}