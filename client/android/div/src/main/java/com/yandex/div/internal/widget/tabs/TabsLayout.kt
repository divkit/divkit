package com.yandex.div.internal.widget.tabs

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import com.yandex.div.R
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.view2.divs.tabs.DivTabsAdapter

@Mockable
@Suppress("LeakingThis")
internal open class TabsLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    val titleLayout: TabTitlesLayoutView<*>
    val divider: View
    val pagerLayout: ViewPagerFixedSizeLayout
    val viewPager: ScrollableViewPager
    var divTabsAdapter: DivTabsAdapter? = null

    init {
        id = R.id.div_tabs_block
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        orientation = VERTICAL

        titleLayout = TabTitlesLayoutView<Any>(context, null, R.attr.divTabIndicatorLayoutStyle).apply {
            id = R.id.base_tabbed_title_container_scroller
            layoutParams = createTitleLayoutParams()
            val vertical = resources.getDimensionPixelSize(R.dimen.title_tab_title_margin_vertical)
            val horizontal = resources.getDimensionPixelSize(R.dimen.title_tab_title_margin_horizontal)
            setPadding(horizontal, vertical, horizontal, vertical)
            clipToPadding = false
        }

        divider = View(context).apply {
            id = R.id.div_tabs_divider
            layoutParams = createDividerLayoutParams()
            setBackgroundResource(R.color.div_separator_color)
        }

        viewPager = ScrollableViewPager(context).apply {
            id = R.id.div_tabs_pager_container
            layoutParams = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            overScrollMode = View.OVER_SCROLL_NEVER
            ViewCompat.setNestedScrollingEnabled(this, true)
        }

        pagerLayout = ViewPagerFixedSizeLayout(context).apply {
            id = R.id.div_tabs_container_helper
            layoutParams = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            collapsiblePaddingBottom = 0

            val bottomView = FrameLayout(context).apply {
                layoutParams = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                visibility = GONE
            }

            addView(viewPager)
            addView(bottomView)
        }

        addView(titleLayout)
        addView(divider)
        addView(pagerLayout)
    }

    private fun createTitleLayoutParams(): LayoutParams {
        return LayoutParams(LayoutParams.MATCH_PARENT, resources.getDimensionPixelSize(R.dimen.title_tab_title_height)).apply {
            gravity = Gravity.START
        }
    }

    private fun createDividerLayoutParams(): LayoutParams {
        return LayoutParams(LayoutParams.MATCH_PARENT, resources.getDimensionPixelSize(R.dimen.div_separator_delimiter_height)).apply {
            leftMargin = resources.getDimensionPixelSize(R.dimen.div_horizontal_padding)
            rightMargin = leftMargin
            topMargin = resources.getDimensionPixelSize(R.dimen.title_tab_title_separator_margin_top)
            bottomMargin = resources.getDimensionPixelSize(R.dimen.title_tab_title_margin_vertical)
        }
    }
}
