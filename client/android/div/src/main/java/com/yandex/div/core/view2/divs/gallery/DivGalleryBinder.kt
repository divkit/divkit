package com.yandex.div.core.view2.divs.gallery

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.DivLinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.downloader.DivPatchCache
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.state.GalleryState
import com.yandex.div.core.state.UpdateStateScrollListener
import com.yandex.div.core.util.doOnActualLayout
import com.yandex.div.core.util.isLayoutRtl
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
    private val scrollInterceptionAngle: Float,
) : DivViewBinder<DivGallery, DivRecyclerView> {

    @SuppressLint("ClickableViewAccessibility")
    override fun bindView(context: BindingContext, view: DivRecyclerView, div: DivGallery, path: DivStatePath) {
        val divView = context.divView
        val resolver = context.expressionResolver

        val oldDiv = (view as? DivRecyclerView)?.div
        if (div === oldDiv) {
            val adapter = view.adapter as? DivGalleryAdapter ?: return
            adapter.applyPatch(view, divPatchCache, context)
            view.bindStates(divView.rootDiv(), context, resolver, divBinder.get())
            return
        }

        baseBinder.bindView(context, view, div, oldDiv)

        val reusableObserver = { _: Any ->
            updateDecorations(view, div, context)
        }
        view.addSubscription(div.orientation.observe(resolver, reusableObserver))
        view.addSubscription(div.scrollbar.observe(resolver, reusableObserver))
        view.addSubscription(div.scrollMode.observe(resolver, reusableObserver))
        view.addSubscription(div.itemSpacing.observe(resolver, reusableObserver))
        view.addSubscription(div.restrictParentScroll.observe(resolver, reusableObserver))
        div.columnCount?.let {
            view.addSubscription(it.observe(resolver, reusableObserver))
        }

        view.setRecycledViewPool(ReleasingViewPool(divView.releaseViewVisitor))
        view.setScrollingTouchSlop(RecyclerView.TOUCH_SLOP_PAGING)
        view.clipToPadding = false

        view.overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val itemStateBinder = { itemView: View, _: Div ->
            itemView.bindStates(divView.rootDiv(), context, resolver, divBinder.get())
        }
        view.adapter = DivGalleryAdapter(
            div.buildItems(resolver),
            context,
            divBinder.get(),
            viewCreator,
            itemStateBinder,
            path
        )
        view.bindItemBuilder(context, div)
        view.resetAnimatorAndRestoreOnLayout()

        updateDecorations(view, div, context)
    }

    private fun updateDecorations(
        view: DivRecyclerView,
        div: DivGallery,
        context: BindingContext,
    ) {
        val metrics = view.resources.displayMetrics
        val resolver = context.expressionResolver
        val divOrientation = div.orientation.evaluate(resolver)
        val orientation = if (divOrientation == DivGallery.Orientation.HORIZONTAL) {
            RecyclerView.HORIZONTAL
        } else {
            RecyclerView.VERTICAL
        }

        val scrollbarEnabled = div.scrollbar.evaluate(resolver) == DivGallery.Scrollbar.AUTO
        view.isVerticalScrollBarEnabled = scrollbarEnabled && orientation == RecyclerView.VERTICAL
        view.isHorizontalScrollBarEnabled = scrollbarEnabled && orientation == RecyclerView.HORIZONTAL
        view.isScrollbarFadingEnabled = false

        val columnCount = div.columnCount?.evaluate(resolver) ?: 1

        view.clipChildren = false
        view.setItemDecoration(
            if (columnCount == 1L)
                PaddingItemDecoration(
                    midItemPadding = div.itemSpacing.evaluate(resolver)
                        .dpToPx(metrics),
                    orientation = orientation
                )
            else
                PaddingItemDecoration(
                    midItemPadding = div.itemSpacing.evaluate(resolver)
                        .dpToPx(metrics),
                    crossItemPadding = (div.crossSpacing ?: div.itemSpacing).evaluate(resolver)
                        .dpToPx(metrics),
                    orientation = orientation
                )
        )

        val scrollMode = div.scrollMode.evaluate(resolver).also { view.scrollMode = it }
        when (scrollMode) {
            DivGallery.ScrollMode.DEFAULT -> {
                view.pagerSnapStartHelper?.attachToRecyclerView(null)
            }
            DivGallery.ScrollMode.PAGING -> {
                val itemSpacing = div.itemSpacing.evaluate(resolver).dpToPx(view.resources.displayMetrics)

                val helper = view.pagerSnapStartHelper?.also { it.itemSpacing = itemSpacing } ?:
                    PagerSnapStartHelper(itemSpacing).also { view.pagerSnapStartHelper = it }

                helper.attachToRecyclerView(view)
            }
        }

        // Added as a workaround for a bug in R8 that leads to replacing the
        // DivGalleryItemHelper type with DivGridLayoutManager, resulting in
        // casting DivLinearLayoutManager to DivGridLayoutManager exception.
        val itemHelper: DivGalleryItemHelper = if (columnCount == 1L) {
            DivLinearLayoutManager(context, view, div, orientation)
        } else {
            DivGridLayoutManager(context, view, div, orientation)
        }
        view.layoutManager = itemHelper.toLayoutManager()

        view.scrollInterceptionAngle = scrollInterceptionAngle
        view.clearOnScrollListeners()
        context.divView.currentState?.let { state ->
            val id = div.id ?: div.hashCode().toString()
            val galleryState = state.getBlockState(id) as GalleryState?
            val position = galleryState?.visibleItemIndex
                ?: div.defaultItem.evaluate(resolver).toIntSafely()
            val offset = galleryState?.scrollOffset ?: if (view.isLayoutRtl()) view.paddingRight else view.paddingLeft
            view.scrollToPositionInternal(position, offset, scrollMode.toScrollPosition())
            view.addOnScrollListener(UpdateStateScrollListener(id, state, itemHelper))
        }
        view.addOnScrollListener(DivGalleryScrollListener(context, view, itemHelper, div))
        view.onInterceptTouchEventListener = if (div.restrictParentScroll.evaluate(resolver)) {
            ParentScrollRestrictor
        } else {
            null
        }
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
        offset: Int? = null,
        scrollPosition: ScrollPosition
    ) {
        val layoutManager = layoutManager as? DivGalleryItemHelper
        when {
            offset == null && position == 0 -> {
                // Show left or top padding on first position without any snapping
                layoutManager?.instantScrollToPosition(position, scrollPosition)
            }
            offset != null -> layoutManager?.instantScrollToPositionWithOffset(position, offset, scrollPosition)
            else -> {
                // Call on RecyclerView itself for proper snapping.
                layoutManager?.instantScrollToPosition(position, scrollPosition)
            }
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
            (adapter as DivGalleryAdapter?)?.setItems(builder.build(context.expressionResolver))
        }
    }
}
