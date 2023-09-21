package com.example.remak.view.main

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.remak.App
import com.example.remak.dataStore.TokenRepository
import com.example.remak.view.add.AddViewModel
import com.example.remak.view.add.AddViewModelFactory
import com.example.remak.view.add.UploadState
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.InputStream

class ShareReceiverActivity : AppCompatActivity() {
    lateinit var tokenRepository: TokenRepository
    private val viewModel: AddViewModel by viewModels { AddViewModelFactory(tokenRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenRepository = TokenRepository((application as App).dataStore)

        viewModel.isActionComplete.observe(this) {
            if (it) {
                Toast.makeText(this, "Remak에 저장했습니다.", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Remak에 저장하는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        viewModel.uploadState.observe(this) {
            if (it == UploadState.SUCCESS) {
                Toast.makeText(this, "파일 업로드에 성공했습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        if (Intent.ACTION_SEND == intent.action && intent.type != null) { // 공유하기로 들어온 경우
            if ("text/plain" == intent.type) {
                val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                if (sharedText != null) {
                    viewModel.createWebPage(sharedText)
                }
            } else if (intent.type == "image/*") {
                val imageUri: Uri?
                if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    imageUri = intent.getParcelableExtra(
                        Intent.EXTRA_STREAM,
                        Uri::class.java
                    )
                } else {
                    imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM) as Uri?
                }

                if (imageUri != null) {
                    val fileList = mutableListOf<MultipartBody.Part>()
                    val mimeType = contentResolver.getType(imageUri)
                    val inputStream = contentResolver.openInputStream(imageUri)
                    val file = inputStreamToFile(inputStream!!, imageUri)
                    val requestFile = file.asRequestBody(mimeType?.toMediaTypeOrNull())
                    fileList.add(
                        MultipartBody.Part.createFormData(
                            "files",
                            file.name,
                            requestFile
                        )
                    )
                    viewModel.uploadFile(fileList)
                }
            }
        }
    }

    private fun inputStreamToFile(inputStream: InputStream, uri: Uri): File {
        val fileName = getFileNameFromUri(uri)
        val file = File(cacheDir, fileName)
        file.outputStream().use { fileOutputStream ->
            inputStream.copyTo(fileOutputStream)
        }
        return file
    }

    private fun getFileNameFromUri(uri: Uri): String? {
        var fileName: String? = null
        contentResolver.query(uri, null, null, null, null)?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            fileName = it.getString(nameIndex)
        }
        return fileName
    }
}