package com.example.remak

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, "native App Code")
    }

}