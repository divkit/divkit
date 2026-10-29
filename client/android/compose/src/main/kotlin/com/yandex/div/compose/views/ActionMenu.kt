package com.yandex.div.compose.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.actions.ActionMenuHolder
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.actions.DivActions
import com.yandex.div.compose.actions.any
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.dagger.handleActions
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div2.DivAction

private val ACTION_MENU_MIN_WIDTH = 196.dp

@Composable
internal inline fun WithActionMenu(
    actions: DivActions?,
    modifier: Modifier,
    menuOffset: DpOffset,
    content: @Composable (Modifier) -> Unit,
) {
    if (actions?.any { it.hasMenuItems() } != true) {
        content(modifier)
        return
    }

    val menuHolder = LocalDivViewContext.current.component.actionMenuHolder
    val menuAction = menuHolder.expandedMenuAction
    if (menuAction != null && actions?.any { it.hasMenuAction(menuAction) } == true
        && menuAction.isEnabled.observedValue()
    ) {
        Box(modifier) {
            content(Modifier)
            ActionMenu(menuHolder, menuOffset)
        }
    } else {
        content(modifier)
    }
}

@Composable
private fun ActionMenu(menuHolder: ActionMenuHolder, offset: DpOffset) {
    val menuItems = menuHolder.menuItems
    DropdownMenu(
        expanded = menuItems.isNotEmpty(),
        onDismissRequest = { menuHolder.dismiss() },
        modifier = Modifier.widthIn(min = ACTION_MENU_MIN_WIDTH),
        offset = offset,
        containerColor = Color.White,
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

private fun List<DivAction>?.hasMenuItems(): Boolean {
    return this?.any { !it.menuItems.isNullOrEmpty() } ?: false
}

private fun List<DivAction>?.hasMenuAction(action: DivAction): Boolean {
    return this?.contains(action) ?: false
}

