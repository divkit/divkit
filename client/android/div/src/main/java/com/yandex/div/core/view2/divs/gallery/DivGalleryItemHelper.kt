package com.yandex.div.core.view2.divs.gallery

import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.core.view.marginStart
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.R
import com.yandex.div.core.util.doOnActualLayout
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.widget.makeAtMostSpec
import com.yandex.div.core.widget.makeExactSpec
import com.yandex.div.core.widget.makeUnspecifiedSpec
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivGallery
import kotlin.math.min

internal interface DivGalleryItemHelper {
    val divView: Div2View
    val view: RecyclerView
    val div: DivGallery
    val divItems: List<Div>

    val childrenToRelayout: MutableSet<View>

    fun _layoutDecorated(child: View, left: Int, top: Int, right: Int, bottom: Int) {
        trackVisibilityAction(child)
    }

    fun _onLayoutCompleted(state: RecyclerView.State?) {
        for (child in childrenToRelayout) {
            _layoutDecoratedWithMargins(child, child.left, child.top, child.right, child.bottom,
                isRelayoutingChildren = true)
        }
        childrenToRelayout.clear()
    }

    fun _layoutDecoratedWithMargins(
        child: View,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        isRelayoutingChildren: Boolean = false
    ) {
        val childDiv = runCatching { divItems[child.getTag(R.id.div_gallery_item_index) as Int].value() }.getOrNull()

        val resolver = divView.expressionResolver
        val parentAlignment = div.crossContentAlignment
        val orientation = getLayoutManagerOrientation()

        val shouldRelayout = (orientation == RecyclerView.VERTICAL && child.measuredWidth == 0) ||
                (orientation == RecyclerView.HORIZONTAL && child.measuredHeight == 0)

        if (shouldRelayout) {
            superLayoutDecoratedWithMargins(child, left, top, right, bottom)
            if (!isRelayoutingChildren) {
                childrenToRelayout.add(child)
            }
            return
        }

        val horizontalOffset = if (orientation == RecyclerView.VERTICAL) {
            val alignment = childDiv?.alignmentHorizontal.evaluateAlignment(resolver, parentAlignment) {
                asCrossContentAlignment()
            }
            calculateOffset(
                view.measuredWidth - view.paddingLeft - view.paddingRight,
                right - left,
                alignment
            )
        } else {
            0
        }

        val verticalOffset = if (orientation == RecyclerView.HORIZONTAL) {
            val alignment = childDiv?.alignmentVertical.evaluateAlignment(resolver, parentAlignment) {
                asCrossContentAlignment()
            }
            calculateOffset(
                view.measuredHeight - view.paddingTop - view.paddingBottom,
                bottom - top,
                alignment
            )
        } else {
            0
        }

        superLayoutDecoratedWithMargins(
            child,
            left + horizontalOffset,
            top + verticalOffset,
            right + horizontalOffset,
            bottom + verticalOffset
        )
        trackVisibilityAction(child)
        if (!isRelayoutingChildren) {
            childrenToRelayout.remove(child)
        }
    }

    fun firstVisibleItemPosition(): Int
    fun lastVisibleItemPosition(): Int
    fun width(): Int
    fun instantScrollToPosition(position: Int, scrollPosition: ScrollPosition)
    fun instantScrollToPositionWithOffset(position: Int, offset: Int, scrollPosition: ScrollPosition)
    fun getLayoutManagerOrientation(): Int

    fun instantScroll(
        position: Int,
        scrollPosition: ScrollPosition = ScrollPosition.DEFAULT,
        offset: Int = 0
    ) {
        view.doOnActualLayout {
            if (position == 0) {
                view.scrollBy(-offset, -offset)
                return@doOnActualLayout
            }

            view.scrollBy(-view.scrollX, -view.scrollY)

            var targetView: View? = view.layoutManager?.findViewByPosition(position)

            val orientationHelper = OrientationHelper
                .createOrientationHelper(view.layoutManager, getLayoutManagerOrientation())

            while (
                (targetView == null) &&
                (view.canScrollVertically(1) || view.canScrollHorizontally(1))
            ) {
                view.layoutManager?.requestLayout()
                targetView = view.layoutManager?.findViewByPosition(position)
                if (targetView != null) break
                view.scrollBy(view.width, view.height)
            }

            if (targetView == null) return@doOnActualLayout

            when (scrollPosition) {
                ScrollPosition.CENTER -> {
                    val childCoords = intArrayOf(0, 0)
                    val parentCoords = intArrayOf(0, 0)

                    view.getLocationOnScreen(parentCoords)
                    targetView.getLocationOnScreen(childCoords)

                    val startOffset = childCoords[0] - parentCoords[0]
                    val topOffset = childCoords[1] - parentCoords[1]

                    val centerX = (targetView.width - view.width) / 2 + startOffset
                    val centerY = (targetView.height - view.height) / 2 + topOffset
                    view.scrollBy(centerX, centerY)
                }

                ScrollPosition.DEFAULT -> {
                    val startGap = orientationHelper.getDecoratedStart(targetView) -
                            orientationHelper.startAfterPadding -
                            offset +
                            targetView.marginStart

                    view.scrollBy(startGap, startGap)
                }
            }
        }
    }

