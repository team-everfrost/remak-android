package com.example.remak.view.detail

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.example.remak.App
import com.example.remak.R
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

        binding.movePageBtn.setOnClickListener {
            val colorSchemeParams = CustomTabColorSchemeParams.Builder()
                .setToolbarColor(ContextCompat.getColor(this, R.color.black))
                .build()

            val customTabsIntent = CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(colorSchemeParams)
                .build()

            customTabsIntent.launchUrl(this, Uri.parse(url))
        }

        binding.editBtn.setOnClickListener {
            binding.title.isEnabled = true
            binding.editBtn.visibility = View.GONE
            binding.completeBtn.visibility = View.VISIBLE

            binding.backBtn.visibility = View.GONE
            binding.deleteBtn.visibility = View.VISIBLE
        }

        binding.deleteBtn.setOnClickListener {
            showWarnDialog("파일을 삭제하시겠습니까?", linkId, "delete")
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
                binding.title.isEnabled = false
                binding.completeBtn.visibility = View.GONE
                binding.editBtn.visibility = View.VISIBLE

                binding.deleteBtn.visibility = View.GONE
                binding.backBtn.visibility = View.VISIBLE

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