package com.example.remak.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.remak.App
import com.example.remak.R
import com.example.remak.dataStore.TokenRepository
import com.example.remak.databinding.SplashActivityBinding
import com.example.remak.view.account.AccountActivity
import com.example.remak.view.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: SplashActivityBinding
    private lateinit var signInRepository : TokenRepository

    private val viewModel : SplashViewModel by viewModels { SplashViewModelFactory(signInRepository)  }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        Log.d("SplashActivity", "onCreate")



        signInRepository = TokenRepository((this.application as App).dataStore)
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    if (viewModel.returnIsReady()) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        return true
                    }
                    return false
                }
            }
        )

        lifecycleScope.launch {
            val isToken = withContext(Dispatchers.IO) {
                viewModel.isTokenAvailable()
            }
            if (isToken) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, AccountActivity::class.java))
            }
            finish()
        }
    }
}