package com.yandex.div.core.view2.divs.pager

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

internal class FixedPageSizeItemDecoration(
    private val paddings: DivPagerPaddingsHolder,
    private val sizeProvider: FixedPageSizeProvider,
) : RecyclerView.ItemDecoration() {

    private val offsetLeft: Int get() = paddings.alignedLeft.toOffset()

    private val offsetTop: Int get() = paddings.alignedTop.toOffset()

    private val offsetRight: Int get() = paddings.alignedRight.toOffset()

    private val offsetBottom: Int get() = paddings.alignedBottom.toOffset()

    private fun Int?.toOffset() = this ?: sizeProvider.neighbourSize.roundToInt()

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) =
        outRect.set(offsetLeft, offsetTop, offsetRight, offsetBottom)
}
