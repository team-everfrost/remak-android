package com.example.remak.view.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
            }
            binding.titleEditText.setText(it.title!!)
            val date = inputFormat.parse(it.updatedAt)
            val outputDateStr = outputFormat.format(date)
            binding.dateTextView.text = outputDateStr
            fileName = it.title
            adapter.tags = it.tags
            adapter.notifyDataSetChanged()
        }

        binding.shareIcon.setOnClickListener {
            viewModel.shareFile(this, imageId)
        }
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, TagDetailActivity::class.java)
        intent.putExtra("tagName", viewModel.detailData.value!!.tags[position])
        startActivity(intent)
    }
}