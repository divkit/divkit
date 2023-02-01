package com.yandex.div.core.view2.divs

import android.content.Context
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.VisibleForTesting
import androidx.core.view.children
import androidx.core.view.doOnLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.core.Disposable
import com.yandex.div.core.ScrollDirection
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.downloader.DivPatchCache
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.state.PagerState
import com.yandex.div.core.state.UpdateStateChangePageCallback
import com.yandex.div.core.util.expressionSubscriber
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.animations.DivComparator
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.core.view2.divs.widgets.ParentScrollRestrictor
import com.yandex.div.core.view2.divs.widgets.ReleaseUtils.releaseAndRemoveChildren
import com.yandex.div.core.view2.divs.widgets.ReleaseViewVisitor
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.widget.PageItemDecoration
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
    ) : DivViewBinder<DivPager, DivPagerView> {

    private var changePageCallbackForState: ViewPager2.OnPageChangeCallback? = null
    @get:VisibleForTesting
    var changePageCallbackForLogger: ViewPager2.OnPageChangeCallback? = null
        private set
    private var pagerSelectedActionsDispatcher: PagerSelectedActionsDispatcher? = null

    override fun bindView(
        view: DivPagerView,
        div: DivPager,
        divView: Div2View,
        path: DivStatePath
    ) {
        val resolver = divView.expressionResolver
        val oldDiv = view.div
        if (div == oldDiv) {
            val adapter = view.viewPager.adapter as PagerAdapter
            if (!adapter.applyPatch(divPatchCache)) {
                adapter.notifyItemRangeChanged(0, adapter.itemCount);
            }
            return
        }

        val expressionSubscriber = view.expressionSubscriber
        expressionSubscriber.closeAllSubscription()
        view.div = div
        if (oldDiv != null) baseBinder.unbindExtensions(view, oldDiv, divView)
        baseBinder.bindView(view, div, oldDiv, divView)

        val pageTranslations = SparseArray<Float>()
        view.setRecycledViewPool(ReleasingViewPool(divView.releaseViewVisitor))
        view.viewPager.adapter = PagerAdapter(
            divs = div.items,
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
            visitor = divView.releaseViewVisitor
        )

        val reusableObserver = { _: Any ->
            applyDecorations(view, div, resolver)
            updatePageTransformer(view, div, resolver, pageTranslations)
        }

        expressionSubscriber.addSubscription(div.paddings.left.observe(resolver, reusableObserver))
        expressionSubscriber.addSubscription(div.paddings.right.observe(resolver, reusableObserver))
        expressionSubscriber.addSubscription(div.paddings.top.observe(resolver, reusableObserver))
        expressionSubscriber.addSubscription(div.paddings.bottom.observe(resolver, reusableObserver))
        expressionSubscriber.addSubscription(div.itemSpacing.value.observe(resolver, reusableObserver))
        expressionSubscriber.addSubscription(div.itemSpacing.unit.observe(resolver, reusableObserver))

        when (val mode = div.layoutMode) {
            is DivPagerLayoutMode.NeighbourPageSize -> {
                expressionSubscriber.addSubscription(mode.value.neighbourPageWidth.value.observe(resolver, reusableObserver))
                expressionSubscriber.addSubscription(mode.value.neighbourPageWidth.unit.observe(resolver, reusableObserver))
            }
            is DivPagerLayoutMode.PageSize -> {
                expressionSubscriber.addSubscription(mode.value.pageWidth.value.observe(resolver, reusableObserver))
                expressionSubscriber.addSubscription(observeWidthChange(view.viewPager, reusableObserver))
            }
        }.apply { /*exhaustive*/ }

        expressionSubscriber.addSubscription(div.orientation.observeAndGet(resolver) {
            view.orientation = if (it == DivPager.Orientation.HORIZONTAL) {
                ViewPager2.ORIENTATION_HORIZONTAL
            } else {
                ViewPager2.ORIENTATION_VERTICAL
            }

            updatePageTransformer(view, div, resolver, pageTranslations)
            applyDecorations(view, div, resolver)
        })

        pagerSelectedActionsDispatcher?.detach(view.viewPager)
        pagerSelectedActionsDispatcher = PagerSelectedActionsDispatcher(
            divView, div, divActionBinder
        ).apply { attach(view.viewPager) }

        if (changePageCallbackForLogger != null) {
            view.viewPager.unregisterOnPageChangeCallback(changePageCallbackForLogger!!)
        }
        changePageCallbackForLogger = PageChangeCallback(
            div, divView,
            view.viewPager.getChildAt(0) as RecyclerView
        )
        view.viewPager.registerOnPageChangeCallback(changePageCallbackForLogger!!)

        divView.currentState?.let { state ->
            val id = div.id ?: div.hashCode().toString()
            val pagerState = state.getBlockState(id) as PagerState?
            if (changePageCallbackForState != null) {
                view.viewPager.unregisterOnPageChangeCallback(changePageCallbackForState!!)
            }
            changePageCallbackForState = UpdateStateChangePageCallback(id, state)
            view.viewPager.registerOnPageChangeCallback(changePageCallbackForState!!)
            view.currentItem = pagerState?.currentPageIndex ?: div.defaultItem.evaluate(resolver)
        }

        expressionSubscriber.addSubscription(div.restrictParentScroll.observeAndGet(resolver) { restrictParentScroll ->
            view.onInterceptTouchEventListener = if (restrictParentScroll) {
                ParentScrollRestrictor(ParentScrollRestrictor.DIRECTION_HORIZONTAL)
            } else {
                null
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
        val pageWidthPercent = div.evaluatePageWidthPercent(resolver)
        val itemSpacing = div.itemSpacing.toPxF(metrics, resolver)
        val startPadding = if (orientation == DivPager.Orientation.HORIZONTAL) {
            div.paddings.left.evaluate(resolver).dpToPxF(metrics)
        } else {
            div.paddings.top.evaluate(resolver).dpToPxF(metrics)
        }
        val endPadding = if (orientation == DivPager.Orientation.HORIZONTAL) {
            div.paddings.right.evaluate(resolver).dpToPxF(metrics)
        } else {
            div.paddings.bottom.evaluate(resolver).dpToPxF(metrics)
        }

        view.viewPager.setPageTransformer { page, position ->
            val viewPager = page.parent.parent as ViewPager2
            val recyclerView = viewPager.getChildAt(0) as RecyclerView

            val isTwoPage = recyclerView.adapter?.itemCount == 2
            val pagePosition = recyclerView.layoutManager?.getPosition(page)
            val isFirst = pagePosition == 0
            val isSecond = pagePosition == 1
            val isPreLast = pagePosition == recyclerView.adapter!!.itemCount - 2
            val isLast = pagePosition == recyclerView.adapter!!.itemCount - 1
            var neighbourItemWidth = div.evaluateNeighbourItemWidth(view, resolver)

            if (pageWidthPercent != null) {
                val parentSize = if (orientation == DivPager.Orientation.HORIZONTAL) {
                    view.viewPager.width
                } else {
                    view.viewPager.height
                }
                neighbourItemWidth =
                    (parentSize * (1 - pageWidthPercent / 100f) - itemSpacing * 2) / 2
            }


            val additionalOffset = if (isTwoPage) {
                0f
            } else if (isFirst && position < 0f && position >= -1f) {
                neighbourItemWidth + itemSpacing - startPadding
            } else if (isFirst && position < -1f) {
                (neighbourItemWidth + itemSpacing - startPadding) / -position
            } else if (isSecond && position > 0f) {
                neighbourItemWidth + itemSpacing - startPadding
            } else if (isPreLast && position < 0f) {
                neighbourItemWidth + itemSpacing - endPadding
            } else if (isLast && position > 1f) {
                (neighbourItemWidth + itemSpacing - endPadding) / position
            } else if (isLast && position > 0f && position <= 1f) {
                neighbourItemWidth + itemSpacing - endPadding
            } else {
                0f
            }

            val offset = -position * (neighbourItemWidth * 2 + itemSpacing + additionalOffset)
            pagePosition?.let { pageTranslations.put(it, offset) }
            if (orientation == DivPager.Orientation.HORIZONTAL) {
                page.translationX = offset
            } else {
                page.translationY = offset
            }
        }
    }

    private fun applyDecorations(
        view: DivPagerView,
        div: DivPager,
        resolver: ExpressionResolver,
    ) {
        val metrics = view.resources.displayMetrics
        val itemSpacing = div.itemSpacing.toPxF(metrics, resolver)
        val neighbourItemWidth = div.evaluateNeighbourItemWidth(view, resolver)
        view.viewPager.setItemDecoration(
            PageItemDecoration(
                paddingLeft = div.paddings.left.evaluate(resolver).dpToPxF(metrics),
                paddingRight = div.paddings.right.evaluate(resolver).dpToPxF(metrics),
                paddingTop = div.paddings.top.evaluate(resolver).dpToPxF(metrics),
                paddingBottom = div.paddings.bottom.evaluate(resolver).dpToPxF(metrics),
                itemSpacing = itemSpacing,
                neighbourItemWidth = neighbourItemWidth,
                orientation = if (div.orientation.evaluate(resolver) == DivPager.Orientation.HORIZONTAL) {
                    RecyclerView.HORIZONTAL
                } else {
                    RecyclerView.VERTICAL
                }
            )
        )

        val pageWidthPercent = div.evaluatePageWidthPercent(resolver)

        if (neighbourItemWidth != 0f ||
            pageWidthPercent != null && pageWidthPercent < 100
        ) {
            if (view.viewPager.offscreenPageLimit != 1) {
                view.viewPager.offscreenPageLimit = 1
            }
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


    private fun DivPager.evaluatePageWidthPercent(resolver: ExpressionResolver): Int? {
        return (layoutMode as? DivPagerLayoutMode.PageSize)
            ?.value?.pageWidth?.value?.evaluate(resolver)?.toInt()
    }

    private fun DivPager.evaluateNeighbourItemWidth(view: DivPagerView,
                                                    resolver: ExpressionResolver): Float {
        val metrics = view.resources.displayMetrics

        return when (val mode = this.layoutMode) {
            is DivPagerLayoutMode.PageSize -> {
                val parentSize = if (this.orientation.evaluate(resolver) == DivPager.Orientation.HORIZONTAL) {
                    view.viewPager.width
                } else {
                    view.viewPager.height
                }
                val pageWidthPercent = mode.value.pageWidth.value.evaluate(resolver).toInt()
                val itemSpacing = itemSpacing.toPxF(metrics, resolver)
                (parentSize * (1 - pageWidthPercent / 100f) - itemSpacing * 2) / 2
            }
            is DivPagerLayoutMode.NeighbourPageSize -> {
                mode.value.neighbourPageWidth.toPxF(metrics, resolver)
            }
        }}

    class PageChangeCallback(
        private val divPager: DivPager,
        private val divView: Div2View,
        private val recyclerView: RecyclerView
    ) : ViewPager2.OnPageChangeCallback() {
        var prevPosition = RecyclerView.NO_POSITION

        val minimumSignificantDx = divView.config.logCardScrollSignificantThreshold
        var totalDelta = 0

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
            val selectedPage = divPager.items[position]
            if (selectedPage.value().hasVisibilityActions) {
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
                recyclerView.doOnLayout { trackVisibleChildren() }
            }
        }

        private fun trackVisibleChildren() {
            recyclerView.children.forEach { child ->
                val childPosition = recyclerView.getChildAdapterPosition(child)
                if (childPosition == RecyclerView.NO_POSITION) {
                    KAssert.fail { "Requesting child position during layout" }
                    return
                }
                val childDiv = divPager.items[childPosition]
                val visibilityActionTracker = divView.div2Component.visibilityActionTracker
                visibilityActionTracker.trackVisibilityActionsOf(divView, child, childDiv)
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
        private val visitor: ReleaseViewVisitor
    ) : DivPatchableAdapter<PagerViewHolder>(divs, div2View) {

        override val subscriptions = mutableListOf<Disposable>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
            val view = PageLayout(div2View.context)
            view.layoutParams =
                FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

            return PagerViewHolder(view, divBinder, viewCreator, visitor)
        }

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
            val div = items[position]
            holder.bind(div2View, div, path)
            translationBinder.invoke(holder, position)
        }

        override fun onFailedToRecycleView(holder: PagerViewHolder): Boolean {
            val shouldRecycle = super.onFailedToRecycleView(holder)
            if (!shouldRecycle) {
                holder.frameLayout.releaseAndRemoveChildren(div2View)
            }
            return shouldRecycle
        }
    }

    private class PageLayout(context: Context) : FrameLayout(context) {
        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            //when the page height is equal to MATCH_PARENT, the page must be the size of a pager minus padding,
            //otherwise the page height should be enough for its own content regardless of the current pager height
            if (childCount != 0 && getChildAt(0).layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            } else {
                super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
            }
        }
    }

    private class PagerViewHolder(
        val frameLayout: FrameLayout,
        private val divBinder: DivBinder,
        private val viewCreator: DivViewCreator,
        private val visitor: ReleaseViewVisitor
    ) : RecyclerView.ViewHolder(frameLayout) {

        private var oldDiv: Div? = null

        fun bind(div2View: Div2View, div: Div, path: DivStatePath) {
            val resolver = div2View.expressionResolver
            val divView = if (oldDiv != null && DivComparator.areDivsReplaceable(oldDiv, div, resolver)) {
                frameLayout[0]
            } else {
                val newDivView = viewCreator.create(div, resolver)
                frameLayout.releaseAndRemoveChildren(div2View)
                frameLayout.addView(newDivView)
                newDivView
            }

            oldDiv = div
            divBinder.bind(divView, div, div2View, path)
        }
    }

    private fun ViewPager2.setItemDecoration(decoration: RecyclerView.ItemDecoration) {
        for (i in 0 until itemDecorationCount) {
            removeItemDecorationAt(i)
        }
        addItemDecoration(decoration)
    }
}
