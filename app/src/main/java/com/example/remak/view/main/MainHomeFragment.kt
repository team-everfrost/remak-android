package com.example.remak.view.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remak.App
import com.example.remak.R
import com.example.remak.UtilityDialog
import com.example.remak.UtilitySystem
import com.example.remak.adapter.HomeItemOffsetDecoration
import com.example.remak.adapter.HomeRVAdapter
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.MainHomeFragmentBinding
import com.example.remak.view.add.AddActivity
import com.example.remak.view.detail.FileDetailActivity
import com.example.remak.view.detail.ImageDetailActivity
import com.example.remak.view.detail.LinkDetailActivity
import com.example.remak.view.detail.MemoDetailActivity

class MainHomeFragment : Fragment(), HomeRVAdapter.OnItemClickListener {
    private var _binding: MainHomeFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            tokenRepository
        )
    }
    private lateinit var adapter: HomeRVAdapter
    private var initialLoad = true
    lateinit var tokenRepository: TokenRepository
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tokenRepository = TokenRepository((requireActivity().application as App).dataStore)
        _binding = MainHomeFragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener {
            UtilitySystem.hideKeyboard(requireActivity())
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //생성된 activity에서 delete를 받을 시 목록 새로고침
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val isDelete = data?.getBooleanExtra("isDelete", false)
                    if (isDelete == true) {
                        viewModel.resetScrollData()
                        viewModel.getAllMainList()
                    }
                }
            }

        //어댑터 초기화
        adapter = HomeRVAdapter(mutableListOf(), this)
        recyclerView = binding.homeRV
        recyclerView.itemAnimator = null
        val itemDecoration = HomeItemOffsetDecoration(30, adapter)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        //리사이클러 뷰 아이템 간격 조정
        recyclerView.addItemDecoration(itemDecoration)
        viewModel.getAllMainList()

        //뒤로가기 누를 시 선택모드면 선택모드 종료 아니면 앱 종료
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (adapter.isSelectionMode()) {
                onSelectionEnded()
            } else {
                //어플리케이션 종료
                requireActivity().finish()
            }
        }

        //리사이클러 뷰 무한스크롤 기능
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = recyclerView.layoutManager?.itemCount
                val lastVisibleItemCount =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()

                if (!viewModel.isLoading.value!! && totalItemCount!! <= (lastVisibleItemCount + 5)) {
                    viewModel.getNewMainList()
                }
            }
        })

        viewModel.mainListData.observe(viewLifecycleOwner) { data ->
            adapter.dataSet = data
            if (initialLoad) { //첫 로드일 경우
                adapter.notifyDataSetChanged()
            }
        }

        viewModel.isDataEmpty.observe(viewLifecycleOwner) { isEmpty ->
            if (isEmpty) {
                binding.emptyLayout.visibility = View.VISIBLE
                binding.swipeRefresh.visibility = View.GONE
            } else {
                binding.emptyLayout.visibility = View.GONE
                binding.swipeRefresh.visibility = View.VISIBLE
            }
        }

        //위로 스와이프 했을 때 새로고침 기능
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.resetScrollData()
            viewModel.getAllMainList()
            binding.swipeRefresh.isRefreshing = false
        }

        binding.deleteBtn.setOnClickListener {
            val selectedItems = adapter.getSelectedItems()
            UtilityDialog.showWarnDialog(
                requireContext(),
                "삭제하시겠습니까?",
                "삭제시 복구가 불가능해요",
                "삭제하기",
                "취소하기",
                confirmClick = {
                    for (i in selectedItems) {
                        viewModel.deleteDocument(i)
                    }
                    onSelectionEnded()
                },
                cancelClick = {}
            )
        }

        binding.moreIcon.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it, 0, 0, R.style.CustomPopupMenu)
            popupMenu.menuInflater.inflate(R.menu.main_home_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.addCollection -> {
                        val intent = Intent(requireContext(), EditListActivity::class.java)
                        resultLauncher.launch(intent)
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
        }

        binding.addBtn.setOnClickListener {
            val intent = Intent(requireContext(), AddActivity::class.java)
            resultLauncher.launch(intent)
        }

        binding.addDataButton.setOnClickListener {
            val intent = Intent(requireContext(), AddActivity::class.java)
            resultLauncher.launch(intent)
        }
    }

    override fun onItemClick(view: View, position: Int) {
        when (viewModel.mainListData.value!![position].type) {
            "MEMO" -> {
                val intent = Intent(requireContext(), MemoDetailActivity::class.java)
                intent.putExtra("docId", viewModel.mainListData.value!![position].docId)
                resultLauncher.launch(intent)
            }

            "FILE" -> {
                val intent = Intent(requireContext(), FileDetailActivity::class.java)
                intent.putExtra("docId", viewModel.mainListData.value!![position].docId)
                resultLauncher.launch(intent)
            }

            "WEBPAGE" -> {
                val intent = Intent(requireContext(), LinkDetailActivity::class.java)
                intent.putExtra("docId", viewModel.mainListData.value!![position].docId)
                resultLauncher.launch(intent)
            }

            "IMAGE" -> {
                val intent = Intent(requireContext(), ImageDetailActivity::class.java)
                intent.putExtra("docId", viewModel.mainListData.value!![position].docId)
                resultLauncher.launch(intent)
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
        adapter.isSelectionModeEnd()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.resetMainData()
    }

    fun scrollToTop() {
        recyclerView.scrollToPosition(0)
    }

    fun isRecyclerViewInitialized() = ::recyclerView.isInitialized

}

