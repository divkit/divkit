package com.yandex.div.core.state

import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback

/**
 * Save view pager position in [DivViewState]
 */
class UpdateStateChangePageCallback(
    private val mBlockId: String,
    private val mDivViewState: DivViewState
) : OnPageChangeCallback() {
    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        mDivViewState.putBlockState(mBlockId, PagerState(position))
    }
}
