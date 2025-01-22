package com.yandex.div.core.view2.divs.pager

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

internal class PageItemDecoration(
    paddings: DivPagerPaddingsHolder,
    sizeProvider: DivPagerPageSizeProvider,
    isHorizontal: Boolean,
) : RecyclerView.ItemDecoration() {

    private val offsetLeft = (if (!isHorizontal) paddings.left else sizeProvider.neighbourSize).roundToInt()

    private val offsetTop = (if (isHorizontal) paddings.top else sizeProvider.neighbourSize).roundToInt()

    private val offsetRight = (if (!isHorizontal) paddings.right else sizeProvider.neighbourSize).roundToInt()

    private val offsetBottom = (if (isHorizontal) paddings.bottom else sizeProvider.neighbourSize).roundToInt()

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) =
        outRect.set(offsetLeft, offsetTop, offsetRight, offsetBottom)
}
