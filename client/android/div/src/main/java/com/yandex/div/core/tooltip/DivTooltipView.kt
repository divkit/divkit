package com.yandex.div.core.tooltip

import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
import androidx.activity.OnBackPressedCallback
import com.yandex.div.core.actions.logWarning
import com.yandex.div.core.util.AccessibilityStateProvider
import com.yandex.div.core.util.SafePopupWindow
import com.yandex.div.core.util.doOnActualLayout
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivVisibilityActionTracker
import com.yandex.div.core.view2.animations.DivAnimationsEnabledController
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div.internal.widget.DivViewGroup

internal class DivTooltipView(
    private val data: TooltipData,
    private val container: DivTooltipContainer,
    private val popupWindow: SafePopupWindow,
    private val onBackPressedCallback: OnBackPressedCallback?,
    private val accessibilityStateProvider: AccessibilityStateProvider,
    private val divVisibilityActionTracker: DivVisibilityActionTracker,
    private val animationsEnabledController: DivAnimationsEnabledController,
    private val onDismissed: (DivTooltipView) -> Unit,
) {

    private var hasLoggedSizeWarnings = false

    init {
        popupWindow.setOnDismissListener {
            onBackPressedCallback?.remove()
            container.sendTooltipAccessibilityEvent(accessibilityStateProvider)
            onDismiss()
            onDismissed(this)
        }
    }

    val isShown: Boolean
        get() = popupWindow.isShowing

    fun show(onShown: () -> Unit) {
        container.doOnActualLayout {
            recomputePosition()
            container.tooltipView?.sendTooltipAccessibilityEvent(accessibilityStateProvider)
            stopVisibilityTracking()
            data.tooltipBlock.trackVisibility(container.tooltipView)
            data.substrateBlock?.trackVisibility(container.substrateView)
            onShown()
        }
        val tooltipView = container.tooltipView ?: return

        if (animationsEnabledController.isEnabled()) {
            container.substrateView?.let {
                animateEnter(data.divTooltip, data.anchorResolver, tooltipView, it)
            } ?: popupWindow.setupAnimation(data.divTooltip, data.anchorResolver)
        }

        popupWindow.showAtLocation(data.anchor, Gravity.NO_GRAVITY, 0, 0)
    }

    fun dismiss() {
        val wasShown = isShown
        hideTooltip()
        if (!wasShown) {
            handleDismissBeforeInitialShow()
        }
    }

    fun dismissImmediately() {
        val wasShown = isShown
        container.substrateView?.clearAnimation()
        container.tooltipView?.clearAnimation()
        popupWindow.clearAnimation()
        dismissPopup()
        if (!wasShown) {
            handleDismissBeforeInitialShow()
        }
    }

    fun recomputePosition() {
        if (!isShown) {
            return
        }

        val tooltipView = container.tooltipView ?: return
        val layout = calculateLayout(data, container, tooltipView)

        if (!hasLoggedSizeWarnings) {
            logSizeWarnings(
                divView = data.divView,
                tooltipView = tooltipView,
                tooltipSize = layout.tooltipSize,
            )
            hasLoggedSizeWarnings = true
        }

        applyLayout(data, container, popupWindow, layout)
    }

    fun findViewWithTag(id: String): View? {
        return popupWindow.contentView.findViewWithTag(id)
    }

    private fun hideTooltip() {
        val substrateView = container.substrateView
        val tooltipView = container.tooltipView
        if (substrateView == null || tooltipView == null) {
            if (!animationsEnabledController.isEnabled()) {
                popupWindow.clearAnimation()
            }
            popupWindow.dismiss()
            return
        }

        substrateView.clearAnimation()
        tooltipView.clearAnimation()
        if (!animationsEnabledController.isEnabled()) {
            dismissPopup()
            return
        }

        animateExit(data.divTooltip, data.anchorResolver, tooltipView, substrateView) {
            dismissPopup()
        }
    }

    private fun dismissPopup() {
        if (popupWindow.isShowing) {
            popupWindow.dismiss()
        }
    }

    private fun handleDismissBeforeInitialShow() {
        onBackPressedCallback?.remove()
        onDismiss()
    }

    private fun onDismiss() {
        stopVisibilityTracking()
        val tooltipView = container.tooltipView ?: return

        val div = divVisibilityActionTracker.getDivWithWaitingDisappearActions()[tooltipView] ?: return
        divVisibilityActionTracker.trackDetachedView(
            tooltipView,
            div,
            data.tooltipBlock.expressionResolver,
            data.divView,
        )

        val substrateView = container.substrateView
        val substrateDiv = substrateView?.let {
            divVisibilityActionTracker.getDivWithWaitingDisappearActions()[it]
        }
        val substrateResolver = data.substrateBlock?.expressionResolver
        if (substrateView != null && substrateDiv != null && substrateResolver != null) {
            divVisibilityActionTracker.trackDetachedView(
                substrateView,
                substrateDiv,
                substrateResolver,
                data.divView,
            )
        }
    }

    private fun stopVisibilityTracking() {
        data.tooltipBlock.trackVisibility(null)
        data.substrateBlock?.trackVisibility(null)
    }

    private fun DivBlock.trackVisibility(view: View?) {
        divVisibilityActionTracker.trackVisibilityActionsOf(
            scope = data.divView,
            resolver = expressionResolver,
            view = view,
            div = div,
        )
    }
}

