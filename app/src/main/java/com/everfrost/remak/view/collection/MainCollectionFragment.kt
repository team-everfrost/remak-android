package com.everfrost.remak.view.collection

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.everfrost.remak.R
import com.everfrost.remak.UtilityDialog
import com.everfrost.remak.adapter.CollectionRVAdapter
import com.everfrost.remak.adapter.SpacingItemDecorator
import com.everfrost.remak.dataStore.TokenRepository
import com.everfrost.remak.databinding.MainCollectionFragmentBinding
import com.everfrost.remak.view.main.MainActivity

class MainCollectionFragment : Fragment(), CollectionRVAdapter.OnItemClickListener {
    private lateinit var binding: MainCollectionFragmentBinding
    private val viewModel: CollectionViewModel by viewModels {
        CollectionViewModelFactory(
            tokenRepository
        )
    }
    private lateinit var tokenRepository: TokenRepository
    private lateinit var adapter: CollectionRVAdapter
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tokenRepository =
            TokenRepository((requireActivity().application as com.everfrost.remak.App).dataStore)
        binding = MainCollectionFragmentBinding.inflate(inflater, container, false)
        viewModel.getCollectionList()
        //뒤로가기 시 홈 프래그먼트로 이동
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            (activity as MainActivity).binding.bottomNavigation.selectedItemId = R.id.homeFragment
            isEnabled = false
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val isDelete = data?.getBooleanExtra("isChange", true)
                    if (isDelete == true) {
                        viewModel.getCollectionList()
                    }
                }
            }
        viewModel.isCollectionEmpty.observe(viewLifecycleOwner) {
            if (it) {
                binding.emptyCollectionLayout.visibility = View.VISIBLE
                binding.collectionLayout.visibility = View.GONE
            } else {
                binding.emptyCollectionLayout.visibility = View.GONE
                binding.collectionLayout.visibility = View.VISIBLE
            }
        }

        val recyclerView = binding.collectionRecyclerView
        adapter = CollectionRVAdapter(listOf(), this)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(SpacingItemDecorator(30))
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)



        viewModel.collectionList.observe(viewLifecycleOwner) {
            adapter.collectionData = it
            adapter.notifyDataSetChanged()
        }

        viewModel.isActionComplete.observe(viewLifecycleOwner) {
            viewModel.getCollectionList()
        }

        binding.addBtn.setOnClickListener {
            val intent = Intent(requireContext(), AddCollectionActivity::class.java)
            resultLauncher.launch(intent)
        }

        binding.addCollectionButton.setOnClickListener {
            val intent = Intent(requireContext(), AddCollectionActivity::class.java)
            resultLauncher.launch(intent)
        }
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(requireContext(), CollectionDetailActivity::class.java)
        intent.putExtra("collectionName", viewModel.collectionList.value!![position].name)
        intent.putExtra("collectionCount", viewModel.collectionList.value!![position].count)
        intent.putExtra(
            "collectionDescription",
            viewModel.collectionList.value!![position].description
        )
        resultLauncher.launch(intent)
    }

    override fun onItemLongClick(position: Int) {
        UtilityDialog.showCollectionLongClickDialog(
            requireContext(),
            editBtnClick = {
                val intent = Intent(requireContext(), UpdateCollectionActivity::class.java)
                intent.putExtra("collectionName", viewModel.collectionList.value!![position].name)
                intent.putExtra(
                    "collectionDescription",
                    viewModel.collectionList.value!![position].description
                )
                resultLauncher.launch(intent)
            },
            deleteBtnClick = {
                UtilityDialog.showWarnDialog(
                    requireContext(),
                    "정말 삭제하시겠어요?",
                    "삭제시 복구가 불가능해요",
                    "삭제하기",
                    "취소하기",
                    confirmClick = {
                        viewModel.deleteCollection(viewModel.collectionList.value!![position].name)
                    },
                    cancelClick = {}
                )
            }
        )
    }

}