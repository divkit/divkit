package com.yandex.div.core.view2

import android.graphics.Rect
import android.view.View
import androidx.annotation.IntRange
import androidx.annotation.MainThread
import com.yandex.div.core.annotations.Mockable
import javax.inject.Inject

@Mockable
internal class ViewVisibilityCalculator @Inject constructor() {

    private val visibleRect = Rect()

    @MainThread
    @IntRange(from = 0L, to = 100L)
    fun calculateVisibilityPercentage(view: View): Int {
        if (!view.isShown) {
            return 0
        }

        if (!view.getGlobalVisibleRect(visibleRect)) {
            return 0
        }

        val viewArea = view.width * view.height
        val visibleArea = visibleRect.width() * visibleRect.height()
        return visibleArea * 100 / viewArea
    }

    @MainThread
    fun isViewFullyVisible(view: View): Boolean {
        if (!view.isShown || !view.getGlobalVisibleRect(visibleRect)) return false
        return view.width == visibleRect.width() && view.height == visibleRect.height()
    }
}
