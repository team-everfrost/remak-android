package com.example.remak.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import com.example.remak.BaseFragment
import com.example.remak.R
import com.example.remak.databinding.MainProfileFragmentBinding
import com.example.remak.view.main.setting.SettingActivity

class MainProfileFragment : BaseFragment() {
    private lateinit var binding : MainProfileFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MainProfileFragmentBinding.inflate(inflater, container, false)
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

        binding.settingBtn.setOnClickListener {
            val intent = Intent(activity, SettingActivity::class.java)
            startActivity(intent)
        }

    }


    //ondestroy
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("MainProfileFragment", "onDestroy")

        (view as ViewGroup).removeAllViews()

    }
}