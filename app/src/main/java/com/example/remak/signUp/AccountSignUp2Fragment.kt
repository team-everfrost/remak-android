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

class AccountSignUp2Fragment : BaseFragment() {
    private lateinit var binding : AccountSignup2FragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AccountSignup2FragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var passwordValid = false
        var passwordRepeatValid = false

        binding.root.setOnClickListener {
            hideKeyboard()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_accountSignUp2Fragment2_to_accountSignUp1Fragment2)
        }
        passwordCheck(binding.passwordEditText, binding.passwordCheckEditText, binding.nextBtn)
    }

    //비밀번호 유효성 검사




}