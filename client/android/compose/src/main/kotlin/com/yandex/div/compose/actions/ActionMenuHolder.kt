package com.yandex.div.compose.actions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.yandex.div.compose.dagger.DivViewScope
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import javax.inject.Inject

/**
 * Provides access to the current context menu.
 */
@DivViewScope
internal class ActionMenuHolder @Inject constructor() {
    var menuItems: List<DivAction.MenuItem> by mutableStateOf(emptyList())
    var expandedMenuAction: DivAction? by mutableStateOf(null)

    fun showIfNeeded(action: DivAction, expressionResolver: ExpressionResolver) {
        val items = action.getEnabledMenuItems(expressionResolver)
        if (items.isNotEmpty()) {
            menuItems = items
            expandedMenuAction = action
        }
    }

    fun dismiss() {
        menuItems = emptyList()
        expandedMenuAction = null
    }
}

private fun DivAction.getEnabledMenuItems(
    expressionResolver: ExpressionResolver
): List<DivAction.MenuItem> {
    val items = menuItems ?: return emptyList()
    return items.filter { it.hasEnabledActions(expressionResolver) }
}

private fun DivAction.MenuItem.hasEnabledActions(
    expressionResolver: ExpressionResolver
): Boolean {
    val actions = actions ?: action?.let { listOf(it) } ?: return false
    return actions.any { it.isEnabled.evaluate(expressionResolver) }
}
