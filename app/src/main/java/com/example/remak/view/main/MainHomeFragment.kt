package com.example.remak.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.remak.BaseFragment
import com.example.remak.databinding.MainHomeFragmentBinding

class MainHomeFragment : BaseFragment() {
    private lateinit var binding : MainHomeFragmentBinding
    private val viewModel : MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MainHomeFragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener {
            hideKeyboard()
        }
        viewModel.getMainList()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setRV()


    }

    private fun setRV() {
        val itemDecoration = ItemOffsetDecoration(10)
        binding!!.homeRV.addItemDecoration(itemDecoration)
        val testRV = TestRVAdapter(arrayOf("test", "test", "test", "test", "test", "test", "test", "test", "test", "test"))
        binding!!.homeRV.adapter = testRV
        binding!!.homeRV.layoutManager = LinearLayoutManager(requireContext())

    }



}