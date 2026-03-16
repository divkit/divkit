package com.yandex.div.compose.views.modifiers

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.views.LocalDivViewContext
import com.yandex.div.compose.views.actionHandler
import com.yandex.div.compose.views.state.observeAsValue
import com.yandex.div2.Div
import com.yandex.div2.DivAction

@Composable
internal fun Modifier.actions(data: Div): Modifier {
    val actions = data.actions.filter { it.isEnabled.observeAsValue() }
    if (actions.isEmpty()) {
        return this
    }

    val localContext = LocalDivViewContext.current
    val actionHandler = actionHandler
    return clickable {
        val actionHandlingContext = localContext.actionHandlingContext
        actions.forEach {
            actionHandler.handle(context = actionHandlingContext, action = it)
        }
    }
}

private val Div.actions: List<DivAction>
    get() {
        return when (this) {
            is Div.Container -> getActions(value.actions, value.action)
            is Div.Image -> getActions(value.actions, value.action)
            is Div.GifImage -> getActions(value.actions, value.action)
            is Div.Grid -> getActions(value.actions, value.action)
            is Div.Separator -> getActions(value.actions, value.action)
            is Div.State -> getActions(value.actions, value.action)
            is Div.Text -> getActions(value.actions, value.action)
            is Div.Custom,
            is Div.Gallery,
            is Div.Indicator,
            is Div.Input,
            is Div.Pager,
            is Div.Select,
            is Div.Slider,
            is Div.Switch,
            is Div.Tabs,
            is Div.Video -> emptyList()
        }
    }

private fun getActions(actions: List<DivAction>?, action: DivAction?): List<DivAction> {
    return actions ?: action?.let { listOf(it) } ?: emptyList()
}
