package com.example.remak.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remak.App
import com.example.remak.BaseFragment
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.MainHomeFragmentBinding
import com.example.remak.view.detail.FileDetailActivity
import com.example.remak.view.detail.ImageDetailActivity
import com.example.remak.view.detail.LinkDetailActivity
import com.example.remak.view.detail.MemoDetailActivity

class MainHomeFragment : BaseFragment(), HomeRVAdapter.OnItemClickListener {
    private var _binding : MainHomeFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel : MainViewModel by activityViewModels { MainViewModelFactory(tokenRepository)}
    private lateinit var adapter : HomeRVAdapter
    private var initialLoad = true
    lateinit var tokenRepository: TokenRepository


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tokenRepository = TokenRepository((requireActivity().application as App).dataStore)
        _binding = MainHomeFragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener {
            hideKeyboard()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = HomeRVAdapter(mutableListOf(), this)
        val recyclerView : RecyclerView = binding.homeRV
        val itemDecoration = ItemOffsetDecoration(10, adapter)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        //리사이클러 뷰 아이템 간격 조정
        recyclerView.addItemDecoration(itemDecoration)

        viewModel.getAllMainList()

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


        viewModel.uploadFileSuccess.observe(viewLifecycleOwner) { isSuccessful ->
            if (isSuccessful) {
                showInformDialog("파일 업로드에 성공했습니다.")
                viewModel.isUploadFileSuccess()
            }
        }

        binding.deleteBtn.setOnClickListener {
            Log.d("list", adapter.getSelectedItems().toString())
            Log.d("selecteditemcounter", adapter.selectedItemsCount.toString())
        }

        binding.sampleFilter1.setOnClickListener {
            filterClickEvent(binding.sampleFilter1)
        }
        binding.sampleFilter2.setOnClickListener {
            filterClickEvent(binding.sampleFilter2)
        }
        binding.sampleFilter3.setOnClickListener {
            filterClickEvent(binding.sampleFilter3)
        }
        binding.sampleFilter4.setOnClickListener {
            filterClickEvent(binding.sampleFilter4)
        }
    }

    private fun filterClickEvent(button: AppCompatButton) {
        if (button.backgroundTintList?.defaultColor == -1) {
            button.backgroundTintList = resources.getColorStateList(
                com.example.remak.R.color.checkBlue,
                null
            )
        } else {
            button.backgroundTintList = resources.getColorStateList(
                com.example.remak.R.color.white,
                null
            )
        }
    }

    // 다시 접속 시 데이터 갱신
    override fun onResume() {
        super.onResume()
        viewModel.getAllMainList()
    }

    override fun onItemClick(view: View, position: Int) {
        when (viewModel.mainListData.value!![position].type) {
            "MEMO" -> {
                val intent = Intent(requireContext(), MemoDetailActivity::class.java)
                intent.putExtra("docId", viewModel.mainListData.value!![position].docId)
                startActivity(intent)
            }

            "FILE" -> {
                val intent = Intent(requireContext(), FileDetailActivity::class.java)
                intent.putExtra("docId", viewModel.mainListData.value!![position].docId)
                startActivity(intent)
            }

            "WEBPAGE" -> {
                val intent = Intent(requireContext(), LinkDetailActivity::class.java)
                intent.putExtra("docId", viewModel.mainListData.value!![position].docId)
                startActivity(intent)

            }

            "IMAGE" -> {
                val intent = Intent(requireContext(), ImageDetailActivity::class.java)
                intent.putExtra("docId", viewModel.mainListData.value!![position].docId)
                startActivity(intent)
            }


        }
    }

    override fun onSelectionStarted() {
        binding.deleteBtn.visibility = View.VISIBLE
        binding.deleteBtn.alpha = 0f
        binding.deleteBtn.animate().alpha(1f).duration = 200
        binding.swipeRefresh.isEnabled = false
    }

    override fun onSelectionEnded() {
        binding.deleteBtn.visibility = View.GONE
        binding.deleteBtn.alpha = 1f
        binding.deleteBtn.animate().alpha(0f).duration = 300
        binding.swipeRefresh.isEnabled = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

