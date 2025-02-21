package com.yandex.div.core.tooltip

import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
import android.widget.PopupWindow
import androidx.activity.OnBackPressedCallback
import androidx.activity.findViewTreeOnBackPressedDispatcherOwner
import androidx.annotation.VisibleForTesting
import androidx.core.os.postDelayed
import androidx.core.view.children
import com.yandex.div.R
import com.yandex.div.core.DivPreloader
import com.yandex.div.core.DivTooltipRestrictor
import com.yandex.div.core.actions.logError
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.util.AccessibilityStateProvider
import com.yandex.div.core.util.SafePopupWindow
import com.yandex.div.core.util.doOnActualLayout
import com.yandex.div.core.util.isActuallyLaidOut
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivVisibilityActionTracker
import com.yandex.div.core.view2.divs.sendAccessibilityEventUnchecked
import com.yandex.div.core.view2.divs.toLayoutParamsSize
import com.yandex.div.core.view2.divs.toPx
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.internal.Assert
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivTooltip
import com.yandex.div2.DivTooltipMode
import javax.inject.Inject


internal typealias CreatePopupCall = (contentView: View, width: Int, height: Int) -> SafePopupWindow

private const val CANT_FIND_ON_BACKPRESS_DISPATCHER =
    "Can't find onBackPressedDispatcher to set on back press listener on tooltip."
