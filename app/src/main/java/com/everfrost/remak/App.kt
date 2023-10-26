package com.everfrost.remak

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.amplitude.android.Amplitude
import com.kakao.sdk.common.KakaoSdk
import io.sentry.android.core.SentryAndroid

class App : Application() {

    val dataStore: DataStore<Preferences> by preferencesDataStore("user_pref")

    init {
        com.everfrost.remak.App.Companion.instance = this
    }

    fun provideDataStore(): DataStore<Preferences> {
        return dataStore
    }

    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, "native App Code")
        SentryAndroid.init(this) { options ->
            options.dsn =
                "https://903663e729b6e6ce17a6931e43585873@o4505921863155712.ingest.sentry.io/4505922228060160"
        }
        val amplitude = Amplitude(
            com.amplitude.android.Configuration(
                apiKey = "16577f94d092757eef4eb77d6be2c85e",
                context = this
            )
        )


    }

    companion object {
        private var instance: com.everfrost.remak.App? = null
        fun context(): com.everfrost.remak.App {
            return com.everfrost.remak.App.Companion.instance!!
        }
    }

}