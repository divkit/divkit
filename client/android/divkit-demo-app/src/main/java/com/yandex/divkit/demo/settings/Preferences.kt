package com.yandex.divkit.demo.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class Preferences(context: Context) : DivkitDemoPreferences(context) {

    var enableScanNetworkChanges by BooleanPreference(false)

    var nightMode by IntPreference(AppCompatDelegate.MODE_NIGHT_NO)

    var imageLoader by BooleanPreference(true)
}
