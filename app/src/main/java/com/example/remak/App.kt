package com.example.remak

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.kakao.sdk.common.KakaoSdk

class App : Application() {

    init {
        instance = this
    }
    val Context.dataStore : DataStore<Preferences> by preferencesDataStore("user_pref")


    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, "native App Code")
    }

    companion object {
        private var instance : App? = null

        fun context() : App {
            return instance!!
        }
    }

}