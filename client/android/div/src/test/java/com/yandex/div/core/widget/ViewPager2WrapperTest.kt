package com.yandex.div.core.widget

import android.content.Context
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.test.testContextThemeWrapper
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.BeforeTest
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class ViewPager2WrapperTest {
    private val onMeasured = mock<(Int) -> Boolean> {
        on { invoke(any()) } doReturn false
    }
    private val measureChild = mock<(View) -> Unit>()
    private val underTest = MeasuredPager(testContextThemeWrapper(), measureChild)

    @BeforeTest
    fun setUp() {
        underTest.layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        underTest.onViewPagerMeasured = onMeasured
    }

    @Test
    fun `callback receives inner width when pager is horizontal`() {
        underTest.setPadding(20, 12, 10, 8)

        underTest.measure(makeExactSpec(300), makeExactSpec(180))

        verify(onMeasured).invoke(270)
    }

    @Test
    fun `callback receives inner height when pager is vertical`() {
        underTest.orientation = ViewPager2.ORIENTATION_VERTICAL
        underTest.setPadding(20, 12, 10, 8)

        underTest.measure(makeExactSpec(300), makeExactSpec(180))

        verify(onMeasured).invoke(160)
    }

    @Test
    fun `pager is not remeasured when remeasurement is requested with exact dimensions`() {
        whenever(onMeasured(300)).thenReturn(true)

        underTest.measure(makeExactSpec(300), makeExactSpec(180))

        verify(measureChild).invoke(underTest.viewPager)
    }

    @Test
    fun `pager is remeasured when remeasurement is requested with a bounded dimension`() {
        whenever(onMeasured(300)).thenReturn(true)

        underTest.measure(makeExactSpec(300), makeAtMostSpec(180))

        verify(measureChild, times(2)).invoke(underTest.viewPager)
    }

    @Test
    fun `pager is not remeasured when callback does not request remeasurement`() {
        underTest.measure(makeExactSpec(300), makeAtMostSpec(180))

        verify(measureChild).invoke(underTest.viewPager)
    }

    private class MeasuredPager(
        context: Context,
        private val onMeasureChild: (View) -> Unit,
    ) : ViewPager2Wrapper(context) {
        override fun measureChildWithMargins(
            child: View,
            parentWidthMeasureSpec: Int,
            widthUsed: Int,
            parentHeightMeasureSpec: Int,
            heightUsed: Int,
        ) {
            onMeasureChild(child)
            super.measureChildWithMargins(
                child,
                parentWidthMeasureSpec,
                widthUsed,
                parentHeightMeasureSpec,
                heightUsed,
            )
        }
    }
}
