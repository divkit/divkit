package com.yandex.div.core.util

import android.content.Context
import android.view.accessibility.AccessibilityManager
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.dagger.ExperimentFlag
import com.yandex.div.core.experiments.Experiment
import javax.inject.Inject

@DivScope
internal class AccessibilityStateProvider @Inject constructor(
    @ExperimentFlag(Experiment.ACCESSIBILITY_ENABLED) val a11yConfigurationEnabled: Boolean,
) {
    fun isAccessibilityEnabled(context: Context): Boolean {
        return when {
            !a11yConfigurationEnabled -> false
            touchModeEnabled != null -> touchModeEnabled!!
            else -> {
                val accessibilityManager = context.getSystemService(
                    Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
                (accessibilityManager?.isTouchExplorationEnabled == true).let {
                    touchModeEnabled = it
                    return it
                }
            }
        }
    }

    private companion object { var touchModeEnabled: Boolean? = null }
}
