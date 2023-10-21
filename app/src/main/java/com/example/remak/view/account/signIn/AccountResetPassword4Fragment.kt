package com.example.remak.view.account.signIn

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.remak.App
import com.example.remak.R
import com.example.remak.UtilityDialog
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.AccountResetPassword4FragmentBinding
import kotlinx.coroutines.launch

class AccountResetPassword4Fragment : Fragment() {
    private lateinit var binding: AccountResetPassword4FragmentBinding
    private val viewModel: SignInViewModel by activityViewModels {
        SignInViewModelFactory(
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
            TokenRepository((requireActivity().application as App).dataStore)
        binding = AccountResetPassword4FragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener {
            com.example.remak.UtilitySystem.hideKeyboard(requireActivity())
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

        viewModel.isResetPasswordSuccess.observe(viewLifecycleOwner) {
            if (it) {
                viewLifecycleOwner.lifecycleScope.launch {
                    tokenRepository.deleteUser()
                }
                UtilityDialog.showInformDialog(
                    "비밀번호 재설정이 완료되었습니다.", "다시 로그인해주세요", requireContext(),
                    confirmClick = {
                        viewModel.resetAllValue()
                        findNavController().navigate(R.id.action_accountResetPassword4Fragment_to_accountMainFragment)
                    }
                )
            } else {
                binding.passwordErrorText.visibility = View.VISIBLE
            }
        }

        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                Log.d("비밀번호", binding.passwordEditText.text.toString())
                Log.d("비밀번호 확인", viewModel.userNewPassword.value.toString())
                if (binding.passwordEditText.text.toString() == viewModel.userNewPassword.value) {
                    binding.passwordEditText.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.edit_text_round)
                    binding.completeBtn.isEnabled = true
                    binding.completeBtn.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        )
                    )
                    binding.completeBtn.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.custom_ripple_effect_blue_rec
                    )
                    binding.passwordErrorText.visibility = View.INVISIBLE

                } else {
                    binding.passwordEditText.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.edit_text_round_red)
                    binding.completeBtn.isEnabled = false
                    binding.completeBtn.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.disableTextColor
                        )
                    )
                    binding.completeBtn.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.custom_ripple_effect)
                    binding.passwordErrorText.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        binding.completeBtn.setOnClickListener {
            viewModel.resetPassword()
        }
    }
}