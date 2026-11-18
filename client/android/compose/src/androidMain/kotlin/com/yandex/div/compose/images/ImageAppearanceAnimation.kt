package com.yandex.div.compose.images

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.util.lerp
import coil3.decode.DataSource
import com.yandex.div.compose.utils.toEasing
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivFadeTransition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Alpha of a `DivImage` element while its loaded image appears with `appearance_animation`.
 */
@Stable
internal class ImageAppearanceAnimation {
    private var startAlpha by mutableFloatStateOf(1f)
    private var progress by mutableFloatStateOf(1f)
    private var job: Job? = null

    fun alpha(elementAlpha: Float): Float = lerp(startAlpha, elementAlpha, progress)

    fun onImageLoaded(
        dataSource: DataSource,
        animation: DivFadeTransition,
        resolver: ExpressionResolver,
        animationsEnabled: Boolean,
        scope: CoroutineScope,
    ) {
        if (!animationsEnabled || dataSource == DataSource.MEMORY || dataSource == DataSource.MEMORY_CACHE) {
            stop()
            return
        }

        job?.cancel()
        startAlpha = animation.alpha.evaluate(resolver).toFloat()
        progress = 0f
        val animationSpec = tween<Float>(
            durationMillis = animation.duration.evaluate(resolver).toInt(),
            delayMillis = animation.startDelay.evaluate(resolver).toInt(),
            easing = animation.interpolator.evaluate(resolver).toEasing(),
        )
        job = scope.launch {
            animate(initialValue = 0f, targetValue = 1f, animationSpec = animationSpec) { value, _ ->
                progress = value
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
        progress = 1f
    }
}
