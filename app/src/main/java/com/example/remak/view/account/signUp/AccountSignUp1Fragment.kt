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

class AccountSignUp1Fragment : BaseFragment() {
    private lateinit var binding : AccountSignup1FragmentBinding

    private val viewModel : SignUpViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AccountSignup1FragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener {
            hideKeyboard()
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener{
            viewModel.setUserEmail(binding.emailEditText.text.toString())
            findNavController().navigate(R.id.action_accountSignUp1Fragment2_to_accountMainFragment)
        }
        binding.nextBtn.setOnClickListener{
            findNavController().navigate(R.id.action_accountSignUp1Fragment2_to_accountSignUp2Fragment2)
        }
        emailCheck(binding.emailEditText, binding.nextBtn, binding.emailErrorMessage)



    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d("destroy", "onDestroy:")
    }

}