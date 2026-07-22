package com.yandex.div.compose.views

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.actions.observedActions
import com.yandex.div.compose.context.divContext
import com.yandex.div.compose.context.expressionResolver
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.dagger.WithLocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.extensions.DivExtensionEnvironment
import com.yandex.div.compose.utils.reportError
import com.yandex.div.compose.views.container.DivContainerView
import com.yandex.div.compose.views.gallery.DivGalleryView
import com.yandex.div.compose.views.grid.DivGridView
import com.yandex.div.compose.views.image.DivGifImageView
import com.yandex.div.compose.views.image.DivImageView
import com.yandex.div.compose.views.indicator.DivIndicatorView
import com.yandex.div.compose.views.input.DivInputView
import com.yandex.div.compose.views.modifiers.apply
import com.yandex.div.compose.views.modifiers.applyPaddings
import com.yandex.div.compose.views.pager.DivPagerView
import com.yandex.div.compose.views.slider.DivSliderView
import com.yandex.div.compose.views.state.DivStateView
import com.yandex.div.compose.views.tabs.DivTabsView
import com.yandex.div.compose.views.text.DivTextView
import com.yandex.div.compose.views.video.DivVideoView
import com.yandex.div2.Div
import com.yandex.div2.DivBase
import com.yandex.div2.DivExtension
import com.yandex.div2.DivVisibility

@Composable
internal fun DivBlockView(
    data: Div,
    modifier: Modifier = Modifier,
    applyMargins: Boolean = true,
) {
    val divBase = data.value()
    WithLocalComponent(divBase) {
        checkUnsupportedFeatures(divBase)
        if (divBase.visibility.observedValue() == DivVisibility.GONE) {
            return@WithLocalComponent
        }
        val actions = data.observedActions()
        WithActionMenu(actions) {
            BaseViewWithExtensions(
                data = data,
                extensions = divBase.extensions.orEmpty().filter { it.isEnabled.observedValue() },
                modifier = modifier.apply(
                    data,
                    actions = actions,
                    applyMargins = applyMargins
                )
            )
        }
    }
}

@Composable
private fun BaseViewWithExtensions(
    data: Div,
    extensions: List<DivExtension>,
    modifier: Modifier
) {
    val extension = extensions.firstOrNull()
    if (extension == null) {
        BaseView(data = data, modifier = modifier)
        return
    }

    val id = extension.id
    val extensionHandler = divContext.component.extensionHandlers[id]
    if (extensionHandler == null) {
        reportError("No handler for extension: $id")
        BaseViewWithExtensions(
            data = data,
            extensions = extensions.drop(1),
            modifier = modifier
        )
        return
    }

    extensionHandler.Content(
        modifier = modifier,
        environment = DivExtensionEnvironment(
            data = data,
            extension = extension,
            expressionResolver = expressionResolver,
            reporter = LocalComponent.current.reporter
        ),
        content = { modifier ->
            BaseViewWithExtensions(
                data = data,
                extensions = extensions.drop(1),
                modifier = modifier
            )
        }
    )
}

@Composable
private fun BaseView(
    data: Div,
    modifier: Modifier
) {
    when (data) {
        is Div.Container -> DivContainerView(modifier, data.value)
        is Div.Custom -> DivCustomView(modifier.applyPaddings(data), data.value)
        is Div.Gallery -> DivGalleryView(modifier, data.value)
        is Div.GifImage -> DivGifImageView(modifier.applyPaddings(data), data.value)
        is Div.Grid -> DivGridView(modifier.applyPaddings(data), data.value)
        is Div.Image -> DivImageView(modifier.applyPaddings(data), data.value)
        is Div.Indicator -> DivIndicatorView(modifier.applyPaddings(data), data.value)
        is Div.Input -> DivInputView(modifier.applyPaddings(data), data.value)
        is Div.Pager -> DivPagerView(modifier, data.value)
        is Div.Select -> DivSelectView(modifier.applyPaddings(data), data.value)
        is Div.Separator -> DivSeparatorView(modifier.applyPaddings(data), data.value)
        is Div.Slider -> DivSliderView(modifier.applyPaddings(data), data.value)
        is Div.State -> DivStateView(modifier, data.value)
        is Div.Switch -> DivSwitchView(modifier.applyPaddings(data), data.value)
        is Div.Tabs -> DivTabsView(modifier, data.value)
        is Div.Text -> DivTextView(modifier.applyPaddings(data), data.value)
        is Div.Video -> DivVideoView(modifier.applyPaddings(data), data.value)
    }
}

@SuppressLint("ComposableNaming")
@Composable
private fun checkUnsupportedFeatures(data: DivBase) {
    if (!data.animators.isNullOrEmpty()) {
        reportError("div-base.animators not supported")
    }

    if (data.layoutProvider != null) {
        reportError("div-base.layout_provider not supported")
    }

    if (data.transitionChange != null ||
        data.transitionIn != null ||
        data.transitionOut != null
    ) {
        reportError("div-base.transitions not supported")
    }
}
