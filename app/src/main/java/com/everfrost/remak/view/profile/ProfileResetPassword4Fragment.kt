package com.everfrost.remak.view.profile

import android.content.Intent
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
import com.everfrost.remak.R
import com.everfrost.remak.UtilityDialog
import com.everfrost.remak.dataStore.TokenRepository
import com.everfrost.remak.databinding.ProfileResetPassword4FragmentBinding
import com.everfrost.remak.view.account.AccountActivity
import com.everfrost.remak.view.account.signIn.SignInViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileResetPassword4Fragment : Fragment() {
    private lateinit var binding: ProfileResetPassword4FragmentBinding
    private val viewModel: SignInViewModel by activityViewModels()
    lateinit var tokenRepository: TokenRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = ProfileResetPassword4FragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener {
            com.everfrost.remak.UtilitySystem.hideKeyboard(requireActivity())
        }
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
                        val intent = Intent(requireContext(), AccountActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        requireActivity().finish()
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