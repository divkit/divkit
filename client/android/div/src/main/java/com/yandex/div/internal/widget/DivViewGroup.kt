package com.yandex.div.internal.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.yandex.div.core.widget.DivLayoutParams
import kotlin.math.max

abstract class DivViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    override fun measureChild(child: View, parentWidthMeasureSpec: Int, parentHeightMeasureSpec: Int) {
        val lp = child.layoutParams
        val childWidthMeasureSpec = getChildMeasureSpec(
            parentWidthMeasureSpec,
            paddingLeft + paddingRight,
            lp.width
        )
        val childHeightMeasureSpec = getChildMeasureSpec(
            parentHeightMeasureSpec,
            paddingTop + paddingBottom,
            lp.height
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
            lp.width
        )
        val childHeightMeasureSpec = getChildMeasureSpec(
            parentHeightMeasureSpec,
            paddingTop + paddingBottom + lp.topMargin + lp.bottomMargin + heightUsed,
            lp.height
        )
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
    }

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

        internal fun getChildMeasureSpec(parentMeasureSpec: Int, padding: Int, childDimension: Int): Int {
            val parentSpecMode = MeasureSpec.getMode(parentMeasureSpec)
            val parentSpecSize = MeasureSpec.getSize(parentMeasureSpec)
            val size = max(0, parentSpecSize - padding)
            var resultSize = 0
            var resultMode = 0

            when (parentSpecMode) {
                MeasureSpec.EXACTLY -> {
                    when {
                        childDimension >= 0 -> {
                            resultSize = childDimension
                            resultMode = MeasureSpec.EXACTLY
                        }

                        childDimension == LayoutParams.MATCH_PARENT -> {
                            resultSize = size
                            resultMode = MeasureSpec.EXACTLY
                        }

                        childDimension == LayoutParams.WRAP_CONTENT -> {
                            resultSize = size
                            resultMode = MeasureSpec.UNSPECIFIED
                        }

                        childDimension == DivLayoutParams.WRAP_CONTENT_CONSTRAINED -> {
                            resultSize = size
                            resultMode = MeasureSpec.AT_MOST
                        }
                    }
                }

                MeasureSpec.AT_MOST -> {
                    when {
                        childDimension >= 0 -> {
                            resultSize = childDimension
                            resultMode = MeasureSpec.EXACTLY
                        }

                        childDimension == LayoutParams.MATCH_PARENT -> {
                            resultSize = size
                            resultMode = MeasureSpec.AT_MOST
                        }

                        childDimension == LayoutParams.WRAP_CONTENT -> {
                            resultSize = size
                            resultMode = MeasureSpec.UNSPECIFIED
                        }

                        childDimension == DivLayoutParams.WRAP_CONTENT_CONSTRAINED -> {
                            resultSize = size
                            resultMode = MeasureSpec.AT_MOST
                        }
                    }
                }

                MeasureSpec.UNSPECIFIED -> {
                    when {
                        childDimension >= 0 -> {
                            resultSize = childDimension
                            resultMode = MeasureSpec.EXACTLY
                        }

                        childDimension == LayoutParams.MATCH_PARENT -> {
                            resultSize = size
                            resultMode = MeasureSpec.UNSPECIFIED
                        }

                        childDimension == LayoutParams.WRAP_CONTENT -> {
                            resultSize = size
                            resultMode = MeasureSpec.UNSPECIFIED
                        }

                        childDimension == DivLayoutParams.WRAP_CONTENT_CONSTRAINED -> {
                            resultSize = size
                            resultMode = MeasureSpec.UNSPECIFIED
                        }
                    }
                }
            }

            return MeasureSpec.makeMeasureSpec(resultSize, resultMode)
        }
    }
}
