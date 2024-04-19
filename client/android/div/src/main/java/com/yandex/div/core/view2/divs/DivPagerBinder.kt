package com.yandex.div.core.view2.divs

import android.content.Context
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.doOnPreDraw
import androidx.core.view.get
import androidx.core.view.isNotEmpty
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.R
import com.yandex.div.core.Disposable
import com.yandex.div.core.ScrollDirection
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.downloader.DivPatchCache
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.state.PagerState
import com.yandex.div.core.state.UpdateStateChangePageCallback
import com.yandex.div.core.util.AccessibilityStateProvider
import com.yandex.div.core.util.doOnActualLayout
import com.yandex.div.core.util.doOnEveryDetach
import com.yandex.div.core.util.isLayoutRtl
import com.yandex.div.core.util.makeFocusable
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.animations.DivComparator
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.core.view2.divs.widgets.ParentScrollRestrictor
import com.yandex.div.core.view2.divs.widgets.ReleaseUtils.releaseAndRemoveChildren
import com.yandex.div.core.view2.reuse.util.tryRebindRecycleContainerChildren
import com.yandex.div.core.widget.makeUnspecifiedSpec
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.core.nonNullItems
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div.internal.widget.FrameContainerLayout
import com.yandex.div.internal.widget.PageItemDecoration
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivPager
import com.yandex.div2.DivPagerLayoutMode
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.sign

private const val OFFSET_TO_REAL_ITEM = 2

