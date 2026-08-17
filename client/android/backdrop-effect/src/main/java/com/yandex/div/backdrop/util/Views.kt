package com.yandex.div.backdrop.util

import android.view.View
import androidx.annotation.Size

@Size(2)
internal fun View.getCoordinateOffset(other: View): IntArray {
    val viewLocation = IntArray(2)
    val otherLocation = IntArray(2)
    getLocationInWindow(viewLocation)
    other.getLocationInWindow(otherLocation)

    return intArrayOf(
        viewLocation[0] - otherLocation[0],
        viewLocation[1] - otherLocation[1]
    )
}
