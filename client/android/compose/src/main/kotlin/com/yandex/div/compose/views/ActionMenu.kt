package com.yandex.div.compose.views

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.actions.ActionMenuHolder
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.actions.DivActions
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.dagger.handleActions
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div2.DivAction

@Composable
internal fun WithActionMenu(
    actions: DivActions?,
    modifier: Modifier,
    content: @Composable (Modifier) -> Unit,
) {
    val menuHolder = LocalDivViewContext.current.component.actionMenuHolder
    val menuAction = menuHolder.expandedMenuAction
    if (menuAction != null && actions?.hasMenuAction(menuAction) == true
        && menuAction.isEnabled.observedValue()
    ) {
        Box(modifier) {
            content(Modifier)
            ActionMenu(menuHolder)
        }
    } else {
        content(modifier)
    }
}

@Composable
private fun ActionMenu(menuHolder: ActionMenuHolder) {
    val menuItems = menuHolder.menuItems
    DropdownMenu(
        expanded = menuItems.isNotEmpty(),
        onDismissRequest = { menuHolder.dismiss() }
    ) {
        val localComponent = LocalComponent.current
        menuItems.forEach { item ->
            DropdownMenuItem(
                text = { Text(item.text.observedValue()) },
                onClick = {
                    menuHolder.dismiss()
                    localComponent.handleActions(
                        actions = item.actions ?: item.action?.let { listOf(it) } ?: emptyList(),
                        source = DivActionSource.TAP,
                    )
                }
            )
        }
    }
}

private fun DivActions.hasMenuAction(action: DivAction): Boolean {
    return tapActions.hasMenuAction(action)
            || doubleTapActions.hasMenuAction(action)
            || longTapActions.hasMenuAction(action)
}

private fun List<DivAction>?.hasMenuAction(action: DivAction): Boolean {
    return this?.contains(action) ?: false
}
