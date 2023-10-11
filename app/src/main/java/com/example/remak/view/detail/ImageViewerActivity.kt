package com.example.remak.view.detail

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.davemorrissey.labs.subscaleview.ImageSource
import com.example.remak.App
import com.example.remak.BuildConfig
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.ImageViewerActivityBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL

class ImageViewerActivity : AppCompatActivity() {
    private lateinit var binding: ImageViewerActivityBinding
    private val viewModel: DetailViewModel by viewModels { DetailViewModelFactory(tokenRepository) }
    private lateinit var tokenRepository: TokenRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenRepository = TokenRepository((this.application as App).dataStore)
        binding = ImageViewerActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val imageUrl = intent.getStringExtra("imageUrl")
        val imageName = intent.getStringExtra("fileName")

        viewModel.imageUri.observe(this) {
            binding.progressBar.visibility = android.view.View.GONE
            binding.imageFilterView.setImage(ImageSource.uri(it))
        }
        lifecycleScope.launch {
            viewModel.getImageUri(this@ImageViewerActivity, imageUrl!!, imageName!!)

        }
    }

    private suspend fun urlToUri(imageUrl: String, fileName: String): Uri {
        withContext(Dispatchers.IO) {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection()
                connection.doInput = true
                connection.connect()
                val inputStream = connection.getInputStream()
                return@withContext saveImageToInternalStorage(inputStream, fileName)
            } catch (e: Exception) {
                Log.d("ImageDetailActivity", e.toString())
            }
        }
        return null!!
    }

    private fun saveImageToInternalStorage(inputStream: InputStream, fileName: String): Uri {
        val file = File(this.cacheDir, fileName)
        file.outputStream().use { fileOutput ->
            inputStream.use { input ->
                input.copyTo(fileOutput)
            }
        }
        binding.progressBar.visibility = android.view.View.GONE
        return FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.provider", file)
    }


    private fun saveImageToLocal(bitmap: Bitmap): Uri {
        val file = File(this.cacheDir, "image.png")
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close()
        return Uri.fromFile(file)
    }
}