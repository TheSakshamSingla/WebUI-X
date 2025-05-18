package com.dergoogler.mmrl.wx

import android.app.Application
import com.dergoogler.mmrl.platform.Platform
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        Platform.setHiddenApiExemptions()
    }
}