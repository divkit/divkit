package com.yandex.div.compose.histogram

import android.annotation.SuppressLint
import android.view.ViewTreeObserver
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
    private var compositionDuration: Duration? = null
    private var totalDuration: Duration? = null
    private var state = "Cold"

    @Composable
    @SuppressLint("ComposableNaming")
    fun measure(content: @Composable () -> Unit) {
        if (!configuration.isEnabled) {
            content()
            return
        }

        onCompositionStarted()

        content()

        val view = LocalView.current
        DisposableEffect(Unit) {
            onCompositionFinished()

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
                reset()
            }
        }
    }

    private fun now() = TimeSource.Monotonic.markNow()

    private fun onCompositionStarted() {
        if (compositionStartedTime == null) {
            compositionStartedTime = now()
        }
    }

    private fun onCompositionFinished() {
        if (compositionDuration == null) {
            compositionDuration = compositionStartedTime?.elapsedNow()
        }
    }

    private fun onDrawFinished() {
        if (totalDuration == null) {
            totalDuration = compositionStartedTime?.elapsedNow()
            report()
            state = "Warm"
        }
    }

    private fun reset() {
        compositionStartedTime = null
        compositionDuration = null
        totalDuration = null
    }

    private fun report() {
        report(name = "DivCompose.Render.Composition", duration = compositionDuration)
        report(name = "DivCompose.Render.Total", duration = totalDuration)
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
            minBucketValue,
            maxBucketValue,
            timeUnit,
            bucketCount
        )
    }
}

private val timeUnit = TimeUnit.MICROSECONDS
private val minBucketValue = timeUnit.convert(1L, TimeUnit.MILLISECONDS)
private val maxBucketValue = timeUnit.convert(10L, TimeUnit.SECONDS)
private const val bucketCount = 50
