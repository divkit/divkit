package com.yandex.div.core.tooltip

import com.yandex.div.core.Disposable

/** Coordinates tooltip position updates with window and anchor changes. */
internal class TooltipUpdateCoordinator(
    private val events: TooltipUpdateEvents,
    private val recomputePosition: () -> Unit,
) {

    private var isActive = false

    private var updateSubscription: Disposable? = null
    private var anchorUpdateSubscription: Disposable? = null

    fun start() {
        if (isActive) {
            return
        }

        isActive = true
        updateSubscription = events.subscribeToUpdates(::onUpdate)
        restartAnchorTracking()
    }

    fun stop() {
        if (!isActive) {
            return
        }

        isActive = false

        anchorUpdateSubscription?.close()
        updateSubscription?.close()

        anchorUpdateSubscription = null
        updateSubscription = null
    }

    fun onConfigurationChanged() {
        if (!isActive) {
            return
        }

        restartAnchorTracking()
        recomputePosition()
    }

    private fun onUpdate() {
        if (!isActive) {
            return
        }

        recomputePosition()
    }

    private fun restartAnchorTracking() {
        anchorUpdateSubscription?.close()
        anchorUpdateSubscription = events.subscribeToAnchorUpdates(::onUpdate)
    }
}
