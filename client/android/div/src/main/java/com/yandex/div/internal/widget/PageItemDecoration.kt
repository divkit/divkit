package com.yandex.div.internal.widget

import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.view2.divs.toPxF
import com.yandex.div.internal.KAssert
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivPagerLayoutMode
import kotlin.math.max
import kotlin.math.roundToInt

internal class PageItemDecoration @JvmOverloads constructor(
    layoutMode: DivPagerLayoutMode,
    private val metrics: DisplayMetrics,
    private val resolver: ExpressionResolver,
    @Px private val paddingLeft: Float = 0f,
    @Px private val paddingRight: Float = 0f,
    @Px private val paddingTop: Float = 0f,
    @Px private val paddingBottom: Float = 0f,
    @Px private val parentSize: Int,
    @Px private val itemSpacing: Float = 0f,
    private val orientation: Int = RecyclerView.HORIZONTAL,
) : RecyclerView.ItemDecoration() {

    private val paddingLeftInt = paddingLeft.roundToInt()
    private val paddingRightInt = paddingRight.roundToInt()
    private val paddingTopInt = paddingTop.roundToInt()
    private val paddingBottomInt = paddingBottom.roundToInt()
    private val maxPadding = if (orientation == RecyclerView.VERTICAL) {
        max(paddingBottom, paddingTop)
    } else {
        max(paddingLeft, paddingRight)
    }

    private var offset = layoutMode.pageOffset.roundToInt()

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        when (orientation) {
            RecyclerView.HORIZONTAL -> outRect.set(
                offset,
                paddingTopInt,
                offset,
                paddingBottomInt
            )
            RecyclerView.VERTICAL -> outRect.set(
                paddingLeftInt,
                offset,
                paddingRightInt,
                offset
            )
            else -> KAssert.fail { "Unsupported orientation: $orientation" }
        }
    }

    private val DivPagerLayoutMode.pageOffset get() = when (this) {
        is DivPagerLayoutMode.NeighbourPageSize -> max(fixedWidth + itemSpacing, maxPadding / 2)
        is DivPagerLayoutMode.PageSize -> parentSize * (1 - percentageWidth / 100f) / 2
        is DivPagerLayoutMode.PageContentSize -> throw NotImplementedError()
    }

    private val DivPagerLayoutMode.NeighbourPageSize.fixedWidth get() =
        value.neighbourPageWidth.toPxF(metrics, resolver)

    private val DivPagerLayoutMode.PageSize.percentageWidth get() =
        value.pageWidth.value.evaluate(resolver).toInt()
}
