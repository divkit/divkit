package com.yandex.div.core.view2.divs.pager

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.widget.makeExactSpec
import kotlin.math.roundToInt

internal class WrapContentPageSizeItemDecoration(
    private val parentSize: Int,
    private val paddings: DivPagerPaddingsHolder,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val parentWidth = parent.width - paddings.run { left + right }.roundToInt()
        val parentHeight = parent.height - paddings.run { top + bottom }.roundToInt()
        view.measure(makeExactSpec(parentWidth), makeExactSpec(parentHeight))
        val child = (view as? DivPagerPageLayout)?.child ?: return
        val left = paddings.alignedLeft
            ?: leadingOffset(trailingOffset = paddings.alignedRight, size = child.measuredWidth)
        val top = paddings.alignedTop
            ?: leadingOffset(trailingOffset = paddings.alignedBottom, size = child.measuredHeight)
        outRect.set(
            left,
            top,
            paddings.alignedRight ?: (parentSize - child.measuredWidth - left),
            paddings.alignedBottom ?: (parentSize - child.measuredHeight - top),
        )
    }

    private fun leadingOffset(trailingOffset: Int?, size: Int) =
        trailingOffset?.let { parentSize - size - it } ?: ((parentSize - size) / 2f).roundToInt()
}
