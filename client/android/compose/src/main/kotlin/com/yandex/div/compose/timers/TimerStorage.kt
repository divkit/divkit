package com.yandex.div.compose.timers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.actions.DivActionHandler
import com.yandex.div.compose.dagger.DivLocalComponent
import com.yandex.div.compose.dagger.DivViewScope
import com.yandex.div2.DivTimer
import com.yandex.yatagan.Lazy
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

@DivViewScope
@OptIn(ExperimentalTime::class)
internal class TimerStorage @Inject constructor(
    private val actionHandler: Lazy<DivActionHandler>,
    private val coroutineScope: CoroutineScope,
    private val reporter: DivReporter,
    private val timeSource: TimeSource
) {
    private val timers = mutableMapOf<String, TimerController>()

    var isEnabled: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                timers.values.forEach { it.isEnabled = value }
            }
        }

    fun init(
        timers: List<DivTimer>,
        localComponent: DivLocalComponent
    ) {
        timers.forEach { timer ->
            val id = timer.id
            if (this.timers.containsKey(id)) {
                reporter.reportError("Timer with the same id is already exist: $id")
                return@forEach
            }

            this.timers[id] = TimerController(
                timer = timer,
                actionHandler = actionHandler.get(),
                actionHandlingContext = localComponent.actionHandlingContext,
                coroutineScope = coroutineScope,
                expressionResolver = localComponent.expressionResolver,
                reporter = reporter,
                timeSource = timeSource,
                variableController = localComponent.variableController
            )
        }
    }

    fun start(id: String) = withTimer(id) { start() }

    fun stop(id: String) = withTimer(id) { stop() }

    fun pause(id: String) = withTimer(id) { pause() }

    fun resume(id: String) = withTimer(id) { resume() }

    fun cancel(id: String) = withTimer(id) { cancel() }

    fun reset(id: String) = withTimer(id) {
        cancel()
        start()
    }

    private fun withTimer(id: String, action: TimerController.() -> Unit) {
        val timer = timers[id]
        if (timer == null) {
            reporter.reportError("Timer does not exist: $id")
            return
        }

        if (isEnabled) {
            timer.action()
        }
    }
}

@Composable
internal fun TimerStorage.observe() {
    DisposableEffect(this) {
        isEnabled = true
        onDispose {
            isEnabled = false
        }
    }
}
