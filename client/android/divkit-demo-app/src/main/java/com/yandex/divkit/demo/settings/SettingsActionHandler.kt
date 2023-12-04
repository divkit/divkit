package com.yandex.divkit.demo.settings

import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import com.yandex.div.core.experiments.Experiment
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

const val NIGHT_MODE = "night_mode"
const val NIGHT_MODE_NIGHT = "NIGHT"
const val NIGHT_MODE_DAY = "DAY"
const val NIGHT_MODE_AUTO = "AUTO"

internal object SettingsActionHandler {

    fun handleActionUrl(uri: Uri): Boolean {
        if (uri.authority != AUTHORITY_SET_PREFERENCES || uri.scheme != SCHEME_DIV_ACTION)
            return false

        val value = uri.getQueryParameter(PARAM_VALUE)

        if (value?.toIntOrNull() != null) {
            val valueBool = value.toIntOrNull()!! > 0
            when (uri.getQueryParameter(PARAM_NAME)) {
                COMPLEX_REBIND -> setPreferencesBooleanFlag(Experiment.COMPLEX_REBIND_ENABLED, valueBool)
                DIV2_VIEW_POOL -> setPreferencesBooleanFlag(Experiment.VIEW_POOL_ENABLED, valueBool)
                DIV2_VIEW_POOL_PROFILING -> setPreferencesBooleanFlag(Experiment.VIEW_POOL_PROFILING_ENABLED, valueBool)
                DIV2_MULTIPLE_STATE_CHANGE -> setPreferencesBooleanFlag(Experiment.MULTIPLE_STATE_CHANGE_ENABLED, valueBool)
                DIV2_DEMO_SHOW_RENDERING_TIME -> setPreferencesBooleanFlag(Experiment.SHOW_RENDERING_TIME, valueBool)
                IMAGE_LOADER -> Container.preferences.imageLoader = valueBool
                else -> return false
            }
        } else {
            when(uri.getQueryParameter(PARAM_NAME)) {
                NIGHT_MODE ->  {
                    when(value) {
                        NIGHT_MODE_AUTO -> Container.preferences.nightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                        NIGHT_MODE_NIGHT -> Container.preferences.nightMode = AppCompatDelegate.MODE_NIGHT_YES
                        NIGHT_MODE_DAY -> Container.preferences.nightMode = AppCompatDelegate.MODE_NIGHT_NO
                        else -> return false
                    }
                    AppCompatDelegate.setDefaultNightMode(Container.preferences.nightMode)
                    return true
                }
                else -> return false
            }
        }
        return true
    }

    private fun setPreferencesBooleanFlag(flag: Experiment, value: Boolean) =
        Container.flagPreferenceProvider.setExperimentFlag(flag, value)

}
