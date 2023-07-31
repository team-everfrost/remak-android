package com.example.remak.view.detail

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.BlurMaskFilter
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.PopupMenu
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
import com.example.remak.network.model.DetailData
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import org.apache.commons.text.StringEscapeUtils
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class LinkDetailActivity : AppCompatActivity() {
    private lateinit var binding: DetailPageLinkActivityBinding
    private val viewModel: DetailViewModel by viewModels { DetailViewModelFactory(tokenRepository) }
    lateinit var tokenRepository: TokenRepository
    lateinit var url: String
    private lateinit var linkData: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
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
            linkData = it.content
            binding.title.setText(it.title)

            // \n은 <br>로 바꾸고 \t는 스페이스바 4번으로 바꾸기
            linkData = linkData
                .replace(Regex("\\\\t"), "    ")
                .replace(Regex("\\\\n"), "<br>")
                .replace(Regex("\\\\r"), "<br>")
                .replace(Regex("#"), "%23")

            logLongMessage("dataCheck", it.toString())

            val date = inputFormat.parse(it.updatedAt)
            val outputDateStr = outputFormat.format(date)
            binding.date.text = outputDateStr
            if (it.status != "SCRAPE_PENDING" && it.status != "SCRAPE_PROCESSING") {
                binding.webView.visibility = View.VISIBLE
                binding.webView.apply {
                    overScrollMode = WebView.OVER_SCROLL_NEVER
                    isHorizontalScrollBarEnabled = false
                    isVerticalScrollBarEnabled = false
                }
                binding.webView.settings.javaScriptEnabled = true
                val css = """
                        <style type='text/css'>
                        body {
                            max-width: 100%;
                            overflow-x: hidden;
                            word-wrap: break-word;
                            word-break: break-all;
                        }
                        img {
                            max-width: 100%;
                            height: auto;
                        }
                        pre {
                            white-space: pre-wrap;
                        }
                        p {
                            margin-top: 0;
                            margin-bottom: 0;
                        }
                        </style>
                    """

                val htmlData = """
                        <html>
                        <head>
                        $css
                        </head>
                        <body>
                        $linkData
                        </body>
                        </html>
                    """.trimIndent()




                binding.webView.loadData(htmlData, "text/html", "utf-8")


            } else {
                binding.animation.visibility = View.VISIBLE
            }
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

        binding.shareIcon.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, url)
            }
            startActivity(Intent.createChooser(shareIntent, "Share link"))
        }

        binding.moreIcon.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.menuInflater.inflate(R.menu.detail_more_menu, popupMenu.menu)

            popupMenu.show()
        }
    }

    private fun updateUI(detailData: DetailData.Data) {
        binding.url.text = detailData.url
        url = detailData.url!!

        val linkData = prepareLinkData(detailData.content)
        logLongMessage("dataCheck", linkData)


        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val date = inputFormat.parse(detailData.updatedAt)
        val outputFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        val outputDateStr = outputFormat.format(date)
        binding.date.text = outputDateStr
        val formattedTags = detailData.tags.joinToString(separator = "    ", transform = { it -> "#$it" })
        Log.d("tagCheck", formattedTags)
        Log.d("tagCheck", detailData.tags.toString())

        if (detailData.status != "SCRAPE_PENDING" && detailData.status != "SCRAPE_PROCESSING") {
            showContent(linkData)
        }
        binding.tags.text = formattedTags



    }

    private fun prepareLinkData(content : String) : String {
        return content
            .replace(Regex("\\\\t"), "    ")
            .replace(Regex("\\\\n"), "<br>")
            .replace(Regex("\\\\r"), "<br>")
    }

    private fun showContent(linkData: String) {
        binding.animation.visibility = View.GONE
        binding.webView.visibility = View.VISIBLE
        binding.webView.apply {
            visibility = View.VISIBLE
            setupWebView()
            loadHtmlData(linkData)
        }
    }

    private fun WebView.setupWebView() {
        overScrollMode = WebView.OVER_SCROLL_NEVER
        isHorizontalScrollBarEnabled = false
        isVerticalScrollBarEnabled = false
        settings.javaScriptEnabled = true
    }

    private fun WebView.loadHtmlData(linkData: String) {
        val css = """
        <style type='text/css'>
        body {
            max-width: 100%;
            overflow-x: hidden;
            word-wrap: break-word;
            word-break: break-all;
        }
        img {
            max-width: 100%;
            height: auto;
        }
        pre {
            white-space: pre-wrap;
        }
        p {
            margin-top: 0;
            margin-bottom: 0;
        }
        </style>
    """

        val htmlData = """
        <html>
        <head>
        $css
        </head>
        <body>
        $linkData
        </body>
        </html>
    """.trimIndent()
        binding.webView.loadData(htmlData, "text/html", "utf-8")
        loadData(htmlData, "text/html", "utf-8")
    }


    fun logLongMessage(tag: String, message: String) {
        val maxLogSize = 1000
        for (i in 0..message.length / maxLogSize) {
            val start = i * maxLogSize
            var end = (i + 1) * maxLogSize
            end = if (end > message.length) message.length else end
            Log.v(tag, message.substring(start, end))
        }
    }


    private fun showWarnDialog(getContent: String, fileId: String, type: String) {
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