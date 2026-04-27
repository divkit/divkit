package com.yandex.div.compose.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.custom.DivCustomEnvironment
import com.yandex.div.compose.utils.divContext
import com.yandex.div.compose.utils.expressionResolver
import com.yandex.div.compose.utils.reportError
import com.yandex.div2.DivCustom

@Composable
internal fun DivCustomView(
    modifier: Modifier,
    data: DivCustom
) {
    val customType = data.customType
    val factory = divContext.component.customViewFactories[customType]
    if (factory == null) {
        reportError("No custom view factory for custom_type: $customType")
        return
    }

    val childItems = data.items.orEmpty()
    val environment = DivCustomEnvironment(
        data = data,
        expressionResolver = expressionResolver,
        modifier = modifier,
        items = {
            for (child in childItems) {
                DivBlockView(child)
            }
        },
        item = { index, modifier ->
            DivBlockView(childItems[index], modifier)
        },
    )
    factory.Content(environment)
}
