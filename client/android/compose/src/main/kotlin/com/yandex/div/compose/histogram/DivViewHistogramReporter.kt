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
import com.yandex.div.compose.di.DivViewScope
import com.yandex.div.histogram.HistogramFilter
import dev.zacsweers.metro.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.TimeMark
import kotlin.time.TimeSource

@DivViewScope
internal class DivViewHistogramReporter(
    private val configuration: DivHistogramConfiguration,
    private val timeSource: TimeSource,
) {
    private var prepareDuration: Duration? = null
    private var compositionStartedTime: TimeMark? = null
    private var compositionFinishedTime: TimeMark? = null
    private var changesAppliedTime: TimeMark? = null
    private var compositionDuration: Duration? = null
    private var applyChangesDuration: Duration? = null
    private var effectsDuration: Duration? = null
    private var totalDuration: Duration? = null
    private var state = "Cold"
    private var counter by mutableIntStateOf(0)

    @Inject
    constructor(configuration: DivHistogramConfiguration) :
        this(configuration, TimeSource.Monotonic)

    @Composable
    @SuppressLint("ComposableNaming")
    inline fun measure(prepareDuration: Duration, content: @Composable () -> Unit) {
        if (!configuration.isEnabled) {
            content()
            return
        }

        key(counter) {
            onCompositionStarted(prepareDuration)
            DisposableEffect(Unit) {
                onChangesApplied()
                onDispose {}
            }
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

    private fun now() = timeSource.markNow()

    private fun onCompositionStarted(prepareDuration: Duration) {
        reset()
        this.prepareDuration = prepareDuration
        compositionStartedTime = now()
    }

    private fun onCompositionFinished() {
        compositionFinishedTime = now()
        if (compositionDuration == null) {
            compositionDuration = compositionStartedTime?.elapsedNow()
        }
    }

    private fun onChangesApplied() {
        changesAppliedTime = now()
        if (applyChangesDuration == null) {
            applyChangesDuration = compositionFinishedTime?.elapsedNow()
        }
    }

    private fun onEffectsApplied() {
        if (effectsDuration == null) {
            effectsDuration = changesAppliedTime?.elapsedNow()
        }
    }

    private fun onDrawFinished() {
        if (totalDuration == null) {
            totalDuration = compositionStartedTime?.elapsedNow()
            report(
                name = "DivCompose.Render.Prepare",
                duration = prepareDuration,
                filter = configuration.prepareFilter
            )
            report(name = "DivCompose.Render.Composition", duration = compositionDuration)
            report(name = "DivCompose.Render.ApplyChanges", duration = applyChangesDuration)
            report(name = "DivCompose.Render.Effects", duration = effectsDuration)
            report(name = "DivCompose.Render.Total", duration = totalDuration)
            state = "Warm"
        }
    }

    private fun reset() {
        prepareDuration = null
        compositionStartedTime = null
        compositionFinishedTime = null
        changesAppliedTime = null
        compositionDuration = null
        applyChangesDuration = null
        effectsDuration = null
        totalDuration = null
    }

    private fun report(
        name: String,
        duration: Duration?,
        filter: HistogramFilter = HistogramFilter.ON,
    ) {
        val componentName = configuration.componentName
        if (duration == null || !filter.report(componentName.ifEmpty { null })) {
            return
        }

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

private val timeUnit = DurationUnit.MICROSECONDS
private val minValue = 1.milliseconds.inWholeMicroseconds
private val maxValue = 10.seconds.inWholeMicroseconds
private const val bucketCount = 50
