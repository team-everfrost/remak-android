package com.example.remak.view.detail

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
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
            showWarnDialog("제목을 수정하시겠습니까?", fileId, "update")
        }

  

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.shareBtn.setOnClickListener {
            viewModel.shareFile(this, fileId)
        }


    }





    private fun showWarnDialog(getContent : String, fileId : String, type : String) {
        val dialog = Dialog(this)
        val windowManager =
            this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog_warning)

        if (Build.VERSION.SDK_INT < 30) {
            val display = windowManager.defaultDisplay
            val size = Point()

            display.getSize(size)

            val window = dialog.window
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val x = (size.x * 0.85).toInt()


            window?.setLayout(x, WindowManager.LayoutParams.WRAP_CONTENT)

        } else {
            val rect = windowManager.currentWindowMetrics.bounds

            val window = dialog.window
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val x = (rect.width() * 0.85).toInt()
            window?.setLayout(x, WindowManager.LayoutParams.WRAP_CONTENT)

        }
        val confirmBtn = dialog.findViewById<View>(R.id.confirmBtn)
        val cancelBtn = dialog.findViewById<View>(R.id.cancelBtn)
        val content = dialog.findViewById<TextView>(R.id.msgTextView)
        content.text = getContent

        confirmBtn.setOnClickListener {
            if (type == "update") {
//                viewModel.updateMemo(memoId, binding.memoContent.text.toString())
                binding.titleEditText.isEnabled = false
                binding.completeBtn.visibility = View.GONE
                binding.editIcon.visibility = View.VISIBLE
                binding.moreIcon.visibility = View.VISIBLE
                binding.shareIcon.visibility = View.VISIBLE

            } else {
                viewModel.deleteDocument(fileId)
                finish()
            }

            dialog.dismiss()

        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()


    }
}