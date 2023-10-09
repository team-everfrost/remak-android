package com.example.remak.view.detail

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.remak.BuildConfig
import com.example.remak.dataStore.TokenRepository
import com.example.remak.network.model.MainListData
import com.example.remak.repository.NetworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption

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
    private val _isSelfShareSuccess = MutableLiveData<Boolean>()
    val isSelfShareSuccess: LiveData<Boolean> = _isSelfShareSuccess

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

    fun shareFile(context: Context, docId: String, fileName: String) = viewModelScope.launch {
        _isImageShareReady.value = false
        try {
            val response = networkRepository.downloadFile(docId)
            if (response.isSuccessful) {
                val url = response.body()!!.data
                downloadAndShareImage(context, url!!, fileName)
            } else {
            }
        } catch (e: Exception) {
        }
    }

    suspend fun shareSelf(
        context: Context,
        imageUrl: String,
        fileName: String
    ) {
        withContext(Dispatchers.IO) {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection()
                connection.doInput = true
                connection.connect()
                val inputStream = connection.getInputStream()

                val uri = saveImageToInternalStorage(context, fileName, inputStream)
                withContext(Dispatchers.Main) {
                    uriToFile(context, uri)
                }
            } catch (e: Exception) {
            }
        }
    }

    private suspend fun downloadAndShareImage(
        context: Context,
        imageUrl: String,
        fileName: String
    ) {
        withContext(Dispatchers.IO) {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection()
                connection.doInput = true
                connection.connect()
                val inputStream = connection.getInputStream()

                val uri = saveImageToInternalStorage(context, fileName, inputStream)
                withContext(Dispatchers.Main) {
                    _isImageShareReady.value = true
                    shareImageUri(context, uri, fileName)
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun uriToFile(context: Context, uri: Uri) {
        val fileList = mutableListOf<MultipartBody.Part>()
        val mimeType = context.contentResolver.getType(uri)
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = inputStreamToFile(inputStream!!, uri, context)
        val requestFile = file.asRequestBody(mimeType?.toMediaTypeOrNull())
        fileList.add(
            MultipartBody.Part.createFormData(
                "files",
                file.name,
                requestFile
            )
        )
        uploadFile(fileList)
    }


    private fun saveImageToInternalStorage(
        context: Context,
        fileName: String,
        inputStream: InputStream
    ): Uri {
        val file = File(context.cacheDir, fileName)
        file.outputStream().use { fileOutput ->
            inputStream.use { input ->
                input.copyTo(fileOutput)
            }
        }
        Log.d("File Size", file.length().toString()) // 로그 추가
        return FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", file)
    }

    private fun shareImageUri(context: Context, uri: Uri, fileName: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/*"
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        }
        context.startActivity(
            Intent.createChooser(shareIntent, fileName)

        )
    }

    /** 파일 업로드 */
    private fun uploadFile(files: List<MultipartBody.Part>) = viewModelScope.launch {
        _isSelfShareSuccess.value = false
        try {
            val response = networkRepository.uploadFile(files)
            if (response.isSuccessful) {
                _isSelfShareSuccess.value = true
            } else {
            }
        } catch (e: Exception) {
        }
    }

    private fun inputStreamToFile(inputStream: InputStream, uri: Uri, context: Context): File {
        val fileName = getFileNameFromUri(context, uri)
        val file = File(context.cacheDir, fileName!!)

        inputStream.use { input ->
            Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
        return file
    }

    private fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var fileName: String? = null
        context.contentResolver.query(uri, null, null, null, null)?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            fileName = it.getString(nameIndex)
        }
        return fileName
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