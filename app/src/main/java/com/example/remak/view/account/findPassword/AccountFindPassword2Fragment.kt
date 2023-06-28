package com.example.remak.view.account.findPassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.remak.BaseFragment
import com.example.remak.databinding.FindPassword2FragmentBinding

class AccountFindPassword2Fragment : BaseFragment() {
    private lateinit var binding : FindPassword2FragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FindPassword2FragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.setOnClickListener {
            hideKeyboard()
        }




    }
}