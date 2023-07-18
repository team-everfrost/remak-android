package com.example.remak.view.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.remak.App
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.DetailPageLinkActivityBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class LinkDetailActivity : AppCompatActivity() {
    private lateinit var binding : DetailPageLinkActivityBinding
    private val viewModel : DetailViewModel by viewModels { DetailViewModelFactory(tokenRepository)}
    lateinit var tokenRepository: TokenRepository
    lateinit var url : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" , Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())

        tokenRepository = TokenRepository((this.application as App).dataStore)
        binding = DetailPageLinkActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val linkId = intent.getStringExtra("docId")
        viewModel.getDetailData(linkId!!)

        viewModel.detailData.observe(this) {
            binding.url.text = it.url
            url = it.url!!
            val date = inputFormat.parse(it.updatedAt)
            val outputDateStr = outputFormat.format(date)
            binding.date.text = outputDateStr
            Log.d("dataCheck", "dataChanged")
        }

        binding.shareBtn.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, url)
            }
            startActivity(Intent.createChooser(shareIntent, "Share link"))

        }

    }
}