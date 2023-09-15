package com.example.remak.view.collection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.remak.App
import com.example.remak.UtilityDialog
import com.example.remak.UtilityRV
import com.example.remak.adapter.EditCollectionRVAdapter
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.EditCollectionActivityBinding

class EditCollectionActivity : AppCompatActivity() {
    private lateinit var binding: EditCollectionActivityBinding
    private val viewModel: CollectionViewModel by viewModels {
        CollectionViewModelFactory(
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
        recyclerView.addItemDecoration(UtilityRV.CardItemOffsetDecoration(20))
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerView.adapter = adapter
        viewModel.getCollectionDetailData(collectionName!!)
        viewModel.collectionDetailData.observe(this) { data ->
//            binding.collectionName.text = "${collectionName} (${data.size})"
            setTruncatedText(
                collectionName,
                data.size,
                binding.collectionName,
                get70PercentScreenWidth(this)
            )
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
                "삭제하기",
                "취소하기",
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

}