package com.yandex.div.compose.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.State
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.actions.WithAccessibilityMenuScope
import com.yandex.div.compose.actions.observedActions
import com.yandex.div.compose.animation.WithAnimatorTarget
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.di.WithLocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.observeInsets
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
import com.yandex.div.compose.views.transition.VisibilityChangeTransition
import com.yandex.div.compose.views.transition.WithStateChangeTransition
import com.yandex.div.compose.views.transition.rememberVisibilityChangeTransition
import com.yandex.div.compose.views.transition.withVisibilityTransition
import com.yandex.div.compose.views.video.DivVideoView
import com.yandex.div.compose.visibility.hasVisibilityTransition
import com.yandex.div2.Div
import com.yandex.div2.DivVisibility

@Composable
internal inline fun DivBlockView(
    data: Div,
    modifier: Modifier = Modifier,
    layoutParams: DivBlockLayoutParams = DivBlockLayoutParams.Default,
) {
    key(LocalComponent.current.compositionKeyStorage.get(data.value())) {
        val divBase = data.value()
        WithLocalComponent(divBase) {
            WithAnimatorTarget(divBase) {
                val requestedVisibility = divBase.visibility.observedValue()
                val transition = if (divBase.hasVisibilityTransition()) {
                    rememberVisibilityChangeTransition(
                        divBase,
                        requestedVisibility,
                        layoutParams.hasLayoutWrapper,
                    )
                } else {
                    null
                }
                val visibility = transition?.visibility ?: requestedVisibility
                if (visibility == DivVisibility.GONE) return@WithAnimatorTarget
                WithAccessibilityMenuScope(divBase, visibility) {
                    VisibleDivBlockView(
                        data = data,
                        modifier = modifier,
                        layoutParams = layoutParams,
                        visibility = visibility,
                        visibilityTransition = transition,
                    )
                }
            }
        }
    }
}

@Composable
internal fun VisibleDivBlockView(
    data: Div,
    modifier: Modifier,
    layoutParams: DivBlockLayoutParams,
    visibility: DivVisibility,
    visibilityTransition: VisibilityChangeTransition? = null,
) {
    val divBase = data.value()
    val focusState = rememberFocusState(divBase)
    // Content that is animating out is drawn, but the user can no longer interact with it.
    val isExiting = visibilityTransition?.isExiting == true
    val actions = if (isExiting) null else data.observedActions()
    val margins = if (layoutParams.applyMargins) divBase.margins?.observeInsets() else null
    val menuOffset = DpOffset(
        x = 0.dp,
        y = -(margins?.calculateBottomPadding() ?: 0.dp),
    )
    WithActionMenu(actions, modifier, menuOffset) { contentModifier ->
        WithStateChangeTransition(
            data = data,
            visibility = visibility,
            alphaOverride = visibilityTransition?.appearance?.appearanceAlpha,
        ) { stateChangeTransition ->
            val decoration = Modifier.apply(
                data,
                layoutParams = layoutParams,
                actions = actions,
                margins = margins,
                visibility = visibility,
                focusState = focusState,
                stateChangeTransition = stateChangeTransition.withVisibilityTransition(
                    transition = visibilityTransition,
                    blocksPointerInput = isExiting && data.hasInteractiveContent(),
                ),
            )
            val decorator = DivContentDecorator(data, decoration)
            if (data is Div.State) {
                DivStateView(contentModifier, data.value, decorator)
            } else {
                decorator.Content(contentModifier) { modifier ->
                    BaseView(data, modifier, focusState)
                }
            }
        }
    }
}

/** Whether the element reacts to input by itself or through its children, apart from its actions. */
private fun Div.hasInteractiveContent(): Boolean = when (this) {
    is Div.Container,
    is Div.Custom,
    is Div.Gallery,
    is Div.Grid,
    is Div.Input,
    is Div.Pager,
    is Div.Select,
    is Div.Slider,
    is Div.State,
    is Div.Switch,
    is Div.Tabs,
    is Div.Text,
    is Div.Video -> true
    is Div.GifImage,
    is Div.Image,
    is Div.Indicator,
    is Div.Separator -> false
}

@Composable
@NonRestartableComposable
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
        is Div.State -> DivStateView(modifier, data.value, DivContentDecorator(data))
        is Div.Switch -> DivSwitchView(modifier, data.value)
        is Div.Tabs -> DivTabsView(modifier, data.value)
        is Div.Text -> DivTextView(modifier.applyPaddings(data), data.value, focusState)
        is Div.Video -> DivVideoView(modifier.applyPaddings(data), data.value)
    }
}
