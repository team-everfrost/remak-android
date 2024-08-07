package com.everfrost.remak.view.collection

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everfrost.remak.R
import com.everfrost.remak.UtilityDialog
import com.everfrost.remak.UtilityRV
import com.everfrost.remak.adapter.CollectionListRVAdapter
import com.everfrost.remak.dataStore.TokenRepository
import com.everfrost.remak.databinding.DetailPageCollectionActivityBinding
import com.everfrost.remak.view.detail.FileDetailActivity
import com.everfrost.remak.view.detail.ImageDetailActivity
import com.everfrost.remak.view.detail.LinkDetailActivity
import com.everfrost.remak.view.detail.MemoDetailActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CollectionDetailActivity : AppCompatActivity(), CollectionListRVAdapter.OnItemClickListener {
    private lateinit var binding: DetailPageCollectionActivityBinding
    private val viewModel: CollectionViewModel by viewModels()
    lateinit var tokenRepository: TokenRepository
    private lateinit var adapter: CollectionListRVAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DetailPageCollectionActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val collectionName = intent.getStringExtra("collectionName")
        val collectionDescription = intent.getStringExtra("collectionDescription")

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

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = recyclerView.layoutManager?.itemCount
                val lastVisibleItemCount =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                if (totalItemCount!! <= (lastVisibleItemCount + 5)) {
                    viewModel.getNewCollectionDetailData(collectionName)
                }
            }
        })

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val isDelete = data?.getBooleanExtra("isChange", false)
                    if (isDelete == true) {
                        val newName = data.getStringExtra("newName")
                        Log.d("isDelete", isDelete.toString())
                        viewModel.getCollectionDetailData(collectionName)
                        setTruncatedText(
                            newName!!,
                            binding.collectionName,
                            get70PercentScreenWidth(this)
                        )
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
            setTruncatedText(
                collectionName,
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
            adapter.toggleSelectionMode()
            showEditModeView()
        }

        binding.moreIcon.setOnClickListener {
            val popupView = layoutInflater.inflate(R.layout.custom_popup_menu_image_and_file, null)
            val popupWindow = PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            popupWindow.isFocusable = true
            popupWindow.isOutsideTouchable = true
            val editBtn: TextView = popupView.findViewById(R.id.addBtn)
            val deleteBtn: TextView = popupView.findViewById(R.id.deleteBtn)
            editBtn.text = "컬렉션 수정"
            deleteBtn.text = "컬렉션 삭제"

            editBtn.setOnClickListener {
                val intent = Intent(this, UpdateCollectionActivity::class.java)
                intent.putExtra("collectionName", collectionName)
                intent.putExtra("collectionDescription", collectionDescription)
                resultLauncher.launch(intent)
                popupWindow.dismiss()
            }
            deleteBtn.setOnClickListener {
                UtilityDialog.showWarnDialog(
                    this,
                    "정말 삭제하시겠어요?",
                    "삭제 시 복구가 불가능해요",
                    "삭제하기",
                    "취소하기",
                    confirmClick = {
                        viewModel.deleteCollection(collectionName)

                    },
                    cancelClick = {}
                )
                popupWindow.dismiss()
            }
            popupWindow.showAsDropDown(it)
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
                "나중에 다시 추가할 수 있어요",
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

    private fun setTruncatedText(name: String, textView: TextView, maxWidth: Float) {
        var finalText = name

        // 문자열이 TextView의 최대 너비보다 큰지 확인
        if (textView.paint.measureText(finalText) > maxWidth) {
            // "..."와 개수(suffix)의 길이를 포함하여 이름을 줄입니다.
            var truncatedName = name
            while (textView.paint.measureText("$truncatedName...") > maxWidth && truncatedName.isNotEmpty()) {
                truncatedName = truncatedName.dropLast(1)
            }
            finalText = "$truncatedName..."
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

    override fun onItemLongClick(position: Int) {
        if (!adapter.isSelectionMode()) {
            adapter.toggleSelectionMode()
            showEditModeView()
        }
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