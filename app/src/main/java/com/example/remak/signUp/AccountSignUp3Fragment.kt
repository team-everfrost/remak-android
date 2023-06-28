package com.example.remak.signUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.remak.BaseFragment
import com.example.remak.R
import com.example.remak.databinding.AccountSignup2FragmentBinding
import com.example.remak.databinding.AccountSignup3FragmentBinding

class AccountSignUp3Fragment : BaseFragment() {
    private lateinit var binding : AccountSignup3FragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AccountSignup3FragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.root.setOnClickListener {
            hideKeyboard()
        }


        passwordCheck(binding.passwordEditText, binding.passwordCheckEditText, binding.nextBtn)
    }





}