package com.yandex.div.compose.views.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.graphics.Brush
import com.yandex.div.compose.context.divContext
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.gradient.observeLinearGradient
import com.yandex.div.compose.utils.gradient.observeRadialGradient
import com.yandex.div.core.util.AnimatedTextGradientMath
import com.yandex.div2.DivAnimatedTextGradient
import com.yandex.div2.DivStaticTextGradient
import com.yandex.div2.DivTextGradient

@Composable
internal fun DivTextGradient.observedValue(): Brush? {
    return when (this) {
        is DivTextGradient.Linear -> value.observeLinearGradient()
        is DivTextGradient.Radial -> value.observeRadialGradient()
        is DivTextGradient.Animated -> value.observedValue()
    }
}

@Composable
private fun DivAnimatedTextGradient.observedValue(): Brush? {
    val animationsEnabled = divContext.component
        .animationConfiguration
        .isEnabledAsState()
    val duration = duration.observedValue()
    val animationPhase = rememberAnimatedTextGradientPhase(this, duration, animationsEnabled && duration > 0L)
    return when (val gradient = gradient) {
        is DivStaticTextGradient.Linear -> gradient.value.observeLinearGradient(animationPhase)
        is DivStaticTextGradient.Radial -> gradient.value.observeRadialGradient(animationPhase)
    }
}

@Composable
private fun rememberAnimatedTextGradientPhase(
    animationKey: Any,
    durationMillis: Long,
    animationsEnabled: Boolean,
): Float? {
    var phase by remember(animationKey) { mutableFloatStateOf(STATIC_ANIMATION_PHASE) }
    LaunchedEffect(animationKey, animationsEnabled, durationMillis) {
        if (!animationsEnabled) {
            phase = STATIC_ANIMATION_PHASE
            return@LaunchedEffect
        }
        while (true) {
            withFrameMillis { frameTimeMillis ->
                phase = AnimatedTextGradientMath.phase(frameTimeMillis, durationMillis)
            }
        }
    }
    return phase.takeIf { animationsEnabled }
}

private const val STATIC_ANIMATION_PHASE = 0f
