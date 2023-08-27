package com.example.remak.view.account.signUp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import com.example.remak.App
import com.example.remak.UtilityLogin
import com.example.remak.UtilitySystem
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.AccountSignup3FragmentBinding
import com.example.remak.view.main.MainActivity

class AccountSignUp3Fragment : Fragment() {
    private lateinit var binding : AccountSignup3FragmentBinding
    lateinit var signInRepository: TokenRepository
    private val viewModel: SignUpViewModel by activityViewModels { SignUpViewModelFactory(signInRepository) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        signInRepository = TokenRepository((requireActivity().application as App).dataStore)


        binding = AccountSignup3FragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener {
            UtilitySystem.hideKeyboard(requireActivity())
        }

        viewModel.verifyCodeResult.observe(viewLifecycleOwner) { isSuccessful ->
            if (isSuccessful) {
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                viewModel.doneVerifyCodeResult()
                requireActivity().finish()
            }
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.root.setOnClickListener {
            UtilitySystem.hideKeyboard(requireActivity())
        }

        binding.passwordEditText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                if (UtilityLogin.isPasswordValid(binding.passwordEditText.text.toString())) {
                    binding.completeBtn.isEnabled = true
                    binding.completeBtn.setTextColor(ContextCompat.getColor(requireContext(), com.example.remak.R.color.white))
                    binding.completeBtn.background = ContextCompat.getDrawable(requireContext(), com.example.remak.R.drawable.custom_ripple_effect_blue_rec)
                } else {
                    binding.completeBtn.isEnabled = false
                    binding.completeBtn.setTextColor(ContextCompat.getColor(requireContext(), com.example.remak.R.color.disableTextColor))
                    binding.completeBtn.background = ContextCompat.getDrawable(requireContext(), com.example.remak.R.drawable.custom_ripple_effect)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        UtilityLogin.passwordCheck(requireContext(), binding.passwordEditText, binding.passwordCheckEditText, binding.completeBtn)

        binding.completeBtn.setOnClickListener {
            viewModel.setUserPassword(binding.passwordEditText.text.toString())
            viewModel.signup(viewModel.userEmail.value.toString(), binding.passwordEditText.text.toString())
        }
    }


}