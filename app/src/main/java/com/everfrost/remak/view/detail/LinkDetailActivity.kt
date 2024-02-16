package com.everfrost.remak.view.detail

import android.app.Activity
import android.app.DownloadManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.everfrost.remak.R
import com.everfrost.remak.UtilityDialog
import com.everfrost.remak.adapter.LinkTagRVAdapter
import com.everfrost.remak.adapter.SpacingItemDecoration
import com.everfrost.remak.dataStore.TokenRepository
import com.everfrost.remak.databinding.DetailPageLinkActivityBinding
import com.everfrost.remak.network.model.MainListData
import com.everfrost.remak.view.collection.EditCollectionBottomSheetDialog
import com.everfrost.remak.view.tag.TagDetailActivity
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class LinkDetailActivity : AppCompatActivity(), LinkTagRVAdapter.OnItemClickListener {
    private lateinit var binding: DetailPageLinkActivityBinding
    private val viewModel: DetailViewModel by viewModels()
    lateinit var tokenRepository: TokenRepository
    lateinit var url: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DetailPageLinkActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val linkId = intent.getStringExtra("docId")
        viewModel.getDetailData(linkId!!) //상세 데이터 가져오기
        val adapter = LinkTagRVAdapter(listOf(), this)

        //태그 레이아웃 설정
        val flexboxLayoutManager = FlexboxLayoutManager(this).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
        }
        val itemDecoration = SpacingItemDecoration(10, 10)
        binding.tagRV.layoutManager = flexboxLayoutManager
        binding.tagRV.addItemDecoration(itemDecoration)
        binding.tagRV.adapter = adapter

        //상세 데이터에 변경사항이 있을시
        viewModel.detailData.observe(this) {
            Log.d("test", "status: ${it.status}")
            updateUI(it)
            adapter.tags = it.tags
            adapter.notifyDataSetChanged() //태그 변경사항 적용
            //스크랩이 완료되지 않았을 경우 하단 버튼 표시
            if (it.status == "SCRAPE_PENDING" || it.status == "SCRAPE_PROCESSING"
                || it.status == "SCRAPE_FAILED"
            ) {
                binding.scrollView.visibility = View.VISIBLE
                binding.webView.visibility = View.GONE
            }
        }

        viewModel.isActionComplete.observe(this) {
            if (it) {
                val resultIntent = Intent()
                resultIntent.putExtra("isDelete", true)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }

        viewModel.isSelfShareSuccess.observe(this) {
            if (it) {
                Toast.makeText(this, "파일을 저장했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.shareBtn.setOnClickListener {
            val colorSchemeParams = CustomTabColorSchemeParams.Builder()
                .setToolbarColor(ContextCompat.getColor(this, R.color.black))
                .build()
            val customTabsIntent = CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(colorSchemeParams)
                .build()
            customTabsIntent.launchUrl(this, Uri.parse(url))
        }

        binding.title.setOnClickListener {
            val colorSchemeParams = CustomTabColorSchemeParams.Builder()
                .setToolbarColor(ContextCompat.getColor(this, R.color.black))
                .build()
            val customTabsIntent = CustomTabsIntent.Builder()
                .setDefaultColorSchemeParams(colorSchemeParams)
                .build()
            customTabsIntent.launchUrl(this, Uri.parse(url))
        }

        binding.moreIcon.setOnClickListener {
            val popupView = layoutInflater.inflate(R.layout.custom_popup_menu_link, null)
            val popupWindow = PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            popupWindow.isFocusable = true
            popupWindow.isOutsideTouchable = true
            val addBtn: TextView = popupView.findViewById(R.id.addBtn)
            val shareBtn: TextView = popupView.findViewById(R.id.shareBtn)
            val browserBtn: TextView = popupView.findViewById(R.id.browserBtn)
            val deleteBtn: TextView = popupView.findViewById(R.id.deleteBtn)
            addBtn.setOnClickListener {
                val bundle = Bundle()
                val selectedItems = ArrayList<String>()
                selectedItems.add(linkId)
                if (selectedItems.isNotEmpty()) {
                    bundle.putStringArrayList("selected", selectedItems)
                    bundle.putString("type", "detail")
                    val bottomSheet = EditCollectionBottomSheetDialog()
                    bottomSheet.arguments = bundle
                    bottomSheet.show(
                        supportFragmentManager,
                        "EditCollectionBottomSheetDialog"
                    )
                }
                popupWindow.dismiss()
            }
            shareBtn.setOnClickListener {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, url)
                }
                startActivity(Intent.createChooser(shareIntent, "Share link"))
                popupWindow.dismiss()
            }
            browserBtn.setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
                popupWindow.dismiss()
            }
            deleteBtn.setOnClickListener {
                UtilityDialog.showWarnDialog(
                    this,
                    "링크를 삭제하시겠습니까?",
                    "삭제시 복구가 불가능해요",
                    "삭제하기",
                    "취소하기",
                    confirmClick = {
                        viewModel.deleteDocument(linkId)

                    },
                    cancelClick = {}
                )
                popupWindow.dismiss()
            }
            popupWindow.showAsDropDown(it)

        }

        binding.backButton.setOnClickListener {
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

    private fun updateUI(detailData: MainListData.Data) {
        binding.url.text = detailData.url
        url = detailData.url!!
        val linkData = prepareLinkData(detailData.content!!)
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val date = inputFormat.parse(detailData.updatedAt)
        val outputFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        val outputDateStr = outputFormat.format(date)
        binding.date.text = outputDateStr
        binding.title.text = detailData.title
        if (detailData.status != "SCRAPE_PENDING" && detailData.status != "SCRAPE_PROCESSING" && detailData.status != "SCRAPE_FAILED") {
            showContent(linkData)
        }
        if (detailData.summary != null) {
            val lines = detailData.summary.split("\n")
            if (lines.size > 1) {
                binding.summaryTextView.text = lines.subList(1, lines.size).joinToString("\n")
            } else {
                binding.summaryTextView.text = detailData.summary
            }
        } else {
            binding.summaryTextView.text = ""
        }

    }

    private fun prepareLinkData(content: String): String {
        return content
            .replace(Regex("\\\\t"), "    ")
            .replace(Regex("\\\\n"), "<br>")
            .replace(Regex("\\\\r"), "<br>")
            .replace(Regex("#"), "%23")
            .replace(Regex("</p>"), "</p><br>")
    }

    private fun showContent(linkData: String) {
        binding.webView.visibility = View.VISIBLE
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                viewModel.webViewLoaded()
                binding.scrollView.visibility = View.VISIBLE
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url.toString()
                // Custom Tabs으로 URL 로드
                val colorSchemeParams = CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(ContextCompat.getColor(this@LinkDetailActivity, R.color.black))
                    .build()
                val customTabsIntent = CustomTabsIntent.Builder()
                    .setDefaultColorSchemeParams(colorSchemeParams)
                    .build()
                customTabsIntent.launchUrl(this@LinkDetailActivity, Uri.parse(url))
                return true  // true를 반환하여 웹뷰 내에서 URL을 로드하지 않도록 합니다.
            }
        }
        binding.webView.focusable = View.NOT_FOCUSABLE // 웹뷰 터치 시 자동 스크롤 방지
        binding.webView.isFocusableInTouchMode = false //웹뷰 터치 시 자동 스크롤 방지
        binding.webView.apply {
            visibility = View.VISIBLE
            setupWebView()
            loadHtmlData(linkData)
            setOnLongClickListener {
                val hitTestResult = hitTestResult
                if (hitTestResult.type == WebView.HitTestResult.IMAGE_TYPE ||
                    hitTestResult.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE
                ) {
                    val imageUrl = hitTestResult.extra!!
                    val fileName = hitTestResult.extra!!.substringAfterLast("/")
                    Log.d("fileName", fileName)
                    UtilityDialog.showImageDialog(
                        context,
                        downloadBtnClick = {
                            downloadImage(imageUrl)
                        },
                        shareBtnClick = {
                            lifecycleScope.launch {
                                viewModel.downloadAndShareImage(context, imageUrl, fileName)
                            }
                        },
                        selfShareBtnClick = {
                            lifecycleScope.launch {
                                viewModel.shareSelf(context, imageUrl, fileName)
                            }

                        }
                    )
                }
                false
            }
        }
    }

    private fun WebView.setupWebView() {
        webChromeClient = WebChromeClient()
        overScrollMode = WebView.OVER_SCROLL_NEVER
        isHorizontalScrollBarEnabled = false
        isVerticalScrollBarEnabled = false
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
    }

    private fun WebView.loadHtmlData(linkData: String) {
        // 받아온 html코드에 header를 추가하여 웹뷰에 로드
        val css = """ 
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.8.0/styles/default.min.css">
<script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.8.0/highlight.min.js"></script>
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

    override fun onItemClick(position: Int) {
        val intent = Intent(this, TagDetailActivity::class.java)
        intent.putExtra("tagName", viewModel.detailData.value!!.tags[position])
        intent.putExtra("tagCount", viewModel.detailData.value!!.tags.size)
        startActivity(intent)
    }

    private fun downloadImage(url: String) {
        val fileName = URLUtil.guessFileName(url, null, null)
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(fileName)
            .setDescription("Downloading")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "Remak/$fileName"
        )
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        val manager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)
        Toast.makeText(this, "다운로드가 시작되었습니다.", Toast.LENGTH_SHORT).show()
    }

}