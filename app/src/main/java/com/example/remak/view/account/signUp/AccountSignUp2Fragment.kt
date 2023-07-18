package com.example.remak.view.account.signUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.remak.App
import com.example.remak.BaseFragment
import com.example.remak.R
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.AccountSignup2FragmentBinding
import kotlinx.coroutines.launch

class AccountSignUp2Fragment : BaseFragment() {
    private lateinit var binding : AccountSignup2FragmentBinding
    private val viewModel: SignUpViewModel by activityViewModels { SignUpViewModelFactory(signInRepository) }

    lateinit var signInRepository : TokenRepository
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        signInRepository = TokenRepository((requireActivity().application as App).dataStore)

        binding = AccountSignup2FragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener {
            hideKeyboard()
        }

        //이메일 인증 성공 시 다음 화면으로 이동
        viewModel.verifyCodeResult.observe(viewLifecycleOwner) { isSuccessful ->
            if (isSuccessful) {
                findNavController().navigate(R.id.action_accountSignUp2Fragment2_to_accountSignUp3Fragment)
                viewModel.doneVerifyCodeResult()
            }
        }

        viewModel.isSignUpCodeInvalid.observe(viewLifecycleOwner) { isSignUpCodeInvalid ->
            if (isSignUpCodeInvalid) {
                showInformDialog("인증코드가 올바르지 않습니다.")
                viewModel.doneSignUpCodeCheck()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener{
            findNavController().navigate(R.id.action_accountSignUp2Fragment2_to_accountSignUp1Fragment2)
        }

        binding.nextBtn.setOnClickListener {
            //인증코드 확인
            viewModel.viewModelScope.launch {
                viewModel.checkVerifyCode(binding.verifyCodeEditText.text.toString(), viewModel.userEmail.value.toString())
            }
        }


    }
}