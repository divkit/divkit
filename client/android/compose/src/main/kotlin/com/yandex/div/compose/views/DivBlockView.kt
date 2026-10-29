package com.yandex.div.compose.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.actions.observedActions
import com.yandex.div.compose.animation.WithAnimatorTarget
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.context.divContext
import com.yandex.div.compose.context.expressionResolver
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.dagger.WithLocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.extensions.DivExtensionEnvironment
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.compose.utils.observeInsets
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
import com.yandex.div.compose.views.modifiers.rememberFocusState
import com.yandex.div.compose.views.pager.DivPagerView
import com.yandex.div.compose.views.slider.DivSliderView
import com.yandex.div.compose.views.state.DivStateView
import com.yandex.div.compose.views.tabs.DivTabsView
import com.yandex.div.compose.views.text.DivTextView
import com.yandex.div.compose.views.transition.WithStateChangeTransition
import com.yandex.div.compose.views.video.DivVideoView
import com.yandex.div2.Div
import com.yandex.div2.DivBase
import com.yandex.div2.DivExtension
import com.yandex.div2.DivVisibility

/**
 * [matchParentMaxWidth] and [matchParentMaxHeight] bound the size, including margins, that a
 * match_parent element fills when the parent measures it with unbounded constraints, e.g. a gallery
 * item along the scroll axis. The element resolves them against its own min_size and max_size.
 */
@Composable
internal inline fun DivBlockView(
    data: Div,
    modifier: Modifier = Modifier,
    applyMargins: Boolean = true,
    defaultHorizontalAlignment: Alignment.Horizontal = Alignment.Start,
    defaultVerticalAlignment: Alignment.Vertical = Alignment.Top,
    suppressMatchParentIntrinsics: Boolean = false,
    matchParentMaxWidth: Dp = Dp.Unspecified,
    matchParentMaxHeight: Dp = Dp.Unspecified,
    blockTapPropagation: Boolean = false,
) {
    key(LocalDivViewContext.current.compositionKeyStorage.get(data.value())) {
        val divBase = data.value()
        WithLocalComponent(divBase) {
            WithAnimatorTarget(divBase) {
                val visibility = divBase.visibility.observedValue()
                if (visibility == DivVisibility.GONE) return@WithAnimatorTarget
                VisibleDivBlockView(
                    data = data,
                    modifier = modifier,
                    applyMargins = applyMargins,
                    visibility = visibility,
                    defaultHorizontalAlignment = defaultHorizontalAlignment,
                    defaultVerticalAlignment = defaultVerticalAlignment,
                    suppressMatchParentIntrinsics = suppressMatchParentIntrinsics,
                    matchParentMaxWidth = matchParentMaxWidth,
                    matchParentMaxHeight = matchParentMaxHeight,
                    blockTapPropagation = blockTapPropagation,
                )
            }
        }
    }
}

@Composable
internal fun VisibleDivBlockView(
    data: Div,
    modifier: Modifier,
    applyMargins: Boolean,
    visibility: DivVisibility,
    fillMatchParentWidth: Boolean = true,
    fillMatchParentHeight: Boolean = true,
    defaultHorizontalAlignment: Alignment.Horizontal = Alignment.Start,
    defaultVerticalAlignment: Alignment.Vertical = Alignment.Top,
    suppressMatchParentIntrinsics: Boolean = false,
    matchParentMaxWidth: Dp = Dp.Unspecified,
    matchParentMaxHeight: Dp = Dp.Unspecified,
    blockTapPropagation: Boolean = false,
) {
    val divBase = data.value()
    val focusState = rememberFocusState(divBase)
    val actions = data.observedActions()
    val margins = if (applyMargins) divBase.margins?.observeInsets() else null
    val menuOffset = DpOffset(
        x = 0.dp,
        y = -(margins?.calculateBottomPadding() ?: 0.dp),
    )
    WithActionMenu(actions, modifier, menuOffset) { contentModifier ->
        WithStateChangeTransition(data, visibility) { stateChangeTransition ->
            WithExtensions(
                data = data,
                extensions = extensions(divBase),
                modifier = contentModifier.apply(
                    data,
                    actions = actions,
                    margins = margins,
                    visibility = visibility,
                    focusState = focusState,
                    stateChangeTransition = stateChangeTransition,
                    fillMatchParentWidth = fillMatchParentWidth,
                    fillMatchParentHeight = fillMatchParentHeight,
                    defaultHorizontalAlignment = defaultHorizontalAlignment,
                    defaultVerticalAlignment = defaultVerticalAlignment,
                    suppressMatchParentIntrinsics = suppressMatchParentIntrinsics,
                    matchParentMaxWidth = matchParentMaxWidth,
                    matchParentMaxHeight = matchParentMaxHeight,
                    blockTapPropagation = blockTapPropagation,
                )
            ) { baseModifier ->
                BaseView(data = data, modifier = baseModifier, focusState = focusState)
            }
        }
    }
}

@Composable
private fun extensions(div: DivBase): List<Pair<DivExtension, DivExtensionHandler?>> {
    val component = divContext.component
    val extensions = div.extensions
        ?.filter { it.isEnabled.observedValue() }
        ?.map { it to component.extensionHandlers[it.id] }
        .orEmpty()
    val wildcardExtensions = component.configuration.wildcardExtensions
    return if (wildcardExtensions.isEmpty()) extensions else wildcardExtensions + extensions
}

@Composable
private fun WithExtensions(
    data: Div,
    extensions: List<Pair<DivExtension, DivExtensionHandler?>>,
    modifier: Modifier,
    content: @Composable (Modifier) -> Unit,
) {
    val first = extensions.firstOrNull()
    if (first == null) {
        content(modifier)
        return
    }

    val (extension, extensionHandler) = first
    if (extensionHandler == null) {
        reportError("No handler for extension: ${extension.id}")
        WithExtensions(
            data = data,
            extensions = extensions.drop(1),
            modifier = modifier,
            content = content,
        )
        return
    }
    val storedValuesStorage = LocalDivViewContext.current.component.storedValuesStorage

    extensionHandler.Content(
        modifier = modifier,
        environment = DivExtensionEnvironment(
            data = data,
            extension = extension,
            expressionResolver = expressionResolver,
            reporter = LocalComponent.current.reporter,
            animationsEnabled = divContext.component.animationConfiguration.isEnabledAsState(),
            storedValuesStorage = storedValuesStorage,
        ),
        content = { contentModifier ->
            WithExtensions(
                data = data,
                extensions = extensions.drop(1),
                modifier = contentModifier,
                content = content,
            )
        }
    )
}

@Composable
private fun BaseView(
    data: Div,
    modifier: Modifier,
    focusState: State<Boolean>?,
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
        is Div.Select -> DivSelectView(modifier, data.value)
        is Div.Separator -> DivSeparatorView(modifier.applyPaddings(data), data.value)
        is Div.Slider -> DivSliderView(modifier.applyPaddings(data), data.value)
        is Div.State -> DivStateView(modifier, data.value)
        is Div.Switch -> DivSwitchView(modifier, data.value)
        is Div.Tabs -> DivTabsView(modifier, data.value)
        is Div.Text -> DivTextView(modifier.applyPaddings(data), data.value, focusState)
        is Div.Video -> DivVideoView(modifier.applyPaddings(data), data.value)
    }
}
