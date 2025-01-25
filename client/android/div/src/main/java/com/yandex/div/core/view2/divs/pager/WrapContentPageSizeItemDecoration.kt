package com.yandex.div.core.view2.divs.pager

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.widget.makeUnspecifiedSpec
import kotlin.math.roundToInt

internal class WrapContentPageSizeItemDecoration(
    private val parentSize: Int,
    paddings: DivPagerPaddingsHolder,
    isHorizontal: Boolean,
) : RecyclerView.ItemDecoration() {

    private val left = if (isHorizontal) null else paddings.left.roundToInt()
    private val top = if (isHorizontal) paddings.top.roundToInt() else null
    private val right = if (isHorizontal) null else paddings.right.roundToInt()
    private val bottom = if (isHorizontal) paddings.bottom.roundToInt() else null

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        view.measure(makeUnspecifiedSpec(), makeUnspecifiedSpec())
        val child = (view as? DivPagerPageLayout)?.child ?: return
        outRect.set(
            left ?: ((parentSize - child.measuredWidth) / 2f).roundToInt(),
            top ?: ((parentSize - child.measuredHeight) / 2f).roundToInt(),
            right ?: ((parentSize - child.measuredWidth) / 2f).roundToInt(),
            bottom ?: ((parentSize - child.measuredHeight) / 2f).roundToInt()
        )
    }
}
