package com.everfrost.remak.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.everfrost.remak.dataStore.TokenRepository
import com.everfrost.remak.view.account.AccountActivity
import com.everfrost.remak.view.main.MainActivity
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var signInRepository: TokenRepository
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //강제 업데이트 코드
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        // activityResultLauncher 초기화
        val activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result.resultCode != Activity.RESULT_OK) {
                    // 업데이트 실패 시 처리 코드 작성

                }
            }
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    activityResultLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                )
                // Request the update.
            }
        }

        installSplashScreen()
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
            //로딩화면을 보여주기 위해 0.5초 딜레이
            delay(500)
            //토큰이 있는지 없는지 확인
            withContext(Dispatchers.Main) {
                viewModel.isToken.observe(this@SplashActivity) { isToken ->
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
                }
            }
        }
    }

}