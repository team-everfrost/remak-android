package com.example.remak.view.account.signUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.remak.BaseFragment
import com.example.remak.R
import com.example.remak.databinding.AccountSignup2FragmentBinding
import com.example.remak.databinding.AccountSignup3FragmentBinding

class AccountSignUp2Fragment : BaseFragment() {
    private lateinit var binding : AccountSignup2FragmentBinding
    private val viewModel : SignUpViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AccountSignup2FragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener {
            hideKeyboard()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        //기기의 뒤로가기 버튼 재정의

    }
}