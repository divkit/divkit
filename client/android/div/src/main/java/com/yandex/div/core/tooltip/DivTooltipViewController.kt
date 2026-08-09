package com.yandex.div.core.tooltip

import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
import android.widget.PopupWindow
import androidx.activity.OnBackPressedCallback
import androidx.activity.findViewTreeOnBackPressedDispatcherOwner
import androidx.annotation.VisibleForTesting
import androidx.core.view.children
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.util.AccessibilityStateProvider
import com.yandex.div.core.util.SafePopupWindow
import com.yandex.div.core.util.toLayoutParamsSize
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.toPx
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.internal.Assert
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivTooltip
import com.yandex.div2.DivTooltip.Position
import com.yandex.div2.DivTooltipMode
import javax.inject.Inject

internal typealias CreatePopupCall = (contentView: View, width: Int, height: Int) -> SafePopupWindow

private const val CANT_FIND_ON_BACKPRESS_DISPATCHER =
    "Can't find onBackPressedDispatcher to set on back press listener on tooltip."

@DivScope
internal class DivTooltipViewController @VisibleForTesting constructor(
    private val errorCollectors: ErrorCollectors,
    private val divTooltipViewBuilder: DivTooltipViewBuilder,
    private val accessibilityStateProvider: AccessibilityStateProvider,
    private val createPopup: CreatePopupCall,
) {

    @Inject
    constructor(
        divTooltipViewBuilder: DivTooltipViewBuilder,
        errorCollectors: ErrorCollectors,
        accessibilityStateProvider: AccessibilityStateProvider,
    ) : this(
        errorCollectors,
        divTooltipViewBuilder,
        accessibilityStateProvider,
        { c: View, w: Int, h: Int -> DivTooltipWindow(c, w, h) },
    )

    fun createPopupWindow(
        data: TooltipData,
        onTouchOutside: () -> Unit,
        onDismiss: () -> Unit,
    ) {
        val divTooltip = data.divTooltip
        val hasSubstrate = data.substrateBlock != null
        val bringToTopView = divTooltip.bringToTopId?.let { data.divView.findBringToTopView(it) }

        val displayMetrics = data.anchor.resources.displayMetrics
        val width = if (hasSubstrate) {
            ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            with (data.tooltipBlock) {
                div.value().width.toLayoutParamsSize(displayMetrics, expressionResolver)
            }
        }
        val height = if (hasSubstrate) {
            ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            with (data.tooltipBlock) {
                div.value().height.toLayoutParamsSize(displayMetrics, expressionResolver)
            }
        }

        val tooltipContainer = divTooltipViewBuilder.buildTooltipView(data, bringToTopView, width, height)
        val isModal = divTooltip.mode is DivTooltipMode.Modal
        val popup = createPopup(tooltipContainer, width, height)
        val touchTranslationCoordinator = TouchTranslationCoordinator(TouchTranslator(data.anchor), popup)
        val isOutsideTouchable = divTooltip.closeByTapOutside.evaluate(data.anchorResolver)
        val touchListener = PopupWindowTouchListener(
            tooltipContainer,
            isModal,
            isOutsideTouchable,
            divTooltip.tapOutsideActions,
            data.anchorResolver,
            data.divView,
            touchTranslationCoordinator,
            divTooltip.substrateDiv?.hasAction() == true,
            onTouchOutside,
        )

        popup.configure(isOutsideTouchable, isModal, hasSubstrate, touchListener) {
            data.stopAnchorTracking()
            tooltipContainer.sendAccessibilityEventUnchecked()
            data.onBackPressedCallback?.isEnabled = false
            onDismiss()
        }

        data.popupWindow = popup
        data.onBackPressedCallback = createOnBackPressCallback(data, onTouchOutside)

        if (isModal) return

        tooltipContainer.dismissAction = { touchTranslationCoordinator.onTouchDownDiscardedAtRoot(event = it) }
    }

    private fun SafePopupWindow.configure(
        outsideTouchable: Boolean,
        isModal: Boolean,
        hasSubstrate: Boolean,
        touchListener: PopupWindowTouchListener,
        dismissListener: PopupWindow.OnDismissListener,
    ) {
        isTouchable = true
        isOutsideTouchable = outsideTouchable
        isFocusable = isModal
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isTouchModal = isModal
        }

        setTouchInterceptor(touchListener)
        setOnDismissListener(dismissListener)

        if (hasSubstrate) {
            isAttachedInDecor = true
            isClippingEnabled = false
        }
    }

    fun onPopupShown(data: TooltipData) {
        data.tooltipContainer?.tooltipView?.sendAccessibilityEventUnchecked()
        locatePopupWindow(data)
    }

    private fun locatePopupWindow(
        data: TooltipData,
        logSizeWarnings: Boolean = true,
    ) {
        val divView = data.divView
        val popupWindow = data.popupWindow ?: return
        val tooltipContainer = popupWindow.contentView as? DivTooltipContainer ?: return
        val tooltipView = tooltipContainer.tooltipView ?: return

        val windowFrame = divView.getWindowFrame()
        val location = calcPopupLocation(tooltipView, data.anchor, data.divTooltip, data.anchorResolver)
        val tooltipWidth = minOf(tooltipView.width, windowFrame.width())
        val tooltipHeight = minOf(tooltipView.height, windowFrame.height())

        if (logSizeWarnings) {
            if (tooltipWidth < tooltipView.width) {
                errorCollectors.getOrCreate(divView.dataTag, divView.divData)
                    .logWarning(Throwable("Tooltip width > screen size, width was changed"))
            }
            if (tooltipHeight < tooltipView.height) {
                errorCollectors.getOrCreate(divView.dataTag, divView.divData)
                    .logWarning(Throwable("Tooltip height > screen size, height was changed"))
            }
        }

        if (data.divTooltip.substrateDiv == null) {
            popupWindow.update(location.x, location.y, tooltipWidth, tooltipHeight)
            return
        }

        popupWindow.update(0, 0, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        tooltipContainer.setTooltipPosition(
            x = location.x,
            y = location.y,
            width = tooltipWidth,
            height = tooltipHeight,
        )

        tooltipContainer.locateBringToTopView(data)
    }

    private fun DivTooltipContainer.locateBringToTopView(data: TooltipData) {
        val bringToTopId = data.divTooltip.bringToTopId ?: return
        val bringToTopView = data.divView.findBringToTopView(bringToTopId) ?: return
        val locationArray = IntArray(2)
        bringToTopView.getLocationOnScreen(locationArray)
        setBringToTopPosition(locationArray[0], locationArray[1])
    }

    private fun View.sendAccessibilityEventUnchecked() {
        if (!accessibilityStateProvider.isAccessibilityEnabled(context)) return

        val accessibilityEvent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            AccessibilityEvent(TYPE_WINDOW_STATE_CHANGED)
        } else {
            @Suppress("DEPRECATION")
            AccessibilityEvent.obtain(TYPE_WINDOW_STATE_CHANGED)
        }
        sendAccessibilityEventUnchecked(accessibilityEvent)
    }

    fun createOnBackPressCallback(data: TooltipData, onBackPressed: () -> Unit): OnBackPressedCallback? {
        val divView = data.divView
        if (!accessibilityStateProvider.isAccessibilityEnabled(divView.getContext())) return null

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = onBackPressed()
        }

        divView.findViewTreeOnBackPressedDispatcherOwner()?.onBackPressedDispatcher?.addCallback(callback) ?: run {
            divView.logError(AssertionError(CANT_FIND_ON_BACKPRESS_DISPATCHER))
            Assert.fail(CANT_FIND_ON_BACKPRESS_DISPATCHER)
        }
        return callback
    }

    fun startAnchorPositionTracking(tooltip: TooltipData, divView: Div2View, handler: Handler) {
        if (tooltip.divView != divView) return
        val popup = tooltip.popupWindow ?: return
        if (!popup.isShowing) return

        val tooltipContainer = popup.contentView as? DivTooltipContainer ?: return
        tooltipContainer.tooltipView ?: return

        tooltip.stopAnchorTracking()
        tooltip.anchorTrackingDisposable = TooltipAnchorTracker(tooltip, handler) {
            locatePopupWindow(tooltip, logSizeWarnings = false)
        }
    }

    companion object {

        val windowFrame = Rect()

        private fun View.findBringToTopView(bringToTopId: String): View? {
            if (tag == bringToTopId) return this
            if (this !is ViewGroup) return null
            children.forEach { child ->
                child.findBringToTopView(bringToTopId)?.let { return it }
            }
            return null
        }

        @VisibleForTesting
        fun calcPopupLocation(
            popupView: View,
            anchor: View,
            divTooltip: DivTooltip,
            resolver: ExpressionResolver,
        ): Point {
            val locationArray = IntArray(2)
            anchor.getLocationInWindow(locationArray)
            val location = Point(locationArray[0], locationArray[1])

            val position = divTooltip.position.evaluate(resolver)
            location.x += when (position) {
                Position.LEFT, Position.TOP_LEFT, Position.BOTTOM_LEFT -> -popupView.width
                Position.TOP_RIGHT, Position.RIGHT, Position.BOTTOM_RIGHT -> anchor.width
                Position.TOP, Position.BOTTOM, Position.CENTER -> (anchor.width - popupView.width) / 2
            }

            location.y += when (position) {
                Position.TOP_LEFT, Position.TOP, Position.TOP_RIGHT -> -popupView.height
                Position.BOTTOM_LEFT, Position.BOTTOM, Position.BOTTOM_RIGHT -> anchor.height
                Position.LEFT, Position.RIGHT, Position.CENTER -> (anchor.height - popupView.height) / 2
            }

            val displayMetrics = anchor.resources.displayMetrics

            location.x += divTooltip.offset?.x?.toPx(displayMetrics, resolver) ?: 0
            location.y += divTooltip.offset?.y?.toPx(displayMetrics, resolver) ?: 0

            return location
        }

        private fun Div2View.getWindowFrame(): Rect {
            getWindowVisibleDisplayFrame(windowFrame)
            return windowFrame
        }

        private fun Div.hasAction(): Boolean {
            val divBase = value()
            if (!divBase.selectedActions.isNullOrEmpty()) return true
            return when (this) {
                is Div.Container -> value.action != null
                    || !value.actions.isNullOrEmpty()
                    || !value.doubletapActions.isNullOrEmpty()
                    || !value.longtapActions.isNullOrEmpty()
                    || value.items?.any { it.hasAction() } == true

                is Div.Custom -> value.items?.any { it.hasAction() } == true
                is Div.Gallery -> true
                is Div.GifImage -> value.action != null
                    || !value.actions.isNullOrEmpty()
                    || !value.doubletapActions.isNullOrEmpty()
                    || !value.longtapActions.isNullOrEmpty()

                is Div.Grid -> value.action != null
                    || !value.actions.isNullOrEmpty()
                    || !value.doubletapActions.isNullOrEmpty()
                    || !value.longtapActions.isNullOrEmpty()
                    || value.items?.any { it.hasAction() } == true

                is Div.Image -> value.action != null
                    || !value.actions.isNullOrEmpty()
                    || !value.doubletapActions.isNullOrEmpty()
                    || !value.longtapActions.isNullOrEmpty()

                is Div.Indicator -> false
                is Div.Input -> true
                is Div.Pager -> true
                is Div.Select -> true
                is Div.Separator -> value.action != null
                    || !value.actions.isNullOrEmpty()
                    || !value.doubletapActions.isNullOrEmpty()
                    || !value.longtapActions.isNullOrEmpty()

                is Div.Slider -> true
                is Div.State -> value.action != null
                    || !value.actions.isNullOrEmpty()
                    || !value.doubletapActions.isNullOrEmpty()
                    || !value.longtapActions.isNullOrEmpty()
                    || value.states.any { state -> state.div?.hasAction() == true }

                is Div.Switch -> true
                is Div.Tabs -> true
                is Div.Text -> value.action != null
                    || !value.actions.isNullOrEmpty()
                    || !value.doubletapActions.isNullOrEmpty()
                    || !value.longtapActions.isNullOrEmpty()

                is Div.Video -> false
            }
        }
    }
}
