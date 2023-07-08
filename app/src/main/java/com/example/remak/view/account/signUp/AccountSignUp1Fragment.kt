package com.example.remak.view.account.signUp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.remak.BaseFragment
import com.example.remak.R
import com.example.remak.databinding.AccountSignup1FragmentBinding
import com.example.remak.App
import com.example.remak.dataStore.TokenRepository

class AccountSignUp1Fragment : BaseFragment() {
    private lateinit var binding : AccountSignup1FragmentBinding

    private val viewModel: SignUpViewModel by activityViewModels { SignUpViewModelFactory(signInRepository) }

    lateinit var signInRepository : TokenRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        signInRepository = TokenRepository((requireActivity().application as App).dataStore)



        binding = AccountSignup1FragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener {
            hideKeyboard()
        }

        viewModel.verifyCodeResult.observe(viewLifecycleOwner) { isSuccessful ->
            if (isSuccessful) {
                findNavController().navigate(R.id.action_accountSignUp1Fragment2_to_accountSignUp2Fragment2)
                viewModel.doneVerifyCodeResult()
                viewModel.setUserEmail(binding.emailEditText.text.toString())
            }
        }

        viewModel.isEmailExist.observe(viewLifecycleOwner) { isEmailExist ->
            if (isEmailExist) {
                showDialog("이미 존재하는 이메일입니다.")
                viewModel.doneEmailCheck()
            }
        }

        viewModel.isEmailInvalid.observe(viewLifecycleOwner) { isEmailValid ->
            if (isEmailValid) {
                showDialog("이메일 형식이 올바르지 않습니다.")
                viewModel.doneEmailCheck()
            }
        }



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener{
            findNavController().navigate(R.id.action_accountSignUp1Fragment2_to_accountMainFragment)
        }
        binding.nextBtn.setOnClickListener{
            viewModel.getVerifyCode(binding.emailEditText.text.toString())

        }
        emailCheck(binding.emailEditText, binding.nextBtn, binding.emailErrorMessage)



    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d("destroy", "onDestroy:")
    }

}