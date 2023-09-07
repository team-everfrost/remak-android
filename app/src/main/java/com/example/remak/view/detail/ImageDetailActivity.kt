package com.example.remak.view.detail

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.remak.App
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.DetailPageImageActivityBinding

class ImageDetailActivity : AppCompatActivity() {
    private lateinit var binding: DetailPageImageActivityBinding
    private val viewModel: DetailViewModel by viewModels { DetailViewModelFactory(tokenRepository) }
    private lateinit var tokenRepository: TokenRepository
    private var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenRepository = TokenRepository((this.application as App).dataStore)
        binding = DetailPageImageActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val imageId = intent.getStringExtra("docId")
        viewModel.getDetailData(imageId!!)

        viewModel.detailData.observe(this) {
            url = it.url
        }

        binding.shareBtn.setOnClickListener {
            viewModel.shareFile(this, imageId)
        }
    }
}