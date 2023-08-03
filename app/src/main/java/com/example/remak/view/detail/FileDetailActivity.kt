package com.example.remak.view.detail

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.remak.App
import com.example.remak.UtilityDialog
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.DetailPageFileActivityBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class FileDetailActivity : AppCompatActivity() {


    private lateinit var extension : String
    private lateinit var binding : DetailPageFileActivityBinding
    private val viewModel : DetailViewModel by viewModels { DetailViewModelFactory(tokenRepository)}
    private lateinit var tokenRepository: TokenRepository
    private lateinit var fileName : String
    private var url : String? = null

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
            url = it.url
            binding.titleEditText.setText(it.title!!.substringBefore("."))
            extension = it.title.substringAfter(".")
            val date = inputFormat.parse(it.updatedAt)
            val outputDateStr = outputFormat.format(date)
            binding.dateTextView.text = outputDateStr

            fileName = it.title!!
        }

        binding.downloadBtn.setOnClickListener {
            viewModel.downloadFile(this, fileId, fileName)
        }

        binding.editIcon.setOnClickListener {
            binding.titleEditText.isEnabled = true
            binding.editIcon.visibility = View.GONE
            binding.completeBtn.visibility = View.VISIBLE
            binding.moreIcon.visibility = View.GONE
            binding.shareIcon.visibility = View.GONE
        }

        binding.completeBtn.setOnClickListener {
            UtilityDialog.showWarnDialog(this, "제목을 수정하시겠습니까?", confirmClick = {
                binding.titleEditText.isEnabled = false
                binding.completeBtn.visibility = View.GONE
                binding.editIcon.visibility = View.VISIBLE
                binding.moreIcon.visibility = View.VISIBLE
                binding.shareIcon.visibility = View.VISIBLE
            }, cancelClick = {
                //do nothing
            })
        }

  

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.shareBtn.setOnClickListener {
            viewModel.shareFile(this, fileId)
        }
    }


}