package com.yandex.div.core.view2.divs.gallery

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.R
import com.yandex.div.core.ScrollDirection
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.downloader.DivPatchCache
import com.yandex.div.core.state.DivPathUtils.compactPathList
import com.yandex.div.core.state.DivPathUtils.findDivState
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.state.GalleryState
import com.yandex.div.core.state.UpdateStateScrollListener
import com.yandex.div.core.util.expressionSubscriber
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.animations.DivComparator
import com.yandex.div.core.view2.divs.DivBaseBinder
import com.yandex.div.core.view2.divs.DivPatchableAdapter
import com.yandex.div.core.view2.divs.ReleasingViewPool
import com.yandex.div.core.view2.divs.dpToPx
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div.core.view2.divs.widgets.DivSnappyRecyclerView
import com.yandex.div.core.view2.divs.widgets.DivStateLayout
import com.yandex.div.core.view2.divs.widgets.DivViewVisitor
import com.yandex.div.core.view2.divs.widgets.ParentScrollRestrictor
import com.yandex.div.core.view2.divs.widgets.ReleaseUtils.releaseAndRemoveChildren
import com.yandex.div.core.view2.divs.widgets.visitViewTree
import com.yandex.div.core.widget.ViewWrapper
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.util.dpToPx
import com.yandex.div.view.OnInterceptTouchEventListenerHost
import com.yandex.div.view.PaddingItemDecoration
import com.yandex.div.view.SnappyRecyclerView
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
) : DivViewBinder<DivGallery, RecyclerView> {

    @SuppressLint("ClickableViewAccessibility")
    override fun bindView(view: RecyclerView, div: DivGallery, divView: Div2View, path: DivStatePath) {
        val oldDiv = (view as? DivRecyclerView)?.div ?: (view as? DivSnappyRecyclerView)?.div
        if (div == oldDiv) {
            val adapter = view.adapter as GalleryAdapter
            adapter.applyPatch(divPatchCache)
            bindStates(view, div.items, divView)
            return
        }

        if (oldDiv != null) baseBinder.unbindExtensions(view, oldDiv, divView)
        val expressionSubscriber = view.expressionSubscriber
        expressionSubscriber.closeAllSubscription()

        baseBinder.bindView(view, div, oldDiv, divView)

        val resolver = divView.expressionResolver


        val reusableObserver = { _: Any ->
            updateDecorations(view, div, divView, resolver)
        }
        expressionSubscriber.addSubscription(div.orientation.observe(resolver, reusableObserver))
        expressionSubscriber.addSubscription(div.itemSpacing.observe(resolver, reusableObserver))
        expressionSubscriber.addSubscription(div.restrictParentScroll.observe(resolver, reusableObserver))
        div.columnCount?.let {
            expressionSubscriber.addSubscription(it.observe(resolver, reusableObserver))
        }

        view.setRecycledViewPool(ReleasingViewPool(divView.releaseViewVisitor))
        view.setScrollingTouchSlop(RecyclerView.TOUCH_SLOP_PAGING)
        view.clipToPadding = false

        view.overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val itemStateBinder =
            { itemView: View, div: Div -> bindStates(itemView, listOf(div), divView) }
        view.adapter =
            GalleryAdapter(div.items, divView, divBinder.get(), viewCreator, itemStateBinder, path)
        if (view is DivRecyclerView) {
            view.div = div
        } else if (view is DivSnappyRecyclerView) {
            view.div = div
        }

        updateDecorations(view, div, divView, resolver)
    }

    /**
     * Binds all descendants of [view] which are [DivStateLayout]s corresponding to DivStates in [divs]
     */
    private fun bindStates(
        view: View,
        divs: List<Div>,
        divView: Div2View
    ) {
        val viewsToBind = mutableListOf<DivStateLayout>()
        val divStateVisitor = object : DivViewVisitor() {
            override fun visit(view: DivStateLayout) {
                viewsToBind.add(view)
            }
        }
        divStateVisitor.visitViewTree(view)

        val viewsByPath = mutableMapOf<DivStatePath, MutableList<DivStateLayout>>()
        viewsToBind.forEach { viewToBind ->
            viewToBind.path?.let { path ->
                viewsByPath.getOrPut(path) { mutableListOf() } += viewToBind
            }
        }
        val paths = viewsToBind.mapNotNull { it.path }
        val compactPaths = compactPathList(paths)

        compactPaths.forEach { path ->
            val divByPath = divs.firstNotNullOfOrNull { item ->
                item.findDivState(path)
            }
            val views = viewsByPath[path]

            if (divByPath != null && views != null) {
                val binder = divBinder.get()
                val parentState = path.parentState()
                views.forEach { view ->
                    binder.bind(view, divByPath, divView, parentState)
                }
            }
        }
    }

    private fun updateDecorations(
        view: RecyclerView,
        div: DivGallery,
        divView: Div2View,
        resolver: ExpressionResolver,
    ) {
        val metrics = view.resources.displayMetrics
        val divOrientation = div.orientation.evaluate(resolver)
        val orientation = if (divOrientation == DivGallery.Orientation.HORIZONTAL) {
            RecyclerView.HORIZONTAL
        } else {
            RecyclerView.VERTICAL
        }

        if (view is DivSnappyRecyclerView) {
            view.orientation = orientation
        }

        val columnCount = div.columnCount?.evaluate(resolver) ?: 1

        view.clipChildren = false
        view.setItemDecoration(
            if (columnCount == 1)
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

        if (view is SnappyRecyclerView) {
            view.itemSpacing = dpToPx(div.itemSpacing.evaluate(resolver))
        }

        val layoutManager = if (columnCount == 1) {
            DivLinearLayoutManager(divView, view, div, orientation)
        } else {
            DivGridLayoutManager(divView, view, div, orientation)
        }
        view.layoutManager = layoutManager
        view.clearOnScrollListeners()
        divView.currentState?.let { state ->
            val id = div.id ?: div.hashCode().toString()
            val galleryState = state.getBlockState(id) as GalleryState?
            val position = galleryState?.visibleItemIndex
                ?: div.defaultItem.evaluate(resolver)
            val offset = galleryState?.scrollOffset
            view.scrollToPositionInternal(position, offset)
            view.addOnScrollListener(UpdateStateScrollListener(id, state, layoutManager))
        }
        view.addOnScrollListener(ScrollListener(divView, view, layoutManager, div))
        if (view is OnInterceptTouchEventListenerHost) {
            view.onInterceptTouchEventListener =
                if (div.restrictParentScroll.evaluate(resolver)) {
                    ParentScrollRestrictor(
                        divOrientation.toRestrictorDirection()
                    )
                } else {
                    null
                }
        }
    }

    private fun RecyclerView.scrollToPositionInternal(position: Int, offset: Int? = null) {
        val layoutManager = layoutManager as? DivGalleryItemHelper
        when {
            offset == null && position == 0 -> {
                // Show left or top padding on first position without any snapping
                layoutManager?.instantScrollToPosition(position)
            }
            offset != null -> layoutManager?.instantScrollToPositionWithOffset(position, offset)
            else -> {
                // Call on RecyclerView itself for proper snapping.
                layoutManager?.instantScrollToPosition(position)
            }
        }
    }

    private fun RecyclerView.setItemDecoration(decoration: RecyclerView.ItemDecoration) {
        removeItemDecorations()
        addItemDecoration(decoration)
    }

    private fun RecyclerView.removeItemDecorations() {
        for (i in itemDecorationCount - 1 downTo 0) {
            removeItemDecorationAt(i)
        }
    }

    private class ScrollListener(
        private val divView: Div2View,
        private val recycler: RecyclerView,
        private val galleryItemHelper: DivGalleryItemHelper,
        private val galleryDiv: DivGallery,
    ) : RecyclerView.OnScrollListener() {

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
                trackVisibleViews()
            }
        }

        private fun trackVisibleViews() {
            recycler.children.forEach { child ->
                val position = recycler.getChildAdapterPosition(child)
                val div = (recycler.adapter as GalleryAdapter).items[position]
                val visibilityActionTracker = divView.div2Component.visibilityActionTracker
                visibilityActionTracker.trackVisibilityActionsOf(divView, child, div)
            }
        }
    }

    internal class GalleryAdapter(
        divs: List<Div>,
        private val div2View: Div2View,
        private val divBinder: DivBinder,
        private val viewCreator: DivViewCreator,
        private val itemStateBinder: (itemView: View, div: Div) -> Unit,
        private val path: DivStatePath
    ) : DivPatchableAdapter<GalleryViewHolder>(divs, div2View)  {

        private val ids = WeakHashMap<Div, Long>()
        private var lastItemId = 0L

        init {
            setHasStableIds(true)
        }

        override fun onViewAttachedToWindow(holder: GalleryViewHolder) {
            super.onViewAttachedToWindow(holder)
            holder.oldDiv?.let { div ->
                itemStateBinder.invoke(holder.rootView, div)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
            val view = ViewWrapper(div2View.context)
            return GalleryViewHolder(view, divBinder, viewCreator)
        }

        override fun getItemId(position: Int): Long {
            val item = items[position]
            return ids[item] ?: (lastItemId++).also { ids[item] = it }
        }

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
            val div = items[position]
            holder.rootView.setTag(R.id.div_gallery_item_index, position)
            holder.bind(div2View, div, path)
        }

        override fun onFailedToRecycleView(holder: GalleryViewHolder): Boolean {
            val shouldRecycle = super.onFailedToRecycleView(holder)
            if (!shouldRecycle) {
                holder.rootView.releaseAndRemoveChildren(div2View)
            }
            return shouldRecycle
        }

    }

    internal class GalleryViewHolder(
        val rootView: ViewWrapper,
        private val divBinder: DivBinder,
        private val viewCreator: DivViewCreator
    ) : RecyclerView.ViewHolder(rootView) {

        var oldDiv: Div? = null

        fun bind(div2View: Div2View, div: Div, path: DivStatePath) {
            val resolver = div2View.expressionResolver
            val divView = if (oldDiv != null && DivComparator.areDivsReplaceable(oldDiv, div, resolver)) {
                rootView.child!!
            } else {
                val newDivView = viewCreator.create(div, resolver)
                rootView.releaseAndRemoveChildren(div2View)
                rootView.addView(newDivView)
                newDivView
            }

            oldDiv = div
            divBinder.bind(divView, div, div2View, path)
        }
    }

    @ParentScrollRestrictor.Direction
    private fun DivGallery.Orientation.toRestrictorDirection(): Int {
        return when (this) {
            DivGallery.Orientation.HORIZONTAL -> ParentScrollRestrictor.DIRECTION_HORIZONTAL
            DivGallery.Orientation.VERTICAL -> ParentScrollRestrictor.DIRECTION_VERTICAL
        }
    }
}
