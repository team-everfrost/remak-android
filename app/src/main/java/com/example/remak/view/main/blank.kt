package com.example.remak.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.remak.databinding.BlankBinding

class blank : Fragment() {

    private lateinit var binding: BlankBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BlankBinding.inflate(inflater, container, false)
        return binding.root
    }

}