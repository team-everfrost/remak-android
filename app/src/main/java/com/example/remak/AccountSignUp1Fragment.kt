package com.example.remak

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.remak.databinding.AccountSignup1FragmentBinding

class AccountSignUp1Fragment : Fragment() {
    private lateinit var binding : AccountSignup1FragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AccountSignup1FragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener{
            findNavController().navigate(R.id.action_accountSignUp1Fragment2_to_accountMainFragment)

        }
    }

    fun initEvent(){

        }
}