package com.yandex.divkit.regression

import android.content.Context
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

private const val REGRESSION_CONFIG_PREFERENCES = "regression_config"
private const val RECORD_SCREEN_KEY = "record_screen_key"

@Singleton
class RegressionConfig @Inject constructor(context: Context) {
    private val sharedPrefs =
        context.getSharedPreferences(REGRESSION_CONFIG_PREFERENCES, Context.MODE_PRIVATE)

    var isRecordScreenEnabled: Boolean
        get() = sharedPrefs.getBoolean(RECORD_SCREEN_KEY, false)
        set(value) = sharedPrefs.edit { putBoolean(RECORD_SCREEN_KEY, value) }
}
