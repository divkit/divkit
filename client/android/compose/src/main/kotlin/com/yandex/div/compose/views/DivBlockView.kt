package com.yandex.div.compose.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.views.container.DivContainerView
import com.yandex.div.compose.views.modifiers.apply
import com.yandex.div2.Div
import com.yandex.div2.DivVisibility

@Composable
internal fun DivBlockView(
    data: Div,
    modifier: Modifier = Modifier,
    applyMargins: Boolean = true,
) {
    if (data.value().visibility.observedValue() == DivVisibility.GONE) return

    val modifier = modifier.apply(
        data,
        applyPaddings = data !is Div.Container,
        applyMargins = applyMargins
    )

    when (data) {
        is Div.Container -> DivContainerView(modifier, data.value)
        is Div.Image -> DivImageView(modifier, data.value)
        is Div.Separator -> DivSeparatorView(modifier, data.value)
        is Div.Text -> DivTextView(modifier, data.value)
        else -> reporter.reportError("Element not supported")
    }
}
