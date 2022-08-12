package com.yandex.test.screenshot

import android.app.Service
import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

class DeviceSpecs(private val context: Context) {

    private val displayMetrics: DisplayMetrics
        get() = DisplayMetrics().apply { windowManager.defaultDisplay.getRealMetrics(this) }

    val displayWidth: Int
        get() = displayMetrics.widthPixels

    val displayHeight: Int
        get() = displayMetrics.heightPixels

    val density: Density
        get() {
            return when (displayMetrics.densityDpi) {
                in 0 until DisplayMetrics.DENSITY_MEDIUM -> Density.LDPI
                in DisplayMetrics.DENSITY_MEDIUM until DisplayMetrics.DENSITY_HIGH -> Density.MDPI
                in DisplayMetrics.DENSITY_HIGH until DisplayMetrics.DENSITY_XHIGH -> Density.HDPI
                in DisplayMetrics.DENSITY_XHIGH until DisplayMetrics.DENSITY_XXHIGH -> Density.XHDPI
                in DisplayMetrics.DENSITY_XXHIGH until DisplayMetrics.DENSITY_XXXHIGH -> Density.XXHDPI
                else -> Density.XXXHDPI
            }
        }

    private val windowManager: WindowManager
        get() = context.getSystemService(Service.WINDOW_SERVICE) as WindowManager

    enum class Density {
        LDPI,
        MDPI,
        HDPI,
        XHDPI,
        XXHDPI,
        XXXHDPI
    }
}
