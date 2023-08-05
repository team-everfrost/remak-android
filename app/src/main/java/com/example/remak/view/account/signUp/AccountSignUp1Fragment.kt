package com.example.remak.view.account.signUp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.remak.R
import com.example.remak.databinding.AccountSignup1FragmentBinding
import com.example.remak.App
import com.example.remak.UtilityDialog
import com.example.remak.UtilityLogin
import com.example.remak.UtilitySystem
import com.example.remak.dataStore.TokenRepository

class AccountSignUp1Fragment : Fragment() {
    private lateinit var binding : AccountSignup1FragmentBinding

    private val viewModel: SignUpViewModel by activityViewModels { SignUpViewModelFactory(signInRepository) }

    lateinit var signInRepository : TokenRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        signInRepository = TokenRepository((requireActivity().application as App).dataStore)



        binding = AccountSignup1FragmentBinding.inflate(inflater, container, false)


        viewModel.verifyCodeResult.observe(viewLifecycleOwner) { isSuccessful ->
            if (isSuccessful) {
                findNavController().navigate(R.id.action_accountSignUp1Fragment2_to_accountSignUp2Fragment2)
                viewModel.doneVerifyCodeResult()
                viewModel.setUserEmail(binding.emailEditText.text.toString())
            }
        }

        viewModel.isEmailExist.observe(viewLifecycleOwner) { isEmailExist ->
            if (isEmailExist) {
                UtilityDialog.showInformDialog("이미 존재하는 이메일입니다.", requireContext())
                viewModel.doneEmailCheck()
            }
        }

        viewModel.isEmailInvalid.observe(viewLifecycleOwner) { isEmailValid ->
            if (isEmailValid) {
                UtilityDialog.showInformDialog("이메일 형식이 올바르지 않습니다.", requireContext())
                viewModel.doneEmailCheck()
            }
        }

        binding.root.setOnClickListener {
            UtilitySystem.hideKeyboard(requireActivity())
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
        UtilityLogin.emailCheck(requireContext(), binding.emailEditText, binding.nextBtn, binding.emailErrorMessage)

    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d("destroy", "onDestroy:")
    }

}