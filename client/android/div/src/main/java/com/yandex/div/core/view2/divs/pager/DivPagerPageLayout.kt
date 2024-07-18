package com.yandex.div.core.view2.divs.pager

import android.annotation.SuppressLint
import android.content.Context
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.core.util.makeFocusable
import com.yandex.div.core.widget.makeUnspecifiedSpec
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div.internal.widget.FrameContainerLayout

@SuppressLint("ViewConstructor")
internal class DivPagerPageLayout(
    context: Context,
    private val orientationProvider: () -> Int
) : FrameContainerLayout(context) {

    init {
        makeFocusable()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // When the page cross size is equal to MATCH_PARENT or WRAP_CONTENT_CONSTRAINED,
        // the page must be the size of a pager minus padding,
        // otherwise the page size should be enough for its own content regardless of the current pager height
        if (childCount == 0) return super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val lp = getChildAt(0).layoutParams
        val isHorizontal = orientationProvider() == ViewPager2.ORIENTATION_HORIZONTAL
        val widthSpec = getSpec(lp.width, widthMeasureSpec, isHorizontal)
        val heightSpec = getSpec(lp.height, heightMeasureSpec, !isHorizontal)
        super.onMeasure(widthSpec, heightSpec)
    }

    private fun getSpec(size: Int, parentSpec: Int, alongScrollAxis: Boolean): Int {
        return when {
            alongScrollAxis -> parentSpec
            size == LayoutParams.MATCH_PARENT -> parentSpec
            size == DivLayoutParams.WRAP_CONTENT_CONSTRAINED -> parentSpec
            else -> makeUnspecifiedSpec()
        }
    }
}
