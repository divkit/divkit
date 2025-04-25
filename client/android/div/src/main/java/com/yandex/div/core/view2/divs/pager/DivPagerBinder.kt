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
import com.yandex.div2.Div
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
) : DivViewBinder<Div.Pager, DivPager, DivPagerView>(baseBinder) {

    override fun bindView(context: BindingContext, view: DivPagerView, div: Div.Pager, path: DivStatePath) {
        pagerIndicatorConnector.submitPager(view, div.value)

        val oldDiv = view.div
        if (div === oldDiv) {
            val pager = view.viewPager
            val adapter = pager.adapter as? DivPagerAdapter ?: return
            if (adapter.applyPatch(view.getRecyclerView(), divPatchCache, context)) {
                view.pagerOnItemsCountChange?.onItemsUpdated()
                return
            }

            view.bindStates(context, divBinder.get())
            pager.doOnNextLayout { pager.requestTransform() }
            return
        }

        oldDiv?.let {
            view.changePageCallbackForOffScreenPages = null
            view.viewPager.removeItemDecorations()
            view.pageTransformer = null
        }

        baseBinder.bindView(context, view, div, oldDiv)
        view.bind(context, div.value, path)
    }

    private fun DivPagerView.bind(bindingContext: BindingContext, div: DivPager, path: DivStatePath) {
        val recyclerView = getRecyclerView() ?: return
        
        val divView = bindingContext.divView
        val resolver = bindingContext.expressionResolver
        val pageTranslations = SparseArray<Float>()
        val a11yEnabled = accessibilityStateProvider.isAccessibilityEnabled(context)
        setRecycledViewPool(ReleasingViewPool(divView.releaseViewVisitor))
        val adapter = DivPagerAdapter(
            div.buildItems(bindingContext.divView, resolver),
            bindingContext,
            divBinder.get(),
            pageTranslations,
            viewCreator,
            path,
            a11yEnabled,
            this
        )
        viewPager.adapter = adapter
        bindInfiniteScroll(div, resolver)
        pagerOnItemsCountChange?.onItemsUpdated()
        clipToPage = divView.div2Component.isPagerPageClipEnabled

        orientation =
            if (div.isHorizontal(resolver)) ViewPager2.ORIENTATION_HORIZONTAL else ViewPager2.ORIENTATION_VERTICAL
        adapter.crossAxisAlignment = div.crossAxisAlignment.evaluate(resolver)

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
        addSubscription(viewPager.observeSizeChange(div, reusableObserver))

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
            divActionBinder = divActionBinder,
        )

        changePageCallbackForLogger = DivPagerPageChangeCallback(
            bindingContext = bindingContext,
            divPager = div,
            recyclerView = recyclerView,
            items = adapter.itemsToShow,
            pagerView = this,
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

        bindItemBuilder(bindingContext, div)
        if (a11yEnabled) {
            enableAccessibility()
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

        val isHorizontal = div.isHorizontal(resolver)
        orientation = if (isHorizontal) ViewPager2.ORIENTATION_HORIZONTAL else ViewPager2.ORIENTATION_VERTICAL
        adapter.crossAxisAlignment = div.crossAxisAlignment.evaluate(resolver)

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

    private fun DivPager.isHorizontal(resolver: ExpressionResolver) =
        orientation.evaluate(resolver) == DivPager.Orientation.HORIZONTAL

    private fun ViewPager2.observeSizeChange(div: DivPager, observer: (_: Any) -> Unit): Disposable {
        return object : Disposable, View.OnLayoutChangeListener {
            private var oldSize = 0

            init {
                addOnLayoutChangeListener(this)
                doOnPreDraw {
                    val newSize = getSize()
                    observer(newSize)
                    oldSize = newSize
                }
            }

            override fun close() {
                removeOnLayoutChangeListener(this)
            }

            override fun onLayoutChange(
                v: View, left: Int, top: Int, right: Int, bottom: Int,
                oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int
            ) {
                val newSize = getSize()
                if (oldSize == newSize) {
                    if (div.layoutMode is DivPagerLayoutMode.PageContentSize) {
                        requestTransform()
                    }
                    return
                }

                oldSize = newSize
                observer.invoke(newSize)
            }

            private fun getSize() = if (orientation == ViewPager2.ORIENTATION_HORIZONTAL) width else height
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

    private fun DivPagerView.bindItemBuilder(context: BindingContext, div: DivPager) {
        val builder = div.itemBuilder ?: return
        bindItemBuilder(builder, context.expressionResolver) {
            (viewPager.adapter as DivPagerAdapter?)?.setItems(
                builder.build(context.divView, context.expressionResolver)
            )
            pagerOnItemsCountChange?.onItemsUpdated()
            getRecyclerView()?.scrollToPosition(currentItem)
            viewPager.doOnNextLayout { viewPager.requestTransform() }
        }
    }
}
