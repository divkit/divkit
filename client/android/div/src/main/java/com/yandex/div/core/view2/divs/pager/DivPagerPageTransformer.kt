package com.yandex.div.core.view2.divs.pager

import android.util.SparseArray
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.core.util.isLayoutRtl
import com.yandex.div.core.view2.divs.dpToPxF
import com.yandex.div.core.view2.divs.toPxF
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivPager
import com.yandex.div2.DivPagerLayoutMode
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max

internal class DivPagerPageTransformer(
    private val view: DivPagerView,
    private val div: DivPager,
    private val resolver: ExpressionResolver,
    private val pageTranslations: SparseArray<Float>,
) : ViewPager2.PageTransformer {
    private val metrics = view.resources.displayMetrics
    private val orientation = div.orientation.evaluate(resolver)

    private val itemSpacing = div.itemSpacing.toPxF(metrics, resolver)
    private var startPadding = 0f
    private var endPadding = 0f

    private val viewPager = view.viewPager
    private val recyclerView = view.getRecyclerView()

    private var parentSize: Int = 0
    private var itemsCount: Int = 0
    private var neighbourItemSize: Float = 0f
    private var onScreenPages: Float = 0f
    private var scrollRange: Int = 0

    private var scrollPositionOnLastScreen: Float = 0f
    private var scrollPositionOnFirstScreen: Float = 0f
    private var scrollPositionForLastItemDiff: Float = 0f

    init {
        recyclerView?.setItemViewCacheSize(ceil(onScreenPages).toInt() + 2)
    }

    override fun transformPage(page: View, position: Float) {
        calculateConstantsIfNeeded()
        val pagePosition = recyclerView?.layoutManager?.getPosition(page) ?: return
        val scrollOffset = evaluateRecyclerOffset()

        /**
         * This initial values is used to stick edge items to the edges of the pager.
         */
        var offset = when {
            div.infiniteScroll.evaluate(resolver) -> 0f
            scrollOffset < abs(scrollPositionOnFirstScreen) ->
                (scrollOffset + scrollPositionOnFirstScreen) / onScreenPages
            scrollOffset > abs(scrollPositionOnLastScreen + scrollPositionForLastItemDiff) ->
                (scrollOffset - scrollPositionOnLastScreen) / onScreenPages
            else -> 0f
        }

        offset -= position * (neighbourItemSize * 2 - itemSpacing)
        if (view.isLayoutRtl() && orientation == DivPager.Orientation.HORIZONTAL) offset = -offset

        pageTranslations.put(pagePosition, offset)
        if (orientation == DivPager.Orientation.HORIZONTAL) {
            page.translationX = offset
        } else {
            page.translationY = offset
        }
    }

    internal fun onItemsCountChanged() {
        calculateConstantsIfNeeded(true)
    }

    private fun calculateConstantsIfNeeded(forced: Boolean = false) {
        val scrollRangeEvaluated = when (orientation) {
            DivPager.Orientation.HORIZONTAL -> recyclerView?.computeHorizontalScrollRange()
            else -> recyclerView?.computeVerticalScrollRange()
        } ?: 0
        val parentSizeEvaluated = when (orientation) {
            DivPager.Orientation.HORIZONTAL -> viewPager.width
            else -> viewPager.height
        }
        if (scrollRangeEvaluated == scrollRange && parentSizeEvaluated == parentSize && !forced) return

        /**
         * These constants depends only item count and some variables,
         * on change which DivPagerTransformer is recreated.
         * So we can calculate these values only when items count is changed.
         */
        scrollRange = scrollRangeEvaluated
        parentSize = parentSizeEvaluated
        startPadding = evaluateStartPadding()
        endPadding = evaluateEndPadding()

        neighbourItemSize = evaluateNeighbourItemWidth()
        itemsCount = recyclerView?.adapter?.itemCount ?: 0
        val itemSize = parentSize - neighbourItemSize * 2

        onScreenPages = parentSize.toFloat() / itemSize

        val scrollPerItem = if (itemsCount > 0) scrollRange.toFloat() / itemsCount else 0f
        val scrollPerEndPadding = scrollPerItem * (endPadding / itemSize)
        val scrollPerStartPadding = scrollPerItem * (startPadding / itemSize)
        val scrollPerNeighborItem = scrollPerItem * (neighbourItemSize / itemSize)

        scrollPositionOnLastScreen = scrollRange - scrollPerItem * onScreenPages +
            scrollPerNeighborItem + scrollPerEndPadding
        scrollPositionForLastItemDiff =
            if (neighbourItemSize > endPadding) 0f * (endPadding - neighbourItemSize) / itemSize else 0f
        scrollPositionOnFirstScreen = if (view.isLayoutRtl()) {
            scrollPerStartPadding - scrollPerNeighborItem
        } else {
            parentSize * (startPadding - neighbourItemSize) / itemSize
        }
    }

    // Exact the same size as calculated at PageItemDecoration.
    private fun evaluateNeighbourItemWidth(): Float = when (val mode = div.layoutMode) {
        is DivPagerLayoutMode.NeighbourPageSize -> {
            val padding = max(startPadding, endPadding)
            max(mode.value.neighbourPageWidth.toPxF(metrics, resolver) + itemSpacing, padding / 2)
        }

        is DivPagerLayoutMode.PageSize -> {
            parentSize * (1 - mode.value.pageWidth.value.evaluate(resolver).toInt() / 100f) / 2
        }
    }

    private fun evaluateStartPadding(): Float {
        val paddings = div.paddings
        return when {
            paddings == null -> 0.0f
            orientation == DivPager.Orientation.VERTICAL -> paddings.top.evaluate(resolver).dpToPxF(metrics)
            paddings.start != null -> paddings.start?.evaluate(resolver).dpToPxF(metrics)
            view.isLayoutRtl() -> paddings.right.evaluate(resolver).dpToPxF(metrics)
            else -> paddings.left.evaluate(resolver).dpToPxF(metrics)
        }
    }

    private fun evaluateEndPadding(): Float {
        val paddings = div.paddings
        return when {
            paddings == null -> 0.0f
            orientation == DivPager.Orientation.VERTICAL -> paddings.bottom.evaluate(resolver).dpToPxF(metrics)
            paddings.end != null -> paddings.end?.evaluate(resolver).dpToPxF(metrics)
            view.isLayoutRtl() -> paddings.left.evaluate(resolver).dpToPxF(metrics)
            else -> paddings.right.evaluate(resolver).dpToPxF(metrics)
        }
    }

    private fun evaluateRecyclerOffset() = recyclerView?.let {
        when (orientation) {
            DivPager.Orientation.VERTICAL -> it.computeVerticalScrollOffset().toFloat()
            DivPager.Orientation.HORIZONTAL -> {
                if (view.isLayoutRtl()) {
                    parentSize * (itemsCount - 1) - it.computeHorizontalScrollOffset().toFloat()
                } else {
                    it.computeHorizontalScrollOffset().toFloat()
                }
            }
        }
    } ?: 0f
}
