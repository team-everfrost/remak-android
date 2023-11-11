package com.everfrost.remak.view.main

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
import com.amplitude.android.Amplitude
import com.amplitude.android.Configuration
import com.amplitude.android.DefaultTrackingOptions
import com.everfrost.remak.R
import com.everfrost.remak.UtilityDialog
import com.everfrost.remak.UtilityRV
import com.everfrost.remak.UtilitySystem
import com.everfrost.remak.adapter.HomeRVAdapter
import com.everfrost.remak.databinding.MainHomeFragmentBinding
import com.everfrost.remak.view.add.AddActivity
import com.everfrost.remak.view.collection.EditCollectionBottomSheetDialog
import com.everfrost.remak.view.detail.FileDetailActivity
import com.everfrost.remak.view.detail.ImageDetailActivity
import com.everfrost.remak.view.detail.LinkDetailActivity
import com.everfrost.remak.view.detail.MemoDetailActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainHomeFragment : Fragment(), HomeRVAdapter.OnItemClickListener {
    private var _binding: MainHomeFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: HomeRVAdapter
    private var initialLoad = true
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = MainHomeFragmentBinding.inflate(inflater, container, false)
        binding.root.setOnClickListener {
            UtilitySystem.hideKeyboard(requireActivity())
        }
        //코루틴 으로 로그출력

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Amplitude(
            Configuration(
                apiKey = "16577f94d092757eef4eb77d6be2c85e",
                context = requireContext(),
                defaultTracking = DefaultTrackingOptions.ALL
            )
        )

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
        val itemDecoration = UtilityRV.HomeItemOffsetDecoration(30, adapter)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        //리사이클러 뷰 아이템 간격 조정
        recyclerView.addItemDecoration(itemDecoration)

        if (savedInstanceState == null || viewModel.mainListData.value.isNullOrEmpty()) {
            viewModel.getAllMainList()
        } else {
            adapter.dataSet = viewModel.mainListData.value!!
        }

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

        binding.previousBtn.setOnClickListener {
            onSelectionEnded()
        }

        binding.registerBtn.setOnClickListener {
            val bundle = Bundle()
            val selectedItems = adapter.getSelectedItems()
            if (selectedItems.isNotEmpty()) {
                bundle.putStringArrayList("selected", selectedItems)
                val bottomSheet = EditCollectionBottomSheetDialog()
                bottomSheet.onDismissCallback = {
                    onSelectionEnded()
                }
                bundle.putString("type", "detail")
                bottomSheet.arguments = bundle
                bottomSheet.show(
                    requireActivity().supportFragmentManager,
                    "EditCollectionBottomSheetDialog"
                )
            }
        }
    }

    override fun onItemClick(position: Int) {
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
        recyclerView.isNestedScrollingEnabled = false
        binding.addBtn.visibility = View.GONE
        binding.moreIcon.visibility = View.GONE
        binding.deleteBtn.visibility = View.VISIBLE
        binding.deleteBtn.alpha = 0f
        binding.deleteBtn.animate().alpha(1f).duration = 200
        binding.registerBtn.visibility = View.VISIBLE
        binding.registerBtn.alpha = 0f
        binding.registerBtn.animate().alpha(1f).duration = 200
        binding.swipeRefresh.isEnabled = false
        binding.previousBtn.visibility = View.VISIBLE
        (activity as MainActivity).hideBottomNavi()
        recyclerView.isNestedScrollingEnabled = true //꾹누르고 스크롤 시 앱바 보이게 하기 위함
        binding.bottomLayout.visibility = View.VISIBLE

    }

    override fun onSelectionEnded() {
        recyclerView.isNestedScrollingEnabled = true
        binding.addBtn.visibility = View.VISIBLE
        binding.moreIcon.visibility = View.VISIBLE
//        binding.deleteBtn.visibility = View.GONE
//        binding.deleteBtn.alpha = 1f
//        binding.deleteBtn.animate().alpha(0f).duration = 300
//        binding.registerBtn.visibility = View.GONE
//        binding.registerBtn.alpha = 1f
//        binding.registerBtn.animate().alpha(0f).duration = 300
        binding.bottomLayout.visibility = View.GONE
        binding.previousBtn.visibility = View.GONE
        binding.swipeRefresh.isEnabled = true
        adapter.isSelectionModeEnd()
        (activity as MainActivity).showBottomNavi()
    }

    fun scrollToTop() {
        recyclerView.scrollToPosition(0)
    }

    fun isRecyclerViewInitialized() = ::recyclerView.isInitialized

}

