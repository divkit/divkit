package com.yandex.div.core.view2.divs.pager

import android.util.SparseArray
import android.view.View
import androidx.core.view.doOnNextLayout
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.core.Disposable
import com.yandex.div.core.DivActionPerformer
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.state.PagerState
import com.yandex.div.core.state.UpdateStateChangePageCallback
import com.yandex.div.core.util.AccessibilityStateProvider
import com.yandex.div.core.util.isLayoutRtl
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.divs.DivBaseBinder
import com.yandex.div.core.view2.divs.ReleasingViewPool
import com.yandex.div.core.view2.divs.bindItemBuilder
import com.yandex.div.core.view2.divs.bindStates
import com.yandex.div.core.view2.divs.toPxF
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.core.view2.divs.widgets.ParentScrollRestrictor
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.core.VariableMutationHandler
import com.yandex.div.internal.core.build
import com.yandex.div.internal.core.buildItems
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivPager
import com.yandex.div2.DivPagerLayoutMode
import javax.inject.Inject
import javax.inject.Provider

@DivScope
internal class DivPagerBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val viewCreator: DivViewCreator,
    private val divBinder: Provider<DivBinder>,
    private val actionPerformer: DivActionPerformer,
    private val pagerIndicatorConnector: PagerIndicatorConnector,
    private val accessibilityStateProvider: AccessibilityStateProvider,
    private val variableMutationHandler: VariableMutationHandler,
) : DivViewBinder<DivBlock.Pager, DivPagerView>(baseBinder) {

    override fun bindView(view: DivPagerView, divBlock: DivBlock.Pager, divView: Div2View) {
        pagerIndicatorConnector.submitPager(view, divBlock.divValue)

        val oldDivBlock = view.divBlock
        if (divBlock.div === oldDivBlock?.div) {
            val pager = view.viewPager
            val adapter = pager.adapter as? DivPagerAdapter ?: return
            adapter.setItems(divBlock.buildItems())
            view.getRecyclerView()?.scrollToPosition(adapter.normalizeItemPosition(view.currentItem))

            view.notifyItemsUpdated(divBlock.divValue, divBlock.expressionResolver, adapter, divView.errorCollector)
            view.bindStates(divBinder.get(), divView)
            pager.doOnNextLayout { pager.requestTransform() }
            return
        }

        oldDivBlock?.let {
            view.changePageCallbackForOffScreenPages = null
            view.viewPager.removeItemDecorations()
            view.pageTransformer = null
        }

        baseBinder.bindView(view, divBlock, oldDivBlock, divView)
        view.bind(divBlock, divView)
    }

    private fun DivPagerView.bind(divBlock: DivBlock.Pager, divView: Div2View) {
        val recyclerView = getRecyclerView() ?: return

        val div = divBlock.divValue
        val resolver = divBlock.expressionResolver
        val pageTranslations = SparseArray<Float>()
        val a11yEnabled = accessibilityStateProvider.isAccessibilityEnabled(context)
        setRecycledViewPool(ReleasingViewPool(divView.viewComponent.releaseViewVisitor))
        val adapter =
            DivPagerAdapter(divBlock.buildItems(), divView, divBinder.get(), pageTranslations, viewCreator, this)
        viewPager.adapter = adapter
        val errorCollector = divView.errorCollector
        adapter.registerAdapterDataObserver(ItemCountObserver(this, div, resolver, adapter, errorCollector))
        bindScrollModes(div, resolver, adapter)
        notifyItemsUpdated(div, resolver, adapter, errorCollector)
        clipToPage = divView.div2Component.isPagerPageClipEnabled

        orientation =
            if (div.isHorizontal(resolver)) ViewPager2.ORIENTATION_HORIZONTAL else ViewPager2.ORIENTATION_VERTICAL
        crossAxisAlignment = div.crossAxisAlignment.evaluate(resolver)

        val reusableObserver = { _: Any -> applyDecorations(div, resolver, pageTranslations, adapter) }

        addSubscription(div.paddings?.left?.observe(resolver, reusableObserver))
        addSubscription(div.paddings?.right?.observe(resolver, reusableObserver))
        addSubscription(div.paddings?.top?.observe(resolver, reusableObserver))
        addSubscription(div.paddings?.bottom?.observe(resolver, reusableObserver))
        addSubscription(div.itemSpacing.value.observe(resolver, reusableObserver))
        addSubscription(div.itemSpacing.unit.observe(resolver, reusableObserver))
        addSubscription(div.scrollAxisAlignment.observe(resolver, reusableObserver))
        addSubscription(div.crossAxisAlignment.observe(resolver, reusableObserver))
        addSubscription(div.orientation.observe(resolver, reusableObserver))
        addSubscription(observeSizeChange(div) { parentSize ->
            applyDecorations(div, resolver, pageTranslations, adapter, parentSize)
        })

        when (val mode = div.layoutMode) {
            is DivPagerLayoutMode.NeighbourPageSize -> {
                addSubscription(mode.value.neighbourPageWidth.value.observe(resolver, reusableObserver))
                addSubscription(mode.value.neighbourPageWidth.unit.observe(resolver, reusableObserver))
            }
            is DivPagerLayoutMode.PageSize ->
                addSubscription(mode.value.pageWidth.value.observe(resolver, reusableObserver))
            is DivPagerLayoutMode.PageContentSize -> Unit
        }

        pagerSelectedActionsDispatcher = PagerSelectedActionsDispatcher(
            divView = divView,
            items = adapter.itemsToShow,
            actionPerformer = actionPerformer,
        )

        changePageCallbackForLogger = DivPagerPageChangeCallback(
            divPager = div,
            recyclerView = recyclerView,
            items = adapter.itemsToShow,
            pagerView = this,
            divView = divView,
        )

        divView.currentState?.let { state ->
            val id = div.id ?: div.hashCode().toString()
            val pagerState = state.getBlockState(id) as? PagerState
            changePageCallbackForState = UpdateStateChangePageCallback(id, state)
            currentItem = pagerState?.currentPageIndex?.takeIf {
                it < adapter.getRealPosition(adapter.itemsToShow.size)
            } ?: adapter.getPosition(div.defaultItem.evaluate(resolver).toIntSafely())
        }

        addSubscription(div.restrictParentScroll.observeAndGet(resolver) {
            onInterceptTouchEventListener = if (it) ParentScrollRestrictor else null
        })

        bindItemBuilder(div, resolver, divBlock.path, errorCollector)
        if (a11yEnabled) {
            enableAccessibility()
        }
    }

    private fun DivPagerView.notifyItemsUpdated(
        div: DivPager,
        resolver: ExpressionResolver,
        adapter: DivPagerAdapter,
        errorCollector: ErrorCollector,
    ) {
        div.itemCountVariable?.let {
            variableMutationHandler.setVariable(it, adapter.visibleItems.size.toString(), resolver, errorCollector)
        }
        pagerOnItemsCountChange?.onItemsUpdated()
    }

    private fun DivPagerView.bindScrollModes(
        div: DivPager,
        resolver: ExpressionResolver,
        adapter: DivPagerAdapter,
    ) {
        val recyclerView = viewPager.getChildAt(0) as RecyclerView
        var listener: RecyclerView.OnScrollListener? = null
        addSubscription(div.multiPageScroll.observeAndGet(resolver) { enabled: Boolean ->
            adapter.setVirtualItemCount(
                infiniteScrollEnabled = div.infiniteScroll.evaluate(resolver),
                multiPageScrollEnabled = enabled,
            )
            setMultiPageScrollEnabled(enabled)
        })
        var lastInfiniteScroll: Boolean? = null
        addSubscription(div.infiniteScroll.observeAndGet(resolver) { enabled: Boolean ->
            adapter.setVirtualItemCount(
                infiniteScrollEnabled = enabled,
                multiPageScrollEnabled = div.multiPageScroll.evaluate(resolver),
            )
            // Re-adding the same listener on an unchanged value would make it run twice per scroll.
            if (enabled == lastInfiniteScroll) return@observeAndGet
            lastInfiniteScroll = enabled
            if (enabled) {
                (listener ?: createInfiniteScrollListener().also { listener = it })
                    .let { recyclerView.addOnScrollListener(it) }
            } else {
                listener?.let { recyclerView.removeOnScrollListener(it) }
            }
        })
    }

    private fun DivPagerAdapter.setVirtualItemCount(infiniteScrollEnabled: Boolean, multiPageScrollEnabled: Boolean) {
        requestedVirtualItemCount = when {
            !infiniteScrollEnabled -> 0
            multiPageScrollEnabled -> VIRTUAL_ITEM_COUNT_EXTENDED
            else -> VIRTUAL_ITEM_COUNT
        }
    }

    private fun DivPagerView.createInfiniteScrollListener() = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val adapter = viewPager.adapter as? DivPagerAdapter ?: return
            val virtualItemCount = adapter.virtualItemCount.takeIf { it > 0 } ?: return
            val scrollDelta = if (viewPager.orientation == ViewPager2.ORIENTATION_HORIZONTAL) dx else dy
            val anchorPosition = loopAnchorPosition(
                firstItemVisible = layoutManager.findFirstVisibleItemPosition(),
                lastItemVisible = layoutManager.findLastVisibleItemPosition(),
                scrollDelta = scrollDelta,
                itemCount = adapter.itemCount,
                virtualItemCount = virtualItemCount,
            ) ?: return
            loopToRealPosition(recyclerView, layoutManager, adapter, anchorPosition)
        }
    }

    /**
     * Moves the pager from a virtual item to the real item repeating the same content.
     *
     * The anchor is the item actually on screen rather than the loop edge, because a fast fling
     * can advance past the seam within a single frame and a fixed target would then shift the
     * content by the overshoot. `RecyclerView.scrollToPosition()` also stops any running scroll,
     * which would cut a multi-page fling short at the seam, so while such a fling is running the
     * anchor is scheduled on the layout manager together with its current pixel offset instead.
     */
    private fun DivPagerView.loopToRealPosition(
        recyclerView: RecyclerView,
        layoutManager: LinearLayoutManager,
        adapter: DivPagerAdapter,
        anchorPosition: Int,
    ) {
        val realPosition = adapter.getPosition(adapter.realItemPosition(anchorPosition))
        if (realPosition == anchorPosition) return

        val anchorView = layoutManager.findViewByPosition(anchorPosition)
        if (!isMultiPageScrolling || anchorView == null) {
            recyclerView.scrollToPosition(realPosition)
            return
        }

        val helper = OrientationHelper.createOrientationHelper(layoutManager, layoutManager.orientation)
        val offset = if (layoutManager.isLayoutReversed(recyclerView)) {
            helper.endAfterPadding - helper.getDecoratedEnd(anchorView)
        } else {
            helper.getDecoratedStart(anchorView) - helper.startAfterPadding
        }
        layoutManager.scrollToPositionWithOffset(realPosition, offset)
    }

    /**
     * Mirrors `LinearLayoutManager.resolveShouldLayoutReverse()`, which is not public. It decides
     * whether `scrollToPositionWithOffset` measures its offset from the start or from the end.
     */
    private fun LinearLayoutManager.isLayoutReversed(recyclerView: RecyclerView): Boolean =
        if (orientation == RecyclerView.HORIZONTAL) {
            reverseLayout != recyclerView.isLayoutRtl()
        } else {
            reverseLayout
        }

    private fun DivPagerView.applyDecorations(
        div: DivPager,
        resolver: ExpressionResolver,
        pageTranslations: SparseArray<Float>,
        adapter: DivPagerAdapter,
        measuredParentSize: Int? = null,
    ) {
        val recyclerView = getRecyclerView() ?: return

        val isHorizontal = div.isHorizontal(resolver)
        orientation = if (isHorizontal) ViewPager2.ORIENTATION_HORIZONTAL else ViewPager2.ORIENTATION_VERTICAL
        crossAxisAlignment = div.crossAxisAlignment.evaluate(resolver)

        val metrics = resources.displayMetrics
        val parentSize = measuredParentSize ?: if (isHorizontal) viewPager.width else viewPager.height
        if (parentSize <= 0) return
        val itemSpacing = div.itemSpacing.toPxF(metrics, resolver)
        val infiniteScroll = div.infiniteScroll.evaluate(resolver)
        val scrollAxisAlignment = div.scrollAxisAlignment.evaluate(resolver)
        val paddings = DivPagerPaddingsHolder(div.paddings, resolver, this, metrics, isHorizontal, scrollAxisAlignment)

        val sizeProvider = when (val layoutMode = div.layoutMode) {
            is DivPagerLayoutMode.PageSize -> {
                PercentagePageSizeProvider(
                    layoutMode.value,
                    resolver,
                    parentSize,
                    paddings,
                    scrollAxisAlignment,
                )
            }
            is DivPagerLayoutMode.NeighbourPageSize -> {
                NeighbourPageSizeProvider(
                    layoutMode.value,
                    resolver,
                    metrics,
                    parentSize,
                    itemSpacing,
                    paddings,
                    scrollAxisAlignment,
                )
            }
            is DivPagerLayoutMode.PageContentSize ->
                WrapContentPageSizeProvider(recyclerView, isHorizontal, parentSize, paddings, scrollAxisAlignment)
        }

        val decoration = if (sizeProvider is FixedPageSizeProvider) {
            FixedPageSizeOffScreenPagesController(
                this,
                parentSize,
                itemSpacing,
                sizeProvider,
                paddings,
                infiniteScroll,
                adapter
            )
            FixedPageSizeItemDecoration(paddings, sizeProvider)
        } else {
            WrapContentPageSizeOffScreenPagesController(this, itemSpacing, sizeProvider, paddings, adapter)
            WrapContentPageSizeItemDecoration(parentSize, paddings, scrollAxisAlignment)
        }

        viewPager.setItemDecoration(decoration)

        val offsetProvider = DivPagerPageOffsetProvider(
            parentSize,
            itemSpacing,
            sizeProvider,
            paddings,
            infiniteScroll,
            adapter,
            scrollAxisAlignment,
        )
        pageTransformer = DivPagerPageTransformer(
            recyclerView,
            resolver,
            pageTranslations,
            parentSize,
            div.pageTransformation,
            offsetProvider,
            isHorizontal
        )
    }

    private fun DivPager.isHorizontal(resolver: ExpressionResolver) =
        orientation.evaluate(resolver) == DivPager.Orientation.HORIZONTAL

    private fun DivPagerView.observeSizeChange(div: DivPager, observer: (Int) -> Unit): Disposable {
        return object : Disposable, View.OnLayoutChangeListener {
            private var oldSize = 0
            private val onMeasured: (Int) -> Boolean = { newSize ->
                if (div.layoutMode is DivPagerLayoutMode.PageContentSize) false else updateSize(newSize)
            }
            private val preDrawListener = viewPager.doOnPreDraw {
                // ViewPager2 needs a laid-out size to include offscreen pages in wrap-content measurement.
                val newSize = getSize()
                if (newSize > 0) {
                    observer(newSize)
                }
                oldSize = newSize
            }

            init {
                viewPager.addOnLayoutChangeListener(this)
                onViewPagerMeasured = onMeasured
            }

            override fun close() {
                preDrawListener.removeListener()
                if (onViewPagerMeasured === onMeasured) {
                    onViewPagerMeasured = null
                }
                viewPager.removeOnLayoutChangeListener(this)
            }

            override fun onLayoutChange(
                v: View, left: Int, top: Int, right: Int, bottom: Int,
                oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int
            ) {
                val newSize = getSize()
                if (oldSize == newSize) {
                    if (div.layoutMode is DivPagerLayoutMode.PageContentSize) {
                        viewPager.requestTransform()
                    }
                    return
                }

                updateSize(newSize)
            }

            private fun updateSize(newSize: Int): Boolean {
                if (newSize <= 0 || oldSize == newSize) return false
                oldSize = newSize
                observer(newSize)
                return true
            }

            private fun getSize() = if (orientation == ViewPager2.ORIENTATION_HORIZONTAL) {
                viewPager.width
            } else {
                viewPager.height
            }
        }
    }

    private fun ViewPager2.setItemDecoration(decoration: RecyclerView.ItemDecoration) {
        removeItemDecorations()
        addItemDecoration(decoration)
    }

    private fun ViewPager2.removeItemDecorations() {
        for (i in 0 until itemDecorationCount) {
            removeItemDecorationAt(i)
        }
    }

    private fun DivPagerView.bindItemBuilder(
        div: DivPager,
        resolver: ExpressionResolver,
        path: DivStatePath,
        errorCollector: ErrorCollector,
    ) {
        val builder = div.itemBuilder ?: return
        bindItemBuilder(builder, resolver) {
            (viewPager.adapter as DivPagerAdapter?)?.let { adapter ->
                adapter.setItems(builder.build(resolver, path))
                notifyItemsUpdated(div, resolver, adapter, errorCollector)
                getRecyclerView()?.scrollToPosition(adapter.normalizeItemPosition(currentItem))
                viewPager.doOnNextLayout { viewPager.requestTransform() }
            }
        }
    }

    companion object {
        const val VIRTUAL_ITEM_COUNT = 2
        const val VIRTUAL_ITEM_COUNT_EXTENDED = 20
    }

    private inner class ItemCountObserver(
        private val pagerView: DivPagerView,
        private val div: DivPager,
        private val resolver: ExpressionResolver,
        private val adapter: DivPagerAdapter,
        private val errorCollector: ErrorCollector,
    ) : RecyclerView.AdapterDataObserver() {

        private var prevCount = adapter.visibleItems.size

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) = notifyItemsUpdated()

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) = notifyItemsUpdated()

        private fun notifyItemsUpdated() {
            val count = adapter.visibleItems.size
            if (count == prevCount) return
            prevCount = count
            pagerView.notifyItemsUpdated(div, resolver, adapter, errorCollector)
        }
    }
}

/**
 * Picks the on-screen item a looping pager should re-anchor on, or `null` while it stays within
 * the real items.
 *
 * The anchor is the item that is actually visible: a fast fling can cross the seam and land
 * several items deep into the virtual range within one frame, and re-anchoring on the loop edge
 * would then shift the content by that overshoot.
 */
internal fun loopAnchorPosition(
    firstItemVisible: Int,
    lastItemVisible: Int,
    scrollDelta: Int,
    itemCount: Int,
    virtualItemCount: Int,
): Int? = when {
    firstItemVisible == RecyclerView.NO_POSITION || lastItemVisible == RecyclerView.NO_POSITION -> null
    scrollDelta > 0 && firstItemVisible >= itemCount - virtualItemCount -> firstItemVisible
    scrollDelta < 0 && lastItemVisible <= virtualItemCount - 1 -> lastItemVisible
    else -> null
}
