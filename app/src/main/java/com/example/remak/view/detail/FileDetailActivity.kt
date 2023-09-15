package com.example.remak.view.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.example.remak.App
import com.example.remak.R
import com.example.remak.UtilityDialog
import com.example.remak.adapter.LinkTagRVAdapter
import com.example.remak.adapter.SpacingItemDecoration
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.DetailPageFileActivityBinding
import com.example.remak.view.main.EditCollectionBottomSheetDialog
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class FileDetailActivity : AppCompatActivity(), LinkTagRVAdapter.OnItemClickListener {

    private lateinit var extension: String
    private lateinit var binding: DetailPageFileActivityBinding
    private val viewModel: DetailViewModel by viewModels { DetailViewModelFactory(tokenRepository) }
    private lateinit var tokenRepository: TokenRepository
    private lateinit var fileName: String
    private var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        tokenRepository = TokenRepository((this.application as App).dataStore)
        binding = DetailPageFileActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fileId = intent.getStringExtra("docId")
        viewModel.getDetailData(fileId!!)

        val flexboxLayoutManager = FlexboxLayoutManager(this).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
        }

        val adapter = LinkTagRVAdapter(listOf(), this)
        val itemDecoration = SpacingItemDecoration(10, 10)
        binding.tagRV.addItemDecoration(itemDecoration)
        binding.tagRV.layoutManager = flexboxLayoutManager
        binding.tagRV.adapter = adapter


        viewModel.isActionComplete.observe(this) {
            if (it) {
                val resultIntent = Intent()
                resultIntent.putExtra("isDelete", true)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
        viewModel.detailData.observe(this) {
            url = it.url
            var summary = it.summary
            //summary의 첫 한줄은 제거
            if (summary != null) {
                summary = summary.substringAfter('\n').trim()
            }
            binding.summaryTextView.text = summary
            binding.titleEditText.setText(it.title!!)
            extension = it.title.substringAfter(".")
            val date = inputFormat.parse(it.updatedAt)
            val outputDateStr = outputFormat.format(date)
            binding.dateTextView.text = outputDateStr
            fileName = it.title
            adapter.tags = it.tags
            adapter.notifyDataSetChanged()
        }

        binding.downloadBtn.setOnClickListener {
            viewModel.downloadFile(this, fileId, fileName)
        }


        binding.completeBtn.setOnClickListener {
            UtilityDialog.showWarnDialog(
                this,
                "제목을 수정하시겠습니까?",
                "",
                "네",
                "아니오",
                confirmClick = {
                    binding.titleEditText.isEnabled = false
                    binding.completeBtn.visibility = View.GONE
                    binding.moreIcon.visibility = View.VISIBLE
                    binding.shareIcon.visibility = View.VISIBLE
                }, cancelClick = {
                    //do nothing
                })
        }
        binding.moreIcon.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.menuInflater.inflate(R.menu.detail_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.addCollection -> {
                        val bundle = Bundle()
                        val selectedItems = ArrayList<String>()
                        selectedItems.add(fileId)
                        if (selectedItems.isNotEmpty()) {
                            bundle.putStringArrayList("selected", selectedItems)
                            bundle.putString("type", "detail")
                            val bottomSheet = EditCollectionBottomSheetDialog()
                            bottomSheet.arguments = bundle
                            bottomSheet.show(
                                supportFragmentManager,
                                "EditCollectionBottomSheetDialog"
                            )
                        }
                        true
                    }

                    R.id.removeBtn -> {
                        UtilityDialog.showWarnDialog(
                            this,
                            "파일을 삭제하시겠습니까?",
                            "삭제시 복구가 불가능해요",
                            "삭제하기",
                            "취소하기",
                            confirmClick = {
                                viewModel.deleteDocument(fileId)
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
        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.shareIcon.setOnClickListener {
            viewModel.shareFile(this, fileId)
        }
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, TagDetailActivity::class.java)
        intent.putExtra("tagName", viewModel.detailData.value!!.tags[position])
        startActivity(intent)
    }

}