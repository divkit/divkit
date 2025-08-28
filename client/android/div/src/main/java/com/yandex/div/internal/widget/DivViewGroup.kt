package com.yandex.div.internal.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
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
                newGravity = newGravity or Gravity.START
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
            horizontalPaddings,
            lp.width,
            child.minimumWidth,
            lp.maxWidth
        )
        val childHeightMeasureSpec = getChildMeasureSpec(
            parentHeightMeasureSpec,
            verticalPaddings,
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
            horizontalPaddings + lp.horizontalMargins + widthUsed,
            lp.width,
            child.minimumWidth,
            lp.maxWidth
        )
        val childHeightMeasureSpec = getChildMeasureSpec(
            parentHeightMeasureSpec,
            verticalPaddings + lp.verticalMargins + heightUsed,
            lp.height,
            child.minimumHeight,
            lp.maxHeight
        )
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
    }

    internal val horizontalGravity get() = gravity.toHorizontalGravity()

    internal val verticalGravity get() = gravity.toVerticalGravity()

    internal val horizontalPaddings get() = paddingLeft + paddingRight

    internal val verticalPaddings get() = paddingTop + paddingBottom

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
                        resultSize = min(max(size, minSize), maxSize)
                        resultMode = MeasureSpec.EXACTLY
                    }

                    LayoutParams.WRAP_CONTENT -> {
                        if (maxSize == DivLayoutParams.DEFAULT_MAX_SIZE) {
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
                        resultSize = min(max(size, minSize), maxSize)
                        resultMode = MeasureSpec.AT_MOST
                    }

                    LayoutParams.WRAP_CONTENT -> {
                        if (maxSize == DivLayoutParams.DEFAULT_MAX_SIZE) {
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
                        resultSize = min(max(size, minSize), maxSize)
                        resultMode = MeasureSpec.AT_MOST
                    }

                    LayoutParams.WRAP_CONTENT, DivLayoutParams.WRAP_CONTENT_CONSTRAINED -> {
                        if (maxSize == DivLayoutParams.DEFAULT_MAX_SIZE) {
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

        internal fun getSpaceAroundPart(freeSpace: Float, childCount: Int) = freeSpace / (childCount * 2)

        internal fun getSpaceBetweenPart(freeSpace: Float, childCount: Int) =
            if (childCount == 1) 0f else freeSpace / (childCount - 1)

        internal fun getSpaceEvenlyPart(freeSpace: Float, childCount: Int) = freeSpace / (childCount + 1)
    }

    internal inner class OffsetsHolder(
        var firstChildOffset: Float = 0f,
        var spaceBetweenChildren: Float = 0f,
        var edgeDividerOffset: Int = 0
    ) {
        fun update(freeSpace: Float, gravity: Int, childCount: Int) {
            firstChildOffset = 0f
            spaceBetweenChildren = 0f
            edgeDividerOffset = 0
            when (gravity) {
                Gravity.LEFT, Gravity.TOP -> Unit
                Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL -> firstChildOffset = freeSpace / 2
                Gravity.RIGHT, Gravity.BOTTOM -> firstChildOffset = freeSpace
                DivGravity.SPACE_AROUND_HORIZONTAL, DivGravity.SPACE_AROUND_VERTICAL -> {
                    getSpaceAroundPart(freeSpace, childCount).let {
                        firstChildOffset = it
                        spaceBetweenChildren = it * 2
                        edgeDividerOffset = (it / 2).toInt()
                    }
                }

                DivGravity.SPACE_BETWEEN_HORIZONTAL, DivGravity.SPACE_BETWEEN_VERTICAL ->
                    spaceBetweenChildren = getSpaceBetweenPart(freeSpace, childCount)

                DivGravity.SPACE_EVENLY_HORIZONTAL, DivGravity.SPACE_EVENLY_VERTICAL -> {
                    getSpaceEvenlyPart(freeSpace, childCount).let {
                        firstChildOffset = it
                        spaceBetweenChildren = it
                        edgeDividerOffset = (it / 2).toInt()
                    }
                }

                else -> throw java.lang.IllegalStateException("Invalid gravity is set: $gravity")
            }
        }
    }
}
