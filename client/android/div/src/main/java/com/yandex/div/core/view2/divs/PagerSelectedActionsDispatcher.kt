package com.yandex.div.core.view2.divs

import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.core.DivActionHandler.DivActionReason
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.internal.KLog
import com.yandex.div2.Div
import com.yandex.div2.DivPager

private const val TAG = "Ya:PagerSelectedActionsTracker"

/**
 * Responsible to dispatch item's `selected_actions` for [DivPager].
 */
internal class PagerSelectedActionsDispatcher(
    private val bindingContext: BindingContext,
    private val divs: List<Div>,
    private val divActionBinder: DivActionBinder
) {

    @get:VisibleForTesting
    var pageSelectionTracker: ViewPager2.OnPageChangeCallback? = null
        private set

    fun attach(viewPager: ViewPager2) {
        pageSelectionTracker = PageSelectionTracker().apply(viewPager::registerOnPageChangeCallback)
    }

    fun detach(viewPager: ViewPager2) {
        pageSelectionTracker?.let(viewPager::unregisterOnPageChangeCallback)
        pageSelectionTracker = null
    }

    private inner class PageSelectionTracker : ViewPager2.OnPageChangeCallback() {

        private var currentPage = RecyclerView.NO_POSITION
        private val selectedPages = ArrayDeque<Int>()

        override fun onPageSelected(position: Int) {
            KLog.d(TAG) { "onPageSelected($position)" }
            if (currentPage == position) {
                return
            }
            selectedPages += position
            if (currentPage == RecyclerView.NO_POSITION) {
                trackSelectedPages()
            }
            currentPage = position
        }

        override fun onPageScrollStateChanged(state: Int) {
            if (state == ViewPager2.SCROLL_STATE_IDLE) {
                trackSelectedPages()
            }
        }

        private fun trackSelectedPages() {
            while (selectedPages.isNotEmpty()) {
                val page = selectedPages.removeFirst()
                KLog.d(TAG) { "dispatch selected actions for page $page" }
                dispatchSelectedActions(divs[page])
            }
        }
    }

    private fun dispatchSelectedActions(div: Div) {
        div.value().selectedActions?.let { actions ->
            bindingContext.divView.bulkActions {
                divActionBinder.handleActions(
                    bindingContext.divView,
                    bindingContext.expressionResolver,
                    actions,
                    DivActionReason.SELECTION
                )
            }
        }
    }
}