private data class TooltipLayout(
    val tooltipPosition: Point,
    val tooltipSize: Point,
    val windowSize: Point,
)

private fun calculateLayout(
    data: TooltipData,
    container: DivTooltipContainer,
    tooltipView: View,
): TooltipLayout {
    val windowFrame = data.divView.getWindowFrame()
    val windowSize = data.divView.getWindowContentSize()
    tooltipView.requestLayout()
    val measuredTooltipSize = measureTooltip(container, tooltipView, windowFrame)
    val tooltipSize = resolveTooltipSize(measuredTooltipSize, windowFrame)

    return TooltipLayout(
        tooltipPosition = calculateTooltipPosition(data, tooltipSize),
        tooltipSize = tooltipSize,
        windowSize = windowSize,
    )
}

private fun applyLayout(
    data: TooltipData,
    container: DivTooltipContainer,
    popupWindow: SafePopupWindow,
    layout: TooltipLayout,
) {
    if (container.substrateView == null) {
        popupWindow.update(
            layout.tooltipPosition.x,
            layout.tooltipPosition.y,
            layout.tooltipSize.x,
            layout.tooltipSize.y,
        )
        return
    }

    popupWindow.update(
        0,
        0,
        layout.windowSize.x,
        layout.windowSize.y,
    )
    container.setTooltipPosition(
        layout.tooltipPosition.x,
        layout.tooltipPosition.y,
        layout.tooltipSize.x,
        layout.tooltipSize.y,
    )
    updateBringToTopPosition(data, container)
}

private fun updateBringToTopPosition(data: TooltipData, container: DivTooltipContainer) {
    val bringToTopId = data.divTooltip.bringToTopId ?: return
    val sourceView = data.divView.findBringToTopView(bringToTopId) ?: return
    val sourcePosition = sourceView.getPositionInWindow()

    container.setBringToTopPosition(
        sourcePosition.x,
        sourcePosition.y,
        sourceView.width,
        sourceView.height,
    )
}

private fun measureTooltip(
    container: DivTooltipContainer,
    tooltipView: View,
    windowFrame: Rect,
): Point {
    val layoutParams = (tooltipView.layoutParams as? DivLayoutParams)
        ?: DivLayoutParams(tooltipView.layoutParams)
    val horizontalSpace = container.paddingLeft + container.paddingRight +
        layoutParams.leftMargin + layoutParams.rightMargin
    val verticalSpace = container.paddingTop + container.paddingBottom +
        layoutParams.topMargin + layoutParams.bottomMargin
    val parentWidthSpec = View.MeasureSpec.makeMeasureSpec(
        windowFrame.width(),
        View.MeasureSpec.EXACTLY,
    )
    val parentHeightSpec = View.MeasureSpec.makeMeasureSpec(
        windowFrame.height(),
        View.MeasureSpec.EXACTLY,
    )
    val widthSpec = DivViewGroup.getChildMeasureSpec(
        parentWidthSpec,
        horizontalSpace,
        layoutParams.width,
        tooltipView.minimumWidth,
        layoutParams.maxWidth,
    )
    val heightSpec = DivViewGroup.getChildMeasureSpec(
        parentHeightSpec,
        verticalSpace,
        layoutParams.height,
        tooltipView.minimumHeight,
        layoutParams.maxHeight,
    )

    tooltipView.measure(widthSpec, heightSpec)
    return Point(tooltipView.measuredWidth, tooltipView.measuredHeight)
}

private fun resolveTooltipSize(
    measuredSize: Point,
    windowFrame: Rect,
): Point {
    return Point(
        minOf(measuredSize.x, windowFrame.width()),
        minOf(measuredSize.y, windowFrame.height()),
    )
}

private fun logSizeWarnings(
    divView: Div2View,
    tooltipView: View,
    tooltipSize: Point,
) {
    if (!divView.visualErrorsEnabled) {
        return
    }

    if (tooltipSize.x < tooltipView.width) {
        divView.logWarning(Throwable("Tooltip width > screen size, width was changed"))
    }
    if (tooltipSize.y < tooltipView.height) {
        divView.logWarning(Throwable("Tooltip height > screen size, height was changed"))
    }
}

private fun View.sendTooltipAccessibilityEvent(
    accessibilityStateProvider: AccessibilityStateProvider,
) {
    if (!accessibilityStateProvider.isAccessibilityEnabled(context)) {
        return
    }

    val accessibilityEvent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        AccessibilityEvent(TYPE_WINDOW_STATE_CHANGED)
    } else {
        @Suppress("DEPRECATION")
        AccessibilityEvent.obtain(TYPE_WINDOW_STATE_CHANGED)
    }
    sendAccessibilityEventUnchecked(accessibilityEvent)
}
