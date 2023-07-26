package com.example.remak.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.addCallback
import com.example.remak.BaseFragment
import com.example.remak.R
import com.example.remak.databinding.MainSearchFragmentBinding
import com.example.remak.view.search.SearchResultActivity
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
        //뒤로가기 시 홈 프래그먼트로 이동
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            (activity as MainActivity).binding.bottomNavigation.selectedItemId = R.id.homeFragment
            isEnabled = false

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val intent = Intent(requireContext(), SearchResultActivity::class.java)
                intent.putExtra("query", binding.searchEditText.text.toString())
                startActivity(intent)

            }
            false
        }


    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainSearchFragment", "onDestroy")

    }
}