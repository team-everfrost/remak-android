package com.example.remak.view.account.signUp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.remak.App
import com.example.remak.BaseFragment
import com.example.remak.R
import com.example.remak.dataStore.SignInRepository
import com.example.remak.databinding.AccountSignup2FragmentBinding
import com.example.remak.databinding.AccountSignup3FragmentBinding
import com.example.remak.view.main.MainActivity
import kotlinx.coroutines.launch

class AccountSignUp3Fragment : BaseFragment() {
    private lateinit var binding : AccountSignup3FragmentBinding
    lateinit var signInRepository: SignInRepository
    private val viewModel: SignUpViewModel by activityViewModels { SignUpViewModelFactory(signInRepository) }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        signInRepository = SignInRepository((requireActivity().application as App).dataStore)


        binding = AccountSignup3FragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener {
            hideKeyboard()
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
            hideKeyboard()
        }

        passwordCheck(binding.passwordEditText, binding.passwordCheckEditText, binding.completeBtn)

        binding.completeBtn.setOnClickListener {
                viewModel.viewModelScope.launch {
                viewModel.signup(viewModel.userEmail.value.toString(), binding.passwordEditText.text.toString())
            }

        }
    }


}