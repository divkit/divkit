package com.yandex.div.core.state

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback

/**
 * Save view pager position in [DivViewState]
 */
internal class UpdateStateChangePageCallback(
    private val mBlockId: String,
    private val mDivViewState: DivViewState
) : OnPageChangeCallback() {
    override fun onPageSelected(position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            mDivViewState.putBlockState(mBlockId, PagerState(position))
        }
    }
}
