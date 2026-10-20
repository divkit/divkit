package com.yandex.div.rule

import android.view.View
import androidx.core.view.allViews
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.widgets.LoadableImage
import com.yandex.div.internal.widget.tabs.ScrollableViewPager
import com.yandex.divkit.demo.Container

internal class UiReadyChecker {

    fun isReady(view: Div2View): Boolean = view.isUiReady()

    private fun Div2View.isUiReady(): Boolean {
        return when {
            divData == null || isBackgroundBindingInProgress() -> false
            !isLaidOut || isLayoutRequested -> false
            !Container.imageLoader.isIdle || !Container.downloader.isIdle -> false
            else -> allViews.all { view ->
                when (view) {
                    is LoadableImage -> view.getLoadingTask() == null
                    else -> view.isScrollIdle()
                }
            }
        }
    }

    private fun View.isScrollIdle(): Boolean {
        if (!isAttachedToWindow || !isShown) return true
        return when (this) {
            is RecyclerView -> {
                scrollState == RecyclerView.SCROLL_STATE_IDLE &&
                    layoutManager?.isSmoothScrolling != true
            }
            is ViewPager2 -> scrollState == ViewPager2.SCROLL_STATE_IDLE
            is ScrollableViewPager -> scrollState == ViewPager.SCROLL_STATE_IDLE
            is ViewPager -> !isFakeDragging
            else -> true
        }
    }
}
