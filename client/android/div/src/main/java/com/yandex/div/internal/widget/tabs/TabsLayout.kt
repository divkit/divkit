package com.yandex.div.internal.widget.tabs

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.core.view.children
import com.yandex.div.R
import com.yandex.div.core.Disposable
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.view2.divs.tabs.DivTabsAdapter
import com.yandex.div.core.view2.divs.updateBorderDrawer
import com.yandex.div.core.view2.divs.widgets.DivBorderDrawer
import com.yandex.div.core.view2.divs.widgets.DivBorderSupports
import com.yandex.div.core.view2.divs.widgets.drawClipped
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBorder
import com.yandex.div2.DivTabs

@Mockable
internal class TabsLayout
@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs), DivBorderSupports, ExpressionSubscriber {
    val titleLayout: TabTitlesLayoutView<*>
    val divider: View
    val pagerLayout: ViewPagerFixedSizeLayout
    val viewPager: ScrollableViewPager
    var divTabsAdapter: DivTabsAdapter? = null
    var div: DivTabs? = null

    private var borderDrawer: DivBorderDrawer? = null
    override val border: DivBorder?
        get() = borderDrawer?.border

    override fun getDivBorderDrawer() = borderDrawer

    override val subscriptions = mutableListOf<Disposable>()

    private var isDrawing = false

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

    private fun createTitleLayoutParams(): LinearLayout.LayoutParams {
        return LayoutParams(LayoutParams.MATCH_PARENT, resources.getDimensionPixelSize(R.dimen.title_tab_title_height)).apply {
            gravity = Gravity.START
        }
    }

    private fun createDividerLayoutParams(): LinearLayout.LayoutParams {
        return LayoutParams(LayoutParams.MATCH_PARENT, resources.getDimensionPixelSize(R.dimen.div_separator_delimiter_height)).apply {
            leftMargin = resources.getDimensionPixelSize(R.dimen.div_horizontal_padding)
            rightMargin = leftMargin
            topMargin = resources.getDimensionPixelSize(R.dimen.title_tab_title_separator_margin_top)
            bottomMargin = resources.getDimensionPixelSize(R.dimen.title_tab_title_margin_vertical)
        }
    }

    override fun setBorder(border: DivBorder?, resolver: ExpressionResolver) {
        borderDrawer = updateBorderDrawer(border, resolver)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        borderDrawer?.onBoundsChanged(w, h)
    }

    override fun draw(canvas: Canvas) {
        isDrawing = true
        borderDrawer.drawClipped(canvas) { super.draw(canvas) }
        isDrawing = false
    }

    override fun dispatchDraw(canvas: Canvas) {
        children.forEach { child ->
            (child as? DivBorderSupports)?.getDivBorderDrawer()?.drawShadow(canvas)
        }
        if (isDrawing) {
            super.dispatchDraw(canvas)
        } else {
            borderDrawer.drawClipped(canvas) { super.dispatchDraw(canvas) }
        }
    }

    override fun release() {
        super.release()
        borderDrawer?.release()
    }
}
