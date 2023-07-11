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
    private val orientation: Int = RecyclerView.HORIZONTAL
) : RecyclerView.ItemDecoration() {

    private val paddingLeftInt = paddingLeft.roundToInt()
    private val paddingRightInt = paddingRight.roundToInt()
    private val paddingTopInt = paddingTop.roundToInt()
    private val paddingBottomInt = paddingBottom.roundToInt()

    private val middlePadding = (layoutMode.middleNeighbourWidth + itemSpacing).roundToInt()
    private val paddingEndForFirstItem = layoutMode.getPaddingForSideItem(paddingLeft, paddingTop)
    private val paddingStartForLastItem = layoutMode.getPaddingForSideItem(paddingRight, paddingBottom)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        val isFirst = parent.layoutManager?.getPosition(view) == 0
        val isLast = parent.layoutManager?.getPosition(view) == parent.adapter!!.itemCount - 1

        when (orientation) {
            RecyclerView.HORIZONTAL -> outRect.set(
                when {
                    isFirst -> paddingLeftInt
                    isLast -> paddingStartForLastItem
                    else -> middlePadding
                },
                paddingTopInt,
                when {
                    isFirst -> paddingEndForFirstItem
                    isLast -> paddingRightInt
                    else -> middlePadding
                },
                paddingBottomInt
            )
            RecyclerView.VERTICAL -> outRect.set(
                paddingLeftInt,
                when {
                    isFirst -> paddingTopInt
                    isLast -> paddingStartForLastItem
                    else -> middlePadding
                },
                paddingRightInt,
                when {
                    isFirst -> paddingEndForFirstItem
                    isLast -> paddingBottomInt
                    else -> middlePadding
                }
            )
            else -> KAssert.fail { "Unsupported orientation: $orientation" }
        }
    }

    private val DivPagerLayoutMode.middleNeighbourWidth get() = when (this) {
        is DivPagerLayoutMode.NeighbourPageSize -> fixedWidth
        is DivPagerLayoutMode.PageSize -> parentSize * (1 - percentageWidth / 100f) / 2
    }

    private fun DivPagerLayoutMode.getPaddingForSideItem(horizontalPadding: Float, verticalPadding: Float) =
        if (orientation == RecyclerView.HORIZONTAL) {
            when (this) {
                is DivPagerLayoutMode.NeighbourPageSize -> getPaddingForSideItem(horizontalPadding)
                is DivPagerLayoutMode.PageSize -> getPaddingForSideItem(horizontalPadding)
            }
        } else {
            when (this) {
                is DivPagerLayoutMode.NeighbourPageSize -> getPaddingForSideItem(verticalPadding)
                is DivPagerLayoutMode.PageSize -> getPaddingForSideItem(verticalPadding)
            }
        }

    private fun DivPagerLayoutMode.NeighbourPageSize.getPaddingForSideItem(padding: Float) =
        (2 * (fixedWidth + itemSpacing) - padding).roundToInt().coerceAtLeast(0)

    private fun DivPagerLayoutMode.PageSize.getPaddingForSideItem(padding: Float) =
        ((parentSize - padding) * (1 - percentageWidth / 100f)).roundToInt()

    private val DivPagerLayoutMode.NeighbourPageSize.fixedWidth get() =
        value.neighbourPageWidth.toPxF(metrics, resolver)

    private val DivPagerLayoutMode.PageSize.percentageWidth get() = value.pageWidth.value.evaluate(resolver).toInt()
}
