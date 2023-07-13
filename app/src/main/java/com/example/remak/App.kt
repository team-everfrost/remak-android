package com.example.remak

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.kakao.sdk.common.KakaoSdk
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {


    val dataStore : DataStore<Preferences> by preferencesDataStore("user_pref")


    init {
        instance = this

    }

    fun provideDataStore(): DataStore<Preferences> {
        return dataStore
    }



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