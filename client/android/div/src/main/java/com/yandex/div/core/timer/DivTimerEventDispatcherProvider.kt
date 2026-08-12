package com.yandex.div.core.timer

import com.yandex.div.core.DivActionPerformer
import com.yandex.div.core.dagger.DivDataScope
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivData
import com.yandex.div2.DivTimer
import javax.inject.Inject

@DivDataScope
internal class DivTimerEventDispatcherProvider @Inject constructor(
    private val actionPerformer: DivActionPerformer,
    private val errorCollectors: ErrorCollectors
) {

    private var controller: DivTimerEventDispatcher? = null

    internal fun getOrCreate(
        data: DivData,
        expressionResolver: ExpressionResolver
    ): DivTimerEventDispatcher? {
        val timers = data.timers ?: return null

        val errorCollector = errorCollectors.getOrCreate(data)
        val timerEventDispatcher = controller ?: run {
            val divTimerEventDispatcher = DivTimerEventDispatcher(errorCollector)

            timers.forEach { timer ->
                val controller = timer.toTimerController(errorCollector, expressionResolver)

                divTimerEventDispatcher.addTimerController(controller)
            }

            controller = divTimerEventDispatcher
            divTimerEventDispatcher
        }

        timerEventDispatcher.invalidateTimersSet(timers, errorCollector, expressionResolver)

        return timerEventDispatcher
    }

    private fun DivTimer.toTimerController(
        errorCollector: ErrorCollector,
        expressionResolver: ExpressionResolver
    ): TimerController {
        return TimerController(
            this,
            actionPerformer,
            errorCollector,
            expressionResolver
        )
    }

    private fun DivTimerEventDispatcher.invalidateTimersSet(
        timers: List<DivTimer>,
        errorCollector: ErrorCollector,
        expressionResolver: ExpressionResolver
    ) {
        timers.forEach { timer ->
            val contains = getTimerController(timer.id) != null

            if (!contains) {
                addTimerController(timer.toTimerController(errorCollector, expressionResolver))
            }
        }

        setActiveTimerIds(timers.map { it.id })
    }
}