package com.yandex.div.compose.views.modifiers

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp

/**
 * How a match_parent axis of an element resolves against its parent.
 */
@Immutable
internal sealed interface MatchParentSize {

    /** Fills the parent's constraints. */
    data object Fill : MatchParentSize

    /**
     * Fills at most [max], margins included, where the parent measures with unbounded constraints,
     * e.g. a gallery item along the scroll axis. The element's own min_size and max_size take
     * precedence over the bound.
     */
    data class Bounded(val max: Dp) : MatchParentSize
}
