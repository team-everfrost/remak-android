package com.example.remak.view.detail

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.remak.BuildConfig
import com.example.remak.dataStore.TokenRepository
import com.example.remak.network.model.MainListData
import com.example.remak.repository.NetworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class DetailViewModel(private val tokenRepository: TokenRepository) : ViewModel() {
    private val networkRepository = NetworkRepository()
    private val _detailData = MutableLiveData<MainListData.Data>()
    val detailData: LiveData<MainListData.Data> = _detailData
    private val _isActionComplete = MutableLiveData<Boolean>()
    val isActionComplete: LiveData<Boolean> = _isActionComplete
    private val _isWebViewLoaded = MutableLiveData<Boolean>()
    val isWebViewLoaded: LiveData<Boolean> = _isWebViewLoaded
    private val _isImageShareReady = MutableLiveData<Boolean>()
    val isImageShareReady: LiveData<Boolean> = _isImageShareReady

    fun webViewLoaded() {
        _isWebViewLoaded.value = true
    }

    fun deleteDocument(docId: String) = viewModelScope.launch {
        try {
            val response = networkRepository.deleteDocument(docId)
            if (response.isSuccessful) {
                _isActionComplete.value = true
            } else {
            }
        } catch (e: Exception) {
        }
    }

    fun getDetailData(docId: String) = viewModelScope.launch {
        try {
            val response = networkRepository.getDetailData(docId)
            if (response.isSuccessful) {

                _detailData.value = response.body()!!.data
            } else {
            }
        } catch (e: Exception) {
        }
    }

    fun updateMemo(docId: String, content: String) = viewModelScope.launch {
        try {
            val response = networkRepository.updateMemo(docId, content)
            if (response.isSuccessful) {
            } else {
            }
        } catch (e: Exception) {
        }
    }

    fun downloadFile(context: Context, docId: String, fileName: String) = viewModelScope.launch {
        try {
            val response = networkRepository.downloadFile(docId)
            if (response.isSuccessful) {
                val url = response.body()!!.data
                val request = DownloadManager.Request(Uri.parse(url)) // 다운로드 요청 세팅
                    .setTitle(fileName)
                    .setDescription("Downloading")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // Notification bar에 다운로드 중임을 알려줌
                    .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        fileName
                    ) // 다운로드 경로 및 파일 이름
                    .setAllowedOverMetered(true) // 데이터 사용시에도 다운로드 여부
                    .setAllowedOverRoaming(true) // 로밍 여부
                withContext(Dispatchers.Main) {
                    val downloadManager =
                        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    downloadManager.enqueue(request) //다운로드 시작
                    Toast.makeText(context, "다운로드가 시작되었습니다.", Toast.LENGTH_SHORT).show()
                }

            } else {
            }
        } catch (e: Exception) {
        }
    }

    fun shareFile(context: Context, docId: String) = viewModelScope.launch {
        _isImageShareReady.value = false
        try {
            val response = networkRepository.downloadFile(docId)
            if (response.isSuccessful) {
                val url = response.body()!!.data
                shareImageFromUrl(context, url!!)
            } else {
            }
        } catch (e: Exception) {
        }
    }

    private fun shareImageFromUrl(context: Context, imageUrl: String) {
        val glide = Glide.with(context)
        glide.asBitmap().load(imageUrl).into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                _isImageShareReady.value = true
                val uri = saveImageToInternalStorage(context, resource)
                shareImageUri(context, uri)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }
        })
    }

    private fun saveImageToInternalStorage(context: Context, bitmap: Bitmap): Uri {
        val file = File(context.cacheDir, "shared_image.png")
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close()
        return FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", file)
    }

    private fun shareImageUri(context: Context, uri: Uri) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/png"
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }

}

class DetailViewModelFactory(private val tokenRepository: TokenRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(tokenRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}