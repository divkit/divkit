package com.yandex.div.core.timer

import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.errors.ErrorCollector
import java.util.Timer

internal class DivTimerEventDispatcher(
    private val errorCollector: ErrorCollector
) {
    private val timerControllers: MutableMap<String, TimerController> = mutableMapOf()

    private val activeTimerIds: MutableSet<String> = mutableSetOf()

    private var parentTimer: Timer? = null

    private var div2View: Div2View? = null

    fun getTimerController(id: String): TimerController? {
        return if (activeTimerIds.contains(id)) {
            timerControllers[id]
        } else null
    }

    fun addTimerController(timerController: TimerController) {
        val id = timerController.divTimer.id

        if (!timerControllers.containsKey(id)) {
            timerControllers[id] = timerController
        }
    }

    fun setActiveTimerIds(ids: List<String>) {
        timerControllers
            .filter { !ids.contains(it.key) }
            .values
            .forEach { timerController ->
                timerController.onDetach()
            }

        activeTimerIds.clear()

        activeTimerIds.addAll(ids)
    }

    fun changeState(id: String, command: String) {
        getTimerController(id)
            ?.applyCommand(command)
            ?: errorCollector.logError(IllegalArgumentException("Timer with id '$id' does not exist!"))
    }

    fun onAttach(view: Div2View) {
        val newParentTimer = Timer()
        parentTimer = newParentTimer

        div2View = view

        activeTimerIds.forEach { id ->
            timerControllers[id]?.onAttach(view, newParentTimer)
        }
    }

    fun onDetach(view: Div2View) {
        /*
        * Fix bug, when div2view in recycler view.
        * In recycler during bind first called attach for new view, than detach for old one.
        */
        if (div2View != view) return

        timerControllers.values.forEach { timerController ->
            timerController.onDetach()
        }

        parentTimer?.cancel()
        parentTimer = null
    }
}
