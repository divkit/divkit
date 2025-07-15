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
            touchExplorationEnabled != null -> touchExplorationEnabled!!
            else -> {
                evaluateTouchModeEnabled(context)
                touchExplorationEnabled!!
            }
        }
    }

    companion object {
        var touchExplorationEnabled: Boolean? = null

        fun evaluateTouchModeEnabled(context: Context) {
            if (touchExplorationEnabled != null) return
            val accessibilityManager = context.getSystemService(
                Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
            touchExplorationEnabled = accessibilityManager?.isTouchExplorationEnabled ?: false
        }
    }
}
