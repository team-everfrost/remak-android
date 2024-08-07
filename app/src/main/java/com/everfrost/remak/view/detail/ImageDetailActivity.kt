package com.everfrost.remak.view.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.everfrost.remak.R
import com.everfrost.remak.UtilityDialog
import com.everfrost.remak.adapter.LinkTagRVAdapter
import com.everfrost.remak.adapter.SpacingItemDecoration
import com.everfrost.remak.dataStore.TokenRepository
import com.everfrost.remak.databinding.DetailPageImageActivityBinding
import com.everfrost.remak.view.collection.EditCollectionBottomSheetDialog
import com.everfrost.remak.view.tag.TagDetailActivity
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


@AndroidEntryPoint
class ImageDetailActivity : AppCompatActivity(), LinkTagRVAdapter.OnItemClickListener {
    private lateinit var binding: DetailPageImageActivityBinding
    private val viewModel: DetailViewModel by viewModels()
    private lateinit var tokenRepository: TokenRepository
    private lateinit var fileName: String
    private var url: String? = null
    private lateinit var fileId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        binding = DetailPageImageActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val imageId = intent.getStringExtra("docId")
        val adapter = LinkTagRVAdapter(listOf(), this)
        val itemDecoration = SpacingItemDecoration(10, 10)
        val flexboxLayoutManager = FlexboxLayoutManager(this).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
        }
        binding.tagRV.addItemDecoration(itemDecoration)
        binding.tagRV.layoutManager = flexboxLayoutManager
        binding.tagRV.adapter = adapter
        viewModel.getDetailData(imageId!!)

        viewModel.detailData.observe(this) {
            url = it.url
            fileId = it.docId!!
            var summary = it.summary
            //summary의 첫 한줄은 제거
            if (summary != null) {
                summary = summary.substringAfter('\n').trim()
                binding.summaryTextView.text = summary
            }
            binding.titleEditText.setText(it.title!!)
            val date = inputFormat.parse(it.updatedAt)
            val outputDateStr = outputFormat.format(date)
            binding.dateTextView.text = outputDateStr
            fileName = it.title
            adapter.tags = it.tags
            adapter.notifyDataSetChanged()
            setThumbnail(it.thumbnailUrl!!)
        }

        viewModel.isActionComplete.observe(this) {
            if (it) {
                val resultIntent = Intent()
                resultIntent.putExtra("isDelete", true)
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }

        binding.downloadBtn.setOnClickListener {
            viewModel.downloadFile(this, imageId, fileName)
        }

        binding.shareIcon.setOnClickListener {
            viewModel.shareFile(this, imageId, fileName)
            Log.d(fileName, "fileName")
        }

        viewModel.isGetImageUrlSuccess.observe(this) {
            if (it) {
                val imageUrl = viewModel.imageUrl.value
                val intent = Intent(this, ImageViewerActivity::class.java)
                intent.putExtra("imageUrl", imageUrl)
                intent.putExtra("fileName", fileName)
                startActivity(intent)
            }
        }

        viewModel.isImageShareReady.observe(this) {
            if (it) {
                binding.progressBar.visibility = android.view.View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            } else {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
                binding.progressBar.visibility = android.view.View.VISIBLE
            }
        }

        binding.thumbnail.setOnClickListener {
            viewModel.getImageUrl(imageId)
        }

        binding.backBtn.setOnClickListener {
            finish()
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
                    },
                    cancelClick = {}
                )
                popupWindow.dismiss()
            }
            popupWindow.showAsDropDown(it)
        }
    }

    private fun setThumbnail(thumbnailUrl: String) {
        Log.d("thumbnailUrl", thumbnailUrl)
        Glide.with(this)
            .load(thumbnailUrl)
            .transform(CenterCrop(), RoundedCorners(47))
            .into(binding.thumbnail)
        binding.thumbnail.background =
            AppCompatResources.getDrawable(
                this,
                R.drawable.item_link_radius_whitegray
            )

    }


    override fun onItemClick(position: Int) {
        val intent = Intent(this, TagDetailActivity::class.java)
        intent.putExtra("tagName", viewModel.detailData.value!!.tags[position])
        startActivity(intent)
    }
}
