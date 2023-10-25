package com.everfrost.remak

import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.everfrost.remak.network.TokenExpiredEvent
import com.everfrost.remak.view.account.AccountActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

open class BaseActivity : AppCompatActivity() {

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTokenExpiredEvent(event: TokenExpiredEvent) {
        Log.d("BaseActivity", "onTokenExpiredEvent: ")
        com.everfrost.remak.UtilityDialog.showInformDialog(
            "로그인 정보가 만료되었습니다",
            "다시 로그인해주세요",
            this,
            confirmClick = {
                val intent = Intent(this, AccountActivity::class.java)
                startActivity(intent)
                finish()
            }
        )

    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }
}