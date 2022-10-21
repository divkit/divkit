package com.yandex.div.core.widget.indicator

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.core.widget.indicator.animations.*
import com.yandex.div.core.widget.indicator.forms.Circle
import com.yandex.div.core.widget.indicator.forms.RoundedRect
import com.yandex.div.core.widget.indicator.forms.SingleIndicatorDrawer
import com.yandex.div.core.widget.indicator.forms.getIndicatorDrawer
import kotlin.math.min

@SuppressWarnings("rawtypes")
open class PagerIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var stripDrawer: IndicatorsStripDrawer? = null

    private var pager: ViewPager2? = null
    private var pagerAdapter: RecyclerView.Adapter<*>? = null
    private var onPageChangeListener: ViewPager2.OnPageChangeCallback? = null
    private var dataSetObserver: RecyclerView.AdapterDataObserver? = null
    private var style: IndicatorParams.Style? = null

    fun setStyle(style: IndicatorParams.Style) {
        this.style = style

        stripDrawer = IndicatorsStripDrawer(style, getIndicatorDrawer(style), getIndicatorAnimator(style))

        stripDrawer?.calculateMaximumVisibleItems(
            measuredWidth - paddingLeft - paddingRight,
            measuredHeight - paddingTop - paddingBottom
        )

        pager?.let {
            detachPager()
            attachPager(it)
        }
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val selectedHeight = style?.shape?.height ?: 0f
        val desiredHeight = (selectedHeight + paddingTop + paddingBottom).toInt()
        val measuredHeight = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> min(desiredHeight, heightSize)
            MeasureSpec.UNSPECIFIED -> desiredHeight
            else -> desiredHeight
        }

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val selectedWidth = style?.shape?.width ?: 0f
        val spaceBetweenCenters = style?.spaceBetweenCenters ?: 0.0f
        val desiredWidth = (spaceBetweenCenters * (pagerAdapter?.itemCount ?: 0) + selectedWidth).toInt() + paddingLeft + paddingRight
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

    fun attachPager(pager2: ViewPager2) {
        pagerAdapter = pager2.adapter
        if (pagerAdapter == null) {
            throw IllegalStateException("Attached pager adapter is null!")
        }
        pagerAdapter?.let {
            stripDrawer?.setItemsCount(it.itemCount)
            invalidate()
        }
        stripDrawer?.onPageSelected(pager2.currentItem)
        onPageChangeListener = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // ViewPager may emit negative positionOffset for very fast scrolling
                val offset = when {
                    positionOffset < 0 -> 0f
                    positionOffset > 1 -> 1f
                    else -> positionOffset
                }
                stripDrawer?.onPageScrolled(position, offset)
                invalidate()
            }

            override fun onPageSelected(position: Int) {
                stripDrawer?.onPageSelected(position)
                invalidate()
            }
        }.apply {
            pager2.registerOnPageChangeCallback(this)
        }
        pager = pager2
    }

    private fun detachPager() {
        onPageChangeListener?.let {
            pager?.unregisterOnPageChangeCallback(it)
        }
        dataSetObserver?.let { pagerAdapter?.unregisterAdapterDataObserver(it) }
    }
}
