package com.yandex.div.compose.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.custom.DivCustomEnvironment
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.utils.reportError
import com.yandex.div2.DivCustom

@Composable
internal fun DivCustomView(
    modifier: Modifier,
    data: DivCustom
) {
    val component = LocalComponent.current
    val customType = data.customType
    val factory = component.customViewFactories[customType]
    if (factory == null) {
        reportError("No custom view factory for custom_type: $customType")
        return
    }

    val childItems = data.items.orEmpty()
    val environment = DivCustomEnvironment(
        data = data,
        expressionResolver = component.expressionResolver,
        storedValuesStorage = component.storedValuesStorage,
        items = {
            for (child in childItems) {
                DivBlockView(child)
            }
        },
        item = { index, modifier ->
            DivBlockView(childItems[index], modifier)
        },
    )
    factory.Content(modifier, environment)
}
