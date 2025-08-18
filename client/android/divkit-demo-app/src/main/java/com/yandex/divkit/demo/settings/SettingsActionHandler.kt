package com.yandex.divkit.demo.settings

import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import com.yandex.div.core.experiments.Experiment
import com.yandex.div.internal.util.toBoolean
import com.yandex.divkit.demo.Container
import com.yandex.divkit.demo.ui.SCHEME_DIV_ACTION

private const val AUTHORITY_SET_PREFERENCES = "set_preferences"

private const val PARAM_NAME = "name"
private const val PARAM_VALUE = "value"

const val DIV2_VIEW_POOL = "div2_view_pool"
const val DIV2_VIEW_POOL_PROFILING = "div2_view_pool_profiling"
const val DIV2_MULTIPLE_STATE_CHANGE = "multiple_state_change"
const val DIV2_DEMO_SHOW_RENDERING_TIME = "demo_activity_rendering_time"
const val IMAGE_LOADER = "image_loader"
const val COMPLEX_REBIND = "complex_rebind"
const val PERMANENT_DEBUG_PANEL = "permanent_debug_panel"

const val RENDER_EFFECT_ENABLED = "render_effect_enabled"

const val NIGHT_MODE = "night_mode"
const val NIGHT_MODE_NIGHT = "NIGHT"
const val NIGHT_MODE_DAY = "DAY"
const val NIGHT_MODE_AUTO = "AUTO"

internal object SettingsActionHandler {

    fun handleActionUrl(uri: Uri): Boolean {
        if (uri.authority != AUTHORITY_SET_PREFERENCES || uri.scheme != SCHEME_DIV_ACTION)
            return false

        val value = uri.getQueryParameter(PARAM_VALUE)
        val valueInt = value?.toIntOrNull() ?: 0

        when (uri.getQueryParameter(PARAM_NAME)) {
            IMAGE_LOADER -> {
                Container.preferences.imageLoader = Preferences.ImageLoaderOption.fromInt(valueInt)
            }
            NIGHT_MODE -> {
                when (value) {
                    NIGHT_MODE_AUTO -> Container.preferences.nightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    NIGHT_MODE_NIGHT -> Container.preferences.nightMode = AppCompatDelegate.MODE_NIGHT_YES
                    NIGHT_MODE_DAY -> Container.preferences.nightMode = AppCompatDelegate.MODE_NIGHT_NO
                    else -> return false
                }
                AppCompatDelegate.setDefaultNightMode(Container.preferences.nightMode)
                return true
            }
            COMPLEX_REBIND -> setPreferencesBooleanFlag(Experiment.COMPLEX_REBIND_ENABLED, valueInt.toBooleanOrException())
            DIV2_VIEW_POOL -> setPreferencesBooleanFlag(Experiment.VIEW_POOL_ENABLED, valueInt.toBooleanOrException())
            DIV2_VIEW_POOL_PROFILING -> setPreferencesBooleanFlag(Experiment.VIEW_POOL_PROFILING_ENABLED, valueInt.toBooleanOrException())
            DIV2_MULTIPLE_STATE_CHANGE -> setPreferencesBooleanFlag(Experiment.MULTIPLE_STATE_CHANGE_ENABLED, valueInt.toBooleanOrException())
            DIV2_DEMO_SHOW_RENDERING_TIME -> setPreferencesBooleanFlag(Experiment.SHOW_RENDERING_TIME, valueInt.toBooleanOrException())
            PERMANENT_DEBUG_PANEL -> setPreferencesBooleanFlag(Experiment.PERMANENT_DEBUG_PANEL_ENABLED, valueInt.toBooleanOrException())
            RENDER_EFFECT_ENABLED -> setPreferencesBooleanFlag(Experiment.RENDER_EFFECT_ENABLED, valueInt.toBooleanOrException())
            else -> return false
        }

        return true
    }
    
    private fun Int.toBooleanOrException(): Boolean {
        return toBoolean() ?: throw IllegalArgumentException("Unable to convert $this to boolean")
    }

    private fun setPreferencesBooleanFlag(flag: Experiment, value: Boolean) =
        Container.flagPreferenceProvider.setExperimentFlag(flag, value)

}
