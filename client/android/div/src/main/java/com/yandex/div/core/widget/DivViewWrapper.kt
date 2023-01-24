package com.yandex.div.core.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import com.yandex.div.core.view2.divs.widgets.DivBorderDrawer
import com.yandex.div.core.view2.divs.widgets.DivBorderSupports
import com.yandex.div.internal.widget.TransientView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBorder

/**
 * ViewGroup that mimics its child view dimensions.
 * NOTE: there is a limited support of [LayoutParams] forwarding, so to reduce unwanted effects follow next steps:
 * - first, add [DivViewWrapper] to view hierarchy
 * - next, add child view to it.
 */
internal class DivViewWrapper @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr), DivBorderSupports, TransientView {

    override var isTransient = false
        set(value) = invalidateAfter {
            field = value
        }

    val child: View?
        get() = if (childCount == 0) null else getChildAt(0)

    override fun addView(child: View?, index: Int, params: LayoutParams?) {
        require(childCount == 0) { "ViewWrapper can host only one child view" }
        super.addView(child, 0, params)
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
        val selfLayoutParams = layoutParams ?: WrapperLayoutParams()
        return selfLayoutParams.setBy(childLayoutParams)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return layoutParams ?: WrapperLayoutParams()
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

    @Suppress("unused")
    class WrapperLayoutParams : MarginLayoutParams {

        constructor() : this(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

        constructor(width: Int, height: Int) : super(width, height)

        constructor(source: LayoutParams) : super(source)

        constructor(source: MarginLayoutParams) : super(source)
    }

    override val border: DivBorder?
        get() = (child as? DivBorderSupports)?.border

    override fun getDivBorderDrawer(): DivBorderDrawer? = (child as? DivBorderSupports)?.getDivBorderDrawer()

    override fun setBorder(border: DivBorder?, resolver: ExpressionResolver) {
        (child as? DivBorderSupports)?.setBorder(border, resolver)
    }
}

private fun LayoutParams.setBy(other: LayoutParams?): LayoutParams {
    if (other == null) {
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

    return this
}
