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
import com.example.remak.network.model.DetailData
import com.example.remak.network.model.TagDetailData
import com.example.remak.repository.NetworkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailViewModel(private val tokenRepository: TokenRepository) : ViewModel() {
    private val networkRepository = NetworkRepository()
    private val _detailData = MutableLiveData<DetailData.Data>()
    val detailData: LiveData<DetailData.Data> = _detailData

    private val _tagDetailData = MutableLiveData<List<TagDetailData.Data>>()
    val tagDetailData: LiveData<List<TagDetailData.Data>> = _tagDetailData

    private val _collectionDetailData = MutableLiveData<List<TagDetailData.Data>>()
    val collectionDetailData: LiveData<List<TagDetailData.Data>> = _collectionDetailData

    private val _isCollectionEmpty = MutableLiveData<Boolean>()
    val isCollectionEmpty: LiveData<Boolean> = _isCollectionEmpty

    private val _selectedItemsCount = MutableLiveData<Int>()
    val selectedItemsCount: LiveData<Int> = _selectedItemsCount

    private val _isActionComplete = MutableLiveData<Boolean>()
    val isActionComplete: LiveData<Boolean> = _isActionComplete

    private var cursor: String? = null
    private var docId: String? = null

    var isLoadEnd = false

    fun deleteDocument(docId: String) = viewModelScope.launch {
        try {
            val response = networkRepository.deleteDocument(docId)
            if (response.isSuccessful) {
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

    fun getTagDetailData(tagName: String) = viewModelScope.launch {
        try {
            val response = networkRepository.getTagDetailData(tagName, null, null)
            if (response.isSuccessful) {
                _tagDetailData.value = response.body()!!.data
                response.body()!!.data.let {
                    cursor = it.last().updatedAt
                    docId = it.last().docId
                }
            } else {
            }
        } catch (e: Exception) {
        }
    }

    fun getNewTagDetailData(tagName: String) = viewModelScope.launch {
        if (!isLoadEnd) {
            val tempData = tagDetailData.value?.toMutableList() ?: mutableListOf()
            try {
                val response = networkRepository.getTagDetailData(tagName, cursor, docId)
                if (response.isSuccessful) {
                    response.body()!!.data.let {
                        if (it.isNotEmpty()) {
                            tempData.addAll(it)
                            _tagDetailData.value = tempData
                            cursor = it.last().updatedAt
                            docId = it.last().docId
                        } else {
                            isLoadEnd = true
                        }
                    }
                    _tagDetailData.value = tempData
                } else {
                }
            } catch (e: Exception) {
            }
        }
    }

    fun getCollectionDetailData(collectionName: String) = viewModelScope.launch {
        val response = networkRepository.getCollectionDetailData(collectionName, null, null)
        try {
            if (response.isSuccessful) {
                _collectionDetailData.value = response.body()!!.data
                _isCollectionEmpty.value = response.body()!!.data.isEmpty()
                response.body()!!.data.let {
                    cursor = it.last().updatedAt
                    docId = it.last().docId
                }
            } else {
            }
        } catch (e: Exception) {

        }
    }

    fun getNewCollectionDetailData(collectionName: String) = viewModelScope.launch {
        if (!isLoadEnd) {
            val tempData = collectionDetailData.value?.toMutableList() ?: mutableListOf()
            try {
                val response =
                    networkRepository.getCollectionDetailData(collectionName, cursor, docId)
                if (response.isSuccessful) {
                    response.body()!!.data.let {
                        if (it.isNotEmpty()) {
                            tempData.addAll(it)
                            _collectionDetailData.value = tempData
                            cursor = it.last().updatedAt
                            docId = it.last().docId
                        } else {
                            isLoadEnd = true
                        }
                    }
                    _collectionDetailData.value = tempData
                } else {
                }
            } catch (e: Exception) {
            }
        }
    }

    fun removeDataInCollection(collectionName: String, docIds: List<String>) =
        viewModelScope.launch {
            val response = networkRepository.removeDataInCollection(collectionName, docIds)
            try {
                if (response.isSuccessful) {
                    getCollectionDetailData(collectionName)
                } else {
                }
            } catch (e: Exception) {
            }
        }

    fun deleteCollection(name: String) = viewModelScope.launch {

        val response = networkRepository.deleteCollection(name)
        try {
            if (response.isSuccessful) {
            } else {
            }
            _isActionComplete.value = true
        } catch (e: Exception) {
        }
    }

    fun resetTagDetailData() {
        _tagDetailData.value = listOf()
    }

    fun increaseSelectionCount() {
        _selectedItemsCount.value = (_selectedItemsCount.value ?: 0) + 1
    }

    fun decreaseSelectionCount() {
        _selectedItemsCount.value = (_selectedItemsCount.value ?: 0) - 1
    }

    fun resetSelectionCount() {
        _selectedItemsCount.value = 0
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