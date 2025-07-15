package com.yandex.div.internal.widget.indicator

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.core.view2.divs.pager.DivPagerAdapter
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.internal.widget.indicator.animations.getIndicatorAnimator
import com.yandex.div.internal.widget.indicator.forms.getIndicatorDrawer
import kotlin.math.min

internal open class PagerIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var stripDrawer: IndicatorsStripDrawer? = null
    private var divPager: DivPagerView? = null
    private var style: IndicatorParams.Style? = null

    private val onPageChangeListener: ViewPager2.OnPageChangeCallback = object: ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            stripDrawer?.let {
                // ViewPager may emit negative positionOffset for very fast scrolling
                val offset = when {
                    positionOffset < 0 -> 0f
                    positionOffset > 1 -> 1f
                    else -> positionOffset
                }
                it.onPageScrolled(position.toRealPosition(), offset)
                invalidate()
            }
        }

        override fun onPageSelected(position: Int) {
            stripDrawer?.let {
                it.onPageSelected(position.toRealPosition())
                invalidate()
            }
        }

        private fun Int.toRealPosition(): Int {
            val adapter = divPager?.viewPager?.adapter as? DivPagerAdapter ?: return this
            val count = adapter.visibleItems.size
            return (adapter.getRealPosition(this) + count) % count
        }
    }

    fun setStyle(style: IndicatorParams.Style) {
        this.style = style

        stripDrawer = IndicatorsStripDrawer(style, getIndicatorDrawer(style), getIndicatorAnimator(style), this).apply {
            calculateMaximumVisibleItems(
                measuredWidth - paddingLeft - paddingRight,
                measuredHeight - paddingTop - paddingBottom
            )
            update()
        }

        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val highestState = maxOf(
            style?.activeShape?.itemSize?.height ?: 0f,
            style?.inactiveShape?.itemSize?.height ?: 0f,
            style?.minimumShape?.itemSize?.height ?: 0f,
        )
        val desiredHeight = (highestState + paddingTop + paddingBottom).toInt()
        val measuredHeight = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> min(desiredHeight, heightSize)
            MeasureSpec.UNSPECIFIED -> desiredHeight
            else -> desiredHeight
        }

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val selectedWidth = style?.activeShape?.itemSize?.width ?: 0f
        val desiredWidth = when (val itemPlacement = style?.itemsPlacement) {
            is IndicatorParams.ItemPlacement.Default -> (itemPlacement.spaceBetweenCenters *
                (divPager?.viewPager?.adapter?.itemCount ?: 0) + selectedWidth).toInt() +
                paddingLeft + paddingRight
            is IndicatorParams.ItemPlacement.Stretch -> widthSize
            null -> selectedWidth.toInt() + paddingLeft + paddingRight
        }
        val measuredWidth = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> min(desiredWidth, widthSize)
            MeasureSpec.UNSPECIFIED -> desiredWidth
            else -> desiredWidth
        }
        setMeasuredDimension(measuredWidth, measuredHeight)

        stripDrawer?.calculateMaximumVisibleItems(
            measuredWidth - paddingLeft - paddingRight,
            measuredHeight - paddingTop - paddingBottom
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.translate(paddingLeft.toFloat(), paddingTop.toFloat())
        stripDrawer?.onDraw(canvas)
    }

    fun attachPager(newDivPager: DivPagerView) {
        divPager?.removeChangePageCallbackForIndicators(onPageChangeListener)
        newDivPager.addChangePageCallbackForIndicators(onPageChangeListener)

        if (newDivPager === divPager) return

        divPager = newDivPager
        newDivPager.viewPager.apply {
            requireNotNull(adapter) { "Attached pager adapter is null!" }
        }

        stripDrawer?.update()
        newDivPager.pagerOnItemsCountChange = DivPagerView.OnItemsUpdatedCallback {
            stripDrawer?.update()
        }
    }

    private fun IndicatorsStripDrawer.update() {
        (divPager?.viewPager?.adapter as? DivPagerAdapter)?.let {
            setItemsCount(it.visibleItems.size)
            onPageSelected(it.currentRealItem)
            invalidate()
        }
    }
}
