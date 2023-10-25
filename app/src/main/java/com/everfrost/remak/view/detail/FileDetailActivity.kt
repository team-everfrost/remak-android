package com.everfrost.remak.view.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.everfrost.remak.R
import com.everfrost.remak.UtilityDialog
import com.everfrost.remak.adapter.LinkTagRVAdapter
import com.everfrost.remak.adapter.SpacingItemDecoration
import com.everfrost.remak.dataStore.TokenRepository
import com.everfrost.remak.databinding.DetailPageFileActivityBinding
import com.everfrost.remak.view.collection.EditCollectionBottomSheetDialog
import com.everfrost.remak.view.tag.TagDetailActivity
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
        tokenRepository = TokenRepository((this.application as com.everfrost.remak.App).dataStore)
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
            val popupView = layoutInflater.inflate(R.layout.custom_popup_menu_image_and_file, null)
            val popupWindow = PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            popupWindow.isFocusable = true
            popupWindow.isOutsideTouchable = true
            val addBtn: TextView = popupView.findViewById(R.id.addBtn)
            val deleteBtn: TextView = popupView.findViewById(R.id.deleteBtn)
            addBtn.setOnClickListener {
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
                popupWindow.dismiss()
            }
            deleteBtn.setOnClickListener {
                UtilityDialog.showWarnDialog(
                    this,
                    "파일을 삭제하시겠습니까?",
                    "삭제시 복구가 불가능해요",
                    "삭제하기",
                    "취소하기",
                    confirmClick = {
                        viewModel.deleteDocument(fileId)
                        finish()
                    },
                    cancelClick = {}
                )
                popupWindow.dismiss()
            }
            popupWindow.showAsDropDown(it)

        }
        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.shareIcon.setOnClickListener {
            viewModel.shareFile(this, fileId, fileName)
        }
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, TagDetailActivity::class.java)
        intent.putExtra("tagName", viewModel.detailData.value!!.tags[position])
        startActivity(intent)
    }

}