@Mockable
@DivScope
internal class DivTooltipController @VisibleForTesting constructor(
        private val tooltipRestrictor: DivTooltipRestrictor,
        private val divVisibilityActionTracker: DivVisibilityActionTracker,
        private val divPreloader: DivPreloader,
        private val errorCollectors: ErrorCollectors,
        private val divTooltipViewBuilder: DivTooltipViewBuilder,
        private val accessibilityStateProvider: AccessibilityStateProvider,
        private val createPopup: CreatePopupCall
) {
    private val tooltips = mutableMapOf<String, TooltipData>()
    private val mainThreadHandler = Handler(Looper.getMainLooper())

    @Inject
    constructor(
            tooltipRestrictor: DivTooltipRestrictor,
            divVisibilityActionTracker: DivVisibilityActionTracker,
            divPreloader: DivPreloader,
            divTooltipViewBuilder: DivTooltipViewBuilder,
            accessibilityStateProvider: AccessibilityStateProvider,
            errorCollectors: ErrorCollectors
    ) : this(
        tooltipRestrictor,
        divVisibilityActionTracker,
        divPreloader,
        errorCollectors,
        divTooltipViewBuilder,
        accessibilityStateProvider,
        { c: View, w: Int, h: Int -> DivTooltipWindow(c, w, h) })

    fun showTooltip(tooltipId: String, context: BindingContext, multiple: Boolean = false) {
        findChildWithTooltip(tooltipId, context.divView)?.let { (divTooltip, anchor) ->
            showTooltip(divTooltip, anchor, context, multiple)
        } ?: run {
            context.divView.logError(IllegalStateException(
                "Unable to find view for tooltip '$tooltipId'"))
        }
    }

    private fun showTooltip(divTooltip: DivTooltip, anchor: View, context: BindingContext, multiple: Boolean) {
        if (tooltips.contains(divTooltip.id)) {
            return
        }
        anchor.doOnActualLayout {
            tryShowTooltip(anchor, divTooltip, context, multiple)
        }
        if (!anchor.isActuallyLaidOut && !anchor.isLayoutRequested) {
            anchor.requestLayout()
        }
    }

    fun hideTooltip(id: String, div2View: Div2View) {
        tooltips[id]?.popupWindow?.dismiss()
    }

    fun cancelTooltips(context: BindingContext) = cancelTooltips(context, context.divView, context.divView)

    private fun cancelTooltips(context: BindingContext, view: View, div2View: Div2View) {
        @Suppress("UNCHECKED_CAST")
        (view.getTag(R.id.div_tooltips_tag) as? List<DivTooltip>)?.let { tooltipList ->
            tooltipList.forEach { tooltip ->
                val forRemove = mutableListOf<String>()
                tooltips[tooltip.id]?.apply {
                    dismissed = true
                    if (popupWindow.isShowing) {
                        popupWindow.clearAnimation()
                        popupWindow.dismiss()
                    } else {
                        forRemove.add(tooltip.id)
                        stopVisibilityTracking(context, tooltip.div)
                    }
                    ticket?.cancel()
                }
                forRemove.forEach { tooltips.remove(it) }
            }
        }
        if (view is ViewGroup) {
            view.children.forEach { child ->
                cancelTooltips(context, child, div2View)
            }
        }
    }

    fun mapTooltip(view: View, tooltips: List<DivTooltip>?) {
        view.setTag(R.id.div_tooltips_tag, tooltips)
    }

    fun clear() {
        tooltips.forEach {
            it.value.popupWindow.dismiss()
            it.value.ticket?.cancel()
        }
        tooltips.clear()
        mainThreadHandler.removeCallbacksAndMessages(null)
    }

    fun findViewWithTag(id: String): View? {
        tooltips.entries.mapNotNull { it.value.popupWindow.contentView }.forEach {
            it.findViewWithTag<View>(id)?.let { foundView -> return foundView }
        }
        return null
    }

    private fun tryShowTooltip(
        anchor: View,
        divTooltip: DivTooltip,
        context: BindingContext,
        multiple: Boolean
    ) {
        val div2View = context.divView
        if (!tooltipRestrictor.canShowTooltip(div2View, anchor, divTooltip, multiple)) {
            return
        }
        val resolver = context.expressionResolver
        val div = divTooltip.div

        val displayMetrics = anchor.resources.displayMetrics
        val width = divTooltip.div.value().width.toLayoutParamsSize(displayMetrics, resolver)
        val height = divTooltip.div.value().height.toLayoutParamsSize(displayMetrics, resolver)

        val tooltipContainer = divTooltipViewBuilder.buildTooltipView(context, div, width, height)
        val tooltipView = tooltipContainer.tooltipView ?: return

        val popup = createPopup(
            tooltipContainer,
            width,
            height,
        ).apply {
            isTouchable = true
            isOutsideTouchable = divTooltip.shouldDismissByOutsideTouch(resolver)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                isFocusable = true
                isTouchModal = divTooltip.isModal()
            } else {
                isFocusable = divTooltip.isModal()
            }
            setTouchInterceptor(
                PopupWindowTouchListener(
                    this,
                    tooltipView,
                    divTooltip.isModal(),
                    divTooltip.shouldDismissByOutsideTouch(resolver)
                )
            )
            setupAnimation(divTooltip, resolver)
        }
        val onBackPressedCallback = createOnBackPressCallback(divTooltip, div2View)
        val tooltipData = TooltipData(popup, div, null, onBackPressedCallback)

        popup.setOnDismissListener {
            tooltips.remove(divTooltip.id)
            stopVisibilityTracking(context, divTooltip.div)
            divVisibilityActionTracker.getDivWithWaitingDisappearActions()[tooltipContainer]?.let {
                divVisibilityActionTracker.trackDetachedView(context, tooltipContainer, it)
            }
            tooltipRestrictor.tooltipShownCallback?.onDivTooltipDismissed(div2View, anchor, divTooltip)
            popup.removeBackPressedCallback(tooltipData, accessibilityStateProvider)
        }

        tooltips[divTooltip.id] = tooltipData
        val ticket = divPreloader.preload(div, resolver) { hasFailures ->
            if (!hasFailures && !tooltipData.dismissed && anchor.isAttachedToWindow
                    && tooltipRestrictor.canShowTooltip(div2View, anchor, divTooltip, multiple)) {
                tooltipContainer.doOnActualLayout {
                    val windowFrame = div2View.getWindowFrame()
                    val location = calcPopupLocation(tooltipView, anchor, divTooltip, resolver)
                    val tooltipWidth = minOf(tooltipView.width, windowFrame.width())
                    val tooltipHeight = minOf(tooltipView.height, windowFrame.height())

                    if (tooltipWidth < tooltipView.width) {
                        errorCollectors.getOrCreate(div2View.dataTag, div2View.divData)
                                .logWarning(Throwable("Tooltip width > screen size, width was changed"))
                    }
                    if (tooltipHeight < tooltipView.height) {
                        errorCollectors.getOrCreate(div2View.dataTag, div2View.divData)
                                .logWarning(Throwable("Tooltip height > screen size, height was changed"))
                    }

                    popup.update(location.x, location.y, tooltipWidth, tooltipHeight)
                    startVisibilityTracking(context, div, tooltipContainer)
                    tooltipRestrictor.tooltipShownCallback?.onDivTooltipShown(div2View, anchor, divTooltip)
                }

                popup.showAtLocation(anchor, Gravity.NO_GRAVITY, 0, 0)
                sendAccessibilityEventUnchecked(TYPE_WINDOW_STATE_CHANGED, tooltipView, accessibilityStateProvider)
                if (divTooltip.duration.evaluate(resolver) != 0L) {
                    mainThreadHandler.postDelayed(divTooltip.duration.evaluate(resolver)) {
                        hideTooltip(divTooltip.id, div2View)
                    }
                }
            }
        }
        tooltips[divTooltip.id]?.ticket = ticket
    }

    private fun createOnBackPressCallback(divTooltip: DivTooltip, divView: Div2View) =
        if (accessibilityStateProvider.isAccessibilityEnabled(divView.getContext())) {
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    hideTooltip(divTooltip.id, divView)
                }
            }.also {
                divView.findViewTreeOnBackPressedDispatcherOwner()?.onBackPressedDispatcher?.apply {
                    addCallback(it)
                } ?: run {
                    divView.logError(AssertionError(CANT_FIND_ON_BACKPRESS_DISPATCHER))
                    Assert.fail(CANT_FIND_ON_BACKPRESS_DISPATCHER)
                }
            }
        } else {
            null
        }

    private fun startVisibilityTracking(context: BindingContext, div: Div, tooltipView: View) {
        stopVisibilityTracking(context, div)
        divVisibilityActionTracker.trackVisibilityActionsOf(
            context.divView,
            context.expressionResolver,
            tooltipView,
            div
        )
    }

    private fun stopVisibilityTracking(context: BindingContext, div: Div) {
        divVisibilityActionTracker.trackVisibilityActionsOf(context.divView, context.expressionResolver, null, div)
    }
}

