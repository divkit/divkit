package com.yandex.div.core.view2.divs.pager

import android.util.SparseArray
import android.view.View
import androidx.core.view.doOnNextLayout
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.core.Disposable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.downloader.DivPatchCache
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.state.PagerState
import com.yandex.div.core.state.UpdateStateChangePageCallback
import com.yandex.div.core.util.AccessibilityStateProvider
import com.yandex.div.core.util.isActuallyLaidOut
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.divs.DivActionBinder
import com.yandex.div.core.view2.divs.DivBaseBinder
import com.yandex.div.core.view2.divs.ReleasingViewPool
import com.yandex.div.core.view2.divs.bindItemBuilder
import com.yandex.div.core.view2.divs.bindStates
import com.yandex.div.core.view2.divs.pager.DivPagerAdapter.Companion.OFFSET_TO_REAL_ITEM
import com.yandex.div.core.view2.divs.toPxF
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.core.view2.divs.widgets.ParentScrollRestrictor
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
    private val divPatchCache: DivPatchCache,
    private val divActionBinder: DivActionBinder,
    private val pagerIndicatorConnector: PagerIndicatorConnector,
    private val accessibilityStateProvider: AccessibilityStateProvider,
) : DivViewBinder<DivPager, DivPagerView> {

    override fun bindView(
        context: BindingContext,
        view: DivPagerView,
        div: DivPager,
        path: DivStatePath
    ) {
        div.id?.let {
            pagerIndicatorConnector.submitPager(it, view)
        }
        val divView = context.divView
        val resolver = context.expressionResolver
        val oldDiv = view.div
        if (div === oldDiv) {
            val pager = view.viewPager
            val adapter = pager.adapter as? DivPagerAdapter ?: return
            if (!adapter.applyPatch(view.getRecyclerView(), divPatchCache, context)) {
                view.pagerOnItemsCountChange?.onItemsUpdated()
                pager.doOnNextLayout { pager.requestTransform() }
            }
            view.bindStates(divView.rootDiv(), context, resolver, divBinder.get())
            return
        }

        val recyclerView = view.getRecyclerView() ?: return

        baseBinder.bindView(context, view, div, oldDiv)

        val pageTranslations = SparseArray<Float>()
        val a11yEnabled = accessibilityStateProvider.isAccessibilityEnabled(view.context)
        view.setRecycledViewPool(ReleasingViewPool(divView.releaseViewVisitor))
        val adapter = DivPagerAdapter(
            div.buildItems(resolver),
            context,
            divBinder.get(),
            pageTranslations,
            viewCreator,
            path,
            a11yEnabled,
            view
        )
        view.viewPager.adapter = adapter
        view.bindInfiniteScroll(div, resolver)
        view.pagerOnItemsCountChange?.onItemsUpdated()
        view.clipToPage = divView.div2Component.isPagerPageClipEnabled

        val reusableObserver = { _: Any -> view.applyDecorations(div, resolver, pageTranslations, adapter) }

        view.addSubscription(div.paddings?.left?.observe(resolver, reusableObserver))
        view.addSubscription(div.paddings?.right?.observe(resolver, reusableObserver))
        view.addSubscription(div.paddings?.top?.observe(resolver, reusableObserver))
        view.addSubscription(div.paddings?.bottom?.observe(resolver, reusableObserver))
        view.addSubscription(div.itemSpacing.value.observe(resolver, reusableObserver))
        view.addSubscription(div.itemSpacing.unit.observe(resolver, reusableObserver))
        view.addSubscription(div.scrollAxisAlignment.observe(resolver, reusableObserver))
        view.addSubscription(div.orientation.observeAndGet(resolver, reusableObserver))
        view.addSubscription(view.viewPager.observeSizeChange(reusableObserver))

        when (val mode = div.layoutMode) {
            is DivPagerLayoutMode.NeighbourPageSize -> {
                view.addSubscription(mode.value.neighbourPageWidth.value.observe(resolver, reusableObserver))
                view.addSubscription(mode.value.neighbourPageWidth.unit.observe(resolver, reusableObserver))
            }
            is DivPagerLayoutMode.PageSize ->
                view.addSubscription(mode.value.pageWidth.value.observe(resolver, reusableObserver))
            is DivPagerLayoutMode.PageContentSize -> Unit
        }

        view.pagerSelectedActionsDispatcher = PagerSelectedActionsDispatcher(
            divView = divView,
            items = adapter.itemsToShow,
            divActionBinder = divActionBinder,
        )

        view.changePageCallbackForLogger = DivPagerPageChangeCallback(
            bindingContext = context,
            divPager = div,
            recyclerView = recyclerView,
            items = adapter.itemsToShow,
            pagerView = view,
        )

        divView.currentState?.let { state ->
            val id = div.id ?: div.hashCode().toString()
            val pagerState = state.getBlockState(id) as? PagerState
            view.changePageCallbackForState = UpdateStateChangePageCallback(id, state)
            view.currentItem = pagerState?.currentPageIndex?.takeIf {
                it < adapter.getRealPosition(adapter.itemsToShow.size)
            } ?: adapter.getPosition(div.defaultItem.evaluate(resolver).toIntSafely())
        }

        view.addSubscription(div.restrictParentScroll.observeAndGet(resolver) { restrictParentScroll ->
            view.onInterceptTouchEventListener = if (restrictParentScroll) {
                ParentScrollRestrictor
            } else {
                null
            }
        })

        view.addSubscription(div.crossAxisAlignment.observeAndGet(resolver) {
            adapter.crossAxisAlignment = it
            view.requestLayout()
        })

        view.bindItemBuilder(context, div)
        if (a11yEnabled) {
            view.enableAccessibility()
        }
    }

    private fun DivPagerView.bindInfiniteScroll(div: DivPager, resolver: ExpressionResolver) {
        val recyclerView = viewPager.getChildAt(0) as RecyclerView
        var listener: RecyclerView.OnScrollListener? = null
        div.infiniteScroll.observeAndGet(resolver) { enabled: Boolean ->
            (viewPager.adapter as? DivPagerAdapter)?.infiniteScrollEnabled = enabled
            if (enabled) {
                (listener ?: createInfiniteScrollListener().also { listener = it })
                    .let { recyclerView.addOnScrollListener(it) }
            } else {
                listener?.let { recyclerView.removeOnScrollListener(it) }
            }
        }
    }

    private fun DivPagerView.createInfiniteScrollListener() = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val itemCount = viewPager.adapter?.itemCount ?: 0
            val firstItemVisible = layoutManager.findFirstVisibleItemPosition()
            val lastItemVisible = layoutManager.findLastVisibleItemPosition()
            if (firstItemVisible == (itemCount - OFFSET_TO_REAL_ITEM) && dx > 0) {
                recyclerView.scrollToPosition(OFFSET_TO_REAL_ITEM)
            } else if (lastItemVisible == OFFSET_TO_REAL_ITEM - 1 && dx < 0) {
                recyclerView.scrollToPosition(itemCount - 1 - OFFSET_TO_REAL_ITEM)
            }
        }
    }

    private fun DivPagerView.applyDecorations(
        div: DivPager,
        resolver: ExpressionResolver,
        pageTranslations: SparseArray<Float>,
        adapter: DivPagerAdapter,
    ) {
        val recyclerView = getRecyclerView() ?: return

        val isHorizontal = div.orientation.evaluate(resolver) == DivPager.Orientation.HORIZONTAL
        orientation = if (isHorizontal) ViewPager2.ORIENTATION_HORIZONTAL else ViewPager2.ORIENTATION_VERTICAL

        changePageCallbackForOffScreenPages = null

        if (!isActuallyLaidOut) return

        val metrics = resources.displayMetrics
        val parentSize = if (isHorizontal) viewPager.width else viewPager.height
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

    private fun ViewPager2.observeSizeChange(observer: (_: Any) -> Unit): Disposable {
        return object : Disposable, View.OnLayoutChangeListener {
            private var oldSize = 0

            private fun getSize() = if (orientation == ViewPager2.ORIENTATION_HORIZONTAL) width else height

            init {
                addOnLayoutChangeListener(this)
                // First onLayoutChange triggered too late.
                // Used for set layout paddings before user actions
                doOnPreDraw { updateSize() }
            }

            override fun close() {
                removeOnLayoutChangeListener(this)
            }

            override fun onLayoutChange(
                v: View, left: Int, top: Int, right: Int, bottom: Int,
                oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int
            ) = updateSize()

            private fun updateSize() {
                val newSize = getSize()
                if (oldSize == newSize) return

                oldSize = newSize
                observer.invoke(newSize)
            }
        }
    }

    private fun ViewPager2.setItemDecoration(decoration: RecyclerView.ItemDecoration) {
        for (i in 0 until itemDecorationCount) {
            removeItemDecorationAt(i)
        }
        addItemDecoration(decoration)
    }

    private fun DivPagerView.bindItemBuilder(context: BindingContext, div: DivPager) {
        val builder = div.itemBuilder ?: return
        bindItemBuilder(builder, context.expressionResolver) {
            (viewPager.adapter as DivPagerAdapter?)?.setItems(builder.build(context.expressionResolver))
            pagerOnItemsCountChange?.onItemsUpdated()
            getRecyclerView()?.scrollToPosition(currentItem)
            viewPager.doOnNextLayout { viewPager.requestTransform() }
        }
    }
}
