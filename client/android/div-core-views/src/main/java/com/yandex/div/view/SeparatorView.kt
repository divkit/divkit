package com.yandex.div.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.IntDef
import androidx.core.content.ContextCompat
import com.yandex.div.core.util.Assert

open class SeparatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val dividerPaint = Paint().apply { color = Color.TRANSPARENT }
    private val dividerRect = Rect()
    private var isDividerRectChanged = false

    var isHorizontal: Boolean = true
        set(value) {
            if (field != value) {
                field = value
                isDividerRectChanged = true
                requestLayout()
            }
        }

    var dividerThickness: Int = 0
        set(height) {
            if (field != height) {
                field = height
                isDividerRectChanged = true
                requestLayout()
            }
        }

    @DividerGravity
    var dividerGravity = Gravity.CENTER
        set(@DividerGravity dividerGravity) {
            if (field != dividerGravity) {
                field = dividerGravity
                isDividerRectChanged = true
                invalidate()
            }
        }

    var dividerColor: Int
        get() = dividerPaint.color
        set(color) {
            if (dividerPaint.color != color) {
                dividerPaint.color = color
                invalidate()
            }
        }

    private val isDividerVisible: Boolean
        get() = Color.alpha(dividerPaint.color) > 0

    @IntDef(Gravity.START, Gravity.CENTER, Gravity.END)
    annotation class DividerGravity

    fun setDividerColorResource(@ColorRes resId: Int) {
        dividerColor = ContextCompat.getColor(context, resId)
    }

    fun setDividerHeightResource(@DimenRes resId: Int) {
        dividerThickness = resources.getDimensionPixelSize(resId)
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        isDividerRectChanged = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var contentWidth = paddingLeft + paddingRight
        var contentHeight = paddingTop + paddingBottom
        if (isHorizontal) {
            contentHeight += dividerThickness
        } else {
            contentWidth += dividerThickness
        }
        setMeasuredDimension(
            calcSize(Math.max(contentWidth, suggestedMinimumWidth), widthMeasureSpec),
            calcSize(Math.max(contentHeight, suggestedMinimumHeight), heightMeasureSpec)
        )
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        isDividerRectChanged = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isDividerVisible) {
            updateDividerRect()
            canvas.drawRect(dividerRect, dividerPaint)
        }
    }

    private fun updateDividerRect() {
        if (!isDividerRectChanged) return

        val paddingStart = if (isHorizontal) paddingTop else paddingLeft
        val paddingEnd = if (isHorizontal) paddingBottom else paddingRight
        val size = if (isHorizontal) height else width
        val contentSize = size - paddingStart - paddingEnd


        val rectStart = when (dividerGravity) {
            Gravity.START -> paddingStart
            Gravity.END -> size - paddingEnd - dividerThickness
            Gravity.CENTER -> paddingStart + (contentSize - dividerThickness) / 2
            else -> {
                Assert.fail("Unknown divider gravity value")
                0
            }
        }
        if (isHorizontal) {
            dividerRect.top = rectStart
            dividerRect.bottom = rectStart + Math.min(contentSize, dividerThickness)
            dividerRect.left = paddingLeft
            dividerRect.right = width - paddingRight
        } else {
            dividerRect.left = rectStart
            dividerRect.right = rectStart + Math.min(contentSize, dividerThickness)
            dividerRect.top = paddingTop
            dividerRect.bottom = height - paddingBottom
        }

        isDividerRectChanged = false
    }

    private fun calcSize(size: Int, measureSpec: Int): Int {
        val specMode = View.MeasureSpec.getMode(measureSpec)
        val specSize = View.MeasureSpec.getSize(measureSpec)

        return when (specMode) {
            View.MeasureSpec.UNSPECIFIED -> size
            View.MeasureSpec.AT_MOST -> Math.min(size, specSize)
            View.MeasureSpec.EXACTLY -> specSize
            else -> size
        }
    }
}
