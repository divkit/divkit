package com.yandex.div.core.view2.divs.pager

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.widget.makeExactSpec
import com.yandex.div2.DivPager.ItemAlignment
import kotlin.math.roundToInt

internal class WrapContentPageSizeItemDecoration(
    private val parentSize: Int,
    private val paddings: DivPagerPaddingsHolder,
    private val alignment: ItemAlignment,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val parentWidth = parent.width - paddings.run { left + right }.roundToInt()
        val parentHeight = parent.height - paddings.run { top + bottom }.roundToInt()
        view.measure(makeExactSpec(parentWidth), makeExactSpec(parentHeight))
        val child = (view as? DivPagerPageLayout)?.child ?: return
        outRect.set(
            paddings.alignedLeft ?: child.horizontalOffset,
            paddings.alignedTop ?: child.topOffset,
            paddings.alignedRight ?: child.horizontalOffset,
            paddings.alignedBottom ?: child.bottomOffset
        )
    }

    private val View.horizontalOffset: Int get() {
        return when (alignment) {
            ItemAlignment.START -> parentSize - paddings.start - measuredWidth
            ItemAlignment.CENTER -> (parentSize - measuredWidth) / 2f
            ItemAlignment.END -> parentSize - paddings.end - measuredWidth
        }.roundToInt()
    }

    private val View.topOffset: Int get() {
        return when (alignment) {
            ItemAlignment.START -> paddings.start
            ItemAlignment.CENTER -> (parentSize - measuredHeight) / 2f
            ItemAlignment.END -> parentSize - paddings.end - measuredHeight
        }.roundToInt()
    }

    private val View.bottomOffset: Int get() {
        return when (alignment) {
            ItemAlignment.START -> parentSize - paddings.start - measuredHeight
            ItemAlignment.CENTER -> (parentSize - measuredHeight) / 2f
            ItemAlignment.END -> paddings.end
        }.roundToInt()
    }
}
