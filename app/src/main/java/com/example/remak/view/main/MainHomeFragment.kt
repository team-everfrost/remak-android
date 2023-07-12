package com.example.remak.view.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remak.BaseFragment
import com.example.remak.databinding.MainHomeFragmentBinding

class MainHomeFragment : BaseFragment(), HomeRVAdapter.OnItemClickListener {
    private lateinit var binding : MainHomeFragmentBinding
    private val viewModel : MainViewModel by activityViewModels()
    private lateinit var adapter : HomeRVAdapter
    private var initialLoad = true


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MainHomeFragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener {
            hideKeyboard()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = HomeRVAdapter(mutableListOf(), this)

        val recyclerView : RecyclerView = binding.homeRV
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter


        //리사이클러 뷰 무한스크롤 기능
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val totalItemCount = recyclerView.layoutManager?.itemCount
                val lastVisibleItemCount = (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()

                if (!viewModel.isLoading.value!! && totalItemCount!! <= (lastVisibleItemCount + 5)) {
                    viewModel.getNewMainList()
                }
            }
        })

        val itemDecoration = ItemOffsetDecoration(10)
        recyclerView.addItemDecoration(itemDecoration)

        viewModel.mainListData.observe(viewLifecycleOwner) {data ->
            adapter.dataSet = data
            if (initialLoad) { //첫 로드일 경우
                adapter.notifyDataSetChanged()
            }
        }

        binding.swipeRefresh.setOnRefreshListener { //위로 스와이프 했을 때 새로고침 기능
            viewModel.getAllMainList()
            binding.swipeRefresh.isRefreshing = false
        }

        viewModel.getAllMainList()


    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllMainList()
    }

    override fun onItemClick(view: View, position: Int) {
        Log.d("item", viewModel.mainListData.value!![position].type)
        when (viewModel.mainListData.value!![position].type) {


        }
    }




}

