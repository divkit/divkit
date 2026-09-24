package com.yandex.div.core.view2.divs.utils

import androidx.recyclerview.widget.LinearLayoutManager
import com.yandex.div.internal.scroll.SnapPositions

internal fun isForwardScroll(
    isVertical: Boolean,
    velocityX: Int,
    velocityY: Int,
    isRtl: Boolean,
): Boolean {
    val velocity = when {
        isVertical -> velocityY
        isRtl -> -velocityX
        else -> velocityX
    }
    return velocity >= 0
}

internal fun LinearLayoutManager.snapPositions(): SnapPositions {
    val manager = this
    return object : SnapPositions {
        override val itemCount get() = manager.itemCount
        override val isLinearLayout get() = true
        override val firstCompletelyVisible get() = manager.findFirstCompletelyVisibleItemPosition()
        override val lastCompletelyVisible get() = manager.findLastCompletelyVisibleItemPosition()
        override val firstVisible get() = manager.findFirstVisibleItemPosition()
        override val lastVisible get() = manager.findLastVisibleItemPosition()
    }
}
