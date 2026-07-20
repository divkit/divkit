package com.yandex.div.compose.histogram

import android.annotation.SuppressLint
import android.view.ViewTreeObserver
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import com.yandex.div.compose.dagger.DivViewScope
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark
import kotlin.time.TimeSource

@DivViewScope
@OptIn(ExperimentalTime::class)
internal class DivViewHistogramReporter @Inject constructor(
    private val configuration: DivHistogramConfiguration
) {
    private var compositionStartedTime: TimeMark? = null
    private var compositionFinishedTime: TimeMark? = null
    private var compositionDuration: Duration? = null
    private var effectsDuration: Duration? = null
    private var totalDuration: Duration? = null
    private var state = "Cold"
    private var counter by mutableIntStateOf(0)

    @Composable
    @SuppressLint("ComposableNaming")
    inline fun measure(content: @Composable () -> Unit) {
        if (!configuration.isEnabled) {
            content()
            return
        }

        key(counter) {
            onCompositionStarted()
            content()
            onCompositionFinished()
        }

        val view = LocalView.current
        DisposableEffect(counter) {
            onEffectsApplied()

            var listener: ViewTreeObserver.OnDrawListener? = null
            listener = ViewTreeObserver.OnDrawListener {
                onDrawFinished()
                view.post {
                    view.viewTreeObserver.removeOnDrawListener(listener)
                }
            }
            view.viewTreeObserver.addOnDrawListener(listener)

            onDispose {
                view.viewTreeObserver.removeOnDrawListener(listener)
            }
        }
    }

    fun forceRecomposition() {
        counter++
    }

    private fun now() = TimeSource.Monotonic.markNow()

    private fun onCompositionStarted() {
        reset()
        compositionStartedTime = now()
    }

    private fun onCompositionFinished() {
        compositionFinishedTime = now()
        if (compositionDuration == null) {
            compositionDuration = compositionStartedTime?.elapsedNow()
        }
    }

    private fun onEffectsApplied() {
        if (effectsDuration == null) {
            effectsDuration = compositionFinishedTime?.elapsedNow()
        }
    }

    private fun onDrawFinished() {
        if (totalDuration == null) {
            totalDuration = compositionStartedTime?.elapsedNow()
            report(name = "DivCompose.Render.Composition", duration = compositionDuration)
            report(name = "DivCompose.Render.Effects", duration = effectsDuration)
            report(name = "DivCompose.Render.Total", duration = totalDuration)
            state = "Warm"
        }
    }

    private fun reset() {
        compositionStartedTime = null
        compositionFinishedTime = null
        compositionDuration = null
        effectsDuration = null
        totalDuration = null
    }

    private fun report(name: String, duration: Duration?) {
        if (duration == null) {
            return
        }

        val componentName = configuration.componentName
        val name = if (componentName.isEmpty()) name else "$componentName.$name"
        configuration.histogramBridge.recordTimeHistogram(
            "$name.$state",
            duration.inWholeMicroseconds,
            minValue,
            maxValue,
            timeUnit,
            bucketCount
        )
    }
}

private val timeUnit = TimeUnit.MICROSECONDS
private val minValue = timeUnit.convert(1L, TimeUnit.MILLISECONDS)
private val maxValue = timeUnit.convert(10L, TimeUnit.SECONDS)
private const val bucketCount = 50
