package com.yandex.div.core.view2.divs.gallery

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div.core.widget.DivViewWrapper
import com.yandex.div.core.widget.isUnspecified
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
        val child = child ?: return setEmptySize(widthMeasureSpec, heightMeasureSpec)

        val lp = child.lp
        val recyclerView = parent as DivRecyclerView
        val isHorizontal = orientation() == RecyclerView.HORIZONTAL
        val widthSpec = getMeasureSpec(
            recyclerView.widthMeasureSpec,
            recyclerView.paddingLeft + recyclerView.paddingRight,
            lp.width,
            child.minimumWidth,
            lp.maxWidth,
            lp.horizontalMargins,
            isHorizontal,
            recyclerView.considerMatchParent,
        )
        val heightSpec = getMeasureSpec(
            recyclerView.heightMeasureSpec,
            recyclerView.paddingTop + recyclerView.paddingBottom,
            lp.height,
            child.minimumHeight,
            lp.maxHeight,
            lp.verticalMargins,
            !isHorizontal,
            recyclerView.considerMatchParent,
        )

        if (widthSpec == null || heightSpec == null) {
            setMeasuredDimension(0, 0)
        } else {
            super.onMeasure(widthSpec, heightSpec)
            requestGalleryRemeasureIfNeeded(recyclerView, isHorizontal)
        }
    }

    private fun requestGalleryRemeasureIfNeeded(recyclerView: DivRecyclerView, isHorizontal: Boolean) {
        val galleryCrossAxisSpec = recyclerView.parentCrossAxisMeasureSpec
        val galleryCrossAxisMode = MeasureSpec.getMode(galleryCrossAxisSpec)
        if (galleryCrossAxisMode == MeasureSpec.EXACTLY) return

        val galleryPaddings = if (isHorizontal) {
            recyclerView.paddingTop + recyclerView.paddingBottom
        } else {
            recyclerView.paddingLeft + recyclerView.paddingRight
        }
        val itemCrossAxisSize = if (isHorizontal) measuredHeight else measuredWidth
        val requiredCrossAxisSize = itemCrossAxisSize * columnCount() + totalCrossSpacing() + galleryPaddings
        val targetCrossAxisSize = if (galleryCrossAxisMode == MeasureSpec.AT_MOST) {
            min(requiredCrossAxisSize, MeasureSpec.getSize(galleryCrossAxisSpec))
        } else {
            requiredCrossAxisSize
        }

        recyclerView.requestCrossAxisRemeasure(targetCrossAxisSize, isHorizontal)
    }

    private fun totalCrossSpacing(): Int {
        val halfCrossSpacing = crossSpacing().roundToInt() / 2
        return 2 * (columnCount() - 1) * halfCrossSpacing
    }

    private fun setEmptySize(widthMeasureSpec: Int, heightMeasureSpec: Int) {
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
        considerMatchParent: Boolean,
    ): Int? {
        val parentSize = (MeasureSpec.getSize(parentSpec) - paddings).let {
            if (alongScrollAxis) it else {
                (it - totalCrossSpacing()) / columnCount()
            }
        }
        val actualMaxSize = if (maxSize == DivLayoutParams.DEFAULT_MAX_SIZE) maxSize else maxSize + margins
        val actualSize = when {
            alongScrollAxis -> size
            size != LayoutParams.MATCH_PARENT -> size
            !isUnspecified(parentSpec) -> size
            considerMatchParent -> LayoutParams.WRAP_CONTENT
            else -> return null
        }

        return when (actualSize) {
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
