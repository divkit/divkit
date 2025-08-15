package com.yandex.div.core.view2.divs.gallery

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import com.yandex.div.core.widget.DivViewWrapper
import com.yandex.div.internal.widget.DivLayoutParams

internal class DivGalleryItemLayout(
    context: Context
) : DivViewWrapper(context) {

    override fun addView(child: View?, index: Int, params: LayoutParams?) {
        super.addView(child, 0, params)

        if (layoutParams == null && child?.layoutParams != null) {
            setLayoutParams(child.layoutParams)
        }
    }

    override fun setLayoutParams(params: LayoutParams?) {
        val childView = child
        if (childView == null ) {
            super.setLayoutParams(params)
        } else {
            params?.setBy(childView.layoutParams)
            super.setLayoutParams(params)
            childView.layoutParams = params
        }
    }

    override fun checkLayoutParams(params: LayoutParams?): Boolean {
        return params == null || params != layoutParams
    }

    override fun generateLayoutParams(childLayoutParams: LayoutParams?): LayoutParams {
        val selfLayoutParams = generateDefaultLayoutParams()
        return selfLayoutParams.setBy(childLayoutParams)
    }

    override fun generateDefaultLayoutParams(): LayoutParams = when (val lp = layoutParams) {
        is DivLayoutParams -> lp
        null -> DivLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        else -> super.generateLayoutParams(lp)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val child = child
        if (child == null) {
            val minWidth = paddingLeft + suggestedMinimumWidth + paddingRight
            val minHeight = paddingTop + suggestedMinimumHeight + paddingBottom
            setMeasuredDimension(
                resolveSizeAndState(minWidth, widthMeasureSpec, 0),
                resolveSizeAndState(minHeight, heightMeasureSpec, 0 shl MEASURED_HEIGHT_STATE_SHIFT)
            )
        } else {
            child.measure(widthMeasureSpec, heightMeasureSpec)
            setMeasuredDimension(child.measuredWidthAndState, child.measuredHeightAndState)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        child?.layout(0, 0, right - left, bottom - top)
    }
}

private fun LayoutParams.setBy(other: LayoutParams?): LayoutParams {
    if (other == null || this == other) {
        return this
    }

    width = other.width
    height = other.height

    if (this is ViewGroup.MarginLayoutParams && other is ViewGroup.MarginLayoutParams) {
        leftMargin = other.leftMargin
        topMargin = other.topMargin
        rightMargin = other.rightMargin
        bottomMargin = other.bottomMargin
        if (other.isMarginRelative) {
            marginStart = other.marginStart
            marginEnd = other.marginEnd
        }
    }

    if (this is DivLayoutParams && other is DivLayoutParams) {
        maxWidth = other.maxWidth
        maxHeight = other.maxHeight
    }

    return this
}
