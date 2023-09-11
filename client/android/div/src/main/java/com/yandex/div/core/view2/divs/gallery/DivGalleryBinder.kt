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
import com.yandex.div.core.state.DivPathUtils.compactPathList
import com.yandex.div.core.state.DivPathUtils.findDivState
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.state.GalleryState
import com.yandex.div.core.state.UpdateStateScrollListener
import com.yandex.div.core.util.expressionSubscriber
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.animations.DivComparator
import com.yandex.div.core.view2.divs.DivBaseBinder
import com.yandex.div.core.view2.divs.DivPatchableAdapter
import com.yandex.div.core.view2.divs.PagerSnapStartHelper
import com.yandex.div.core.view2.divs.ReleasingViewPool
import com.yandex.div.core.view2.divs.dpToPx
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div.core.view2.divs.widgets.DivStateLayout
import com.yandex.div.core.view2.divs.widgets.DivViewVisitor
import com.yandex.div.core.view2.divs.widgets.ParentScrollRestrictor
import com.yandex.div.core.view2.divs.widgets.ReleaseUtils.releaseAndRemoveChildren
import com.yandex.div.core.view2.divs.widgets.visitViewTree
import com.yandex.div.core.widget.DivViewWrapper
import com.yandex.div.internal.widget.PaddingItemDecoration
import com.yandex.div.json.expressions.ExpressionResolver
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
    override fun bindView(view: DivRecyclerView, div: DivGallery, divView: Div2View, path: DivStatePath) {
        val oldDiv = (view as? DivRecyclerView)?.div
        if (div == oldDiv) {
            val adapter = view.adapter as GalleryAdapter
            adapter.applyPatch(divPatchCache, divView)
            adapter.closeAllSubscription()
            adapter.subscribeOnElements()
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
        expressionSubscriber.addSubscription(div.scrollMode.observe(resolver, reusableObserver))
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
        view.div = div

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
        view: DivRecyclerView,
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

        val scrollMode = div.scrollMode.evaluate(resolver)
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

        val layoutManager = if (columnCount == 1L) {
            DivLinearLayoutManager(divView, view, div, orientation)
        } else {
            DivGridLayoutManager(divView, view, div, orientation)
        }
        view.layoutManager = layoutManager
        view.scrollInterceptionAngle = scrollInterceptionAngle
        view.clearOnScrollListeners()
        divView.currentState?.let { state ->
            val id = div.id ?: div.hashCode().toString()
            val galleryState = state.getBlockState(id) as GalleryState?
            val position = galleryState?.visibleItemIndex
                ?: div.defaultItem.evaluate(resolver).toIntSafely()
            val offset = galleryState?.scrollOffset
            view.scrollToPositionInternal(position, offset, scrollMode.toScrollPosition())
            view.addOnScrollListener(UpdateStateScrollListener(id, state, layoutManager))
        }
        view.addOnScrollListener(ScrollListener(divView, view, layoutManager, div))
        view.onInterceptTouchEventListener = if (div.restrictParentScroll.evaluate(resolver)) {
            ParentScrollRestrictor(
                divOrientation.toRestrictorDirection()
            )
        } else {
            null
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
        private val divView: Div2View,
        private val recycler: DivRecyclerView,
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
                visibilityActionTracker.trackVisibilityActionsOf(divView, child, div)
            }

            // Find and track recycled views containing DisappearActions that are waiting for disappear
            with(visibilityActionTracker) {
                getActionsWaitingForDisappear().filter { it.key !in recycler.children }.forEach { (view, div) ->
                    trackDetachedView(divView, view, div)
                }
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
    ) : DivPatchableAdapter<GalleryViewHolder>(divs, div2View) {

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
            val view = DivViewWrapper(div2View.context)
            return GalleryViewHolder(view, divBinder, viewCreator)
        }

        override fun getItemId(position: Int): Long {
            val item = activeItems[position]
            return ids[item] ?: (lastItemId++).also { ids[item] = it }
        }

        override fun getItemCount() = activeItems.size

        override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
            holder.bind(div2View, activeItems[position], path)
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

        fun bind(div2View: Div2View, div: Div, path: DivStatePath) {
            val resolver = div2View.expressionResolver
            val divView = if (oldDiv != null
                    && rootView.child != null
                    && DivComparator.areDivsReplaceable(oldDiv, div, resolver)) {
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
