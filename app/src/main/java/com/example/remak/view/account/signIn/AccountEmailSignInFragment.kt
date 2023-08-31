package com.example.remak.view.account.signIn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.remak.App
import com.example.remak.UtilitySystem
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.AccountEmailSignin1FragmentBinding

class AccountEmailSignInFragment : Fragment() {
    private lateinit var binding : AccountEmailSignin1FragmentBinding
    private val viewModel: SignInViewModel by activityViewModels { SignInViewModelFactory(signInRepository) }
    lateinit var signInRepository : TokenRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        signInRepository = TokenRepository((requireActivity().application as App).dataStore)
        binding = AccountEmailSignin1FragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener{
            UtilitySystem.hideKeyboard(requireActivity())
        }
        return binding.root
    }



}