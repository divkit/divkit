package com.yandex.divkit.demo.div

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView

class DemoNestedScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr) {

    override fun measureChild(child: View, parentWidthMeasureSpec: Int, parentHeightMeasureSpec: Int) {
        val lp = child.layoutParams as MarginLayoutParams

        val childWidthMeasureSpec: Int = getChildMeasureSpec(parentWidthMeasureSpec,
            paddingLeft + paddingRight, lp.width)
        val childHeightMeasureSpec: Int = if (lp.height == ViewGroup.LayoutParams.MATCH_PARENT) {
            getChildMeasureSpec(parentHeightMeasureSpec, paddingTop + paddingBottom, lp.height)
        } else {
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        }

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
    }

    override fun measureChildWithMargins(child: View, parentWidthMeasureSpec: Int, widthUsed: Int, parentHeightMeasureSpec: Int, heightUsed: Int) {
        val lp = child.layoutParams as MarginLayoutParams

        val childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
            paddingLeft + paddingRight + lp.leftMargin + lp.rightMargin + widthUsed, lp.width)
        val childHeightMeasureSpec = if (lp.height == ViewGroup.LayoutParams.MATCH_PARENT) {
            getChildMeasureSpec(parentHeightMeasureSpec,
                paddingTop + paddingBottom + lp.topMargin + lp.bottomMargin + heightUsed, lp.height)
        } else {
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        }

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
    }
}