private class TooltipData(
    val popupWindow: SafePopupWindow,
    val div: Div,
    var ticket: DivPreloader.Ticket? = null,
    val onBackPressedCallback: OnBackPressedCallback?,
    var dismissed: Boolean = false,
)

private class PopupWindowTouchListener(
    private val popupWindow: PopupWindow,
    private val tooltipView: View,
    private val isModal: Boolean,
    private val shouldDismissByOutsideTouch: Boolean
) : View.OnTouchListener {

    private val hitRect = Rect()

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        tooltipView.getHitRect(hitRect)
        return when {
            hitRect.contains(event.x.toInt(), event.y.toInt()) -> false

            else -> {
                if (shouldDismissByOutsideTouch) popupWindow.dismiss()
                isModal
            }
        }
    }
}

private fun findChildWithTooltip(tooltipId: String, view: View): Pair<DivTooltip, View>? {
    @Suppress("UNCHECKED_CAST")
    (view.getTag(R.id.div_tooltips_tag) as? List<DivTooltip>)?.let { tooltips ->
        tooltips.forEach {
            if (it.id == tooltipId) {
                return it to view
            }
        }
    }
    if (view is ViewGroup) {
        view.children.forEach { child ->
            findChildWithTooltip(tooltipId, child)?.let {
                return it
            }
        }
    }
    return null
}

@VisibleForTesting
internal fun calcPopupLocation(
    popupView: View,
    anchor: View,
    divTooltip: DivTooltip,
    resolver: ExpressionResolver
): Point {
    val locationArray = IntArray(2)
    anchor.getLocationInWindow(locationArray)
    val location = Point(locationArray[0], locationArray[1])

    val position = divTooltip.position.evaluate(resolver)
    location.x += when (position) {
        DivTooltip.Position.LEFT, DivTooltip.Position.TOP_LEFT, DivTooltip.Position.BOTTOM_LEFT -> -popupView.width
        DivTooltip.Position.TOP_RIGHT, DivTooltip.Position.RIGHT, DivTooltip.Position.BOTTOM_RIGHT -> anchor.width
        DivTooltip.Position.TOP, DivTooltip.Position.BOTTOM, DivTooltip.Position.CENTER -> (anchor.width - popupView.width) / 2
    }

    location.y += when (position) {
        DivTooltip.Position.TOP_LEFT, DivTooltip.Position.TOP, DivTooltip.Position.TOP_RIGHT -> -popupView.height
        DivTooltip.Position.BOTTOM_LEFT, DivTooltip.Position.BOTTOM, DivTooltip.Position.BOTTOM_RIGHT -> anchor.height
        DivTooltip.Position.LEFT, DivTooltip.Position.RIGHT, DivTooltip.Position.CENTER -> (anchor.height - popupView.height) / 2
    }

    val displayMetrics = anchor.resources.displayMetrics

    location.x += divTooltip.offset?.x?.toPx(displayMetrics, resolver) ?: 0
    location.y += divTooltip.offset?.y?.toPx(displayMetrics, resolver) ?: 0

    return location
}

private fun PopupWindow.removeBackPressedCallback(
    data: TooltipData,
    accessibilityStateProvider: AccessibilityStateProvider
) {
    sendAccessibilityEventUnchecked(TYPE_WINDOW_STATE_CHANGED, contentView, accessibilityStateProvider)
    data.onBackPressedCallback?.isEnabled = false
}

private fun DivTooltip.isModal(): Boolean {
    return mode is DivTooltipMode.Modal
}

private fun DivTooltip.shouldDismissByOutsideTouch(resolver: ExpressionResolver): Boolean {
    return closeByTapOutside.evaluate(resolver)
}

private fun Div2View.getWindowFrame(): Rect {
    val windowFrame = Rect()
    getWindowVisibleDisplayFrame(windowFrame)
    return windowFrame
}
