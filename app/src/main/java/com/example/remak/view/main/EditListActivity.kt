package com.example.remak.view.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.remak.App
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
            bundle.putStringArrayList("selected", adapter.getSelectedItems())
            val bottomSheet = EditCollectionBottomSheetDialog()
            bottomSheet.arguments = bundle
            bottomSheet.show(supportFragmentManager, "EditCollectionBottomSheetDialog")
        }

        onBackPressedDispatcher.addCallback(this) {
            val resultIntent = Intent()
            resultIntent.putExtra("isDelete", true)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

}