    fun superLayoutDecoratedWithMargins(child: View, left: Int, top: Int, right: Int, bottom: Int)

    fun _removeAndRecycleAllViews(recycler: RecyclerView.Recycler) {
        view.forEach { child ->
            trackVisibilityAction(child, clear = true)
        }
    }

    fun _onAttachedToWindow(view: RecyclerView) {

        view.forEach { child ->
            trackVisibilityAction(child)
        }
    }

    fun _onDetachedFromWindow(view: RecyclerView, recycler: RecyclerView.Recycler) {
        view.forEach { child ->
            trackVisibilityAction(child, clear = true)
        }
    }

    fun _detachView(child: View) {
        trackVisibilityAction(child, clear = true)
    }

    fun _detachViewAt(index: Int) {
        val child = _getChildAt(index) ?: return
        trackVisibilityAction(child, clear = true)
    }

    fun _removeView(child: View) {
        trackVisibilityAction(child, clear = true)
    }

    fun _removeViewAt(index: Int) {
        val child = _getChildAt(index) ?: return
        trackVisibilityAction(child, clear = true)
    }

    fun _getChildAt(index: Int): View?

    fun _getPosition(child: View): Int

    fun trackVisibilityAction(child: View, clear: Boolean = false) {
        val position = _getPosition(child)
        if (position == RecyclerView.NO_POSITION) return

        val container = (child as? ViewGroup) ?: return
        val itemView = container.children.firstOrNull() ?: return
        val div = divItems[position]

        if (clear) {
            divView.div2Component.visibilityActionTracker.trackVisibilityActionsOf(divView, null, div)
            divView.unbindViewFromDiv(itemView)
        } else {
            divView.div2Component.visibilityActionTracker.trackVisibilityActionsOf(divView, itemView, div)
            divView.bindViewToDiv(itemView, div)
        }
    }

    companion object {
        private fun calculateOffset(
            totalSpace: Int,
            decoratedMeasurement: Int,
            crossContentAlignment: DivGallery.CrossContentAlignment
        ): Int {
            val availableSpace = totalSpace - decoratedMeasurement

            return when (crossContentAlignment) {
                DivGallery.CrossContentAlignment.START -> 0
                DivGallery.CrossContentAlignment.CENTER -> availableSpace / 2
                DivGallery.CrossContentAlignment.END -> availableSpace
            }
        }

        private inline fun <T : Any> Expression<T>?.evaluateAlignment(
            resolver: ExpressionResolver,
            parentAlignment: Expression<DivGallery.CrossContentAlignment>,
            asCrossContentAlignment: T.() -> DivGallery.CrossContentAlignment
        ): DivGallery.CrossContentAlignment {
            return this?.evaluate(resolver)?.asCrossContentAlignment() ?: parentAlignment.evaluate(resolver)
        }

        private fun DivAlignmentHorizontal.asCrossContentAlignment(): DivGallery.CrossContentAlignment {
            return when (this) {
                DivAlignmentHorizontal.LEFT -> DivGallery.CrossContentAlignment.START
                DivAlignmentHorizontal.CENTER -> DivGallery.CrossContentAlignment.CENTER
                DivAlignmentHorizontal.RIGHT -> DivGallery.CrossContentAlignment.END
                DivAlignmentHorizontal.START -> DivGallery.CrossContentAlignment.START
                DivAlignmentHorizontal.END -> DivGallery.CrossContentAlignment.END
            }
        }

        private fun DivAlignmentVertical.asCrossContentAlignment(): DivGallery.CrossContentAlignment {
            return when (this) {
                DivAlignmentVertical.TOP, DivAlignmentVertical.BASELINE -> DivGallery.CrossContentAlignment.START
                DivAlignmentVertical.CENTER -> DivGallery.CrossContentAlignment.CENTER
                DivAlignmentVertical.BOTTOM -> DivGallery.CrossContentAlignment.END
            }
        }
    }

    fun getChildMeasureSpec(
        parentSize: Int,
        parentMode: Int,
        padding: Int,
        childDimension: Int,
        maxSize: Int,
        canScroll: Boolean
    ): Int {
        val size = (parentSize - padding).coerceAtLeast(0)
        return when (childDimension) {
            in 0 .. Int.MAX_VALUE -> makeExactSpec(childDimension)
            RecyclerView.LayoutParams.MATCH_PARENT -> {
                if (canScroll && parentMode == MeasureSpec.UNSPECIFIED) {
                    makeUnspecifiedSpec()
                } else {
                    MeasureSpec.makeMeasureSpec(size, parentMode)
                }
            }
            RecyclerView.LayoutParams.WRAP_CONTENT -> {
                if (maxSize == Int.MAX_VALUE) makeUnspecifiedSpec() else makeAtMostSpec(maxSize)
            }
            DivLayoutParams.WRAP_CONTENT_CONSTRAINED -> when (parentMode) {
                MeasureSpec.AT_MOST, MeasureSpec.EXACTLY -> makeAtMostSpec(min(size, maxSize))
                else -> if (maxSize == Int.MAX_VALUE) makeUnspecifiedSpec() else makeAtMostSpec(maxSize)
            }
            else -> makeUnspecifiedSpec()
        }
    }
}
