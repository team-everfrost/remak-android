package com.example.remak.view.account.signIn

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.remak.App
import com.example.remak.R
import com.example.remak.UtilityDialog
import com.example.remak.UtilityLogin
import com.example.remak.UtilitySystem
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.AccountMainFragmentBinding
import com.example.remak.view.main.MainActivity


class AccountMainFragment : Fragment() {
    private lateinit var binding : AccountMainFragmentBinding
    private val viewModel: SignInViewModel by activityViewModels { SignInViewModelFactory(signInRepository) }
    lateinit var signInRepository : TokenRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        signInRepository = TokenRepository((requireActivity().application as App).dataStore)
        binding = AccountMainFragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener{
            UtilitySystem.hideKeyboard(requireActivity())
        }

        viewModel.loginResult.observe(viewLifecycleOwner) { isSuccessful ->
            if (isSuccessful) {
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                viewModel.doneLogin()
                requireActivity().finish()
            }
        }

        UtilityLogin.signInCheck(requireContext(), binding.idEditText, binding.pwEditText, binding.signInBtn)

        viewModel.showDialog.observe(viewLifecycleOwner) { showDialog ->
            if (showDialog) {
                UtilityDialog.showInformDialog( "아이디 또는 비밀번호를 확인해주세요", requireContext())
                viewModel.doneDialog()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signUpBtn.setOnClickListener{
            findNavController().navigate(R.id.action_accountMainFragment_to_accountSignUp1Fragment2)
        }
        binding.findAccountBtn.setOnClickListener {
            findNavController().navigate(R.id.action_accountMainFragment_to_accountFindPassword1Fragment)
        }

        binding.kakaoBtn.setOnClickListener {
            viewModel.kakaoLogin(requireActivity())

        }
        binding.signInBtn.setOnClickListener{
            viewModel.emailLogin(binding.idEditText.text.toString(), binding.pwEditText.text.toString())
        }




        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }

    }





}