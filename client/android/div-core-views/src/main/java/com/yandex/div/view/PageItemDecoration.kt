package com.yandex.div.view

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.util.KAssert
import kotlin.math.roundToInt

class PageItemDecoration @JvmOverloads constructor(
    @Px private val paddingLeft: Float = 0f,
    @Px private val paddingRight: Float = 0f,
    @Px private val paddingTop: Float = 0f,
    @Px private val paddingBottom: Float = 0f,
    @Px private var neighbourItemWidth: Float = 0f,
    @Px private val itemSpacing: Float = 0f,
    private val orientation: Int = RecyclerView.HORIZONTAL
) : RecyclerView.ItemDecoration() {

    private val paddingLeftInt = paddingLeft.roundToInt()
    private val paddingRightInt = paddingRight.roundToInt()
    private val paddingTopInt = paddingTop.roundToInt()
    private val paddingBottomInt = paddingBottom.roundToInt()

    private val middlePadding = (neighbourItemWidth + itemSpacing).roundToInt()
    private val paddingEndForFirstItem = when (orientation) {
        RecyclerView.HORIZONTAL -> ((neighbourItemWidth + itemSpacing) * 2 - paddingLeft).roundToInt()
        RecyclerView.VERTICAL -> ((neighbourItemWidth + itemSpacing) * 2 - paddingBottom).roundToInt()
        else -> 0
    }
    private val paddingStartForLastItem = when (orientation) {
        RecyclerView.HORIZONTAL -> ((neighbourItemWidth + itemSpacing) * 2 - paddingRight).roundToInt()
        RecyclerView.VERTICAL -> ((neighbourItemWidth + itemSpacing) * 2 - paddingTop).roundToInt()
        else -> 0
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        val isTwoPage = parent.adapter?.itemCount == 2
        val isFirst = parent.layoutManager?.getPosition(view) == 0
        val isLast = parent.layoutManager?.getPosition(view) == parent.adapter!!.itemCount - 1

        when (orientation) {
            RecyclerView.HORIZONTAL -> outRect.set(
                when {
                    isFirst -> paddingLeftInt
                    isLast && !isTwoPage -> paddingStartForLastItem
                    else -> middlePadding
                },
                paddingTopInt,
                when {
                    isLast -> paddingRightInt
                    isFirst && !isTwoPage -> paddingEndForFirstItem
                    else -> middlePadding
                },
                paddingBottomInt
            )
            RecyclerView.VERTICAL -> outRect.set(
                paddingLeftInt,
                when {
                    isFirst -> paddingTopInt
                    isLast && !isTwoPage -> paddingStartForLastItem
                    else -> middlePadding
                },
                paddingRightInt,
                when {
                    isLast -> paddingBottomInt
                    isFirst && !isTwoPage -> paddingEndForFirstItem
                    else -> middlePadding
                }
            )
            else -> KAssert.fail { "Unsupported orientation: $orientation" }
        }
    }
}
