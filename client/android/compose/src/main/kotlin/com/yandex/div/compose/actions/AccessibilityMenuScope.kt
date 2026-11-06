package com.yandex.div.compose.actions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div2.DivAccessibility.Mode
import com.yandex.div2.DivBase
import com.yandex.div2.DivInput
import com.yandex.div2.DivSlider
import com.yandex.div2.DivSwitch
import com.yandex.div2.DivVisibility

/**
 * Stores menu visibility for a composed Div subtree and the lifetime of its menu callbacks.
 */
internal class AccessibilityMenuScope(
    /**
     * Hides this element's menu, including both slider thumbs, due to local or ancestor state.
     */
    val hidesOwnMenu: Boolean,
    /**
     * Hides descendant menus; MERGE sets this even when the element's own menu remains visible.
     */
    val hidesDescendantMenus: Boolean,
) {
    /**
     * Becomes true after scope replacement or removal, invalidating its retained menu callbacks.
     */
    var isDisposed = false
}

internal val LocalAccessibilityMenuScope = compositionLocalOf {
    AccessibilityMenuScope(hidesOwnMenu = false, hidesDescendantMenus = false)
}

@Composable
internal fun WithAccessibilityMenuScope(
    data: DivBase,
    visibility: DivVisibility,
    content: @Composable () -> Unit,
) {
    val parent = LocalAccessibilityMenuScope.current
    val mode = data.accessibility?.mode?.observedValue()
    val hasMenu = !data.accessibility?.menu.isNullOrEmpty() ||
        (data is DivSlider && !data.secondaryValueAccessibility?.menu.isNullOrEmpty())
    val enabled = if (hasMenu) {
        when (data) {
            is DivSlider -> data.isEnabled.observedValue()
            is DivSwitch -> data.isEnabled.observedValue()
            is DivInput -> data.isEnabled.observedValue()
            else -> true
        }
    } else {
        true
    }
    val hidesOwnMenu = parent.hidesDescendantMenus || !enabled || mode == Mode.EXCLUDE ||
        visibility != DivVisibility.VISIBLE
    val hidesDescendantMenus = hidesOwnMenu || mode == Mode.MERGE
    val scope = if (!hasMenu && hidesOwnMenu == parent.hidesOwnMenu &&
        hidesDescendantMenus == parent.hidesDescendantMenus) {
        parent
    } else {
        remember(data, hidesOwnMenu, hidesDescendantMenus) {
            AccessibilityMenuScope(
                hidesOwnMenu = hidesOwnMenu,
                hidesDescendantMenus = hidesDescendantMenus,
            )
        }
    }
    if (hasMenu) {
        DisposableEffect(scope) {
            onDispose {
                scope.isDisposed = true
            }
        }
    }
    // Keep the provider call site stable so visibility changes preserve remembered child state.
    CompositionLocalProvider(LocalAccessibilityMenuScope provides scope, content = content)
}
