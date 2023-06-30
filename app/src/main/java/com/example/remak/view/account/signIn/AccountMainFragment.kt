package com.example.remak.view.account.signIn

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.remak.App
import com.example.remak.BaseFragment
import com.example.remak.R
import com.example.remak.dataStore.SignInRepository
import com.example.remak.databinding.AccountMainFragmentBinding
import com.example.remak.view.main.MainActivity

class AccountMainFragment : BaseFragment() {

    private lateinit var binding : AccountMainFragmentBinding
    //    val testSignInRepository = SignInRepository((requireActivity().application as App).testDataStore)

//    val signInRepository = SignInRepository((requireActivity().application as App).dataStore)
    lateinit var signInRepository : SignInRepository
    lateinit var viewModel : SignInViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        signInRepository = SignInRepository((requireActivity().application as App).dataStore)

        viewModel = ViewModelProvider(this, SignInViewModelFactory(signInRepository)).get(
            SignInViewModel::class.java)
        binding = AccountMainFragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener{
            hideKeyboard()
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
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }

    }



}