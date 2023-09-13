package com.example.remak.view.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.remak.App
import com.example.remak.dataStore.TokenRepository

class ShareReceiverActivity : AppCompatActivity() {
    lateinit var tokenRepository: TokenRepository
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(tokenRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenRepository = TokenRepository((application as App).dataStore)

        viewModel.isWebPageCreateSuccess.observe(this) {
            if (it) {
                Toast.makeText(this, "Remak에 저장했습니다.", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Remak에 저장하는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        if (Intent.ACTION_SEND == intent.action && intent.type != null) { // 공유하기로 들어온 경우
            if ("text/plain" == intent.type) {
                val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                if (sharedText != null) {
                    viewModel.createWebPage(sharedText)
                }
            }
        }
    }
}