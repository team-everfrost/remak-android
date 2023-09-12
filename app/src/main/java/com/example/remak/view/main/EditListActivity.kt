package com.example.remak.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.remak.App
import com.example.remak.UtilityDialog
import com.example.remak.adapter.EditListItemOffsetDecoration
import com.example.remak.adapter.EditListRVAdapter
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.EditListPageActivityBinding

class EditListActivity : AppCompatActivity() {
    private lateinit var binding: EditListPageActivityBinding
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(
            tokenRepository
        )
    }
    private lateinit var adapter: EditListRVAdapter
    private var initialLoad = true
    lateinit var tokenRepository: TokenRepository
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenRepository = TokenRepository((application as App).dataStore)
        binding = EditListPageActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = EditListRVAdapter(mutableListOf())
        recyclerView = binding.editListRecyclerView
        recyclerView.addItemDecoration(EditListItemOffsetDecoration(20, adapter))
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerView.adapter = adapter
        viewModel.getAllMainList()


        viewModel.mainListData.observe(this) { data ->
            adapter.dataSet = data
            if (initialLoad) { //첫 로드일 경우
                adapter.notifyDataSetChanged()
            }
        }

        binding.deleteBtn.setOnClickListener {
            val selectedItems = adapter.getSelectedItems()
            Log.d("selectedItems", selectedItems.toString())
        }

        binding.addBtn.setOnClickListener {
            val bundle = Bundle()
            val selectedItems = adapter.getSelectedItems()
            if (selectedItems.isNotEmpty()) {
                bundle.putStringArrayList("selected", selectedItems)
                val bottomSheet = EditCollectionBottomSheetDialog()
                bottomSheet.arguments = bundle
                bottomSheet.show(supportFragmentManager, "EditCollectionBottomSheetDialog")
            }
        }

        binding.deleteBtn.setOnClickListener {
            val selectedItemCount = adapter.getSelectedItemsCount()
            val selectedItems = adapter.getSelectedItems()
            Log.d("selectedItemCount", selectedItemCount.toString())
            if (selectedItemCount != 0) {
                UtilityDialog.showWarnDialog(this, "${selectedItemCount}개의 정보를 삭제하시겠어요?",
                    "삭제시 복구가 불가능해요",
                    confirmClick = {
                        for (item in selectedItems) {
                            viewModel.deleteDocument(item)
                        }
                    },
                    cancelClick = {}
                )
            }
        }

        onBackPressedDispatcher.addCallback(this) {
            val resultIntent = Intent()
            resultIntent.putExtra("isDelete", true)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        binding.backBtn.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("isDelete", true)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

}