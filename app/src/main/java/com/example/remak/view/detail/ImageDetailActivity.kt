package com.example.remak.view.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.remak.App
import com.example.remak.adapter.LinkTagRVAdapter
import com.example.remak.adapter.SpacingItemDecoration
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.DetailPageImageActivityBinding
import com.example.remak.view.tag.TagDetailActivity
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class ImageDetailActivity : AppCompatActivity(), LinkTagRVAdapter.OnItemClickListener {
    private lateinit var binding: DetailPageImageActivityBinding
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
            Log.d("url", it.toString())
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
                com.example.remak.R.drawable.item_link_radius_whitegray
            )

    }


    override fun onItemClick(position: Int) {
        val intent = Intent(this, TagDetailActivity::class.java)
        intent.putExtra("tagName", viewModel.detailData.value!!.tags[position])
        startActivity(intent)
    }
}