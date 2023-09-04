package com.yandex.div.internal.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import kotlin.math.max
import kotlin.math.min

abstract class DivViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    @DivGravity
    var gravity: Int = DivLayoutParams.DEFAULT_GRAVITY
        set(value) {
            if (field == value) return

            var newGravity = value
            if ((newGravity.toHorizontalGravity()) == 0) {
                newGravity = newGravity or GravityCompat.START
            }
            if ((newGravity.toVerticalGravity()) == 0) {
                newGravity = newGravity or Gravity.TOP
            }
            field = newGravity
            requestLayout()
        }

    init {
        clipToPadding = false
    }

    override fun measureChild(child: View, parentWidthMeasureSpec: Int, parentHeightMeasureSpec: Int) {
        val lp = child.lp
        val childWidthMeasureSpec = getChildMeasureSpec(
            parentWidthMeasureSpec,
            paddingLeft + paddingRight,
            lp.width,
            child.minimumWidth,
            lp.maxWidth
        )
        val childHeightMeasureSpec = getChildMeasureSpec(
            parentHeightMeasureSpec,
            paddingTop + paddingBottom,
            lp.height,
            child.minimumHeight,
            lp.maxHeight
        )
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
    }

    override fun measureChildWithMargins(
        child: View,
        parentWidthMeasureSpec: Int,
        widthUsed: Int,
        parentHeightMeasureSpec: Int,
        heightUsed: Int
    ) {
        val lp = child.lp
        val childWidthMeasureSpec = getChildMeasureSpec(
            parentWidthMeasureSpec,
            paddingLeft + paddingRight + lp.leftMargin + lp.rightMargin + widthUsed,
            lp.width,
            child.minimumWidth,
            lp.maxWidth
        )
        val childHeightMeasureSpec = getChildMeasureSpec(
            parentHeightMeasureSpec,
            paddingTop + paddingBottom + lp.topMargin + lp.bottomMargin + heightUsed,
            lp.height,
            child.minimumHeight,
            lp.maxHeight
        )
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
    }

    internal val horizontalGravity get() = gravity.toHorizontalGravity()

    internal val verticalGravity get() = gravity.toVerticalGravity()

    override fun checkLayoutParams(p: LayoutParams?): Boolean = p is DivLayoutParams

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams = DivLayoutParams(context, attrs)

    override fun generateLayoutParams(lp: LayoutParams?): LayoutParams = when (lp) {
        is DivLayoutParams -> DivLayoutParams(lp)
        is MarginLayoutParams -> DivLayoutParams(lp)
        else -> DivLayoutParams(lp)
    }

    override fun generateDefaultLayoutParams(): LayoutParams =
        DivLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

    internal companion object {

        val View.lp inline get() = layoutParams as DivLayoutParams

        fun getChildMeasureSpec(
            parentMeasureSpec: Int,
            padding: Int,
            childDimension: Int,
            minSize: Int,
            maxSize: Int
        ): Int {
            val parentSpecMode = MeasureSpec.getMode(parentMeasureSpec)
            val parentSpecSize = MeasureSpec.getSize(parentMeasureSpec)
            val size = max(0, parentSpecSize - padding)
            var resultSize = 0
            var resultMode = 0

            when (parentSpecMode) {
                MeasureSpec.EXACTLY -> when (childDimension) {
                    in 0 .. Int.MAX_VALUE -> {
                        resultSize = childDimension
                        resultMode = MeasureSpec.EXACTLY
                    }

                    LayoutParams.MATCH_PARENT -> {
                        resultSize = size
                        resultMode = MeasureSpec.EXACTLY
                    }

                    LayoutParams.WRAP_CONTENT -> {
                        if (maxSize == Int.MAX_VALUE) {
                            resultSize = size
                            resultMode = MeasureSpec.UNSPECIFIED
                        } else {
                            resultSize = maxSize
                            resultMode = MeasureSpec.AT_MOST
                        }
                    }

                    DivLayoutParams.WRAP_CONTENT_CONSTRAINED -> {
                        resultSize = min(max(size, minSize), maxSize)
                        resultMode = MeasureSpec.AT_MOST
                    }
                }

                MeasureSpec.AT_MOST -> when (childDimension) {
                    in 0 .. Int.MAX_VALUE -> {
                        resultSize = childDimension
                        resultMode = MeasureSpec.EXACTLY
                    }

                    LayoutParams.MATCH_PARENT -> {
                        resultSize = size
                        resultMode = MeasureSpec.AT_MOST
                    }

                    LayoutParams.WRAP_CONTENT -> {
                        if (maxSize == Int.MAX_VALUE) {
                            resultSize = size
                            resultMode = MeasureSpec.UNSPECIFIED
                        } else {
                            resultSize = maxSize
                            resultMode = MeasureSpec.AT_MOST
                        }
                    }

                    DivLayoutParams.WRAP_CONTENT_CONSTRAINED -> {
                        resultSize = min(max(size, minSize), maxSize)
                        resultMode = MeasureSpec.AT_MOST
                    }
                }

                MeasureSpec.UNSPECIFIED -> when (childDimension) {
                    in 0 .. Int.MAX_VALUE -> {
                        resultSize = childDimension
                        resultMode = MeasureSpec.EXACTLY
                    }

                    LayoutParams.MATCH_PARENT -> {
                        resultSize = size
                        resultMode = MeasureSpec.UNSPECIFIED
                    }

                    LayoutParams.WRAP_CONTENT, DivLayoutParams.WRAP_CONTENT_CONSTRAINED -> {
                        if (maxSize == Int.MAX_VALUE) {
                            resultSize = size
                            resultMode = MeasureSpec.UNSPECIFIED
                        } else {
                            resultSize = maxSize
                            resultMode = MeasureSpec.AT_MOST
                        }
                    }
                }
            }

            return MeasureSpec.makeMeasureSpec(resultSize, resultMode)
        }

        @SuppressLint("WrongConstant")
        fun Int.toHorizontalGravity() = this and DivGravity.HORIZONTAL_GRAVITY_MASK

        @SuppressLint("WrongConstant")
        fun Int.toVerticalGravity() = this and DivGravity.VERTICAL_GRAVITY_MASK
    }
}
