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

    val titleLayout: TabTitlesLayoutView<*> = createTitleLayout<Any>(context)
    val divider: View = createDivider(context)
    val pagerLayout: ViewPagerFixedSizeLayout = createPagerLayout(context)
    val viewPager: ScrollableViewPager = createViewPager(context)
    private val bottomView = createBottomView(context)
    var divTabsAdapter: DivTabsAdapter? = null

    init {
        id = R.id.div_tabs_block
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        orientation = VERTICAL

        pagerLayout.apply {
            addView(viewPager)
            addView(bottomView)
        }

        addView(titleLayout)
        addView(divider)
        addView(pagerLayout)
    }

    override fun getAccessibilityClassName() = "android.widget.TabWidget"

    private companion object {
        private fun <T> createTitleLayout(context: Context): TabTitlesLayoutView<T> {
            return TabTitlesLayoutView<T>(context, null, R.attr.divTabIndicatorLayoutStyle).apply {
                id = R.id.base_tabbed_title_container_scroller
                layoutParams = createTitleLayoutParams(context)
                val vertical = resources.getDimensionPixelSize(R.dimen.title_tab_title_margin_vertical)
                val horizontal = resources.getDimensionPixelSize(R.dimen.title_tab_title_margin_horizontal)
                setPadding(horizontal, vertical, horizontal, vertical)
                clipToPadding = false
            }
        }

        private fun createTitleLayoutParams(context: Context): LayoutParams {
            val resources = context.resources
            return LayoutParams(
                LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelSize(R.dimen.title_tab_title_height)
            ).apply {
                gravity = Gravity.START
            }
        }

        private fun createDivider(context: Context): View {
            return View(context).apply {
                id = R.id.div_tabs_divider
                layoutParams = createDividerLayoutParams(context)
                setBackgroundResource(R.color.div_separator_color)
            }
        }

        private fun createDividerLayoutParams(context: Context): LayoutParams {
            val resources = context.resources
            return LayoutParams(
                LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelSize(R.dimen.div_separator_delimiter_height)
            ).apply {
                leftMargin = resources.getDimensionPixelSize(R.dimen.div_horizontal_padding)
                rightMargin = leftMargin
                topMargin = resources.getDimensionPixelSize(R.dimen.title_tab_title_separator_margin_top)
                bottomMargin = resources.getDimensionPixelSize(R.dimen.title_tab_title_margin_vertical)
            }
        }

        private fun createPagerLayout(context: Context): ViewPagerFixedSizeLayout {
            return ViewPagerFixedSizeLayout(context).apply {
                id = R.id.div_tabs_container_helper
                layoutParams = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                collapsiblePaddingBottom = 0
            }
        }

        private fun createViewPager(context: Context): ScrollableViewPager {
            return ScrollableViewPager(context).apply {
                id = R.id.div_tabs_pager_container
                layoutParams = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                overScrollMode = View.OVER_SCROLL_NEVER
                ViewCompat.setNestedScrollingEnabled(this, true)
            }
        }

        private fun createBottomView(context: Context): View {
            return FrameLayout(context).apply {
                layoutParams = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                visibility = GONE
            }
        }
    }
}
