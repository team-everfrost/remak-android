package com.everfrost.remak.view.account.signIn

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.everfrost.remak.R
import com.everfrost.remak.UtilityDialog
import com.everfrost.remak.UtilityLogin
import com.everfrost.remak.UtilitySystem
import com.everfrost.remak.databinding.AccountEmailSignin1FragmentBinding
import com.everfrost.remak.view.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AccountEmailSignInFragment : Fragment() {
    private lateinit var binding: AccountEmailSignin1FragmentBinding
    private val viewModel: SignInViewModel by viewModels()
    private var isWritingEmail = true
    private var isWrongPassword = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = AccountEmailSignin1FragmentBinding.inflate(inflater, container, false)
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

        viewModel.isNetworkError.observe(viewLifecycleOwner) {
            if (it) {
                UtilityDialog.showInformDialog(
                    "네트워크 오류",
                    "네트워크 상태를 확인해주세요.",
                    requireContext(),
                    confirmClick = {
                        viewModel.networkErrorHandled()
                    })
            }
        }

        viewModel.isEmailValid.observe(viewLifecycleOwner) {
            if (it == true) {

                binding.emailErrorMessage.visibility = View.INVISIBLE
                binding.pwEditText.visibility = View.VISIBLE

                binding.nextBtn.text = "로그인"


                if (binding.pwEditText.length() == 0) {
                    binding.nextBtn.isEnabled = false
                    binding.nextBtn.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.whiteGray
                        )
                    )
                    binding.nextBtn.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.custom_ripple_effect
                    )
                }

                isWritingEmail = false
                binding.emailEditText.isEnabled = false
                binding.pwEditText.requestFocus()
                UtilitySystem.showKeyboard(requireActivity())
            } else if (it == false) {
                findNavController().navigate(R.id.action_accountEmailSignInFragment_to_accountSignUpAgreeFragment)
                viewModel.resetIsEmailValid()
            }
        }

        viewModel.loginResult.observe(viewLifecycleOwner) { isSuccessful ->
            if (isSuccessful) {
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            } else {
                binding.pwErrorMessage.visibility = View.VISIBLE
                binding.signUpBtn.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                binding.signUpBtn.setText(R.string.findPassword)
                isWrongPassword = true
                binding.pwEditText.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.edit_text_round_red
                )
            }
        }

        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (UtilityLogin.isEmailValid(binding.emailEditText.text.toString())) {
                    binding.nextBtn.isEnabled = true
                    binding.nextBtn.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.custom_ripple_effect_blue_rec
                    )
                    binding.nextBtn.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    binding.emailEditText.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.edit_text_round
                    )
                } else {
                    binding.nextBtn.isEnabled = false
                    binding.nextBtn.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.custom_ripple_effect
                    )
                    binding.emailEditText.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.edit_text_round_red
                    )
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("이메일", binding.emailEditText.text.toString())
            }
        })

        binding.pwEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (binding.pwEditText.length() > 0) {
                    binding.nextBtn.isEnabled = true
                    binding.nextBtn.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.custom_ripple_effect_blue_rec
                    )
                    binding.nextBtn.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    binding.pwEditText.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.edit_text_round
                    )
                } else {
                    binding.nextBtn.isEnabled = false
                    binding.nextBtn.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.custom_ripple_effect
                    )
                    binding.nextBtn.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.whiteGray
                        )
                    )
                    binding.pwEditText.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.edit_text_round_red
                    )
                    //Todo : 모듈로 빼기
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (binding.pwEditText.length() > 0) {
                    binding.nextBtn.isEnabled = true
                    binding.nextBtn.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.custom_ripple_effect_blue_rec
                    )
                    binding.nextBtn.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    binding.pwEditText.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.edit_text_round
                    )
                }
            }

            override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.nextBtn.setOnClickListener {
            if (isWritingEmail) {
                viewModel.checkEmail(binding.emailEditText.text.toString())
            } else {
                viewModel.emailLogin(
                    binding.emailEditText.text.toString(),
                    binding.pwEditText.text.toString()
                )
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_accountEmailSignInFragment_to_accountMainFragment)
        }

        binding.signUpBtn.setOnClickListener {
            if (isWrongPassword) {
                findNavController().navigate(R.id.action_accountEmailSignInFragment_to_accountResetPassword1Fragment)

            } else {
                findNavController().navigate(R.id.action_accountEmailSignInFragment_to_accountSignUpAgreeFragment)
            }
        }

    }

}