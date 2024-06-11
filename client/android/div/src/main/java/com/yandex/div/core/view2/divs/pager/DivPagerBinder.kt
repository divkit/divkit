package com.yandex.div.core.view2.divs.pager

import android.util.SparseArray
import android.view.View
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
import com.yandex.div.core.util.isLayoutRtl
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.divs.DivActionBinder
import com.yandex.div.core.view2.divs.DivBaseBinder
import com.yandex.div.core.view2.divs.ReleasingViewPool
import com.yandex.div.core.view2.divs.dpToPxF
import com.yandex.div.core.view2.divs.pager.DivPagerAdapter.Companion.OFFSET_TO_REAL_ITEM
import com.yandex.div.core.view2.divs.toPxF
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.core.view2.divs.widgets.ParentScrollRestrictor
import com.yandex.div.internal.core.buildItems
import com.yandex.div.internal.widget.PageItemDecoration
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
            val adapter = view.viewPager.adapter as? DivPagerAdapter ?: return
            if (!adapter.applyPatch(view.getRecyclerView(), divPatchCache, context)) {
                view.pageTransformer?.onItemsCountChanged()
                view.pagerOnItemsCountChange?.onItemsUpdated()
            }
            return
        }

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
            )
        view.viewPager.adapter = adapter
        view.bindInfiniteScroll(div, resolver)
        view.pagerOnItemsCountChange?.onItemsUpdated()

        val reusableObserver = { _: Any ->
            val isHorizontal = div.orientation.evaluate(resolver) == DivPager.Orientation.HORIZONTAL
            view.orientation = if (isHorizontal) ViewPager2.ORIENTATION_HORIZONTAL else ViewPager2.ORIENTATION_VERTICAL
            view.applyDecorations(div, resolver, isHorizontal)
            view.updatePageTransformer(div, resolver, pageTranslations)
        }

        view.addSubscription(div.paddings?.left?.observe(resolver, reusableObserver))
        view.addSubscription(div.paddings?.right?.observe(resolver, reusableObserver))
        view.addSubscription(div.paddings?.top?.observe(resolver, reusableObserver))
        view.addSubscription(div.paddings?.bottom?.observe(resolver, reusableObserver))
        view.addSubscription(div.itemSpacing.value.observe(resolver, reusableObserver))
        view.addSubscription(div.itemSpacing.unit.observe(resolver, reusableObserver))
        view.addSubscription(div.orientation.observeAndGet(resolver, reusableObserver))

        when (val mode = div.layoutMode) {
            is DivPagerLayoutMode.NeighbourPageSize -> {
                view.addSubscription(mode.value.neighbourPageWidth.value.observe(resolver, reusableObserver))
                view.addSubscription(mode.value.neighbourPageWidth.unit.observe(resolver, reusableObserver))
            }
            is DivPagerLayoutMode.PageSize -> {
                view.addSubscription(mode.value.pageWidth.value.observe(resolver, reusableObserver))
                view.addSubscription(observeWidthChange(view.viewPager, reusableObserver))
            }
        }

        view.pagerSelectedActionsDispatcher = PagerSelectedActionsDispatcher(
            divView = divView,
            items = adapter.itemsToShow,
            divActionBinder = divActionBinder,
        )

        view.changePageCallbackForLogger = PageChangeCallback(
            bindingContext = context,
            divPager = div,
            recyclerView = view.viewPager.getChildAt(0) as RecyclerView,
            items = adapter.itemsToShow,
            pagerView = view,
        )

        divView.currentState?.let { state ->
            val id = div.id ?: div.hashCode().toString()
            val pagerState = state.getBlockState(id) as PagerState?
            view.changePageCallbackForState = UpdateStateChangePageCallback(id, state)
            view.currentItem = pagerState?.currentPageIndex
                ?: adapter.getPosition(div.defaultItem.evaluate(resolver).toIntSafely())
        }

        view.addSubscription(div.restrictParentScroll.observeAndGet(resolver) { restrictParentScroll ->
            view.onInterceptTouchEventListener = if (restrictParentScroll) {
                ParentScrollRestrictor
            } else {
                null
            }
        })
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

    private fun DivPagerView.updatePageTransformer(
        div: DivPager,
        resolver: ExpressionResolver,
        pageTranslations: SparseArray<Float>
    ) {
        pageTransformer = DivPagerPageTransformer(this, div, resolver, pageTranslations)
    }

    private fun DivPagerView.applyDecorations(div: DivPager, resolver: ExpressionResolver, isHorizontal: Boolean) {
        val metrics = resources.displayMetrics
        viewPager.setItemDecoration(
            PageItemDecoration(
                layoutMode = div.layoutMode,
                metrics = metrics,
                resolver = resolver,
                paddingLeft = evaluateLeftPadding(div, resolver, isHorizontal),
                paddingRight = evaluateRightPadding(div, resolver, isHorizontal),
                paddingTop = div.paddings?.top?.evaluate(resolver).dpToPxF(metrics),
                paddingBottom = div.paddings?.bottom?.evaluate(resolver).dpToPxF(metrics),
                parentSize = if (isHorizontal) viewPager.width else viewPager.height,
                itemSpacing = div.itemSpacing.toPxF(metrics, resolver),
                orientation = if (isHorizontal) RecyclerView.HORIZONTAL else RecyclerView.VERTICAL,
            )
        )

        val neighbourItemIsShown = when (val mode = div.layoutMode) {
            is DivPagerLayoutMode.PageSize -> mode.value.pageWidth.value.evaluate(resolver) < 100
            is DivPagerLayoutMode.NeighbourPageSize -> mode.value.neighbourPageWidth.value.evaluate(resolver) > 0
        }
        if (neighbourItemIsShown && viewPager.offscreenPageLimit != 1) {
            viewPager.offscreenPageLimit = 1
        }
    }

    private fun DivPagerView.evaluateRightPadding(
        div: DivPager,
        resolver: ExpressionResolver,
        isHorizontal: Boolean
    ): Float {
        val metrics = resources.displayMetrics
        val isLayoutRtl = isLayoutRtl()
        val paddings = div.paddings

        return when {
            paddings == null -> 0.0f
            isHorizontal && isLayoutRtl && paddings.start != null -> paddings.start?.evaluate(resolver).dpToPxF(metrics)
            isHorizontal && !isLayoutRtl && paddings.end != null -> paddings.end?.evaluate(resolver).dpToPxF(metrics)
            else -> paddings.right.evaluate(resolver).dpToPxF(metrics)
        }
    }

    private fun DivPagerView.evaluateLeftPadding(
        div: DivPager,
        resolver: ExpressionResolver,
        isHorizontal: Boolean
    ): Float {
        val metrics = resources.displayMetrics
        val isLayoutRtl = isLayoutRtl()
        val paddings = div.paddings

        return when {
            paddings == null -> 0.0f
            isHorizontal && isLayoutRtl && paddings.end != null -> paddings.end?.evaluate(resolver).dpToPxF(metrics)
            isHorizontal && !isLayoutRtl && paddings.start != null ->
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

    private fun ViewPager2.setItemDecoration(decoration: RecyclerView.ItemDecoration) {
        for (i in 0 until itemDecorationCount) {
            removeItemDecorationAt(i)
        }
        addItemDecoration(decoration)
    }
}
