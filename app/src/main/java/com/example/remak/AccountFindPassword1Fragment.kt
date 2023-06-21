package com.example.remak

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.remak.databinding.FindPassword1FragmentBinding

class AccountFindPassword1Fragment : BaseFragment() {
    private lateinit var binding : FindPassword1FragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FindPassword1FragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
}