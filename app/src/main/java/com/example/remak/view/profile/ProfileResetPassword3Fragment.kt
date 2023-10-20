package com.example.remak.view.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.remak.R
import com.example.remak.UtilityLogin
import com.example.remak.UtilitySystem
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.AccountResetPassword3FragmentBinding
import com.example.remak.view.account.signIn.SignInViewModel
import com.example.remak.view.account.signIn.SignInViewModelFactory

class ProfileResetPassword3Fragment : Fragment() {

    private lateinit var binding: AccountResetPassword3FragmentBinding
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
            TokenRepository((requireActivity().application as com.example.remak.App).dataStore)
        binding = AccountResetPassword3FragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener {
            UtilitySystem.hideKeyboard(requireActivity())
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                UtilityLogin.passwordEnglishCheck(
                    requireContext(),
                    binding.passwordEditText.text.toString(),
                    binding.englishCheck
                )
                UtilityLogin.passwordNumberCheck(
                    requireContext(),
                    binding.passwordEditText.text.toString(),
                    binding.numberCheck
                )
                UtilityLogin.passwordLengthCheck(
                    requireContext(),
                    binding.passwordEditText.text.toString(),
                    binding.nineCheck
                )
                if (UtilityLogin.isPasswordValid(binding.passwordEditText.text.toString())) {
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
                } else {
                    binding.completeBtn.isEnabled = false
                    binding.completeBtn.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.disableTextColor
                        )
                    )
                    binding.completeBtn.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.custom_ripple_effect
                    )
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.completeBtn.setOnClickListener {
            viewModel.setUserNewPassword(binding.passwordEditText.text.toString())
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(
                R.anim.from_right,
                R.anim.to_left,
                R.anim.from_left,
                R.anim.to_right
            )
            transaction.replace(R.id.mainFragmentContainerView, ProfileResetPassword4Fragment())
            transaction.addToBackStack(null)
            transaction.commit()

        }

    }

}