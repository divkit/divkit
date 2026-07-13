package com.yandex.div.core.view2.items

import android.util.DisplayMetrics
import android.view.View
import com.yandex.div.core.util.isLayoutRtl
import com.yandex.div.core.view2.divs.availableHeight
import com.yandex.div.core.view2.divs.availableWidth
import com.yandex.div.core.view2.divs.dpToPx
import com.yandex.div.core.view2.divs.gallery.DivGalleryAdapter
import com.yandex.div.core.view2.divs.gallery.DivGalleryItemHelper
import com.yandex.div.core.view2.divs.gallery.DivGallerySmoothScroller
import com.yandex.div.core.view2.divs.pager.DivPagerAdapter
import com.yandex.div.core.view2.divs.spToPx
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div.core.view2.divs.widgets.DivTabsLayout
import com.yandex.div.internal.KAssert
import com.yandex.div2.Div
import com.yandex.div2.DivSizeUnit

/**
 * Abstract view having items.
 */
internal sealed class DivViewWithItems(view: View) {

    /**
     * DisplayMetrics
     */
    val metrics: DisplayMetrics = view.resources.displayMetrics

    /**
     * Total item count.
     */
    abstract val itemCount: Int

    /**
     * Scroll range.
     */
    open val scrollRange: Int = 0

    /**
     * Scroll range.
     */
    open val scrollOffset: Int = 0

    /**
     * sets Current item
     */
    abstract fun setCurrentItem(index: Int, animated: Boolean)

    abstract fun getNearestItem(direction: ScrollDirection): Int

    /**
     * Scroll to the end of recycler view.
     */
    abstract fun scrollToTheEnd(animated: Boolean)

    abstract fun getIndicesOfItemWithId(id: String): List<Int>

    /**
     * Scroll to @param dp value.
     */
    open fun scrollTo(value: Int, animated: Boolean, sizeUnit: DivSizeUnit = DivSizeUnit.PX) = Unit

    /**
     * Implementation of [DivViewWithItems] specific for div gallery.
     */
    internal open class Gallery(private val view: DivRecyclerView) : DivViewWithItems(view) {

        override val itemCount: Int
            get() = view.adapter?.itemCount ?: 0

        override val scrollRange get() = view.scrollRange()

        override val scrollOffset get(): Int {
            return if (view.itemHelper.isHorizontal) {
                view.computeHorizontalScrollOffset()
            } else {
                view.computeVerticalScrollOffset()
            }
        }

        override fun setCurrentItem(index: Int, animated: Boolean) {
            checkItem(index, itemCount) {
                if (!animated) return@checkItem view.itemHelper.instantScroll(index)

                val smoothScroller = DivGallerySmoothScroller(view)
                smoothScroller.targetPosition = index
                view.layoutManager?.startSmoothScroll(smoothScroller)
            }
        }

        override fun getNearestItem(direction: ScrollDirection): Int {
            val reverse = view.itemHelper.isHorizontal && view.isLayoutRtl()
            val physicalScrollDirection = if (reverse) -direction.value else direction.value
            return view.itemHelper.getNearestItemPosition(physicalScrollDirection)
        }

        override fun scrollToTheEnd(animated: Boolean) =
            view.scrollTo(view.scrollRange(), animated, DivSizeUnit.PX)

        override fun getIndicesOfItemWithId(id: String): List<Int> {
            val adapter = view.adapter as? DivGalleryAdapter ?: return emptyList()
            return adapter.visibleItems.getIndicesWithId(id) { div }
        }

        override fun scrollTo(value: Int, animated: Boolean, sizeUnit: DivSizeUnit) =
            view.scrollTo(value, animated, sizeUnit)

        private fun DivRecyclerView.scrollTo(
            value: Int,
            animated: Boolean,
            sizeUnit: DivSizeUnit,
        ) {
            val valuePx = when (sizeUnit) {
                DivSizeUnit.PX -> value
                DivSizeUnit.SP -> value.spToPx(metrics)
                DivSizeUnit.DP -> value.dpToPx(metrics)
            }
            val scroll: (dx: Int, dy: Int) -> Unit = if (animated) this::smoothScrollBy else this::scrollBy
            if (itemHelper.isHorizontal) {
                scroll(valuePx - computeHorizontalScrollOffset(), 0)
            } else {
                scroll(0, valuePx - computeVerticalScrollOffset())
            }
        }

        private fun DivRecyclerView.scrollRange(): Int {
            return if (itemHelper.isHorizontal) {
                computeHorizontalScrollRange() - availableWidth
            } else {
                computeVerticalScrollRange() - availableHeight
            }
        }

        private val DivRecyclerView.itemHelper get() = layoutManager as DivGalleryItemHelper
    }

    /**
     * Implementation of [DivViewWithItems] specific for div pager.
     */
    internal class Pager(private val view: DivPagerView) : DivViewWithItems(view) {

        override val itemCount: Int
            get() = view.viewPager.adapter?.itemCount ?: 0

        override fun setCurrentItem(index: Int, animated: Boolean) {
            checkItem(index, itemCount) {
                view.viewPager.setCurrentItem(index, animated)
            }
        }

        override fun getNearestItem(direction: ScrollDirection): Int = view.viewPager.currentItem + direction.value

        override fun scrollToTheEnd(animated: Boolean) = view.viewPager.setCurrentItem(itemCount - 1, animated)

        override fun getIndicesOfItemWithId(id: String): List<Int> {
            val adapter = view.viewPager.adapter as? DivPagerAdapter ?: return emptyList()
            return adapter.visibleItems.getIndicesWithId(id) { div }
                .map { adapter.getPosition(it) }
        }
    }

    /**
     * Implementation of [DivViewWithItems] specific for div tabs.
     */
    internal class Tabs(private val view: DivTabsLayout) : DivViewWithItems(view) {

        override val itemCount: Int
            get() = view.viewPager.adapter?.count ?: 0

        override fun setCurrentItem(index: Int, animated: Boolean) {
            checkItem(index, itemCount) {
                view.viewPager.setCurrentItem(index, animated)
            }
        }

        override fun getNearestItem(direction: ScrollDirection) = view.viewPager.currentItem + direction.value

        override fun scrollToTheEnd(animated: Boolean) = view.viewPager.setCurrentItem(itemCount - 1, animated)

        override fun getIndicesOfItemWithId(id: String) =
            view.divTabsAdapter?.tabDivs?.getIndicesWithId(id) { this } ?: emptyList()
    }

    companion object {

        fun create(view: DivScrollActionHolder): DivViewWithItems? {
            return when (view) {
                is DivRecyclerView -> Gallery(view)
                is DivPagerView -> Pager(view)
                is DivTabsLayout -> Tabs(view)
                else -> null
            }
        }

        private inline fun checkItem(item: Int, itemCount: Int, block: () -> Unit) {
            if (item !in 0 until itemCount) {
                KAssert.fail { "$item is not in range [0, $itemCount)" }
            } else {
                block()
            }
        }

        private fun <T> List<T>.getIndicesWithId(id: String, div: T.() -> Div) =
            mapIndexedNotNull { index, item -> if (item.div().value().id == id) index else null }
    }
}
