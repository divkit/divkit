package com.yandex.div.compose.timers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.actions.DivActionHandler
import com.yandex.div.compose.di.DivLocalComponent
import com.yandex.div.compose.di.DivViewScope
import com.yandex.div.compose.di.Names
import com.yandex.div2.DivData
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Named
import kotlin.time.TimeSource
import kotlinx.coroutines.CoroutineScope

@DivViewScope
internal class TimerStorage @Inject constructor(
    data: DivData,
    private val actionHandler: Lazy<DivActionHandler>,
    @Named(Names.MAIN_COROUTINE_SCOPE)
    private val mainCoroutineScope: CoroutineScope,
    private val reporter: DivReporter,
    @Named(Names.ROOT_LOCAL_COMPONENT)
    private val rootLocalComponent: Lazy<DivLocalComponent>,
    private val timeSource: TimeSource,
) {
    private val timers = mutableMapOf<String, TimerController>()

    var isEnabled: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                timers.values.forEach { it.isEnabled = value }
            }
        }

    init {
        val localComponent = rootLocalComponent.value
        data.timers.orEmpty().forEach { timer ->
            val id = timer.id
            if (this.timers.containsKey(id)) {
                reporter.reportError("Timer with the same id is already exist: $id")
                return@forEach
            }

            this.timers[id] = TimerController(
                timer = timer,
                actionHandler = actionHandler,
                actionHandlingContext = localComponent.actionHandlingContext,
                expressionResolver = localComponent.expressionResolver,
                mainCoroutineScope = mainCoroutineScope,
                reporter = reporter,
                timeSource = timeSource
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
