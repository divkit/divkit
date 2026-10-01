package com.yandex.divkit.demo.screenshot

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.yandex.div.core.DivKit
import com.yandex.divkit.demo.Container

/** Initializes the screenshot Activity without the demo application's analytics and beacons. */
class ViewScreenshotTestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Container.initialize(this)
        DivKit.enableAssertions(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
