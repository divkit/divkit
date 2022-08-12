package com.yandex.div.core.tooltip

import android.annotation.SuppressLint
import android.graphics.Point
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.VisibleForTesting
import androidx.core.os.postDelayed
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.core.view.doOnLayout
import com.yandex.div.R
import com.yandex.div.core.DivTooltipRestrictor
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.Assert
import com.yandex.div.core.view2.Div2Builder
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivPreloader
import com.yandex.div.core.view2.DivVisibilityActionTracker
import com.yandex.div.core.view2.divs.toLayoutParamsSize
import com.yandex.div.core.view2.divs.toPx
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivTooltip
import javax.inject.Inject
import javax.inject.Provider


typealias CreatePopupCall = (contentView: View, width: Int, height: Int) -> PopupWindow

@Mockable
@DivScope
internal class DivTooltipController @VisibleForTesting constructor(
    private val div2Builder: Provider<Div2Builder>,
    private val tooltipRestrictor: DivTooltipRestrictor,
    private val divVisibilityActionTracker: DivVisibilityActionTracker,
    private val divPreloader: DivPreloader,
    private val createPopup: CreatePopupCall,
) {
    private val tooltips = mutableMapOf<String, TooltipData>()
    private val mainThreadHandler = Handler(Looper.getMainLooper())

    @Inject
    constructor(
        div2Builder: Provider<Div2Builder>,
        tooltipRestrictor: DivTooltipRestrictor,
        divVisibilityActionTracker: DivVisibilityActionTracker,
        divPreloader: DivPreloader,
    ) : this(
        div2Builder,
        tooltipRestrictor,
        divVisibilityActionTracker,
        divPreloader,
        { c: View, w: Int, h: Int -> DivTooltipWindow(c, w, h) })

    fun showTooltip(tooltipId: String, div2View: Div2View) {
        findChildWithTooltip(tooltipId, div2View)?.let { (divTooltip, anchor) ->
            showTooltip(divTooltip, anchor, div2View)
        }
    }

    private fun showTooltip(divTooltip: DivTooltip, anchor: View, div2View: Div2View) {
        if (tooltips.contains(divTooltip.id)) {
            return
        }
        anchor.doOnLayout {
            tryShowTooltip(anchor, divTooltip, div2View)
        }
        if (!ViewCompat.isLaidOut(anchor) && !anchor.isLayoutRequested) {
            anchor.requestLayout()
        }
    }

    fun hideTooltip(id: String, div2View: Div2View) {
        tooltips[id]?.popupWindow?.dismiss()
    }

    fun cancelTooltips(div2View: Div2View) = cancelTooltips(div2View, div2View)

    private fun cancelTooltips(div2View: Div2View, view: View) {
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
                        stopVisibilityTracking(div2View, tooltip.div)
                    }
                    ticket?.cancel()
                }
                forRemove.forEach { tooltips.remove(it) }
            }
        }
        if (view is ViewGroup) {
            view.children.forEach { child ->
                cancelTooltips(div2View, child)
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

    private fun tryShowTooltip(
        anchor: View,
        divTooltip: DivTooltip,
        div2View: Div2View
    ) {
        if (!tooltipRestrictor.canShowTooltip(div2View, anchor, divTooltip)) {
            return
        }
        val div = divTooltip.div
        val divBase = div.value()
        val tooltipView = div2Builder.get().buildView(div, div2View, DivStatePath.fromState(0))

        if (tooltipView == null) {
            Assert.fail("Broken div in popup")
            return
        }

        val displayMetrics = div2View.resources.displayMetrics
        val resolver = div2View.expressionResolver

        val popup = createPopup(
            tooltipView,
            divBase.width.toLayoutParamsSize(displayMetrics, resolver),
            divBase.height.toLayoutParamsSize(displayMetrics, resolver),
        ).apply {
            setOnDismissListener {
                tooltips.remove(divTooltip.id)
                stopVisibilityTracking(div2View, divTooltip.div)
                tooltipRestrictor.tooltipShownCallback?.onDivTooltipDismissed(div2View, anchor, divTooltip)
            }
            setDismissOnTouchOutside()
            setupAnimation(divTooltip, div2View.expressionResolver)
        }
        val tooltipData = TooltipData(popup, div, null)
        tooltips[divTooltip.id] = tooltipData
        val ticket = divPreloader.preload(div, div2View.expressionResolver) { hasFailures ->
            if (!hasFailures && !tooltipData.dismissed && anchor.isViewAttachedToWindow()
                && tooltipRestrictor.canShowTooltip(div2View, anchor, divTooltip)) {
                tooltipView.doOnLayout {
                    val location = calcPopupLocation(tooltipView, anchor, divTooltip, div2View.expressionResolver)
                    if (fitsInScreen(div2View, tooltipView, location)) {
                        popup.update(location.x, location.y, tooltipView.width, tooltipView.height)
                        startVisibilityTracking(div2View, div, tooltipView)
                        tooltipRestrictor.tooltipShownCallback?.onDivTooltipShown(div2View, anchor, divTooltip)
                    } else {
                        hideTooltip(divTooltip.id, div2View)
                    }
                }

                popup.showAtLocation(anchor, Gravity.NO_GRAVITY, 0, 0)
                if (divTooltip.duration.evaluate(resolver) != 0) {
                    mainThreadHandler.postDelayed(divTooltip.duration.evaluate(resolver).toLong()) {
                        hideTooltip(divTooltip.id, div2View)
                    }
                }
            }
        }
        tooltips[divTooltip.id]?.ticket = ticket
    }

    private fun startVisibilityTracking(div2View: Div2View, div: Div, tooltipView: View) {
        stopVisibilityTracking(div2View, div)
        divVisibilityActionTracker.trackVisibilityActionsOf(div2View, tooltipView, div)
    }

    private fun stopVisibilityTracking(div2View: Div2View, div: Div) {
        divVisibilityActionTracker.trackVisibilityActionsOf(div2View, null, div)
    }
}

private class TooltipData(
    val popupWindow: PopupWindow,
    val div: Div,
    var ticket: DivPreloader.Ticket? = null,
    var dismissed: Boolean = false
)

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

@SuppressLint("ClickableViewAccessibility")
private fun PopupWindow.setDismissOnTouchOutside() {
    isOutsideTouchable = true
    setTouchInterceptor { _, e ->
        if (e.action == MotionEvent.ACTION_OUTSIDE) {
            dismiss()
            true
        } else {
            false
        }
    }
}

private fun View.isViewAttachedToWindow(): Boolean {
    return this.isAttachedToWindow
}

@VisibleForTesting
internal fun calcPopupLocation(popupView: View, anchor: View, divTooltip: DivTooltip,
                               resolver: ExpressionResolver): Point {
    val locationArray = IntArray(2)
    anchor.getLocationInWindow(locationArray)
    val location = Point(locationArray[0], locationArray[1])

    val position = divTooltip.position.evaluate(resolver)
    location.x += when (position) {
        DivTooltip.Position.LEFT, DivTooltip.Position.TOP_LEFT, DivTooltip.Position.BOTTOM_LEFT ->
            -popupView.width
        DivTooltip.Position.TOP_RIGHT, DivTooltip.Position.RIGHT, DivTooltip.Position.BOTTOM_RIGHT ->
            anchor.width
        DivTooltip.Position.TOP, DivTooltip.Position.BOTTOM ->
            anchor.width / 2 - popupView.width / 2
    }

    location.y += when (position) {
        DivTooltip.Position.TOP_LEFT, DivTooltip.Position.TOP, DivTooltip.Position.TOP_RIGHT ->
            -popupView.height
        DivTooltip.Position.BOTTOM_LEFT, DivTooltip.Position.BOTTOM, DivTooltip.Position.BOTTOM_RIGHT ->
            anchor.height
        DivTooltip.Position.LEFT, DivTooltip.Position.RIGHT ->
            anchor.height / 2 - popupView.height / 2
    }

    val displayMetrics = anchor.resources.displayMetrics

    location.x += divTooltip.offset?.x?.toPx(displayMetrics, resolver) ?: 0
    location.y += divTooltip.offset?.y?.toPx(displayMetrics, resolver) ?: 0

    return location
}

private fun fitsInScreen(div2View: Div2View, tooltipView: View, location: Point): Boolean {
    val windowFrame = Rect()
    div2View.getWindowVisibleDisplayFrame(windowFrame)

    return windowFrame.left <= location.x &&
            windowFrame.top <= location.y &&
            windowFrame.right >= location.x + tooltipView.width &&
            windowFrame.bottom >= location.y + tooltipView.height
}
