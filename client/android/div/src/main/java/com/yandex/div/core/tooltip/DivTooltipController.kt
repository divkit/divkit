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
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
import android.widget.PopupWindow
import androidx.activity.OnBackPressedCallback
import androidx.activity.findViewTreeOnBackPressedDispatcherOwner
import androidx.annotation.VisibleForTesting
import androidx.core.os.postDelayed
import androidx.core.view.children
import com.yandex.div.R
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.DivPreloader
import com.yandex.div.core.DivTooltipRestrictor
import com.yandex.div.core.actions.logActionError
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.util.AccessibilityStateProvider
import com.yandex.div.core.util.SafePopupWindow
import com.yandex.div.core.util.doOnActualLayout
import com.yandex.div.core.util.isActuallyLaidOut
import com.yandex.div.core.util.toLayoutParamsSize
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivVisibilityActionTracker
import com.yandex.div.core.view2.ViewLocator
import com.yandex.div.core.view2.divs.toPx
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.internal.Assert
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivActionShowTooltip
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
    private val tooltips = mutableListOf<TooltipData>()
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

    fun showTooltip(tooltipId: String, context: BindingContext, multiple: Boolean = false, scopeId: String? = null) {
        ViewLocator.findSingleViewWithTag(context.divView, tooltipId, scopeId) { tooltipId, _ ->
            findChildWithTooltip(tooltipId)?.let { Result.success(it) }
                ?: Result.failure(IllegalStateException("Unable to find view for tooltip '$tooltipId'"))
        }.onSuccess { (divTooltip, anchor) ->
            showTooltip(context, divTooltip, anchor, multiple, scopeId)
        }.onFailure {
            context.divView.logActionError(DivActionShowTooltip.TYPE, it)
        }
    }

    private fun showTooltip(
        context: BindingContext,
        divTooltip: DivTooltip,
        anchor: View,
        multiple: Boolean,
        scopeId: String?
    ) {
        val containsTooltip = tooltips.any { it.id == divTooltip.id && it.scopeId == scopeId }
        if (containsTooltip) {
            return
        }
        anchor.doOnActualLayout {
            tryShowTooltip(anchor, divTooltip, context, multiple, scopeId)
        }
        if (!anchor.isActuallyLaidOut && !anchor.isLayoutRequested) {
            anchor.requestLayout()
        }
    }

    fun hideTooltip(id: String, scopeId: String? = null) {
        val tooltipData = tooltips.find { it.id == id && it.scopeId == scopeId } ?: return
        val tooltipContainer = tooltipData.popupWindow.contentView as? DivTooltipContainer

        val substrateView = tooltipContainer?.substrateView
        val tooltipView = tooltipContainer?.tooltipView
        if (substrateView != null && tooltipView != null) {
            substrateView.clearAnimation()
            tooltipView.clearAnimation()
            animateExit(
                divTooltip = tooltipData.divTooltip,
                resolver = tooltipData.bindingContext.expressionResolver,
                tooltipView = tooltipView,
                substrateView = substrateView,
            ) {
                if (tooltipData.popupWindow.isShowing) {
                    tooltipData.popupWindow.dismiss()
                }
            }
        } else {
            tooltipData.popupWindow.dismiss()
        }
    }

    fun cancelTooltips(divView: Div2View) {
        tooltips.toList().forEach { tooltip ->
            if (tooltip.bindingContext.divView != divView) return@forEach
            dismissTooltip(tooltip)?.let {
                tooltips.remove(tooltip)
            }
        }
    }

    fun cancelAllTooltips(): Boolean {
        if (tooltips.isEmpty()) {
            return false
        }

        tooltips.toList().forEach { tooltip -> dismissTooltip(tooltip) }
        tooltips.clear()
        return true
    }

    private fun dismissTooltip(tooltip: TooltipData): String? {
        tooltip.apply {
            dismissed = true
            ticket?.cancel()
            return if (popupWindow.isShowing) {
                popupWindow.clearAnimation()
                popupWindow.dismiss()
                null
            } else {
                stopVisibilityTracking(tooltip.bindingContext, tooltip.divTooltip.div)
                tooltip.id
            }
        }
    }

    fun mapTooltip(view: View, tooltips: List<DivTooltip>?) {
        view.setTag(R.id.div_tooltips_tag, tooltips)
    }

    fun clear() {
        tooltips.toList().forEach {
            it.popupWindow.dismiss()
            it.ticket?.cancel()
        }
        tooltips.clear()
        mainThreadHandler.removeCallbacksAndMessages(null)
    }

    fun findViewWithTag(id: String, scopeId: String?): View? {
        tooltips.forEach { tooltip ->
            if (tooltip.scopeId != scopeId) return@forEach
            tooltip.popupWindow.contentView.findViewWithTag<View>(id)?.let { return it }
        }
        return null
    }

    private fun tryShowTooltip(
        anchor: View,
        divTooltip: DivTooltip,
        context: BindingContext,
        multiple: Boolean,
        scopeId: String?,
    ) {
        val div2View = context.divView
        if (!tooltipRestrictor.canShowTooltip(div2View, anchor, divTooltip, multiple, scopeId)) {
            return
        }
        val resolver = context.expressionResolver
        val div = divTooltip.div
        val hasSubstrate = divTooltip.substrateDiv != null

        val displayMetrics = anchor.resources.displayMetrics
        val width = if (hasSubstrate) {
            ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            divTooltip.div.value().width.toLayoutParamsSize(displayMetrics, resolver)
        }
        val height = if (hasSubstrate) {
            ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            divTooltip.div.value().height.toLayoutParamsSize(displayMetrics, resolver)
        }

        val bringToTopView = divTooltip.bringToTopId?.let { findBringToTopView(it, div2View) }

        val tooltipContainer = divTooltipViewBuilder.buildTooltipView(
            context = context,
            divTooltip = divTooltip,
            bringToTopView = bringToTopView,
            width = width,
            height = height,
        )
        val tooltipView = tooltipContainer.tooltipView ?: return
        val isModal: Boolean = divTooltip.isModal()
        val popup = createPopup(
            tooltipContainer,
            width,
            height,
        )
        val touchTranslationCoordinator = TouchTranslationCoordinator(
            touchTranslator = TouchTranslator(anchor),
            popup = popup,
        )
        var isSubstrateSystemBars = false
        popup.apply {
            isTouchable = true
            isOutsideTouchable = divTooltip.shouldDismissByOutsideTouch(resolver)
            isFocusable = isModal
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                isTouchModal = isModal
            }

            setTouchInterceptor(
                PopupWindowTouchListener(
                    tooltipContainer,
                    isModal,
                    isOutsideTouchable,
                    divTooltip.tapOutsideActions,
                    context,
                    touchTranslationCoordinator,
                    divTooltip.substrateDiv?.hasAction() == true
                ) { hideTooltip(divTooltip.id, scopeId) }
            )
            if (!hasSubstrate) {
                setupAnimation(divTooltip, resolver)
            }

            if (hasSubstrate && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                isAttachedInDecor = true
                isClippingEnabled = false
                isSubstrateSystemBars = true
            }
        }
        val onBackPressedCallback = createOnBackPressCallback(divTooltip, div2View, scopeId)
        val tooltipData = TooltipData(
            id = divTooltip.id,
            scopeId = scopeId,
            bindingContext = context,
            divTooltip = divTooltip,
            popupWindow = popup,
            ticket = null,
            onBackPressedCallback = onBackPressedCallback
        )

        if (!isModal) {
            tooltipContainer.dismissAction = {
                touchTranslationCoordinator.onTouchDownDiscardedAtRoot(event = it)
            }
        }
        popup.setOnDismissListener {
            tooltips.remove(tooltipData)
            stopVisibilityTracking(context, divTooltip.div)
            divVisibilityActionTracker.getDivWithWaitingDisappearActions()[tooltipContainer]?.let {
                divVisibilityActionTracker.trackDetachedView(context, tooltipContainer, it)
            }
            tooltipRestrictor.tooltipShownCallback?.onDivTooltipDismissed(div2View, anchor, divTooltip)
            popup.removeBackPressedCallback(tooltipData, accessibilityStateProvider)
        }

        val ticket = divPreloader.preload(div, resolver) { hasFailures ->
            if (!hasFailures && !tooltipData.dismissed && anchor.isAttachedToWindow
                    && tooltipRestrictor.canShowTooltip(div2View, anchor, divTooltip, multiple, scopeId)) {
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

                    if (hasSubstrate) {
                        val windowLocation = if (isSubstrateSystemBars) {
                            Point(0, 0)
                        } else {
                            Point(windowFrame.left, windowFrame.top)
                        }

                        popup.update(0, 0, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                        tooltipContainer.setTooltipPosition(
                            x = location.x - windowLocation.x,
                            y = location.y - windowLocation.y,
                            width = tooltipWidth,
                            height = tooltipHeight
                        )

                        bringToTopView?.let {
                            val locationArray = IntArray(2)
                            bringToTopView.getLocationOnScreen(locationArray)
                            val location = Point(locationArray[0], locationArray[1])

                            tooltipContainer.setBringToTopPosition(
                                x = location.x - windowLocation.x,
                                y = location.y - windowLocation.y,
                                width = it.width,
                                height = it.height,
                            )
                        }
                    } else {
                        popup.update(location.x, location.y, tooltipWidth, tooltipHeight)
                    }
                    startVisibilityTracking(context, div, tooltipContainer)
                    tooltipRestrictor.tooltipShownCallback?.onDivTooltipShown(div2View, anchor, divTooltip)
                }

                popup.showAtLocation(anchor, Gravity.NO_GRAVITY, 0, 0)

                tooltipContainer.substrateView?.let { substrateView ->
                    animateEnter(
                        divTooltip = divTooltip,
                        resolver = resolver,
                        tooltipView = tooltipView,
                        substrateView = substrateView,
                    )
                }
                
                sendAccessibilityEventUnchecked(TYPE_WINDOW_STATE_CHANGED, tooltipView, accessibilityStateProvider)
                if (divTooltip.duration.evaluate(resolver) != 0L) {
                    mainThreadHandler.postDelayed(divTooltip.duration.evaluate(resolver)) {
                        hideTooltip(divTooltip.id, scopeId)
                    }
                }
            } else {
                tooltips.remove(tooltipData)
            }
        }
        tooltipData.ticket = ticket
        tooltips.add(tooltipData)
    }

    private fun createOnBackPressCallback(divTooltip: DivTooltip, divView: Div2View, scopeId: String?) =
        if (accessibilityStateProvider.isAccessibilityEnabled(divView.getContext())) {
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    hideTooltip(divTooltip.id, scopeId)
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

    fun captureCurrentTooltips(): Collection<TooltipData> = tooltips
}

