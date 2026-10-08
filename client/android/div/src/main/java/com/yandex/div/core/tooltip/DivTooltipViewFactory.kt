package com.yandex.div.core.tooltip

import android.graphics.Point
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.findViewTreeOnBackPressedDispatcherOwner
import androidx.core.view.children
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.util.AccessibilityStateProvider
import com.yandex.div.core.util.SafePopupWindow
import com.yandex.div.core.util.toLayoutParamsSize
import com.yandex.div.core.view2.DivVisibilityActionTracker
import com.yandex.div.core.view2.animations.DivAnimationsEnabledController
import com.yandex.div.internal.Assert
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivTooltipMode
import javax.inject.Inject

private const val CANT_FIND_ON_BACKPRESS_DISPATCHER =
    "Can't find onBackPressedDispatcher to set on back press listener on tooltip."

@DivScope
internal class DivTooltipViewFactory @Inject constructor(
    private val viewBuilder: DivTooltipViewBuilder,
    private val accessibilityStateProvider: AccessibilityStateProvider,
    private val divVisibilityActionTracker: DivVisibilityActionTracker,
    private val animationsEnabledController: DivAnimationsEnabledController,
) {

    fun create(
        data: TooltipData,
        onTouchOutside: () -> Unit,
        onDismissed: (DivTooltipView) -> Unit,
    ): DivTooltipView {
        val bringToTopView = findBringToTopView(data)
        val container = viewBuilder.buildTooltipView(
            tooltipData = data,
            bringToTopView = bringToTopView,
        )
        val initialPopupSize = resolveInitialPopupSize(data)
        val popupWindow = DivTooltipWindow(
            container,
            initialPopupSize.x,
            initialPopupSize.y,
        )

        container.layoutParams = ViewGroup.LayoutParams(
            initialPopupSize.x,
            initialPopupSize.y,
        )
        configurePopupInput(
            data = data,
            container = container,
            popupWindow = popupWindow,
            onTouchOutside = onTouchOutside,
        )

        return DivTooltipView(
            data = data,
            container = container,
            popupWindow = popupWindow,
            onBackPressedCallback = createOnBackPressCallback(data, onTouchOutside),
            accessibilityStateProvider = accessibilityStateProvider,
            divVisibilityActionTracker = divVisibilityActionTracker,
            animationsEnabledController = animationsEnabledController,
            onDismissed = onDismissed,
        )
    }

    private fun findBringToTopView(data: TooltipData): View? {
        val bringToTopId = data.divTooltip.bringToTopId ?: return null
        return data.divView.findBringToTopView(bringToTopId)
    }

    private fun configurePopupInput(
        data: TooltipData,
        container: DivTooltipContainer,
        popupWindow: SafePopupWindow,
        onTouchOutside: () -> Unit,
    ) {
        val isModal = data.divTooltip.mode is DivTooltipMode.Modal
        val touchTranslationCoordinator = TouchTranslationCoordinator(
            TouchTranslator(data.anchor),
            popupWindow,
        )
        val isOutsideTouchable = data.divTooltip.closeByTapOutside.evaluate(data.anchorResolver)
        val touchListener = PopupWindowTouchListener(
            tooltipContainer = container,
            isModal = isModal,
            shouldDismissByOutsideTouch = isOutsideTouchable,
            tapOutsideActions = data.divTooltip.tapOutsideActions,
            expressionResolver = data.anchorResolver,
            divView = data.divView,
            touchTranslationCoordinator = touchTranslationCoordinator,
            handleSubstrateClick = data.divTooltip.substrateDiv?.hasAction() == true,
            onTouchOutside = onTouchOutside,
        )

        popupWindow.isTouchable = true
        popupWindow.isOutsideTouchable = isOutsideTouchable
        popupWindow.isFocusable = isModal
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupWindow.isTouchModal = isModal
        }

        popupWindow.setTouchInterceptor(touchListener)
        if (data.substrateBlock != null) {
            popupWindow.isAttachedInDecor = true
            popupWindow.isClippingEnabled = false
        }

        if (!isModal) {
            container.dismissAction = { event ->
                touchTranslationCoordinator.onTouchDownDiscardedAtRoot(event)
            }
        }
    }

    private fun createOnBackPressCallback(
        data: TooltipData,
        onBackPressed: () -> Unit,
    ): OnBackPressedCallback? {
        val divView = data.divView
        if (!accessibilityStateProvider.isAccessibilityEnabled(divView.getContext())) {
            return null
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }

        val dispatcherOwner = divView.findViewTreeOnBackPressedDispatcherOwner()
        if (dispatcherOwner == null) {
            divView.logError(AssertionError(CANT_FIND_ON_BACKPRESS_DISPATCHER))
            Assert.fail(CANT_FIND_ON_BACKPRESS_DISPATCHER)
        } else {
            dispatcherOwner.onBackPressedDispatcher.addCallback(callback)
        }

        return callback
    }
}

internal fun View.findBringToTopView(bringToTopId: String): View? {
    if (tag == bringToTopId) {
        return this
    }
    return (this as? ViewGroup)
        ?.children
        ?.firstNotNullOfOrNull { child -> child.findBringToTopView(bringToTopId) }
}

private fun resolveInitialPopupSize(data: TooltipData): Point {
    if (data.substrateBlock != null) {
        return Point(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
    }

    return resolveTooltipLayoutSize(data)
}

internal fun resolveTooltipLayoutSize(data: TooltipData): Point {
    val displayMetrics = data.anchor.resources.displayMetrics
    val tooltipBlock = data.tooltipBlock
    val tooltipDiv = tooltipBlock.div.value()

    return Point(
        tooltipDiv.width.toLayoutParamsSize(displayMetrics, tooltipBlock.expressionResolver),
        tooltipDiv.height.toLayoutParamsSize(displayMetrics, tooltipBlock.expressionResolver),
    )
}

private fun Div.hasAction(): Boolean {
    val divBase = value()
    if (!divBase.selectedActions.isNullOrEmpty()) {
        return true
    }

    return when (this) {
        is Div.Container -> value.run { hasActions(action, actions, doubletapActions, longtapActions) } ||
            value.items?.any { it.hasAction() } == true

        is Div.Custom -> value.items?.any { it.hasAction() } == true
        is Div.Gallery -> true
        is Div.GifImage -> value.run { hasActions(action, actions, doubletapActions, longtapActions) }
        is Div.Grid -> value.run { hasActions(action, actions, doubletapActions, longtapActions) } ||
            value.items?.any { it.hasAction() } == true

        is Div.Image -> value.run { hasActions(action, actions, doubletapActions, longtapActions) }
        is Div.Indicator -> false
        is Div.Input -> true
        is Div.Pager -> true
        is Div.Select -> true
        is Div.Separator -> value.run { hasActions(action, actions, doubletapActions, longtapActions) }
        is Div.Slider -> true
        is Div.State -> value.run { hasActions(action, actions, doubletapActions, longtapActions) } ||
            value.states.any { state -> state.div?.hasAction() == true }

        is Div.Switch -> true
        is Div.Tabs -> true
        is Div.Text -> value.run { hasActions(action, actions, doubletapActions, longtapActions) }
        is Div.Video -> false
    }
}

private fun hasActions(
    action: DivAction?,
    actions: List<DivAction>?,
    doubletapActions: List<DivAction>?,
    longtapActions: List<DivAction>?,
): Boolean {
    return action != null ||
        !actions.isNullOrEmpty() ||
        !doubletapActions.isNullOrEmpty() ||
        !longtapActions.isNullOrEmpty()
}
