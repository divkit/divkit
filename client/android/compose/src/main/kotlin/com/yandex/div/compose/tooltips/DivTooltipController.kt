package com.yandex.div.compose.tooltips

import androidx.compose.runtime.snapshotFlow
import com.yandex.div.compose.DivView
import com.yandex.div.compose.dagger.DivViewScope
import com.yandex.div.compose.dagger.Names
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Controls tooltips rendered by a single [DivView].
 *
 * Showing a tooltip requires its anchor to be currently composed and laid out. Android View
 * attachment is not tracked separately.
 */
@DivViewScope
class DivTooltipController @Inject internal constructor(
    @Named(Names.CARD_ID)
    private val cardId: String,
    @Named(Names.MAIN_COROUTINE_SCOPE)
    private val coroutineScope: CoroutineScope,
    private val tooltipStateStorage: TooltipStateStorage,
) {
    private val mutableEvents = MutableSharedFlow<DivTooltipEvent>()
    private val mutableAvailableTooltips = MutableStateFlow<Set<String>>(emptySet())
    private val availabilityCollectionJob: Job = coroutineScope.launch {
        snapshotFlow { tooltipStateStorage.availableTooltipIds() }.collect {
            mutableAvailableTooltips.value = it
        }
    }

    /** Popup content composition lifecycle events from both Div actions and controller commands. */
    val events: SharedFlow<DivTooltipEvent> = mutableEvents.asSharedFlow()

    /** Tooltips whose anchors are currently composed and laid out in this card. */
    val availableTooltips: StateFlow<Set<String>> = mutableAvailableTooltips.asStateFlow()

    /**
     * Shows a tooltip if its anchor is currently composed and laid out. Android View attachment is
     * not tracked separately.
     *
     * [multiple] is required for parity with the server-provided tooltip command. It is currently
     * accepted but does not affect behavior.
     *
     * @return `true` when the request is accepted, including when the tooltip is already requested;
     * `false` when the card's [DivView] is not currently composed, or the tooltip anchor is not
     * currently composed or not laid out. Popup content composition lifecycle is reported
     * separately through [events].
     */
    fun showTooltip(
        tooltipId: String,
        @Suppress("UNUSED_PARAMETER") multiple: Boolean,
    ): Boolean {
        if (!tooltipStateStorage.isAvailable(tooltipId)) return false

        tooltipStateStorage.show(tooltipId)
        return true
    }

    /**
     * Hides a requested tooltip.
     *
     * @return `true` when a request was removed; `false` when the tooltip was not requested. Popup
     * content leaving composition is reported separately through [events].
     */
    fun hideTooltip(tooltipId: String): Boolean {
        return tooltipStateStorage.hide(tooltipId)
    }

    /**
     * Cancels all requested tooltips for this card.
     *
     * @return `true` when the command was accepted, including when the card had no requested
     * tooltips. Popup content leaving composition is reported separately through [events].
     */
    fun cancelTooltips(): Boolean {
        tooltipStateStorage.cancelAll()
        return true
    }

    internal fun onShown(tooltipId: String) {
        publish(DivTooltipEvent.Shown(cardId, tooltipId))
    }

    internal fun onDismissed(tooltipId: String) {
        publish(DivTooltipEvent.Dismissed(cardId, tooltipId))
    }

    internal fun dispose() {
        availabilityCollectionJob.cancel()
        mutableAvailableTooltips.value = emptySet()
    }

    private fun publish(event: DivTooltipEvent) {
        coroutineScope.launch {
            mutableEvents.emit(event)
        }
    }
}
