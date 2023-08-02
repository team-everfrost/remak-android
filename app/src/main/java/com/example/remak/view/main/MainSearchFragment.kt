package com.example.remak.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.remak.App
import com.example.remak.BaseFragment
import com.example.remak.R
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.MainSearchFragmentBinding
import com.example.remak.view.detail.FileDetailActivity
import com.example.remak.view.detail.ImageDetailActivity
import com.example.remak.view.detail.LinkDetailActivity
import com.example.remak.view.detail.MemoDetailActivity
import com.example.remak.view.search.ItemOffsetDecoration
import com.example.remak.view.search.SearchRVAdapter
import com.example.remak.view.search.SearchResultActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainSearchFragment : BaseFragment(), SearchRVAdapter.OnItemClickListener {
    private lateinit var binding : MainSearchFragmentBinding
    private val viewModel : MainViewModel by activityViewModels { MainViewModelFactory(tokenRepository)}
    lateinit var tokenRepository: TokenRepository
    private lateinit var adapter : SearchRVAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MainSearchFragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener {
            hideKeyboard()
        }
        tokenRepository = TokenRepository((requireActivity().application as App).dataStore)
        adapter = SearchRVAdapter(mutableListOf(), this)
        val recyclerView = binding.searchRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val itemDecoration = ItemOffsetDecoration(10, adapter)
        recyclerView.addItemDecoration(itemDecoration)
        //뒤로가기 시 홈 프래그먼트로 이동
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            (activity as MainActivity).binding.bottomNavigation.selectedItemId = R.id.homeFragment
            isEnabled = false
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.searchResult.observe(viewLifecycleOwner) { data ->
            binding.shimmerLayout.stopShimmer()
            binding.shimmerLayout.visibility = View.GONE
            binding.searchRecyclerView.visibility = View.VISIBLE
            adapter.dataSet = data
            adapter.notifyDataSetChanged()
        }

        binding.searchEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.searchRecyclerView.visibility = View.GONE
                binding.shimmerLayout.startShimmer()
                binding.shimmerLayout.visibility = View.VISIBLE
                viewModel.getSearchResult(binding.searchEditText.text.toString())
            }
            false
        }


        binding.sampleFilter1.setOnClickListener {
            //backgroundtint가 white일경우 checkblue로 변경
            if (binding.sampleFilter1.backgroundTintList?.defaultColor == -1) {
                binding.sampleFilter1.backgroundTintList = resources.getColorStateList(
                    com.example.remak.R.color.checkBlue,
                    null
                )
            } else {
                binding.sampleFilter1.backgroundTintList = resources.getColorStateList(
                    com.example.remak.R.color.white,
                    null
                )
            }
        }


    }

    override fun onItemClick(view: View, position: Int) {
        when (viewModel.searchResult.value!![position].type) {
            "MEMO" -> {
                val intent = Intent(requireContext(), MemoDetailActivity::class.java)
                intent.putExtra("docId", viewModel.searchResult.value!![position].docId)
                startActivity(intent)
            }

            "FILE" -> {
                val intent = Intent(requireContext(), FileDetailActivity::class.java)
                intent.putExtra("docId", viewModel.searchResult.value!![position].docId)
                startActivity(intent)
            }

            "WEBPAGE" -> {
                val intent = Intent(requireContext(), LinkDetailActivity::class.java)
                intent.putExtra("docId", viewModel.searchResult.value!![position].docId)
                startActivity(intent)

            }

            "IMAGE" -> {
                val intent = Intent(requireContext(), ImageDetailActivity::class.java)
                intent.putExtra("docId", viewModel.searchResult.value!![position].docId)
                startActivity(intent)
            }


        }
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainSearchFragment", "onDestroy")

    }

    override fun onResume() {
        super.onResume()
        binding.shimmerLayout.stopShimmer()
        binding.shimmerLayout.visibility = View.GONE
        binding.searchRecyclerView.visibility = View.GONE
        binding.searchEditText.setText("")
        viewModel.resetData()

    }
}