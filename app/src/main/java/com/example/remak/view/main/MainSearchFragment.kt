package com.example.remak.view.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remak.App
import com.example.remak.R
import com.example.remak.UtilitySystem
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.MainSearchFragmentBinding
import com.example.remak.view.detail.FileDetailActivity
import com.example.remak.view.detail.ImageDetailActivity
import com.example.remak.view.detail.LinkDetailActivity
import com.example.remak.view.detail.MemoDetailActivity
import com.example.remak.adapter.ItemOffsetDecoration
import com.example.remak.adapter.SearchRVAdapter

class MainSearchFragment : Fragment(), SearchRVAdapter.OnItemClickListener {
    private lateinit var binding : MainSearchFragmentBinding
    private val viewModel : MainViewModel by activityViewModels { MainViewModelFactory(tokenRepository)}
    lateinit var tokenRepository: TokenRepository
    private lateinit var adapter : SearchRVAdapter
    var isSearchBtnClicked = false
    private var isTextSearch = false
    private var isEmbeddingSearch = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MainSearchFragmentBinding.inflate(inflater, container, false)
        tokenRepository = TokenRepository((requireActivity().application as App).dataStore)
        adapter = SearchRVAdapter(mutableListOf(), this)


        //뒤로가기 시 홈 프래그먼트로 이동
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            (activity as MainActivity).binding.bottomNavigation.selectedItemId = R.id.homeFragment
            isEnabled = false
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.searchRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val itemDecoration = ItemOffsetDecoration(10, adapter)
        recyclerView.addItemDecoration(itemDecoration)
        recyclerView.visibility = View.GONE

        binding.root.setOnClickListener {
            Log.d("MainSearchFragment", "root click")
            UtilitySystem.hideKeyboard(requireActivity())
        }

        val handler = Handler(Looper.getMainLooper())
        var runnable: Runnable? = null

        binding.searchEditText.requestFocus()
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_IMPLICIT)

        viewModel.searchResult.observe(viewLifecycleOwner) { data ->
            binding.shimmerLayout.stopShimmer()
            binding.shimmerLayout.visibility = View.GONE
            binding.searchRecyclerView.visibility = View.VISIBLE
            adapter.dataSet = data
            adapter.notifyDataSetChanged()
        }

        //엔터 누를시
        binding.searchEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handler.removeCallbacksAndMessages(null)
                isSearchBtnClicked = true
                binding.searchRecyclerView.visibility = View.GONE
                binding.shimmerLayout.startShimmer()
                binding.shimmerLayout.visibility = View.VISIBLE
                viewModel.getEmbeddingSearchResult(binding.searchEditText.text.toString())
                isEmbeddingSearch = true
                isTextSearch = false
                viewModel.resetScrollData()


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
                    R.color.white,
                    null
                )
            }
        }

        binding.searchEditText.addTextChangedListener(object  : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (runnable != null) {
                    handler.removeCallbacks(runnable!!)
                }
                handler.postDelayed(runnable!!, 500)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!isSearchBtnClicked) {
                    runnable = Runnable {
                        if (p0.toString().isNotEmpty()) {
                            viewModel.getTextSearchResult(p0.toString())
                            isTextSearch = true
                            isEmbeddingSearch = false
                            viewModel.resetScrollData()
                            Log.d(isEmbeddingSearch.toString(), isTextSearch.toString())
                        } else {
                            viewModel.resetSearchData()
                            binding.searchRecyclerView.visibility = View.GONE
                        }
                    }
                }
            }
        })

        //리사이클러 뷰 무한스크롤 기능
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = recyclerView.layoutManager?.itemCount
                val lastVisibleItemCount = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()

                if (!viewModel.isLoading.value!! && totalItemCount!! <= (lastVisibleItemCount + 5)) {
                    if (isTextSearch) {
                        viewModel.getNewTextSearchResult()
                        Log.d("새로운 데이터 받아옴", "getNewTextSearchResult")
                    }
                    else if (isEmbeddingSearch && !viewModel.isEmbeddingLoading.value!!) {
                        viewModel.getNewEmbeddingSearch()
                        Log.d("새로운 데이터 받아옴", "getNewEmbeddingSearch")
                    }
                }
            }
        })


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



    override fun onResume() {
        super.onResume()
        if (viewModel.searchResult.value == null || viewModel.searchResult.value!!.isEmpty()) {
            binding.searchRecyclerView.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainSearchFragment", "onDestroy")

    }



    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("MainSearchFragment", "onDestroyView")
        viewModel.resetSearchData()
    }
}