package com.example.remak.view.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.remak.BaseFragment
import com.example.remak.databinding.MainSearchFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainSearchFragment : BaseFragment() {

    private lateinit var binding : MainSearchFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MainSearchFragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener {
            hideKeyboard()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainSearchFragment", "onDestroy")

    }
}