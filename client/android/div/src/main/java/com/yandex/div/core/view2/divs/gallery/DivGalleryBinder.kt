package com.yandex.div.core.view2.divs.gallery

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.DivLinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.R
import com.yandex.div.core.Disposable
import com.yandex.div.core.ScrollDirection
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
import com.yandex.div.core.view2.animations.DivComparator
import com.yandex.div.core.view2.divs.DivBaseBinder
import com.yandex.div.core.view2.divs.DivPatchableAdapter
import com.yandex.div.core.view2.divs.PagerSnapStartHelper
import com.yandex.div.core.view2.divs.ReleasingViewPool
import com.yandex.div.core.view2.divs.bindStates
import com.yandex.div.core.view2.divs.dpToPx
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div.core.view2.divs.widgets.ParentScrollRestrictor
import com.yandex.div.core.view2.divs.widgets.ReleaseUtils.releaseAndRemoveChildren
import com.yandex.div.core.view2.reuse.util.tryRebindRecycleContainerChildren
import com.yandex.div.core.widget.DivViewWrapper
import com.yandex.div.internal.core.nonNullItems
import com.yandex.div.internal.widget.PaddingItemDecoration
import com.yandex.div2.Div
import com.yandex.div2.DivGallery
import java.util.WeakHashMap
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.abs

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
            val adapter = view.adapter as GalleryAdapter
            adapter.applyPatch(view, divPatchCache)
            adapter.closeAllSubscription()
            adapter.subscribeOnElements()
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
        view.adapter =
            GalleryAdapter(div.nonNullItems, context, divBinder.get(), viewCreator, itemStateBinder, path)
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
            val offset = galleryState?.scrollOffset
            view.scrollToPositionInternal(position, offset, scrollMode.toScrollPosition())
            view.addOnScrollListener(UpdateStateScrollListener(id, state, itemHelper))
        }
        view.addOnScrollListener(ScrollListener(context, view, itemHelper, div))
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

    private class ScrollListener(
        private val bindingContext: BindingContext,
        private val recycler: DivRecyclerView,
        private val galleryItemHelper: DivGalleryItemHelper,
        private val galleryDiv: DivGallery,
    ) : RecyclerView.OnScrollListener() {

        private val divView = bindingContext.divView
        private val minimumSignificantDx = divView.config.logCardScrollSignificantThreshold

        var totalDelta = 0
        var alreadyLogged = false
        var direction = ScrollDirection.NEXT

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            // New dragging event resets logged state
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                alreadyLogged = false
            }
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                divView.div2Component.div2Logger.logGalleryCompleteScroll(divView,
                                                                          galleryDiv,
                                                                          galleryItemHelper.firstVisibleItemPosition(),
                                                                          galleryItemHelper.lastVisibleItemPosition(),
                                                                          direction)
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val minimumDelta = when (minimumSignificantDx > 0) {
                true -> minimumSignificantDx
                else -> galleryItemHelper.width() / 20
            }
            totalDelta += abs(dx) + abs(dy)  // because in galleries we used only one scroll direction at a time
            if (totalDelta > minimumDelta) {
                totalDelta = 0
                if (!alreadyLogged) {
                    alreadyLogged = true
                    divView.div2Component.div2Logger.logGalleryScroll(divView)
                    direction = if (dx > 0 || dy > 0) ScrollDirection.NEXT else ScrollDirection.BACK
                }
                trackViews()
            }
        }

        private fun trackViews() {
            val visibilityActionTracker = divView.div2Component.visibilityActionTracker
            visibilityActionTracker.updateVisibleViews(recycler.children.toList())

            recycler.children.forEach { child ->
                val position = recycler.getChildAdapterPosition(child)
                if (position == RecyclerView.NO_POSITION) return@forEach

                val div = (recycler.adapter as GalleryAdapter).items[position]
                visibilityActionTracker.startTrackingViewsHierarchy(bindingContext, child, div)
            }

            // Find and track recycled views containing DisappearActions that are waiting for disappear
            with(visibilityActionTracker) {
                getDivWithWaitingDisappearActions().filter { it.key !in recycler.children }.forEach { (view, div) ->
                    trackDetachedView(bindingContext, view, div)
                }
            }
        }
    }

    internal class GalleryAdapter(
        divs: List<Div>,
        private val bindingContext: BindingContext,
        private val divBinder: DivBinder,
        private val viewCreator: DivViewCreator,
        private val itemStateBinder: (itemView: View, div: Div) -> Unit,
        private val path: DivStatePath
    ) : DivPatchableAdapter<GalleryViewHolder>(divs, bindingContext) {

        private val ids = WeakHashMap<Div, Long>()
        private var lastItemId = 0L

        override val subscriptions = mutableListOf<Disposable>()

        init {
            setHasStableIds(true)
            subscribeOnElements()
        }

        override fun onViewAttachedToWindow(holder: GalleryViewHolder) {
            super.onViewAttachedToWindow(holder)
            holder.oldDiv?.let { div ->
                itemStateBinder.invoke(holder.rootView, div)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
            val view = DivViewWrapper(bindingContext.divView.context)
            return GalleryViewHolder(view, divBinder, viewCreator)
        }

        override fun getItemId(position: Int): Long {
            val item = activeItems[position]
            return ids[item] ?: (lastItemId++).also { ids[item] = it }
        }

        override fun getItemCount() = activeItems.size

        override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
            holder.bind(bindingContext, activeItems[position], path)
            holder.rootView.setTag(R.id.div_gallery_item_index, position)
            divBinder.attachIndicators()
        }
    }

    internal class GalleryViewHolder(
        val rootView: DivViewWrapper,
        private val divBinder: DivBinder,
        private val viewCreator: DivViewCreator
    ) : RecyclerView.ViewHolder(rootView) {

        var oldDiv: Div? = null

        fun bind(context: BindingContext, div: Div, path: DivStatePath) {
            val div2View = context.divView
            val resolver = context.expressionResolver

            if (rootView.tryRebindRecycleContainerChildren(div2View, div)) {
                oldDiv = div
                return
            }

            val divView = if (oldDiv != null && rootView.child != null &&
                DivComparator.areDivsReplaceable(
                    oldDiv,
                    div,
                    div2View.getExpressionResolver(oldDiv?.value()),
                    resolver
                )) {
                rootView.child!!
            } else {
                val newDivView = viewCreator.create(div, resolver)
                rootView.releaseAndRemoveChildren(div2View)
                rootView.addView(newDivView)
                newDivView
            }

            oldDiv = div
            divBinder.bind(context, divView, div, path)
        }
    }
}
