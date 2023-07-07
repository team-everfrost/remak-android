package com.example.remak.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.remak.App
import com.example.remak.dataStore.SignInRepository
import com.example.remak.databinding.SplashActivityBinding
import com.example.remak.view.account.AccountActivity
import com.example.remak.view.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: SplashActivityBinding

    private lateinit var signInRepository : SignInRepository

    private val viewModel : SplashViewModel by viewModels { SplashViewModelFactory(signInRepository)  }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        signInRepository = SignInRepository((this.application as App).dataStore)

        lifecycleScope.launch {
            //2초 후에 실행
            delay(1000)


            //토큰이 있는지 없는지 확인
            withContext(Dispatchers.Main) {
                viewModel.isToken.observe(this@SplashActivity, Observer { isToken ->
                    if (isToken) {
                        //토큰이 있을 때 메인화면으로
                        val intent = Intent(this@SplashActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        //토큰이 없을 때 로그인 화면으로
                        val intent = Intent(this@SplashActivity, AccountActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                })
            }
        }

    }
}