package com.yandex.div.compose.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.custom.DivCustomEnvironment
import com.yandex.div.compose.utils.divContext
import com.yandex.div.compose.utils.expressionResolver
import com.yandex.div2.DivCustom

@Composable
internal fun DivCustomView(
    modifier: Modifier,
    data: DivCustom,
) {
    val factory = divContext.component.customViewFactory
    val childItems = data.items.orEmpty()
    val environment = DivCustomEnvironment(
        div = data,
        expressionResolver = expressionResolver,
        items = {
            for (child in childItems) {
                DivBlockView(child)
            }
        },
        item = { index, modifier ->
            DivBlockView(childItems[index], modifier)
        },
    )
    factory.Content(
        environment = environment,
        modifier = modifier,
    )
}
