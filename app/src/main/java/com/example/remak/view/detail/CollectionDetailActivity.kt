package com.example.remak.view.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.remak.App
import com.example.remak.R
import com.example.remak.UtilityDialog
import com.example.remak.adapter.TagDetailItemOffsetDecoration
import com.example.remak.adapter.TagDetailRVAdapter
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.DetailPageCollectionActivityBinding
import com.example.remak.view.main.EditCollectionActivity

class CollectionDetailActivity : AppCompatActivity(), TagDetailRVAdapter.OnItemClickListener {
    private lateinit var binding: DetailPageCollectionActivityBinding
    private val viewModel: DetailViewModel by viewModels { DetailViewModelFactory(tokenRepository) }
    lateinit var tokenRepository: TokenRepository
    private lateinit var adapter: TagDetailRVAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenRepository = TokenRepository((this.application as App).dataStore)
        binding = DetailPageCollectionActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val collectionName = intent.getStringExtra("collectionName")
        val collectionCount = intent.getIntExtra("collectionCount", 0)

        adapter = TagDetailRVAdapter(listOf(), this)
        recyclerView = binding.collectionDetailRecyclerView
        recyclerView.adapter = adapter
        val itemDecorator = TagDetailItemOffsetDecoration(10)
        recyclerView.addItemDecoration(itemDecorator)
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.getCollectionDetailData(collectionName!!)

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val isDelete = data?.getBooleanExtra("isChange", false)
                    if (isDelete == true) {
                        viewModel.getCollectionDetailData(collectionName)
                    }
                }
            }

        viewModel.isCollectionEmpty.observe(this) {
            if (it) {
                binding.emptyCollectionLayout.visibility = android.view.View.VISIBLE
                binding.collectionDetailLayout.visibility = android.view.View.GONE
            } else {
                binding.emptyCollectionLayout.visibility = android.view.View.GONE
                binding.collectionDetailLayout.visibility = android.view.View.VISIBLE
            }
        }

        viewModel.collectionDetailData.observe(this) {
            binding.collectionName.text = "${collectionName} (${it.size})"

            adapter.dataSet = it
            adapter.notifyDataSetChanged()
        }

        viewModel.isActionComplete.observe(this) {
            if (it) {
                closeActivity()
            }
        }

        binding.editBtn.setOnClickListener {
            val intent = Intent(this, EditCollectionActivity::class.java)
            intent.putExtra("collectionName", collectionName)
            intent.putExtra("collectionCount", collectionCount)
            resultLauncher.launch(intent)
        }

        binding.moreIcon.setOnClickListener {
            val popupMenu = android.widget.PopupMenu(this, it)
            popupMenu.menuInflater.inflate(R.menu.collection_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.editBtn -> {

                        true
                    }

                    R.id.removeBtn -> {
                        UtilityDialog.showWarnDialog(
                            this,
                            "정말 삭제하시겠어요?",
                            "삭제시 복구가 불가능해요",
                            confirmClick = {
                                viewModel.deleteCollection(collectionName)

                            },
                            cancelClick = {}
                        )
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
        }

        onBackPressedDispatcher.addCallback(this) {
            val resultIntent = Intent()
            resultIntent.putExtra("isChange", true)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

    }

    private fun closeActivity() {
        val resultIntent = Intent()
        resultIntent.putExtra("isChange", true)
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    override fun onItemClick(position: Int) {
        when (viewModel.collectionDetailData.value!![position].type) {
            "MEMO" -> {
                val intent = Intent(this, MemoDetailActivity::class.java)
                intent.putExtra("docId", viewModel.collectionDetailData.value!![position].docId)
                startActivity(intent)
            }

            "FILE" -> {
                val intent = Intent(this, FileDetailActivity::class.java)
                intent.putExtra("docId", viewModel.collectionDetailData.value!![position].docId)
                startActivity(intent)
            }

            "WEBPAGE" -> {
                val intent = Intent(this, LinkDetailActivity::class.java)
                intent.putExtra("docId", viewModel.collectionDetailData.value!![position].docId)
                startActivity(intent)
            }

            "IMAGE" -> {
                val intent = Intent(this, ImageDetailActivity::class.java)
                intent.putExtra("docId", viewModel.collectionDetailData.value!![position].docId)
                startActivity(intent)
            }
        }

    }

}