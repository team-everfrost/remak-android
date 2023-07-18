package com.example.remak.view.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.remak.BaseFragment
import com.example.remak.R
import com.example.remak.databinding.MainTagFragmentBinding

class MainTagFragment : BaseFragment() {
    private lateinit var binding : MainTagFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MainTagFragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener {
            hideKeyboard()
        }
        //뒤로가기 시 홈 프래그먼트로 이동
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            (activity as MainActivity).binding.bottomNavigation.selectedItemId = R.id.homeFragment
            isEnabled = false

        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRV()
    }

    private fun setRV() {
        val testTagRV = TestTagRVAdapter(arrayOf("test", "test", "test", "test", "test", "test", "test", "test", "test", "test"))
        binding!!.tagRV.adapter = testTagRV
        binding!!.tagRV.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TagFragment", "onDestroy")
    }



}