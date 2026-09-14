package com.yandex.divkit.demo.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.yandex.divkit.demo.BuildConfig

class Preferences(context: Context) : DivkitDemoPreferences(context) {

    var nightMode by IntPreference(AppCompatDelegate.MODE_NIGHT_NO)

    enum class ImageLoaderOption(val value: Int) {
        COIL(0),
        GLIDE(1);

        companion object {
            fun fromInt(value: Int) = entries.firstOrNull { it.value == value } ?: COIL
        }
    }

    var imageLoader by EnumPreference(ImageLoaderOption.COIL) {
        ImageLoaderOption.entries.toTypedArray()
    }

    var useBackgroundBinding by BooleanPreference(false)

    var limitImageBitmapSizeEnabled by BooleanPreference(true)

    var visualAssertionHandlerEnabled by BooleanPreference(BuildConfig.DEBUG)

    var useComposeRenderer by BooleanPreference(false)

    var disableAnimations by BooleanPreference(false)
}
