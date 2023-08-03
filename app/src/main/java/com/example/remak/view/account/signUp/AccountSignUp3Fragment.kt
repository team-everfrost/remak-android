package com.example.remak.view.account.signUp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import com.example.remak.App
import com.example.remak.UtilityLogin
import com.example.remak.UtilitySystem
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.AccountSignup3FragmentBinding
import com.example.remak.view.main.MainActivity
import kotlinx.coroutines.launch

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

        UtilityLogin.passwordCheck(requireContext(), binding.passwordEditText, binding.passwordCheckEditText, binding.completeBtn)

        binding.completeBtn.setOnClickListener {
                viewModel.viewModelScope.launch {
                viewModel.signup(viewModel.userEmail.value.toString(), binding.passwordEditText.text.toString())
            }

        }
    }


}