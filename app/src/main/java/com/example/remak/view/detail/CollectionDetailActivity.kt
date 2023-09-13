package com.example.remak.view.detail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
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