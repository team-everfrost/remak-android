package com.example.remak.view.detail

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.remak.App
import com.example.remak.R
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.DetailPageFileActivityBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class FileDetailActivity : AppCompatActivity() {



    private lateinit var binding : DetailPageFileActivityBinding

    private val viewModel : DetailViewModel by viewModels { DetailViewModelFactory(tokenRepository)}

    lateinit var tokenRepository: TokenRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" , Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val outputFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())


        tokenRepository = TokenRepository((this.application as App).dataStore)
        binding = DetailPageFileActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fileId = intent.getStringExtra("docId")
        viewModel.getDetailData(fileId!!)

        viewModel.detailData.observe(this) {
            binding.titleEditText.setText(it.title)
            val date = inputFormat.parse(it.updatedAt)
            val outputDateStr = outputFormat.format(date)
            binding.dateTextView.text = outputDateStr
        }


    }
}