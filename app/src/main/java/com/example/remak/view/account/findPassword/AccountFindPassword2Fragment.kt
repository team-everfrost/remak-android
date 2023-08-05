package com.example.remak.view.account.findPassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.remak.UtilitySystem
import com.example.remak.databinding.FindPassword2FragmentBinding

class AccountFindPassword2Fragment : Fragment() {
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
            UtilitySystem.hideKeyboard(requireActivity())
        }
    }
}