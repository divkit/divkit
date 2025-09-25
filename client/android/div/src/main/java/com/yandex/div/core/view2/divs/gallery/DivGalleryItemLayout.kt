package com.yandex.div.core.view2.divs.gallery

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div.core.widget.DivViewWrapper
import com.yandex.div.core.widget.makeAtMostSpec
import com.yandex.div.core.widget.makeExactSpec
import com.yandex.div.core.widget.makeUnspecifiedSpec
import com.yandex.div.internal.widget.DivLayoutParams
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

internal class DivGalleryItemLayout(
    context: Context
) : DivViewWrapper(context) {

    var orientation: () -> Int = { RecyclerView.HORIZONTAL }
    var columnCount: () -> Int = { 1 }
    var crossSpacing: () -> Float = { 0f }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        child?.let {
            val lp = it.lp
            val recyclerView = parent as DivRecyclerView
            val isHorizontal = orientation() == RecyclerView.HORIZONTAL
            val widthSpec = getMeasureSpec(
                recyclerView.widthMeasureSpec,
                recyclerView.paddingLeft + recyclerView.paddingRight,
                lp.width,
                it.minimumWidth,
                lp.maxWidth,
                lp.horizontalMargins,
                isHorizontal,
            )
            val heightSpec = getMeasureSpec(
                recyclerView.heightMeasureSpec,
                recyclerView.paddingTop + recyclerView.paddingBottom,
                lp.height,
                it.minimumHeight,
                lp.maxHeight,
                lp.verticalMargins,
                !isHorizontal,
            )
            super.onMeasure(widthSpec, heightSpec)
            return
        }

        val minWidth = paddingLeft + suggestedMinimumWidth + paddingRight
        val minHeight = paddingTop + suggestedMinimumHeight + paddingBottom
        setMeasuredDimension(
            resolveSizeAndState(minWidth, widthMeasureSpec, 0),
            resolveSizeAndState(minHeight, heightMeasureSpec, 0 shl MEASURED_HEIGHT_STATE_SHIFT)
        )
    }

    private fun getMeasureSpec(
        parentSpec: Int,
        paddings: Int,
        size: Int,
        minSize: Int,
        maxSize: Int,
        margins: Int,
        alongScrollAxis: Boolean,
    ): Int {
        val parentSize = (MeasureSpec.getSize(parentSpec) - paddings).let {
            if (alongScrollAxis) it else ((it - crossSpacing() * (columnCount() - 1)) / columnCount()).roundToInt()
        }
        val actualMaxSize = if (maxSize == DivLayoutParams.DEFAULT_MAX_SIZE) maxSize else maxSize + margins
        return when (size) {
            LayoutParams.MATCH_PARENT -> makeExactSpec(min(max(parentSize, minSize + margins), actualMaxSize))
            LayoutParams.WRAP_CONTENT -> {
                if (maxSize == DivLayoutParams.DEFAULT_MAX_SIZE) {
                    makeUnspecifiedSpec()
                } else {
                    makeAtMostSpec(actualMaxSize)
                }
            }
            DivLayoutParams.WRAP_CONTENT_CONSTRAINED ->
                makeAtMostSpec(min(max(parentSize, minSize + margins), actualMaxSize))
            else -> makeExactSpec(size + margins)
        }
    }
}
