package com.example.remak.view.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.remak.App
import com.example.remak.R
import com.example.remak.adapter.AddCollectionRVAdapter
import com.example.remak.adapter.SearchHistoryItemOffsetDecoration
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.AddCollectionBottomSheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditCollectionBottomSheetDialog : BottomSheetDialogFragment() {
    private lateinit var binding: AddCollectionBottomSheetDialogBinding
    private val viewModel: CollectionViewModel by viewModels {
        CollectionViewModelFactory(
            tokenRepository
        )
    }
    private lateinit var adapter: AddCollectionRVAdapter
    lateinit var tokenRepository: TokenRepository
    private var isEmpty: Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tokenRepository = TokenRepository((requireActivity().application as App).dataStore)
        binding = AddCollectionBottomSheetDialogBinding.inflate(inflater, container, false)
        viewModel.getCollectionList()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val checkedDocuments = arguments?.getStringArrayList("selected")

        viewModel.isCollectionEmpty.observe(viewLifecycleOwner) {
            if (it) {
                binding.emptyCollectionLayout.visibility = View.VISIBLE
                binding.collectionRecyclerView.visibility = View.GONE
                binding.completeBtn.text = "확인"
                isEmpty = true
            } else {
                binding.emptyCollectionLayout.visibility = View.GONE
                binding.collectionRecyclerView.visibility = View.VISIBLE
                binding.completeBtn.text = "등록하기"
                isEmpty = false
            }
        }
        val recyclerView = binding.collectionRecyclerView
        adapter = AddCollectionRVAdapter(listOf())
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(SearchHistoryItemOffsetDecoration(20))
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            requireContext(),
            androidx.recyclerview.widget.LinearLayoutManager.VERTICAL,
            false
        )

        viewModel.collectionList.observe(viewLifecycleOwner) {
            adapter.collectionData = it
            adapter.notifyDataSetChanged()
        }

        binding.completeBtn.setOnClickListener {
            Log.d("collection", adapter.getSelectedItem().toString())
            Log.d("collection", checkedDocuments.toString())
        }

        binding.closeBtn.setOnClickListener {
            this.dismiss()
        }
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

}