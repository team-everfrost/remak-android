package com.example.remak

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.remak.databinding.FindPassword3FragmentBinding

class AccountFindPassword3Fragment : BaseFragment() {
    private lateinit var binding : FindPassword3FragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FindPassword3FragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
}