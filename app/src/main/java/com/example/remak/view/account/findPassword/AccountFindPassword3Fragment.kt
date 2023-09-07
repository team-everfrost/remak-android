package com.example.remak.view.account.findPassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.remak.databinding.FindPassword3FragmentBinding

class AccountFindPassword3Fragment : Fragment() {
    private lateinit var binding: FindPassword3FragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FindPassword3FragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

}