package com.yandex.div.core.timer

import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.errors.ErrorCollector

internal class DivTimerEventDispatcher(
    private val errorCollector: ErrorCollector
) {
    private val timerControllers: MutableMap<String, TimerController> = mutableMapOf()

    private val activeTimerIds: MutableSet<String> = mutableSetOf()

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
            .forEach { it.reset() }

        activeTimerIds.clear()

        activeTimerIds.addAll(ids)
    }

    fun changeState(id: String, command: String) {
        getTimerController(id)
            ?.applyCommand(command)
            ?: errorCollector.logError(IllegalArgumentException("Timer with id '$id' does not exist!"))
    }

    fun onAttach(view: Div2View) {
        activeTimerIds.forEach { id ->
            val controller = timerControllers[id] ?: return@forEach
            if (controller.isAttachedToView(view)) return@forEach

            controller.onAttach(view)
        }
    }

    fun onDetach(view: Div2View) {
        timerControllers.values.forEach { timerController ->
            timerController.onDetach(view)
        }
    }
}
