package com.yandex.div.compose.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.views.container.DivContainerView
import com.yandex.div.compose.views.gallery.DivGalleryView
import com.yandex.div.compose.views.image.DivImageView
import com.yandex.div.compose.views.modifiers.apply
import com.yandex.div.compose.views.modifiers.applyPaddings
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.reporter
import com.yandex.div2.Div
import com.yandex.div2.DivVisibility

@Composable
internal fun DivBlockView(
    data: Div,
    modifier: Modifier = Modifier,
    applyMargins: Boolean = true,
) {
    WithLocalDivContext(data.value()) {
        if (data.value().visibility.observedValue() == DivVisibility.GONE) {
            return@WithLocalDivContext
        }

        val modifier = modifier.apply(data, applyMargins = applyMargins)

        when (data) {
            is Div.Container -> DivContainerView(modifier, data.value)
            is Div.Gallery -> DivGalleryView(modifier, data.value)
            is Div.Image -> DivImageView(modifier.applyPaddings(data), data.value)
            is Div.Separator -> DivSeparatorView(modifier.applyPaddings(data), data.value)
            is Div.Text -> DivTextView(modifier.applyPaddings(data), data.value)
            else -> reporter.reportError("Element not supported")
        }
    }
}
