package com.everfrost.remak

import android.app.Application
import com.amplitude.android.Amplitude
import dagger.hilt.android.HiltAndroidApp
import io.sentry.android.core.SentryAndroid

@HiltAndroidApp
class App : Application() {


    init {
        instance = this
    }


    override fun onCreate() {
        super.onCreate()

        SentryAndroid.init(this) { options ->
            options.dsn =
                "https://903663e729b6e6ce17a6931e43585873@o4505921863155712.ingest.sentry.io/4505922228060160"
        }
        Amplitude(
            com.amplitude.android.Configuration(
                apiKey = "16577f94d092757eef4eb77d6be2c85e",
                context = this
            )
        )
    }

    companion object {
        private var instance: App? = null
        fun context(): App {
            return instance!!
        }
    }

}