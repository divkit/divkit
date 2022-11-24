package com.yandex.div.internal.widget.tabs

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.widget.FrameLayout
import com.yandex.div.R

/**
 * An auxiliary view, that embraces [ViewPager] and controls its height by setting
 * its own height. The height value is calculated at the latest moment possible. It is assumed
 * that a calculator delegate would iterate over pager contents and estimate its height.
 */
internal class ViewPagerFixedSizeLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var heightCalculator: HeightCalculator? = null
    private var _collapsiblePaddingBottom = 0

    // TODO(gulevsky): notify padding changed
    var collapsiblePaddingBottom: Int
        get() = _collapsiblePaddingBottom
        set(padding) {
            if (_collapsiblePaddingBottom != padding) {
                _collapsiblePaddingBottom = padding
            }
        }

    fun setHeightCalculator(heightCalculator: HeightCalculator?) {
        this.heightCalculator = heightCalculator
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightSpec = heightMeasureSpec
        if (heightCalculator != null) {
            val newHeight = heightCalculator!!.measureHeight(widthMeasureSpec, heightMeasureSpec)
            heightSpec = MeasureSpec.makeMeasureSpec(newHeight, MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec, heightSpec)
    }

    interface HeightCalculator {
        fun setPositionAndOffsetForMeasure(position: Int, positionOffset: Float)
        fun measureHeight(widthMeasureSpec: Int, heightMeasureSpec: Int): Int
        fun shouldRequestLayoutOnScroll(position: Int, positionOffset: Float): Boolean
        fun dropMeasureCache()
        fun saveInstanceState(container: SparseArray<Parcelable>)
        fun restoreInstanceState(container: SparseArray<Parcelable>)
    }
}
