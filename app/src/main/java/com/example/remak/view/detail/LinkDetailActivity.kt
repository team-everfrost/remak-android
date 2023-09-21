package com.example.remak.view.detail

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.remak.App
import com.example.remak.BuildConfig
import com.example.remak.R
import com.example.remak.UtilityDialog
import com.example.remak.adapter.LinkTagRVAdapter
import com.example.remak.adapter.SpacingItemDecoration
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.DetailPageLinkActivityBinding
import com.example.remak.network.model.MainListData
import com.example.remak.view.collection.EditCollectionBottomSheetDialog
import com.example.remak.view.tag.TagDetailActivity
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class LinkDetailActivity : AppCompatActivity(), LinkTagRVAdapter.OnItemClickListener {
    private lateinit var binding: DetailPageLinkActivityBinding
    private val viewModel: DetailViewModel by viewModels { DetailViewModelFactory(tokenRepository) }
    lateinit var tokenRepository: TokenRepository
    lateinit var url: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenRepository = TokenRepository((this.application as App).dataStore)
        binding = DetailPageLinkActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val linkId = intent.getStringExtra("docId")
        viewModel.getDetailData(linkId!!)

        val adapter = LinkTagRVAdapter(listOf(), this)

        val flexboxLayoutManager = FlexboxLayoutManager(this).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
        }

        val itemDecoration = SpacingItemDecoration(10, 10)
        binding.tagRV.layoutManager = flexboxLayoutManager
        binding.tagRV.addItemDecoration(itemDecoration)
        binding.tagRV.adapter = adapter

        viewModel.detailData.observe(this) {
            updateUI(it)
            adapter.tags = it.tags
            adapter.notifyDataSetChanged()
        }

        viewModel.isActionComplete.observe(this) {
            if (it) {
                val resultIntent = Intent()
                resultIntent.putExtra("isDelete", true)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
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
            val popupMenu = PopupMenu(this, it)
            popupMenu.menuInflater.inflate(R.menu.detail_link_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.addCollection -> {
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
                        true
                    }

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
                            "삭제시 복구가 불가능해요",
                            "삭제하기",
                            "취소하기",
                            confirmClick = {
                                viewModel.deleteDocument(linkId)

                            },
                            cancelClick = {}
                        )
                        true
                    }

                    R.id.shareBtn -> {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, url)
                        }
                        startActivity(Intent.createChooser(shareIntent, "Share link"))
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
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
        if (detailData.status != "SCRAPE_PENDING" && detailData.status != "SCRAPE_PROCESSING") {
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
                    UtilityDialog.showImageDialog(
                        context,
                        downloadBtnClick = {
                            downloadImage(imageUrl)
                        },
                        shareBtnClick = {
                            shareImageFromUrl(context, imageUrl)
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

    private fun shareImageFromUrl(context: Context, imageUrl: String) {
        val glide = Glide.with(context)
        glide.asBitmap().load(imageUrl).into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                val uri = saveImageToInternalStorage(context, resource)
                shareImageUri(context, uri)
            }

            override fun onLoadCleared(placeholder: Drawable?) {}
        })
    }

    fun saveImageToInternalStorage(context: Context, bitmap: Bitmap): Uri {
        val file = File(context.cacheDir, "shared_image.png")
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close()
        return FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", file)
    }

    fun shareImageUri(context: Context, uri: Uri) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/png"
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }

}