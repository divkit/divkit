package com.yandex.divkit.demo.settings

import androidx.appcompat.app.AppCompatDelegate
import com.yandex.divkit.demo.Container
import com.yandex.divkit.demo.settings.dsl.Setting

enum class NightMode(@AppCompatDelegate.NightMode val code: Int) {
    /**
     * Forced no night.
     */
    DAY(AppCompatDelegate.MODE_NIGHT_NO),

    /**
     * Forced yes night.
     */
    NIGHT(AppCompatDelegate.MODE_NIGHT_YES),

    /**
     * Follow system theme.
     */
    AUTO(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

    companion object {
        val setting = Setting<String?>(
            getter = {
                values().find { it.code == Container.preferences.nightMode}.toString()
            },
            setter = { value ->
                val code = valueOf(value!!).code
                Container.preferences.nightMode = code
            }
        )

        val entries = values().map { it.toString() }.toTypedArray()
    }
}