internal class TooltipData(
    val id: String,
    val scopeId: String?,
    val bindingContext: BindingContext,
    val divTooltip: DivTooltip,
    val popupWindow: SafePopupWindow,
    var ticket: DivPreloader.Ticket? = null,
    val onBackPressedCallback: OnBackPressedCallback?,
    var dismissed: Boolean = false,
)

private class PopupWindowTouchListener(
    private val tooltipContainer: DivTooltipContainer,
    private val isModal: Boolean,
    private val shouldDismissByOutsideTouch: Boolean,
    private val tapOutsideActions: List<DivAction>?,
    private val bindingContext: BindingContext,
    private val touchTranslationCoordinator: TouchTranslationCoordinator,
    private val handleSubstrateClick: Boolean,
    private val onTouchOutside: () -> Unit,
) : View.OnTouchListener {

    private val hitRect = Rect()

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        touchTranslationCoordinator.onTooltipMotionEvent(event)
        if (handleSubstrateClick) {
            tooltipContainer.substrateView?.getHitRect(hitRect)
        } else {
            tooltipContainer.tooltipView?.getHitRect(hitRect)
        }
        return when {
            hitRect.contains(event.x.toInt(), event.y.toInt()) -> false

            else -> {
                if (event.action == MotionEvent.ACTION_UP) {
                    tapOutsideActions?.let { actions ->
                        val resolver = bindingContext.expressionResolver
                        val divView = bindingContext.divView
                        actions.filter { it.isEnabled.evaluate(resolver) }.forEach { action ->
                            divView.div2Component.actionHandler.handleActionWithReason(
                                action,
                                divView,
                                resolver,
                                DivActionHandler.DivActionReason.CLICK
                            )
                        }
                    }

                    if (shouldDismissByOutsideTouch) {
                        onTouchOutside()
                    }
                }
                isModal
            }
        }
    }
}

private fun View.findChildWithTooltip(tooltipId: String): Pair<DivTooltip, View>? {
    @Suppress("UNCHECKED_CAST")
    val tooltips = getTag(R.id.div_tooltips_tag) as? List<DivTooltip>
    tooltips?.forEach {
        if (it.id == tooltipId) return it to this
    }

    if (this !is ViewGroup) return null

    children.forEach { child ->
        child.findChildWithTooltip(tooltipId)?.let { return it }
    }
    return null
}

private fun findBringToTopView(bringToTopId: String, view: View): View? {
    if (view.tag == bringToTopId) {
        return view
    }
    if (view is ViewGroup) {
        view.children.forEach { child ->
            findBringToTopView(bringToTopId, child)?.let {
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

private fun sendAccessibilityEventUnchecked(
    event: Int,
    view: View?,
    accessibilityStateProvider: AccessibilityStateProvider
) {
    view ?: return
    if (!accessibilityStateProvider.isAccessibilityEnabled(view.context)) return
    val accessibilityEvent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        AccessibilityEvent(event)
    } else {
        @Suppress("DEPRECATION")
        AccessibilityEvent.obtain(event)
    }
    view.sendAccessibilityEventUnchecked(accessibilityEvent)
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
