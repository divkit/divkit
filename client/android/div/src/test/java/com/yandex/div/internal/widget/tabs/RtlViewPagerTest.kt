package com.yandex.div.internal.widget.tabs

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.viewpager.widget.ViewPager
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class RtlViewPagerTest {

    private val viewPager = RtlViewPager(ApplicationProvider.getApplicationContext<Context>(), null)

    @Test
    fun `scroll state is updated when pager state changes`() {
        val listener = viewPager.ReversingOnPageChangeListener(mock())

        listener.onPageScrollStateChanged(ViewPager.SCROLL_STATE_SETTLING)

        assertEquals(ViewPager.SCROLL_STATE_SETTLING, viewPager.scrollState)
    }
}
