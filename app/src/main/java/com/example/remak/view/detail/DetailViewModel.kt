package com.example.remak.view.detail

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.remak.dataStore.TokenRepository
import com.example.remak.network.model.DetailData
import com.example.remak.repository.NetworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailViewModel(private val tokenRepository: TokenRepository) : ViewModel() {

    private val networkRepository = NetworkRepository()

    private val _detailData = MutableLiveData<DetailData.Data>()
    val detailData : LiveData<DetailData.Data> = _detailData

   fun deleteDocument(docId : String) = viewModelScope.launch {
        try {
            val response = networkRepository.deleteDocument(docId)
            if (response.isSuccessful) {
                Log.d("success", response.body().toString())
            } else {
                Log.d("fail", response.errorBody().toString())
            }
        } catch (e : Exception) {
            Log.d("networkError", e.toString())
        }
    }


    fun getDetailData(docId : String) = viewModelScope.launch {
        try {
            val response = networkRepository.getDetailData(docId)
            if (response.isSuccessful) {
                _detailData.value = response.body()!!.data
                Log.d("success", response.body().toString())
            } else {
                Log.d("fail", response.errorBody().toString())
            }
        } catch (e : Exception) {
            Log.d("networkError", e.toString())
        }
    }

    fun updateMemo(docId : String, content : String) = viewModelScope.launch {
        try {
            val response = networkRepository.updateMemo(docId, content)
            if (response.isSuccessful) {
                Log.d("success", response.body().toString())
            } else {
                Log.d("fail", response.errorBody().toString())
            }
        } catch (e : Exception) {
            Log.d("networkError", e.toString())
        }
    }

    fun downloadFile(context: Context, docId : String, fileName : String) = viewModelScope.launch {
        try {
            val response = networkRepository.downloadFile(docId)
            if (response.isSuccessful) {
                Log.d("success", response.body().toString())
                val url = response.body()!!.data

                val request = DownloadManager.Request(Uri.parse(url))
                    .setTitle(fileName)
                    .setDescription("Downloading")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

                val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val downloadId = downloadManager.enqueue(request)

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "다운로드가 시작되었습니다.", Toast.LENGTH_SHORT).show()
                }


            } else {
                Log.d("fail", response.errorBody().toString())
            }
        } catch (e : Exception) {
            Log.d("networkError", e.toString())
        }
    }

    fun shareFile(context: Context, docId: String) = viewModelScope.launch {
        try {
            val response = networkRepository.downloadFile(docId)
            if (response.isSuccessful) {
                Log.d("success", response.body().toString())
                val url = response.body()!!.data

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, url)
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share link"))

            } else {
                Log.d("fail", response.errorBody().toString())
            }
        } catch (e : Exception) {
            Log.d("networkError", e.toString())
        }
    }



}

class DetailViewModelFactory(private val tokenRepository: TokenRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(tokenRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}