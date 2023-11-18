package com.yandex.divkit.demo.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class Preferences(context: Context) : DivkitDemoPreferences(context) {

    var enableScanNetworkChanges by BooleanPreference(false)

    var nightMode by IntPreference(AppCompatDelegate.MODE_NIGHT_NO)

    enum class ImageLoaderOption(val value: Int) {
        PICASSO(0),
        GLIDE(1),
        COIL(2);

        companion object {
            fun fromInt(value: Int) = ImageLoaderOption.values().first { it.value == value }
        }
    }

    var imageLoader by EnumPreference(ImageLoaderOption.PICASSO) { ImageLoaderOption.values() }
}
