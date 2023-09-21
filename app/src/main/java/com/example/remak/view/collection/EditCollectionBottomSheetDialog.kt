package com.example.remak.view.collection

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
    var onDismissCallback: (() -> Unit)? = null
    private var isChange: Boolean = false

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
        val checkType =
            arguments?.getString("type", "edit") //edit / detail 타입 있음 detail일 경우 상위 액티비티 닫지말것

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

        viewModel.isUpdateComplete.observe(this) {
            if (it) {
                if (checkType == "detail") {
                    this.dismiss()
                } else {
                    val resultIntent = Intent()
                    resultIntent.putExtra("isDelete", true)
                    requireActivity().setResult(Activity.RESULT_OK, resultIntent)
                    requireActivity().finish()
                }
                Toast.makeText(requireContext(), "컬렉션에 추가되었습니다.", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(requireContext(), "컬렉션 추가에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.completeBtn.setOnClickListener {
            isChange = true
            val selectedItems = adapter.getSelectedItem()
            Log.d("selectedItems", selectedItems.toString())
            if (selectedItems.isNotEmpty()) {
                viewModel.addDataInCollection(selectedItems, checkedDocuments!!.toList())

            } else {
                this.dismiss()
            }
        }

        binding.closeBtn.setOnClickListener {
            this.dismiss()
        }
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (isChange) {
            onDismissCallback?.invoke()

        }
    }

}