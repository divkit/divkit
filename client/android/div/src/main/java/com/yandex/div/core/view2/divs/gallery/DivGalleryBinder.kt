package com.yandex.div.core.view2.divs.gallery

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DivLinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.downloader.DivPatchCache
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.state.GalleryState
import com.yandex.div.core.state.UpdateStateScrollListener
import com.yandex.div.core.util.doOnActualLayout
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.divs.DivBaseBinder
import com.yandex.div.core.view2.divs.ReleasingViewPool
import com.yandex.div.core.view2.divs.bindItemBuilder
import com.yandex.div.core.view2.divs.bindStates
import com.yandex.div.core.view2.divs.dpToPx
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div.core.view2.divs.widgets.ParentScrollRestrictor
import com.yandex.div.internal.core.build
import com.yandex.div.internal.core.buildItems
import com.yandex.div.internal.widget.PaddingItemDecoration
import com.yandex.div2.Div
import com.yandex.div2.DivGallery
import javax.inject.Inject
import javax.inject.Provider

@DivScope
internal class DivGalleryBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val viewCreator: DivViewCreator,
    private val divBinder: Provider<DivBinder>,
    private val divPatchCache: DivPatchCache,
    private val recyclerScrollInterceptionAngle: Float,
) : DivViewBinder<Div.Gallery, DivGallery, DivRecyclerView>(baseBinder) {

    @SuppressLint("ClickableViewAccessibility")
    override fun bindView(context: BindingContext, view: DivRecyclerView, div: Div.Gallery, path: DivStatePath) {
        val oldDiv = (view as? DivRecyclerView)?.div
        if (div === oldDiv) {
            val adapter = view.adapter as? DivGalleryAdapter ?: return
            adapter.applyPatch(view, divPatchCache, context)
            view.bindStates(context, divBinder.get())
            return
        }

        baseBinder.bindView(context, view, div, oldDiv)
        view.bind(context, div.value, path)
    }

    private fun DivRecyclerView.bind(bindingContext: BindingContext, div: DivGallery, path: DivStatePath) {
        val resolver = bindingContext.expressionResolver
        val reusableObserver = { _: Any -> updateDecorations(bindingContext, div) }
        addSubscription(div.orientation.observe(resolver, reusableObserver))
        addSubscription(div.scrollbar.observe(resolver, reusableObserver))
        addSubscription(div.scrollMode.observe(resolver, reusableObserver))
        addSubscription(div.itemSpacing.observe(resolver, reusableObserver))
        addSubscription(div.restrictParentScroll.observe(resolver, reusableObserver))
        div.columnCount?.let { addSubscription(it.observe(resolver, reusableObserver)) }

        setRecycledViewPool(ReleasingViewPool(bindingContext.divView.releaseViewVisitor))
        setScrollingTouchSlop(RecyclerView.TOUCH_SLOP_PAGING)
        clipToPadding = false
        overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        adapter = DivGalleryAdapter(
            div.buildItems(bindingContext.divView, resolver),
            bindingContext,
            divBinder.get(),
            viewCreator,
            path,
        )
        bindItemBuilder(bindingContext, div)
        resetAnimatorAndRestoreOnLayout()
        updateDecorations(bindingContext, div)
    }

    private fun DivRecyclerView.updateDecorations(context: BindingContext, div: DivGallery) {
        val metrics = resources.displayMetrics
        val resolver = context.expressionResolver
        val divOrientation = div.orientation.evaluate(resolver)
        val orientation = if (divOrientation == DivGallery.Orientation.HORIZONTAL) {
            RecyclerView.HORIZONTAL
        } else {
            RecyclerView.VERTICAL
        }

        val scrollbarEnabled = div.scrollbar.evaluate(resolver) == DivGallery.Scrollbar.AUTO
        isVerticalScrollBarEnabled = scrollbarEnabled && orientation == RecyclerView.VERTICAL
        isHorizontalScrollBarEnabled = scrollbarEnabled && orientation == RecyclerView.HORIZONTAL
        isScrollbarFadingEnabled = false

        val columnCount = div.columnCount?.evaluate(resolver) ?: 1

        clipChildren = false
        setItemDecoration(
            if (columnCount == 1L)
                PaddingItemDecoration(
                    midItemPadding = div.itemSpacing.evaluate(resolver).dpToPx(metrics),
                    orientation = orientation
                )
            else
                PaddingItemDecoration(
                    midItemPadding = div.itemSpacing.evaluate(resolver).dpToPx(metrics),
                    crossItemPadding = (div.crossSpacing ?: div.itemSpacing).evaluate(resolver).dpToPx(metrics),
                    orientation = orientation
                )
        )

        val scrollMode = div.scrollMode.evaluate(resolver).also { scrollMode = it }
        when (scrollMode) {
            DivGallery.ScrollMode.DEFAULT -> pagerSnapStartHelper?.attachToRecyclerView(null)
            DivGallery.ScrollMode.PAGING -> {
                val itemSpacing = div.itemSpacing.evaluate(resolver).dpToPx(resources.displayMetrics)

                val helper = pagerSnapStartHelper?.also { it.itemSpacing = itemSpacing } ?:
                    PagerSnapStartHelper(itemSpacing).also { pagerSnapStartHelper = it }

                helper.attachToRecyclerView(this)
            }
        }

        // Added as a workaround for a bug in R8 that leads to replacing the
        // DivGalleryItemHelper type with DivGridLayoutManager, resulting in
        // casting DivLinearLayoutManager to DivGridLayoutManager exception.
        val itemHelper: DivGalleryItemHelper = if (columnCount == 1L) {
            DivLinearLayoutManager(context, this, div, orientation)
        } else {
            DivGridLayoutManager(context, this, div, orientation)
        }
        layoutManager = itemHelper.toLayoutManager()

        scrollInterceptionAngle = recyclerScrollInterceptionAngle
        clearOnScrollListeners()
        context.divView.currentState?.let { state ->
            val id = div.id ?: div.hashCode().toString()
            val galleryState = state.getBlockState(id) as? GalleryState
            val position = galleryState?.visibleItemIndex ?: div.defaultItem.evaluate(resolver).toIntSafely()
            val offset = galleryState?.scrollOffset ?: when {
                position != 0 -> 0
                orientation == RecyclerView.HORIZONTAL -> paddingStart
                else -> paddingTop
            }
            scrollToPositionInternal(position, offset, scrollMode.toScrollPosition())
            addOnScrollListener(UpdateStateScrollListener(id, state, itemHelper))
        }
        addOnScrollListener(DivGalleryScrollListener(context, this, itemHelper, div))
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

    private fun DivRecyclerView.scrollToPositionInternal(
        position: Int,
        offset: Int,
        scrollPosition: ScrollPosition
    ) {
        val layoutManager = layoutManager as? DivGalleryItemHelper ?: return
        if (offset == 0 && position == 0) {
            // Show left or top padding on first position without any snapping
            layoutManager.instantScrollToPosition(position, scrollPosition)
        } else {
            layoutManager.instantScrollToPositionWithOffset(position, offset, scrollPosition)
        }
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

    private fun DivRecyclerView.bindItemBuilder(context: BindingContext, div: DivGallery) {
        val builder = div.itemBuilder ?: return
        bindItemBuilder(builder, context.expressionResolver) {
            (adapter as DivGalleryAdapter?)?.setItems(
                builder.build(context.divView, context.expressionResolver)
            )
        }
    }
}
