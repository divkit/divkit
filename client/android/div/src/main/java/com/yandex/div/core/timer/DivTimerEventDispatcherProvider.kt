package com.yandex.div.core.timer

import com.yandex.div.DivDataTag
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivData
import com.yandex.div2.DivTimer
import java.util.Collections
import javax.inject.Inject

@DivScope
internal class DivTimerEventDispatcherProvider @Inject constructor(
    private val divActionHandler: DivActionHandler,
    private val errorCollectors: ErrorCollectors
) {
    private val controllers =
        Collections.synchronizedMap(mutableMapOf<String, DivTimerEventDispatcher>())

    internal fun getOrCreate(
        dataTag: DivDataTag,
        data: DivData,
        expressionResolver: ExpressionResolver
    ): DivTimerEventDispatcher? {
        val timers = data.timers ?: return null

        val errorCollector = errorCollectors.getOrCreate(dataTag, data)
        val timerEventDispatcher = controllers.getOrPut(dataTag.id) {
            val divTimerEventDispatcher = DivTimerEventDispatcher(errorCollector)

            timers.forEach { timer ->
                val controller = timer.toTimerController(errorCollector, expressionResolver)

                divTimerEventDispatcher.addTimerController(controller)
            }

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
            divActionHandler,
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