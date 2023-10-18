package com.example.remak.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.remak.App
import com.example.remak.dataStore.TokenRepository
import com.example.remak.view.account.AccountActivity
import com.example.remak.view.main.MainActivity
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashActivity : AppCompatActivity() {
    private lateinit var signInRepository: TokenRepository

    private val viewModel: SplashViewModel by viewModels { SplashViewModelFactory(signInRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // waiting for view to draw to better represent a captured error with a screenshot
//    findViewById<android.view.View>(android.R.id.content).viewTreeObserver.addOnGlobalLayoutListener {
//      try {
//        throw Exception("This app uses Sentry! :)")
//      } catch (e: Exception) {
//        Sentry.captureException(e)
//      }
//    }
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // This example applies an immediate update. To apply a flexible update
                // instead, pass in AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    1
                )
                // Request the update.
            }
        }

        installSplashScreen()
        signInRepository = TokenRepository((this.application as App).dataStore)
        val content: View = findViewById(android.R.id.content)

        // 로직이 끝날때까지 화면을 보여주지 않음
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
            //로딩화면을 보여주기 위해 0.5초 딜레이K
            delay(500)
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