@DivScope
internal class DivPagerBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val viewCreator: DivViewCreator,
    private val divBinder: Provider<DivBinder>,
    private val divPatchCache: DivPatchCache,
    private val divActionBinder: DivActionBinder,
    private val pagerIndicatorConnector: PagerIndicatorConnector,
    private val accessibilityStateProvider: AccessibilityStateProvider,
) : DivViewBinder<DivPager, DivPagerView> {

    override fun bindView(
        view: DivPagerView,
        div: DivPager,
        divView: Div2View,
        path: DivStatePath
    ) {
        div.id?.let {
            pagerIndicatorConnector.submitPager(it, view)
        }
        val resolver = divView.expressionResolver
        val oldDiv = view.div
        if (div === oldDiv) {
            val adapter = view.viewPager.adapter as PagerAdapter
            if (!adapter.applyPatch(view.getRecyclerView(), divPatchCache, divView)) {
                adapter.notifyItemRangeChanged(0, adapter.itemCount)
            }
            return
        }

        baseBinder.bindView(view, div, oldDiv, divView)

        val pageTranslations = SparseArray<Float>()
        val a11yEnabled = accessibilityStateProvider.isAccessibilityEnabled(view.context)
        view.setRecycledViewPool(ReleasingViewPool(divView.releaseViewVisitor))
        var divItems = div.nonNullItems
        if (div.infiniteScroll.evaluate(resolver)) {
            val firstItem = divItems.first()
            val secondItem = divItems.getOrNull(OFFSET_TO_REAL_ITEM - 1)
            val lastItem = divItems.last()
            val preLastItem = divItems.getOrNull(divItems.size - OFFSET_TO_REAL_ITEM)
            val newDivItems = ArrayList<Div>(divItems.size + 4).apply {
                add(preLastItem ?: lastItem)
                add(lastItem)
                addAll(divItems)
                add(firstItem)
                add(secondItem ?: firstItem)
            }
            divItems = newDivItems
        }
        view.viewPager.adapter = PagerAdapter(
            divs = divItems,
            div2View = divView,
            divBinder = divBinder.get(),
            translationBinder = { holder: PagerViewHolder, position: Int ->
                pageTranslations[position]?.let {
                    if (div.orientation.evaluate(resolver) == DivPager.Orientation.HORIZONTAL) {
                        holder.itemView.translationX = it
                    } else {
                        holder.itemView.translationY = it
                    }
                }
            },
            viewCreator = viewCreator,
            path = path,
            accessibilityEnabled = a11yEnabled,
        )

        val reusableObserver = { _: Any ->
            applyDecorations(view, div, resolver)
            updatePageTransformer(view, div, resolver, pageTranslations)
        }

        view.addSubscription(div.paddings?.left?.observe(resolver, reusableObserver))
        view.addSubscription(div.paddings?.right?.observe(resolver, reusableObserver))
        view.addSubscription(div.paddings?.top?.observe(resolver, reusableObserver))
        view.addSubscription(div.paddings?.bottom?.observe(resolver, reusableObserver))
        view.addSubscription(div.itemSpacing.value.observe(resolver, reusableObserver))
        view.addSubscription(div.itemSpacing.unit.observe(resolver, reusableObserver))

        when (val mode = div.layoutMode) {
            is DivPagerLayoutMode.NeighbourPageSize -> {
                view.addSubscription(mode.value.neighbourPageWidth.value.observe(resolver, reusableObserver))
                view.addSubscription(mode.value.neighbourPageWidth.unit.observe(resolver, reusableObserver))
            }
            is DivPagerLayoutMode.PageSize -> {
                view.addSubscription(mode.value.pageWidth.value.observe(resolver, reusableObserver))
                view.addSubscription(observeWidthChange(view.viewPager, reusableObserver))
            }
        }.apply { /*exhaustive*/ }

        view.addSubscription(div.orientation.observeAndGet(resolver) {
            view.orientation = if (it == DivPager.Orientation.HORIZONTAL) {
                ViewPager2.ORIENTATION_HORIZONTAL
            } else {
                ViewPager2.ORIENTATION_VERTICAL
            }
            (view.viewPager.adapter as PagerAdapter).orientation = view.orientation

            updatePageTransformer(view, div, resolver, pageTranslations)
            applyDecorations(view, div, resolver)
        })

        view.pagerSelectedActionsDispatcher = PagerSelectedActionsDispatcher(
            divView = divView,
            div = div,
            divs = divItems,
            divActionBinder = divActionBinder,
        )

        view.changePageCallbackForLogger = PageChangeCallback(
            divView = divView,
            divPager = div,
            divs = divItems,
            recyclerView = view.viewPager.getChildAt(0) as RecyclerView
        )

        divView.currentState?.let { state ->
            val id = div.id ?: div.hashCode().toString()
            val pagerState = state.getBlockState(id) as PagerState?
            view.changePageCallbackForState = UpdateStateChangePageCallback(id, state)
            val correctPosition = if (div.infiniteScroll.evaluate(resolver)) OFFSET_TO_REAL_ITEM else 0
            view.currentItem = (pagerState?.currentPageIndex ?: div.defaultItem.evaluate(resolver)
                .toIntSafely()) + correctPosition
        }

        view.addSubscription(div.restrictParentScroll.observeAndGet(resolver) { restrictParentScroll ->
            view.onInterceptTouchEventListener = if (restrictParentScroll) {
                ParentScrollRestrictor(ParentScrollRestrictor.DIRECTION_HORIZONTAL)
            } else {
                null
            }
        })
        if (div.infiniteScroll.evaluate(resolver)) {
            setInfiniteScroll(view)
        }
        if (a11yEnabled) {
            view.enableAccessibility()
        }
    }

    private fun setInfiniteScroll(view: DivPagerView) {
        val recyclerView = view.viewPager.getChildAt(0) as RecyclerView
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val itemCount = view.viewPager.adapter?.itemCount ?: 0
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstItemVisible = layoutManager.findFirstVisibleItemPosition()
                val lastItemVisible = layoutManager.findLastVisibleItemPosition()
                if (firstItemVisible == (itemCount - OFFSET_TO_REAL_ITEM) && dx > 0) {
                    recyclerView.scrollToPosition(OFFSET_TO_REAL_ITEM)
                } else if (lastItemVisible == OFFSET_TO_REAL_ITEM - 1 && dx < 0) {
                    recyclerView.scrollToPosition(itemCount - 1 - OFFSET_TO_REAL_ITEM)
                }
            }
        })
    }

    private fun updatePageTransformer(
        view: DivPagerView,
        div: DivPager,
        resolver: ExpressionResolver,
        pageTranslations: SparseArray<Float>
    ) {
        val metrics = view.resources.displayMetrics
        val orientation = div.orientation.evaluate(resolver)
        val itemSpacing = div.itemSpacing.toPxF(metrics, resolver)
        val startPadding = evaluateStartPadding(view, div, resolver)
        val endPadding = evaluateEndPadding(view, div, resolver)

        view.viewPager.setPageTransformer { page, position ->
            val viewPager = page.parent.parent as ViewPager2
            val recyclerView = viewPager.getChildAt(0) as RecyclerView

            val pagePosition = recyclerView.layoutManager?.getPosition(page) ?: return@setPageTransformer
            val widthOfThisItemAsNeighbour = div.evaluateNeighbourItemWidth(view, resolver,
                pagePosition - sign(position).toInt(), startPadding, endPadding)
            val neighbourItemWidth = div.evaluateNeighbourItemWidth(view, resolver,
                pagePosition, startPadding, endPadding)

            var offset = -position * (widthOfThisItemAsNeighbour + neighbourItemWidth + itemSpacing)
            if (view.isLayoutRtl() && orientation == DivPager.Orientation.HORIZONTAL) {
                offset = -offset
            }
            pageTranslations.put(pagePosition, offset)
            if (orientation == DivPager.Orientation.HORIZONTAL) {
                page.translationX = offset
            } else {
                page.translationY = offset
            }
        }
    }

    private fun evaluateStartPadding(
        view: DivPagerView,
        div: DivPager,
        resolver: ExpressionResolver,
    ): Float {
        val metrics = view.resources.displayMetrics
        val orientation = div.orientation.evaluate(resolver)
        val paddings = div.paddings

        return if (paddings == null) {
            0.0f
        } else if (orientation == DivPager.Orientation.HORIZONTAL) {
            if (paddings.start != null) {
                paddings.start?.evaluate(resolver).dpToPxF(metrics)
            } else {
                if (view.isLayoutRtl()) {
                    paddings.right.evaluate(resolver).dpToPxF(metrics)
                } else {
                    paddings.left.evaluate(resolver).dpToPxF(metrics)
                }
            }
        } else {
            paddings.top.evaluate(resolver).dpToPxF(metrics)
        }
    }

    private fun evaluateEndPadding(
        view: DivPagerView,
        div: DivPager,
        resolver: ExpressionResolver,
    ): Float {
        val metrics = view.resources.displayMetrics
        val orientation = div.orientation.evaluate(resolver)
        val paddings = div.paddings

        return if (paddings == null) {
            0.0f
        } else if (orientation == DivPager.Orientation.HORIZONTAL) {
            if (paddings.end != null) {
                paddings.end?.evaluate(resolver).dpToPxF(metrics)
            } else {
                if (view.isLayoutRtl()) {
                    paddings.left.evaluate(resolver).dpToPxF(metrics)
                } else {
                    paddings.right.evaluate(resolver).dpToPxF(metrics)
                }
            }
        } else {
            paddings.bottom.evaluate(resolver).dpToPxF(metrics)
        }
    }

    private fun applyDecorations(
        view: DivPagerView,
        div: DivPager,
        resolver: ExpressionResolver,
    ) {
        val metrics = view.resources.displayMetrics
        val isHorizontal = div.orientation.evaluate(resolver) == DivPager.Orientation.HORIZONTAL
        view.viewPager.setItemDecoration(
            PageItemDecoration(
                layoutMode = div.layoutMode,
                metrics = metrics,
                resolver = resolver,
                paddingLeft = evaluateLeftPadding(view, div, resolver),
                paddingRight = evaluateRightPadding(view, div, resolver),
                paddingTop = div.paddings?.top?.evaluate(resolver).dpToPxF(metrics),
                paddingBottom = div.paddings?.bottom?.evaluate(resolver).dpToPxF(metrics),
                parentSize = if (isHorizontal) view.viewPager.width else view.viewPager.height,
                itemSpacing = div.itemSpacing.toPxF(metrics, resolver),
                orientation = if (isHorizontal) RecyclerView.HORIZONTAL else RecyclerView.VERTICAL,
                isLayoutRtl = { view.isLayoutRtl() },
            )
        )

        val neighbourItemIsShown = when (val mode = div.layoutMode) {
            is DivPagerLayoutMode.PageSize -> mode.value.pageWidth.value.evaluate(resolver) < 100
            is DivPagerLayoutMode.NeighbourPageSize -> mode.value.neighbourPageWidth.value.evaluate(resolver) > 0
        }
        if (neighbourItemIsShown && view.viewPager.offscreenPageLimit != 1) {
            view.viewPager.offscreenPageLimit = 1
        }
    }

    private fun evaluateRightPadding (
        view: DivPagerView,
        div: DivPager,
        resolver: ExpressionResolver,
    ): Float {
        val metrics = view.resources.displayMetrics
        val orientation = div.orientation.evaluate(resolver)
        val isLayoutRtl = view.isLayoutRtl()
        val paddings = div.paddings

        return when {
            paddings == null -> 0.0f
            orientation == DivPager.Orientation.HORIZONTAL && isLayoutRtl && paddings.start != null ->
                paddings.start?.evaluate(resolver).dpToPxF(metrics)
            orientation == DivPager.Orientation.HORIZONTAL && !isLayoutRtl && paddings.end != null ->
                paddings.end?.evaluate(resolver).dpToPxF(metrics)
            else -> paddings.right.evaluate(resolver).dpToPxF(metrics)
        }
    }

    private fun evaluateLeftPadding (
        view: DivPagerView,
        div: DivPager,
        resolver: ExpressionResolver,
    ): Float {
        val metrics = view.resources.displayMetrics
        val orientation = div.orientation.evaluate(resolver)
        val isLayoutRtl = view.isLayoutRtl()
        val paddings = div.paddings

        return when {
            paddings == null -> 0.0f
            orientation == DivPager.Orientation.HORIZONTAL && isLayoutRtl && paddings.end != null ->
                paddings.end?.evaluate(resolver).dpToPxF(metrics)
            orientation == DivPager.Orientation.HORIZONTAL && !isLayoutRtl && paddings.start != null ->
                paddings.start?.evaluate(resolver).dpToPxF(metrics)
            else -> paddings.left.evaluate(resolver).dpToPxF(metrics)
        }
    }

    private fun observeWidthChange(view: View, observer: (_: Any) -> Unit) = object : Disposable, View.OnLayoutChangeListener {
        var oldWidth = view.width

        init {
            view.addOnLayoutChangeListener(this)
            // First onLayoutChange triggered too late.
            // Used for set layout paddings before user actions
            view.doOnPreDraw {
                observer.invoke(view.width)
            }
        }

        override fun close() {
            view.removeOnLayoutChangeListener(this)
        }

        override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int,
                                    oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
            val newWidth = v.width
            if (oldWidth == newWidth) {
                return
            }
            oldWidth = newWidth
            observer.invoke(newWidth)
        }
    }

    private fun DivPager.evaluateNeighbourItemWidth(
        view: DivPagerView,
        resolver: ExpressionResolver,
        position: Int,
        paddingStart: Float,
        paddingEnd: Float
    ): Float {
        val metrics = view.resources.displayMetrics
        val mode = layoutMode
        val itemSpacing = itemSpacing.toPxF(metrics, resolver)
        val lastPosition = (view.viewPager[0] as RecyclerView).adapter!!.itemCount - 1
        if (mode is DivPagerLayoutMode.NeighbourPageSize) {
            val fixedWidth = mode.value.neighbourPageWidth.toPxF(metrics, resolver)
            val offsetsWidth = 2 * fixedWidth + itemSpacing
            return when (position) {
                0 -> offsetsWidth - paddingStart
                lastPosition -> offsetsWidth - paddingEnd
                else -> fixedWidth
            }.coerceAtLeast(0f)
        }

        val parentSize = if (this.orientation.evaluate(resolver) == DivPager.Orientation.HORIZONTAL) {
            view.viewPager.width
        } else {
            view.viewPager.height
        }
        val pageWidthPercent = (mode as DivPagerLayoutMode.PageSize).value.pageWidth.value.evaluate(resolver).toInt()
        val neighbourItemsPart = 1 - pageWidthPercent / 100f
        val getSideNeighbourPageSize = { padding: Float -> (parentSize - padding) * neighbourItemsPart - itemSpacing }
        return when (position) {
            0 -> getSideNeighbourPageSize(paddingStart)
            lastPosition -> getSideNeighbourPageSize(paddingEnd)
            else -> parentSize * neighbourItemsPart / 2
        }
    }

    class PageChangeCallback(
        private val divPager: DivPager,
        private val divs: List<Div>,
        private val divView: Div2View,
        private val recyclerView: RecyclerView
    ) : ViewPager2.OnPageChangeCallback() {
        private var prevPosition = RecyclerView.NO_POSITION

        private val minimumSignificantDx = divView.config.logCardScrollSignificantThreshold
        private var totalDelta = 0

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)

            when (state) {
                ViewPager2.SCROLL_STATE_IDLE -> trackVisibleViews()
            }
        }

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            trackVisibleViews()
            if (position == prevPosition) {
                return
            }
            if (prevPosition != RecyclerView.NO_POSITION) {
                divView.unbindViewFromDiv(recyclerView)
                val direction =
                    if (position > prevPosition) ScrollDirection.NEXT else ScrollDirection.BACK
                divView.div2Component.div2Logger.logPagerChangePage(
                    divView,
                    divPager,
                    position,
                    direction
                )
            }
            val selectedPage = divs[position]
            if (selectedPage.value().hasSightActions) {
                divView.bindViewToDiv(recyclerView, selectedPage)
            }
            prevPosition = position
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            val minimumDelta = if (minimumSignificantDx > 0) {
                minimumSignificantDx
            } else {
                val width = recyclerView.layoutManager?.width ?: 0
                width / 20
            }
            totalDelta += positionOffsetPixels
            if (totalDelta > minimumDelta) {
                totalDelta = 0
                trackVisibleViews()
            }
        }

        private fun trackVisibleViews() {
            if (recyclerView.children.count() > 0) {
                trackVisibleChildren()
            } else {
                recyclerView.doOnActualLayout { trackVisibleChildren() }
            }
        }

        private fun trackVisibleChildren() {
            recyclerView.children.forEach { child ->
                val childPosition = recyclerView.getChildAdapterPosition(child)
                if (childPosition == RecyclerView.NO_POSITION) {
                    KAssert.fail { "Requesting child position during layout" }
                    return
                }
                val childDiv = divs[childPosition]

                divView.div2Component.visibilityActionTracker.startTrackingViewsHierarchy(divView, child, childDiv)
            }
        }
    }

    private class PagerAdapter(
        divs: List<Div>,
        private val div2View: Div2View,
        private val divBinder: DivBinder,
        private val translationBinder: (holder: PagerViewHolder, position: Int) -> Unit,
        private val viewCreator: DivViewCreator,
        private val path: DivStatePath,
        private val accessibilityEnabled: Boolean,
    ) : DivPatchableAdapter<PagerViewHolder>(divs, div2View) {

        override val subscriptions = mutableListOf<Disposable>()

        var orientation = ViewPager2.ORIENTATION_HORIZONTAL

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
            val view = PageLayout(div2View.context) { orientation }
            view.layoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

            return PagerViewHolder(div2View, view, divBinder, viewCreator, accessibilityEnabled)
        }

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
            val div = items[position]
            holder.bind(div2View, div, path, position)
            translationBinder.invoke(holder, position)
        }

    }

    private class PageLayout(
        context: Context,
        private val orientationProvider: () -> Int
    ) : FrameContainerLayout(context) {

        init {
            makeFocusable()
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            // When the page cross size is equal to MATCH_PARENT or WRAP_CONTENT_CONSTRAINED,
            // the page must be the size of a pager minus padding,
            // otherwise the page size should be enough for its own content regardless of the current pager height
            if (childCount == 0) return super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            val lp = getChildAt(0).layoutParams
            val isHorizontal = orientationProvider() == ViewPager2.ORIENTATION_HORIZONTAL
            val widthSpec = getSpec(lp.width, widthMeasureSpec, isHorizontal)
            val heightSpec = getSpec(lp.height, heightMeasureSpec, !isHorizontal)
            super.onMeasure(widthSpec, heightSpec)
        }

        private fun getSpec(size: Int, parentSpec: Int, alongScrollAxis: Boolean) = if (alongScrollAxis) {
            parentSpec
        } else {
            when (size) {
                LayoutParams.MATCH_PARENT, DivLayoutParams.WRAP_CONTENT_CONSTRAINED -> parentSpec
                else -> makeUnspecifiedSpec()
            }
        }
    }

    private class PagerViewHolder(
        divView: Div2View,
        val frameLayout: PageLayout,
        private val divBinder: DivBinder,
        private val viewCreator: DivViewCreator,
        private val accessibilityEnabled: Boolean,
    ) : RecyclerView.ViewHolder(frameLayout) {

        init {
            itemView.doOnEveryDetach { view ->
                val div = oldDiv ?: return@doOnEveryDetach
                divView.div2Component.visibilityActionTracker.startTrackingViewsHierarchy(divView, view, div)
            }
        }

        private var oldDiv: Div? = null

        fun bind(div2View: Div2View, div: Div, path: DivStatePath, position: Int) {
            val resolver = div2View.expressionResolver

            if (frameLayout.tryRebindRecycleContainerChildren(div2View, div)) {
                oldDiv = div
                return
            }

            val divView = if (oldDiv != null
                    && frameLayout.isNotEmpty()
                    && DivComparator.areDivsReplaceable(oldDiv, div, div2View.oldExpressionResolver, resolver)) {
                frameLayout[0]
            } else {
                val newDivView = viewCreator.create(div, resolver)
                frameLayout.releaseAndRemoveChildren(div2View)
                frameLayout.addView(newDivView)
                newDivView
            }

            if (accessibilityEnabled) {
                frameLayout.setTag(R.id.div_pager_item_clip_id, position)
            }
            oldDiv = div
            divBinder.bind(divView, div, div2View, path)
        }

        private fun getPagerView(): DivPagerView {
            return ((frameLayout.parent as ViewGroup).parent as ViewGroup).parent as DivPagerView
        }
    }

    private fun ViewPager2.setItemDecoration(decoration: RecyclerView.ItemDecoration) {
        for (i in 0 until itemDecorationCount) {
            removeItemDecorationAt(i)
        }
        addItemDecoration(decoration)
    }
}
