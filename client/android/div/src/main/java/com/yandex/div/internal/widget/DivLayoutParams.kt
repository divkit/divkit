package com.yandex.div.internal.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import com.yandex.div.internal.util.PositiveNumberDelegate

class DivLayoutParams: MarginLayoutParams {

    var gravity = DEFAULT_GRAVITY
    var isBaselineAligned = false

    var verticalWeight = DEFAULT_WEIGHT
    var horizontalWeight = DEFAULT_WEIGHT

    var columnSpan by PositiveNumberDelegate(DEFAULT_SPAN)
    var rowSpan by PositiveNumberDelegate(DEFAULT_SPAN)
    var maxHeight = Integer.MAX_VALUE
    var maxWidth = Integer.MAX_VALUE

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(width: Int, height: Int) : super(width, height)

    constructor(source: ViewGroup.LayoutParams?) : super(source)

    constructor(source: MarginLayoutParams?) : super(source)

    constructor(source: DivLayoutParams) : super(source) {
        gravity = source.gravity
        isBaselineAligned = source.isBaselineAligned
        verticalWeight = source.verticalWeight
        horizontalWeight = source.horizontalWeight
        columnSpan = source.columnSpan
        rowSpan = source.rowSpan
        maxHeight = source.maxHeight
        maxWidth = source.maxWidth
    }

    internal val horizontalMargins get() = leftMargin + rightMargin

    internal val verticalMargins get() = topMargin + bottomMargin

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as DivLayoutParams
        return width == other.width &&
                height == other.height &&
                leftMargin == other.leftMargin &&
                rightMargin == other.rightMargin &&
                topMargin == other.topMargin &&
                bottomMargin == other.bottomMargin &&
                gravity == other.gravity &&
                isBaselineAligned == other.isBaselineAligned &&
                columnSpan == other.columnSpan &&
                rowSpan == other.rowSpan &&
                verticalWeight == other.verticalWeight &&
                horizontalWeight == other.horizontalWeight &&
                maxHeight == other.maxHeight &&
                maxWidth == other.maxWidth
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + gravity
        result = 31 * result + if (isBaselineAligned) 1 else 0
        result = 31 * result + columnSpan
        result = 31 * result + rowSpan
        result = 31 * result + verticalWeight.toBits()
        result = 31 * result + horizontalWeight.toBits()
        result = 31 * result + if (maxHeight != Int.MAX_VALUE) maxHeight else 0
        result = 31 * result + if (maxWidth != Int.MAX_VALUE) maxWidth else 0
        return result
    }

    companion object {
        const val DEFAULT_GRAVITY = Gravity.START or Gravity.TOP
        const val DEFAULT_WEIGHT = 0f
        const val DEFAULT_SPAN = 1
        const val WRAP_CONTENT_CONSTRAINED = -3
    }
}
