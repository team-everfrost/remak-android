package com.everfrost.remak.view.detail

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.davemorrissey.labs.subscaleview.ImageSource
import com.everfrost.remak.dataStore.TokenRepository
import com.everfrost.remak.databinding.ImageViewerActivityBinding
import kotlinx.coroutines.launch
import java.io.IOException


class ImageViewerActivity : AppCompatActivity() {
    private lateinit var binding: ImageViewerActivityBinding
    private val viewModel: DetailViewModel by viewModels { DetailViewModelFactory(tokenRepository) }
    private lateinit var tokenRepository: TokenRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenRepository = TokenRepository((this.application as com.everfrost.remak.App).dataStore)
        binding = ImageViewerActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val imageUrl = intent.getStringExtra("imageUrl")
        val imageName = intent.getStringExtra("fileName")

        viewModel.imageUri.observe(this) {
            binding.progressBar.visibility = View.GONE

            val rotation = getImageRotation(it)
            if (rotation != 0) {
                val bitmap = getBitmapFromUri(this.contentResolver, it)
                val rotatedBitmap = rotateBitmap(bitmap)
                Log.d("rotation", "rotation: $rotation")
                binding.imageFilterView.setImage(ImageSource.bitmap(rotatedBitmap))
            } else {
                binding.imageFilterView.setImage(ImageSource.uri(it))
            }

            Log.d("test", "rotation: $rotation")

        }
        lifecycleScope.launch {
            viewModel.getImageUri(this@ImageViewerActivity, imageUrl!!, imageName!!)
        }
    }

    private fun getBitmapFromUri(contentResolver: ContentResolver, uri: Uri): Bitmap {
        val source = ImageDecoder.createSource(contentResolver, uri)
        return ImageDecoder.decodeBitmap(source)
    }

    private fun rotateBitmap(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(0f)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }


    private fun getImageRotation(imageUri: Uri): Int {
        return try {
            val inputStream = this.contentResolver.openInputStream(imageUri)
            val exif = ExifInterface(inputStream!!)
            val rotation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )
            exifToDegrees(rotation)
        } catch (e: IOException) {
            Log.e("ImageDetailActivity", "Error checking exif", e)
            0
        }
    }

    private fun exifToDegrees(exifOrientation: Int): Int {
        return if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            90
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            180
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            270
        } else {
            0
        }
    }
}