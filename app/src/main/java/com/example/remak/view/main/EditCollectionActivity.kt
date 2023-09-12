package com.example.remak.view.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.remak.App
import com.example.remak.UtilityDialog
import com.example.remak.adapter.EditCollectionRVAdapter
import com.example.remak.adapter.TagDetailItemOffsetDecoration
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.EditCollectionActivityBinding
import com.example.remak.view.detail.DetailViewModel
import com.example.remak.view.detail.DetailViewModelFactory

class EditCollectionActivity : AppCompatActivity() {
    private lateinit var binding: EditCollectionActivityBinding
    private val viewModel: DetailViewModel by viewModels {
        DetailViewModelFactory(
            tokenRepository
        )
    }
    private lateinit var adapter: EditCollectionRVAdapter
    private var initialLoad = true
    lateinit var tokenRepository: TokenRepository
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenRepository = TokenRepository((application as App).dataStore)
        binding = EditCollectionActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val collectionName = intent.getStringExtra("collectionName")
        val collectionCount = intent.getIntExtra("collectionCount", 0)
        adapter = EditCollectionRVAdapter(mutableListOf()) { isChecked ->
            if (isChecked) {
                viewModel.increaseSelectionCount()
            } else {
                viewModel.decreaseSelectionCount()
            }
        }
        recyclerView = binding.collectionDetailRecyclerView
        recyclerView.addItemDecoration(TagDetailItemOffsetDecoration(20))
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerView.adapter = adapter
        viewModel.getCollectionDetailData(collectionName!!)
        viewModel.collectionDetailData.observe(this) { data ->
            binding.collectionName.text = "${collectionName} (${data.size})"
            adapter.dataSet = data

            adapter.notifyDataSetChanged()

        }

        viewModel.selectedItemsCount.observe(this) {
            binding.selectedCount.text = "${it}개 선택됨"
            binding.deleteBtn.isEnabled = it != 0
        }

        binding.deleteBtn.setOnClickListener {
            val selectedItemCount = viewModel.selectedItemsCount.value
            val selectedItems = adapter.getSelectedItems()
            UtilityDialog.showWarnDialog(
                this,
                "${selectedItemCount}개의 정보를 삭제하시겠어요?",
                "삭제시 복구가 불가능해요?",
                confirmClick = {
                    viewModel.removeDataInCollection(collectionName, selectedItems)
                    adapter.notifyDataSetChanged()
                    viewModel.resetSelectionCount()

                },
                cancelClick = {},
            )
        }

        onBackPressedDispatcher.addCallback(this) {
            val resultIntent = Intent()
            resultIntent.putExtra("isChange", true)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

}