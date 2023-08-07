package com.example.remak.view.detail

import android.app.Activity
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
import com.example.remak.UtilityDialog
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.DetailPageLinkActivityBinding
import com.example.remak.network.model.DetailData
import com.example.remak.view.main.MainViewModel
import com.example.remak.view.main.MainViewModelFactory
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import org.apache.commons.text.StringEscapeUtils
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class LinkDetailActivity : AppCompatActivity() {
    private lateinit var binding: DetailPageLinkActivityBinding
    private val viewModel: DetailViewModel by viewModels { DetailViewModelFactory(tokenRepository) }
    private val mainViewModel : MainViewModel by viewModels { MainViewModelFactory(tokenRepository) }
    lateinit var tokenRepository: TokenRepository
    lateinit var url: String
    private lateinit var linkData: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        tokenRepository = TokenRepository((this.application as App).dataStore)
        binding = DetailPageLinkActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val linkId = intent.getStringExtra("docId")
        viewModel.getDetailData(linkId!!)

        viewModel.detailData.observe(this) {
            Log.d("dataCheck", it.toString())
            updateUI(it)
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
            popupMenu.setOnMenuItemClickListener {menuItem ->
                when(menuItem.itemId) {
                    R.id.BrowserBtn -> {
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(url)
                        startActivity(i)
                        true
                    }
                    R.id.deleteBtn -> {
                        UtilityDialog.showWarnDialog(
                            this,
                            "링크를 삭제하시겠습니까?",
                            confirmClick = {
                                viewModel.deleteDocument(linkId)
                                val resultIntent = Intent()
                                resultIntent.putExtra("isDelete", true)
                                setResult(Activity.RESULT_OK, resultIntent)
                                finish()
                            },
                            cancelClick = {
                                //do nothing
                            }
                        )
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.scrollView.viewTreeObserver.addOnScrollChangedListener {
            if (binding.scrollView.scrollY == 0) {
                binding.line.visibility = View.INVISIBLE
            } else {
                binding.line.visibility = View.VISIBLE
            }
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
        binding.title.setText(detailData.title)

        if (detailData.status != "SCRAPE_PENDING" && detailData.status != "SCRAPE_PROCESSING") {
            showContent(linkData)
        } else {
            binding.animation.visibility = View.VISIBLE
        }
        binding.tags.text = formattedTags



    }

    private fun prepareLinkData(content : String) : String {
        return content
            .replace(Regex("\\\\t"), "    ")
            .replace(Regex("\\\\n"), "<br>")
            .replace(Regex("\\\\r"), "<br>")
            .replace(Regex("#"), "%23")
            .replace(Regex("</p>"), "</p><br>")
    }

    private fun showContent(linkData: String) {
        binding.webView.visibility = View.VISIBLE
        binding.webView.focusable = View.NOT_FOCUSABLE // 웹뷰 터치 시 자동 스크롤 방지
        binding.webView.isFocusableInTouchMode = false //웹뷰 터치 시 자동 스크롤 방지
        binding.webView.apply {
            visibility = View.VISIBLE
            setupWebView()
            loadHtmlData(linkData)
        }
    }

    private fun WebView.setupWebView() {
        webChromeClient = WebChromeClient()
        overScrollMode = WebView.OVER_SCROLL_NEVER
        isHorizontalScrollBarEnabled = false
        isVerticalScrollBarEnabled = false
        settings.javaScriptEnabled = true
    }

    private fun WebView.loadHtmlData(linkData: String) {
        // 받아온 html코드에 header를 추가하여 웹뷰에 로드
        val css = """ 
            
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.8.0/styles/default.min.css">
<script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.8.0/highlight.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.8.0/languages/go.min.js"></script>
<script>hljs.highlightAll();</script>
         
            
    <style type='text/css'>
    body {
        font-weight: 400;
        line-height: 1.6; 
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
    table {
        border-collapse: collapse;
        width: 100%;
    }
    th, td {
        border: 1px solid black;
        padding: 8px;
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
        loadData(htmlData, "text/html", "utf-8")

    }


    private fun logLongMessage(tag: String, message: String) {
        val maxLogSize = 1000
        for (i in 0..message.length / maxLogSize) {
            val start = i * maxLogSize
            var end = (i + 1) * maxLogSize
            end = if (end > message.length) message.length else end
            Log.v(tag, message.substring(start, end))
        }
    }


}