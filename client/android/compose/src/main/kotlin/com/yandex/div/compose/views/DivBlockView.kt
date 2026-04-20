package com.yandex.div.compose.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.dagger.WithLocalComponent
import com.yandex.div.compose.views.container.DivContainerView
import com.yandex.div.compose.views.gallery.DivGalleryView
import com.yandex.div.compose.views.image.DivImageView
import com.yandex.div.compose.views.pager.DivPagerView
import com.yandex.div.compose.views.modifiers.apply
import com.yandex.div.compose.views.modifiers.applyPaddings
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.reportError
import com.yandex.div.compose.views.input.DivInputView
import com.yandex.div.compose.views.state.DivStateView
import com.yandex.div.compose.views.text.DivTextView
import com.yandex.div2.Div
import com.yandex.div2.DivVisibility

@Composable
internal fun DivBlockView(
    data: Div,
    modifier: Modifier = Modifier,
    applyMargins: Boolean = true,
) {
    WithLocalComponent(data.value()) {
        if (data.value().visibility.observedValue() == DivVisibility.GONE) {
            return@WithLocalComponent
        }

        val modifier = modifier.apply(data, applyMargins = applyMargins)

        when (data) {
            is Div.Container -> DivContainerView(modifier, data.value)
            is Div.Custom -> DivCustomView(modifier.applyPaddings(data), data.value)
            is Div.Gallery -> DivGalleryView(modifier, data.value)
            is Div.Image -> DivImageView(modifier.applyPaddings(data), data.value)
            is Div.Input -> DivInputView(modifier.applyPaddings(data), data.value)
            is Div.Pager -> DivPagerView(modifier, data.value)
            is Div.Separator -> DivSeparatorView(modifier.applyPaddings(data), data.value)
            is Div.State -> DivStateView(modifier, data.value)
            is Div.Text -> DivTextView(modifier.applyPaddings(data), data.value)
            is Div.GifImage -> NotSupported("gif")
            is Div.Grid -> NotSupported("grid")
            is Div.Indicator -> NotSupported("indicator")
            is Div.Select -> NotSupported("select")
            is Div.Slider -> NotSupported("slider")
            is Div.Switch -> NotSupported("switch")
            is Div.Tabs -> NotSupported("tabs")
            is Div.Video -> NotSupported("video")
        }
    }
}

@Composable
private fun NotSupported(name: String) {
    reportError("Element not supported: $name")
}
