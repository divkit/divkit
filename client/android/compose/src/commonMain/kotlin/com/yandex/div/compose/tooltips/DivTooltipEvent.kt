package com.yandex.div.compose.tooltips

/**
 * An event in popup content's Compose lifecycle.
 *
 * Events reflect entry into and removal from composition, not exact visual presentation timing.
 */
sealed class DivTooltipEvent {
    abstract val cardId: String
    abstract val tooltipId: String

    /** The popup content entered composition. */
    data class Shown(
        override val cardId: String,
        override val tooltipId: String,
    ) : DivTooltipEvent()

    /** Popup content was removed from composition after its exit, or immediately during disposal. */
    data class Dismissed(
        override val cardId: String,
        override val tooltipId: String,
    ) : DivTooltipEvent()
}
