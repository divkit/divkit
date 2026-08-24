package com.yandex.div.core.view2.divs.gallery

import android.annotation.SuppressLint
import androidx.core.view.doOnNextLayout
import androidx.recyclerview.widget.DivLinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.state.DivViewState
import com.yandex.div.core.state.GalleryState
import com.yandex.div.core.state.UpdateStateScrollListener
import com.yandex.div.core.util.doOnActualLayout
import com.yandex.div.core.util.expressionSubscriber
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.divs.DivBaseBinder
import com.yandex.div.core.view2.divs.ReleasingViewPool
import com.yandex.div.core.view2.divs.bindItemBuilder
import com.yandex.div.core.view2.divs.bindStates
import com.yandex.div.core.view2.divs.dpToPx
import com.yandex.div.core.view2.divs.dpToPxF
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div.core.view2.divs.widgets.ParentScrollRestrictor
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.core.build
import com.yandex.div.internal.core.buildItems
import com.yandex.div.internal.widget.PaddingItemDecoration
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivGallery
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.roundToInt

@DivScope
internal class DivGalleryBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val viewCreator: DivViewCreator,
    private val divBinder: Provider<DivBinder>,
    private val recyclerScrollInterceptionAngle: Float,
) : DivViewBinder<DivBlock.Gallery, DivRecyclerView>(baseBinder) {

    @SuppressLint("ClickableViewAccessibility")
    override fun bindView(view: DivRecyclerView, divBlock: DivBlock.Gallery, divView: Div2View) {
        val oldDivBlock = view.divBlock
        if (divBlock.div === oldDivBlock?.div) {
            val adapter = view.adapter as? DivGalleryAdapter ?: return
            adapter.setItems(divBlock.buildItems())
            view.bindStates(divBinder.get(), divView)
            return
        }

        baseBinder.bindView(view, divBlock, oldDivBlock, divView)
        view.bind(divBlock, divView)
    }

    private fun DivRecyclerView.bind(divBlock: DivBlock.Gallery, divView: Div2View) {
        val div = divBlock.divValue
        val resolver = divBlock.expressionResolver
        val galleryAdapter = DivGalleryAdapter(divBlock.buildItems(), divView, divBinder.get(), viewCreator)
        val reusableObserver = { _: Any -> updateDecorations(div, galleryAdapter, resolver, divView) }
        addSubscription(div.orientation.observe(resolver, reusableObserver))
        addSubscription(div.scrollbar.observe(resolver, reusableObserver))
        addSubscription(div.scrollMode.observe(resolver, reusableObserver))
        addSubscription(div.crossContentAlignment.observe(resolver, reusableObserver))
        div.scrollContentAlignment?.let { addSubscription(it.observe(resolver, reusableObserver)) }
        addSubscription(div.itemSpacing.observe(resolver, reusableObserver))
        addSubscription(div.restrictParentScroll.observe(resolver, reusableObserver))
        div.columnCount?.let { addSubscription(it.observe(resolver, reusableObserver)) }

        setRecycledViewPool(ReleasingViewPool(divView.viewComponent.releaseViewVisitor))
        setScrollingTouchSlop(RecyclerView.TOUCH_SLOP_PAGING)
        clipToPadding = false
        overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        adapter = galleryAdapter
        bindItemBuilder(div, resolver, divBlock.path, divView)
        resetAnimatorAndRestoreOnLayout()
        updateDecorations(div, galleryAdapter, resolver, divView)
    }

    private fun DivRecyclerView.updateDecorations(
        div: DivGallery,
        adapter: DivGalleryAdapter,
        resolver: ExpressionResolver,
        divView: Div2View,
    ) {
        val metrics = resources.displayMetrics
        val divOrientation = div.orientation.evaluate(resolver)
        val orientation = if (divOrientation == DivGallery.Orientation.HORIZONTAL) {
            RecyclerView.HORIZONTAL
        } else {
            RecyclerView.VERTICAL
        }

        adapter.orientation = orientation
        val scrollbarEnabled = div.scrollbar.evaluate(resolver) == DivGallery.Scrollbar.AUTO
        isVerticalScrollBarEnabled = scrollbarEnabled && orientation == RecyclerView.VERTICAL
        isHorizontalScrollBarEnabled = scrollbarEnabled && orientation == RecyclerView.HORIZONTAL
        isScrollbarFadingEnabled = false

        val columnCount = div.columnCount?.evaluate(resolver)?.toIntSafely() ?: 1

        adapter.columnCount = columnCount
        val itemSpacing = div.itemSpacing.evaluate(resolver).dpToPx(metrics)
        val crossSpacing = div.crossSpacing?.evaluate(resolver)?.dpToPxF(metrics) ?: itemSpacing.toFloat()
        adapter.crossSpacing = crossSpacing
        clipChildren = false
        setItemDecoration(
            if (columnCount == 1)
                PaddingItemDecoration(
                    midItemPadding = itemSpacing,
                    orientation = orientation
                )
            else
                PaddingItemDecoration(
                    midItemPadding = itemSpacing,
                    crossItemPadding = crossSpacing.roundToInt(),
                    orientation = orientation
                )
        )

        val scrollMode = div.scrollMode.evaluate(resolver).also { scrollMode = it }
        snapHelper.itemSpacing = itemSpacing
        snapHelper.alignment = div.scrollContentAlignment?.evaluate(resolver) ?: when (scrollMode) {
            DivGallery.ScrollMode.DEFAULT -> DivGallery.ContentAlignment.START
            DivGallery.ScrollMode.PAGING -> DivGallery.ContentAlignment.CENTER
        }
        when (scrollMode) {
            DivGallery.ScrollMode.DEFAULT -> snapHelper.attachToRecyclerView(null)
            DivGallery.ScrollMode.PAGING -> snapHelper.attachToRecyclerView(this)
        }
        val crossContentAlignment = div.crossContentAlignment.evaluate(resolver)

        // Added as a workaround for a bug in R8 that leads to replacing the
        // DivGalleryItemHelper type with DivGridLayoutManager, resulting in
        // casting DivLinearLayoutManager to DivGridLayoutManager exception.
        val itemHelper: DivGalleryItemHelper = if (columnCount == 1) {
            DivLinearLayoutManager(this, divView, orientation, crossContentAlignment)
        } else {
            DivGridLayoutManager(
                this,
                divView,
                orientation,
                crossContentAlignment,
                columnCount,
                itemSpacing,
                crossSpacing.toInt()
            )
        }
        layoutManager = itemHelper.toLayoutManager()

        scrollInterceptionAngle = recyclerScrollInterceptionAngle
        clearOnScrollListeners()
        divView.currentState?.let { state ->
            val itemCount = adapter.itemCount.takeIf { it > 1 } ?: return@let
            val id = div.id ?: div.hashCode().toString()
            val (position, offset) = state.getPositionAndOffset(id)
                ?: getPositionAndOffset(div, resolver, orientation)
            itemHelper.instantScrollToPosition(position.coerceAtMost(itemCount - 1), offset)
            addOnScrollListener(UpdateStateScrollListener(id, state, itemHelper))
        }
        addOnScrollListener(DivGalleryScrollListener(this, itemHelper, div, resolver, divView))
        onInterceptTouchEventListener =
            if (div.restrictParentScroll.evaluate(resolver)) ParentScrollRestrictor else null
    }

    private fun DivRecyclerView.resetAnimatorAndRestoreOnLayout() {
        val prevItemAnimator = itemAnimator.also { itemAnimator = null }
        doOnActualLayout {
            if (itemAnimator == null) {
                itemAnimator = prevItemAnimator
            }
        }
    }

    private fun DivViewState.getPositionAndOffset(id: String) =
        (getBlockState(id) as? GalleryState)?.let { it.visibleItemIndex to it.scrollOffset }

    private fun DivRecyclerView.getPositionAndOffset(
        div: DivGallery,
        resolver: ExpressionResolver,
        @RecyclerView.Orientation orientation: Int,
    ): Pair<Int, Int> {
        val position = div.defaultItem.evaluate(resolver).toIntSafely()
        val offset = when {
            position != 0 -> 0
            orientation == RecyclerView.HORIZONTAL -> paddingStart
            else -> paddingTop
        }
        return position to offset
    }

    private fun DivRecyclerView.setItemDecoration(decoration: RecyclerView.ItemDecoration) {
        removeItemDecorations()
        addItemDecoration(decoration)
    }

    private fun DivRecyclerView.removeItemDecorations() {
        for (i in itemDecorationCount - 1 downTo 0) {
            removeItemDecorationAt(i)
        }
    }

    private fun DivRecyclerView.bindItemBuilder(
        div: DivGallery,
        resolver: ExpressionResolver,
        path: DivStatePath,
        divView: Div2View,
    ) {
        val builder = div.itemBuilder ?: return
        expressionSubscriber.bindItemBuilder(builder, resolver) {
            val adapter = adapter as? DivGalleryAdapter ?: return@bindItemBuilder
            val id = div.id ?: div.hashCode().toString()
            val hasState = divView.currentState?.getPositionAndOffset(id) != null

            adapter.setItems(builder.build(resolver, path))
            if (hasState) return@bindItemBuilder

            resetAnimatorAndRestoreOnLayout()

            val itemCount = adapter.itemCount.takeIf { it > 1 } ?: return@bindItemBuilder
            val itemHelper = layoutManager as? DivGalleryItemHelper ?: return@bindItemBuilder
            val (position, offset) = getPositionAndOffset(div, resolver, adapter.orientation)
            val targetPosition = position.coerceAtMost(itemCount - 1)
            doOnNextLayout {
                itemHelper.trySnapToPosition(targetPosition, offset)
            }
            scrollToPosition(targetPosition)
        }
    }
}
