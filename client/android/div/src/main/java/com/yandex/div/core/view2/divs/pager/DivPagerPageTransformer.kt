package com.yandex.div.core.view2.divs.pager

import android.util.SparseArray
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.core.util.androidInterpolator
import com.yandex.div.core.util.isLayoutRtl
import com.yandex.div.core.view2.divs.dpToPxF
import com.yandex.div.core.view2.divs.toPxF
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAnimationInterpolator
import com.yandex.div2.DivPageTransformationOverlap
import com.yandex.div2.DivPageTransformationSlide
import com.yandex.div2.DivPager
import com.yandex.div2.DivPagerLayoutMode
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

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

        when (val transformation = div.pageTransformation?.value()) {
            is DivPageTransformationSlide -> transformation.apply(page, position)
            is DivPageTransformationOverlap -> transformation.apply(page, position)
            else -> page.applyOffset(position)
        }
    }

    internal fun onItemsCountChanged() {
        calculateConstantsIfNeeded(true)
    }

    private fun DivPageTransformationSlide.apply(page: View, position: Float) {
        page.applyAlphaAndScale(
            position, interpolator,
            nextPageAlpha, nextPageScale,
            previousPageAlpha, previousPageScale
        )
        page.applyOffset(position)
    }

    private fun DivPageTransformationOverlap.apply(page: View, position: Float) {
        page.applyAlphaAndScale(
            position, interpolator,
            nextPageAlpha, nextPageScale,
            previousPageAlpha, previousPageScale
        )

        if (position > 0 || (position < 0 && reversedStackingOrder.evaluate(resolver))) {
            page.applyOffset(position)
            page.translationZ = 0f
        } else {
            page.applyOverlapOffset(position)
            page.translationZ = -abs(position)
        }
    }

    private fun View.applyAlphaAndScale(
        position: Float,
        interpolator: Expression<DivAnimationInterpolator>,
        nextPageAlpha: Expression<Double>,
        nextPageScale: Expression<Double>,
        previousPageAlpha: Expression<Double>,
        previousPageScale: Expression<Double>,
    ) {
        val coercedPosition = abs(position.coerceAtLeast(-1f).coerceAtMost(1f))
        val androidInterpolator = interpolator.evaluate(resolver).androidInterpolator
        val interpolatedValue = 1 - androidInterpolator.getInterpolation(coercedPosition)

        if (position > 0) {
            applyPageAlpha(interpolatedValue, nextPageAlpha.evaluate(resolver))
            applyPageScale(interpolatedValue, nextPageScale.evaluate(resolver))
        } else {
            applyPageAlpha(interpolatedValue, previousPageAlpha.evaluate(resolver))
            applyPageScale(interpolatedValue, previousPageScale.evaluate(resolver))
        }
    }

    private fun View.applyOffset(position: Float) {
        val pagePosition = recyclerView?.getChildAdapterPosition(this) ?: return
        val scrollOffset = evaluateRecyclerOffset()

        /**
         * This initial values is used to stick edge items to the edges of the pager.
         */
        var offset = when {
            div.pageTransformation?.value() is DivPageTransformationOverlap -> 0f
            div.infiniteScroll.evaluate(resolver) -> 0f
            scrollOffset < abs(scrollPositionOnFirstScreen) ->
                (scrollOffset + scrollPositionOnFirstScreen) / onScreenPages
            scrollOffset > abs(scrollPositionOnLastScreen + scrollPositionForLastItemDiff) ->
                (scrollOffset - scrollPositionOnLastScreen) / onScreenPages
            else -> 0f
        }

        offset -= position * (neighbourItemSize * 2 - itemSpacing)
        if (view.isLayoutRtl() && orientation == DivPager.Orientation.HORIZONTAL) offset = -offset
        applyEvaluatedOffset(pagePosition, offset)
    }

    private fun View.applyOverlapOffset(position: Float) {
        val pagePosition = recyclerView?.getChildAdapterPosition(this) ?: return
        val scrollOffset = evaluateRecyclerOffset()

        var offset = (scrollOffset) / onScreenPages
        offset -= position * (neighbourItemSize * 2)
        offset -= pagePosition * (parentSize - neighbourItemSize * 2)

        if (view.isLayoutRtl() && orientation == DivPager.Orientation.HORIZONTAL) offset = -offset
        applyEvaluatedOffset(pagePosition, offset)
    }

    private fun View.applyEvaluatedOffset(pagePosition: Int, offset: Float) {
        pageTranslations.put(pagePosition, offset)

        if (orientation == DivPager.Orientation.HORIZONTAL) {
            translationX = offset
        } else {
            translationY = offset
        }
    }

    private fun View.applyPageAlpha(interpolatedValue: Float, cornerAlpha: Double) {
        recyclerView ?: return

        val adapterPosition = recyclerView.getChildAdapterPosition(this)
        val adapter = recyclerView.adapter as? DivPagerAdapter ?: return
        val div = adapter.itemsToShow[adapterPosition].div
        val pageAlpha = div.value().alpha.evaluate(resolver)

        alpha = getInterpolation(pageAlpha, cornerAlpha, interpolatedValue).toFloat()
    }

    private fun View.applyPageScale(interpolatedValue: Float, cornerScale: Double) {
        if (cornerScale == 1.0) return

        getInterpolation(1.0, cornerScale, interpolatedValue).toFloat().let {
            scaleX = it
            scaleY = it
        }
    }

    private fun getInterpolation(value1: Double, value2: Double, interpolatedValue: Float): Double {
        return min(value1, value2) + abs(value2 - value1) * interpolatedValue
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

        is DivPagerLayoutMode.PageContentSize -> throw NotImplementedError()
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
