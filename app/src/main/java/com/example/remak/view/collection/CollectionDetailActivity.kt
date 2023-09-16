package com.example.remak.view.collection

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.TextView
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
import com.example.remak.UtilityRV
import com.example.remak.adapter.CollectionListRVAdapter
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.DetailPageCollectionActivityBinding
import com.example.remak.view.detail.FileDetailActivity
import com.example.remak.view.detail.ImageDetailActivity
import com.example.remak.view.detail.LinkDetailActivity
import com.example.remak.view.detail.MemoDetailActivity

class CollectionDetailActivity : AppCompatActivity(), CollectionListRVAdapter.OnItemClickListener {
    private lateinit var binding: DetailPageCollectionActivityBinding
    private val viewModel: CollectionViewModel by viewModels {
        CollectionViewModelFactory(
            tokenRepository
        )
    }
    lateinit var tokenRepository: TokenRepository
    private lateinit var adapter: CollectionListRVAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenRepository = TokenRepository((this.application as App).dataStore)
        binding = DetailPageCollectionActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val collectionName = intent.getStringExtra("collectionName")
        val collectionCount = intent.getIntExtra("collectionCount", 0)

        adapter = CollectionListRVAdapter(listOf(), this) { isChecked ->
            if (isChecked) {
                viewModel.increaseSelectionCount()
            } else {
                viewModel.decreaseSelectionCount()
            }
        }
        recyclerView = binding.collectionDetailRecyclerView
        recyclerView.adapter = adapter
        val itemDecorator = UtilityRV.CardItemOffsetDecoration(10)
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
                binding.emptyCollectionLayout.visibility = View.VISIBLE
                binding.collectionDetailLayout.visibility = View.GONE
            } else {
                binding.emptyCollectionLayout.visibility = View.GONE
                binding.collectionDetailLayout.visibility = View.VISIBLE
            }
        }

        viewModel.selectedItemsCount.observe(this) {
            binding.selectedCount.text = "${it}개 선택됨"
            binding.deleteBtn.isEnabled = it != 0
        }

        viewModel.collectionDetailData.observe(this) {
//            binding.collectionName.text = "${collectionName} (${it.size})"
            setTruncatedText(
                collectionName,
                it.size,
                binding.collectionName,
                get70PercentScreenWidth(this)
            )
            adapter.dataSet = it
            adapter.notifyDataSetChanged()
        }

        viewModel.isActionComplete.observe(this) {
            if (it) {
                closeActivity()
            }
        }

        binding.editBtn.setOnClickListener {
//            val intent = Intent(this, EditCollectionActivity::class.java)
//            intent.putExtra("collectionName", collectionName)
//            intent.putExtra("collectionCount", collectionCount)
//            resultLauncher.launch(intent)
            adapter.toggleSelectionMode()
            showEditModeView()
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
                            "삭제하기",
                            "취소하기",
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

        binding.backButton.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("isChange", true)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        binding.previousBtn.setOnClickListener {
            viewModel.resetSelectionCount()
            adapter.toggleSelectionMode()
            showNormalModeView()
        }

        binding.deleteBtn.setOnClickListener {
            val selectedItemCount = viewModel.selectedItemsCount.value
            val selectedItems = adapter.getSelectedItems()
            UtilityDialog.showWarnDialog(
                this,
                "${selectedItemCount}개의 정보를 삭제하시겠어요?",
                "삭제시 복구가 불가능해요?",
                "삭제하기",
                "취소하기",
                confirmClick = {
                    viewModel.removeDataInCollection(collectionName, selectedItems)
                    adapter.toggleSelectionMode()
                    showNormalModeView()
                    adapter.notifyDataSetChanged()
                    viewModel.resetSelectionCount()
                },
                cancelClick = {},
            )
        }


        onBackPressedDispatcher.addCallback(this) {
            if (adapter.isSelectionMode()) {
                viewModel.resetSelectionCount()
                adapter.toggleSelectionMode()
                showNormalModeView()
            } else {
                val resultIntent = Intent()
                resultIntent.putExtra("isChange", true)
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    private fun showEditModeView() {
        binding.selectedCount.alpha = 0f
        binding.selectedCount.animate().alpha(1f).duration = 200
        binding.previousBtn.alpha = 0f
        binding.previousBtn.animate().alpha(1f).duration = 200
        binding.deleteBtn.alpha = 0f
        binding.deleteBtn.animate().alpha(1f).duration = 200
        binding.editBtn.visibility = View.GONE
        binding.selectedCount.visibility = View.VISIBLE
        binding.previousBtn.visibility = View.VISIBLE
        binding.deleteBtn.visibility = View.VISIBLE
    }

    private fun showNormalModeView() {
        binding.editBtn.visibility = View.VISIBLE
        binding.selectedCount.visibility = View.GONE
        binding.previousBtn.visibility = View.GONE
        binding.deleteBtn.visibility = View.GONE
    }

    private fun setTruncatedText(name: String, count: Int, textView: TextView, maxWidth: Float) {
        val suffix = "($count)"
        var finalText = "$name $suffix"

        // 문자열이 TextView의 최대 너비보다 큰지 확인
        if (textView.paint.measureText(finalText) > maxWidth) {
            // "..."와 개수(suffix)의 길이를 포함하여 이름을 줄입니다.
            var truncatedName = name
            while (textView.paint.measureText("$truncatedName... $suffix") > maxWidth && truncatedName.isNotEmpty()) {
                truncatedName = truncatedName.dropLast(1)
            }
            finalText = "$truncatedName... $suffix"
        }

        textView.text = finalText
    }

    private fun get70PercentScreenWidth(context: Context): Float {
        if (android.os.Build.VERSION.SDK_INT < 30) {
            val windowManager =
                context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = windowManager.defaultDisplay
            val size = DisplayMetrics()
            display.getMetrics(size)
            val x = (size.widthPixels * 0.7).toInt()
            return x.toFloat()
        } else {
            val rect = windowManager.currentWindowMetrics.bounds
            val x = (rect.width() * 0.7).toInt()
            return x.toFloat()
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