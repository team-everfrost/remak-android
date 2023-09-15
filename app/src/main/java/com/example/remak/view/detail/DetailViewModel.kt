package com.example.remak.view.detail

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.remak.dataStore.TokenRepository
import com.example.remak.network.model.MainListData
import com.example.remak.repository.NetworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailViewModel(private val tokenRepository: TokenRepository) : ViewModel() {
    private val networkRepository = NetworkRepository()
    private val _detailData = MutableLiveData<MainListData.Data>()
    val detailData: LiveData<MainListData.Data> = _detailData
    private val _isActionComplete = MutableLiveData<Boolean>()
    val isActionComplete: LiveData<Boolean> = _isActionComplete


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
        try {
            val response = networkRepository.downloadFile(docId)
            if (response.isSuccessful) {
                val url = response.body()!!.data
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, url)
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share link"))

            } else {
            }
        } catch (e: Exception) {
        }
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