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
        var newParentTimer: Timer? = null

        activeTimerIds.forEach { id ->
            val controller = timerControllers[id] ?: return@forEach
            if (controller.isAttachedToView(view)) return@forEach

            (newParentTimer ?: createParentTimer().also { newParentTimer = it }).let {
                controller.onAttach(view, it)
            }
        }
    }

    private fun createParentTimer() = Timer().also {
        parentTimer?.cancel()
        parentTimer = it
    }

    fun onDetach(view: Div2View) {
        timerControllers.values.forEach { timerController ->
            timerController.onDetach(view)
        }

        parentTimer?.cancel()
        parentTimer = null
    }
}
