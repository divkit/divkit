package com.yandex.div.compose.tooltips

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.yandex.div.compose.context.LocalDivViewContext

/**
 * Top-level composable that renders all currently shown [TooltipPopup]s for the
 * enclosing [com.yandex.div.compose.DivView].
 */
@Composable
internal fun TooltipsHost() {
    val tooltipStateStorage = LocalDivViewContext.current.component.tooltipStateStorage
    val shownIds = tooltipStateStorage.shownTooltipIds()

    // Local list of ids currently in composition. Includes ids that have been
    // removed from [tooltipStateStorage] but are still running their exit animation,
    // so their popups stay alive until the animation completes.
    val displayedIds = remember { mutableStateListOf<String>() }

    SideEffect {
        shownIds.forEach { id ->
            if (!displayedIds.contains(id)) {
                displayedIds.add(id)
            }
        }
    }

    displayedIds.forEach { id ->
        val anchorEntry = tooltipStateStorage.getAnchor(id) ?: return@forEach
        key(id) {
            TooltipPopup(
                anchorEntry = anchorEntry,
                isVisible = id in shownIds,
                onDismissRequested = { tooltipStateStorage.hide(id) },
                onHidden = { displayedIds.remove(id) },
            )
        }
    }
}
