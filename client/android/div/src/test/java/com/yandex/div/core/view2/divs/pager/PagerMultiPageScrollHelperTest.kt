package com.yandex.div.core.view2.divs.pager

import android.graphics.PointF
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.viewpager2.widget.ViewPager2
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class PagerMultiPageScrollHelperTest {

    private val flingListener = argumentCaptor<RecyclerView.OnFlingListener>()
    private val scrollListener = argumentCaptor<RecyclerView.OnScrollListener>()
    private var recyclerScrollState = RecyclerView.SCROLL_STATE_IDLE
    private val pager = mock<ViewPager2> {
        on { orientation } doReturn ViewPager2.ORIENTATION_HORIZONTAL
        on { width } doReturn PAGE_STEP
    }
    private val verticalPager = mock<ViewPager2> {
        on { orientation } doReturn ViewPager2.ORIENTATION_VERTICAL
        on { height } doReturn PAGE_STEP
    }
    private val layoutManager = mock<LinearLayoutManager> {
        on { canScrollHorizontally() } doReturn true
        on { findFirstVisibleItemPosition() } doReturn CURRENT_POSITION
    }
    private val recyclerView = mock<RecyclerView> {
        on { context } doReturn ApplicationProvider.getApplicationContext()
        on { layoutManager } doReturn layoutManager
        on { minFlingVelocity } doReturn MIN_FLING_VELOCITY
        on { layoutDirection } doReturn View.LAYOUT_DIRECTION_LTR
        on { onFlingListener = flingListener.capture() } doAnswer {}
        on { scrollState } doAnswer { recyclerScrollState }
        on { addOnScrollListener(scrollListener.capture()) } doAnswer {}
    }
    private val pageView = mock<View> {
        on { layoutParams } doReturn RecyclerView.LayoutParams(PAGE_STEP, PAGE_STEP)
    }
    private val anchorView = mock<View> {
        on { layoutParams } doReturn RecyclerView.LayoutParams(PAGE_STEP, PAGE_STEP)
    }
    private val underTest = PagerMultiPageScrollHelper(pager, recyclerView)
    private val verticalUnderTest = PagerMultiPageScrollHelper(verticalPager, recyclerView)

    @Test
    fun `multi page snap helper is installed when attached`() {
        underTest.attach()
        assertSame(underTest, flingListener.lastValue)
    }

    @Test
    fun `original snap helper is restored when detached`() {
        val originalSnapHelper = mock<SnapHelper> {
            on { attachToRecyclerView(null) } doAnswer { recyclerView.onFlingListener = null }
        }
        doAnswer {
            flingListener.allValues.let { if (it.isEmpty()) originalSnapHelper else it.last() }
        }.whenever(recyclerView).onFlingListener
        underTest.attach()

        underTest.detach()

        verify(originalSnapHelper).attachToRecyclerView(recyclerView)
    }

    @Test
    fun `fling is ignored when viewport has no size`() {
        whenever(pager.width).thenReturn(0)
        stubHorizontalFlingTarget()
        underTest.attach()

        val handled = underTest.onFling(FAST_FLING_VELOCITY, 0)

        assertFalse(handled)
        verify(recyclerView, never()).smoothScrollBy(any(), any(), any(), any())
    }

    @Test
    fun `fling is ignored when velocity does not exceed minimum`() {
        whenever(recyclerView.minFlingVelocity).thenReturn(FLING_VELOCITY)

        val handled = underTest.onFling(FLING_VELOCITY, 0)

        assertFalse(handled)
        verify(recyclerView, never()).smoothScrollBy(any(), any(), any(), any())
    }

    @Test
    fun `fling is ignored when layout manager is not linear`() {
        whenever(recyclerView.layoutManager).thenReturn(mock())

        val handled = underTest.onFling(FLING_VELOCITY, 0)

        assertFalse(handled)
        verify(recyclerView, never()).smoothScrollBy(any(), any(), any(), any())
    }

    @Test
    fun `horizontal fling scrolls by calculated distance with deceleration`() {
        stubHorizontalFlingTarget()
        whenever(layoutManager.findViewByPosition(TARGET_POSITION)).thenReturn(pageView)
        stubHorizontalPageDistance(TARGET_DISTANCE)
        underTest.attach()

        val handled = underTest.onFling(FAST_FLING_VELOCITY, 0)

        assertTrue(handled)
        verify(recyclerView).smoothScrollBy(
            eq(TARGET_DISTANCE),
            eq(0),
            any<DecelerateInterpolator>(),
            eq(FAST_TARGET_DURATION),
        )
    }

    @Test
    fun `vertical fling scrolls along vertical axis`() {
        stubVerticalFlingTarget()
        whenever(layoutManager.findViewByPosition(TARGET_POSITION)).thenReturn(pageView)
        stubVerticalPageDistance(TARGET_DISTANCE)
        verticalUnderTest.attach()

        val handled = verticalUnderTest.onFling(0, FAST_FLING_VELOCITY)

        assertTrue(handled)
        verify(recyclerView).smoothScrollBy(
            eq(0),
            eq(TARGET_DISTANCE),
            any<DecelerateInterpolator>(),
            eq(FAST_TARGET_DURATION),
        )
    }

    @Test
    fun `fallback snap starts smooth scroller for visible target`() {
        stubHorizontalFlingTarget()
        whenever(layoutManager.findLastCompletelyVisibleItemPosition()).thenReturn(CURRENT_POSITION)
        whenever(layoutManager.findViewByPosition(CURRENT_POSITION)).thenReturn(anchorView)
        underTest.attach()
        val smoothScroller = argumentCaptor<RecyclerView.SmoothScroller>()

        val handled = underTest.onFling(SLOW_FLING_VELOCITY, 0)

        assertTrue(handled)
        verify(layoutManager).startSmoothScroll(smoothScroller.capture())
        assertEquals(CURRENT_POSITION, smoothScroller.firstValue.targetPosition)
        verify(recyclerView, never()).smoothScrollBy(any(), any(), any(), any())
    }

    @Test
    fun `fling past last item starts smooth scroller for current target`() {
        stubHorizontalFlingTarget()
        whenever(layoutManager.getPosition(anchorView)).thenReturn(TARGET_POSITION)
        whenever(layoutManager.findLastCompletelyVisibleItemPosition()).thenReturn(TARGET_POSITION)
        whenever(layoutManager.findViewByPosition(TARGET_POSITION)).thenReturn(anchorView)
        underTest.attach()
        val smoothScroller = argumentCaptor<RecyclerView.SmoothScroller>()

        val handled = underTest.onFling(FAST_FLING_VELOCITY, 0)

        assertTrue(handled)
        verify(layoutManager).startSmoothScroll(smoothScroller.capture())
        assertEquals(TARGET_POSITION, smoothScroller.firstValue.targetPosition)
        verify(recyclerView, never()).smoothScrollBy(any(), any(), any(), any())
    }

    @Test
    fun `offscreen target distance includes every page step`() {
        stubHorizontalFlingTarget(CURRENT_DISTANCE)
        underTest.attach()

        val handled = underTest.onFling(FAST_FLING_VELOCITY, 0)

        assertTrue(handled)
        verify(recyclerView).smoothScrollBy(
            eq(OFFSCREEN_TARGET_DISTANCE),
            eq(0),
            any<DecelerateInterpolator>(),
            eq(FAST_OFFSCREEN_TARGET_DURATION),
        )
    }

    @Test
    fun `forward slow drag scrolls to last completely visible page`() {
        whenever(layoutManager.findLastCompletelyVisibleItemPosition()).thenReturn(TARGET_POSITION)
        whenever(layoutManager.findViewByPosition(TARGET_POSITION)).thenReturn(pageView)
        stubHorizontalPageDistance(TARGET_DISTANCE)
        underTest.attach()
        dispatchScrollState(RecyclerView.SCROLL_STATE_DRAGGING)

        dispatchScroll(1)
        dispatchScrollState(RecyclerView.SCROLL_STATE_IDLE)

        verify(recyclerView).smoothScrollBy(TARGET_DISTANCE, 0)
    }

    @Test
    fun `backward slow drag scrolls to first completely visible page`() {
        whenever(layoutManager.findFirstCompletelyVisibleItemPosition()).thenReturn(TARGET_POSITION)
        whenever(layoutManager.findViewByPosition(TARGET_POSITION)).thenReturn(pageView)
        stubHorizontalPageDistance(-TARGET_DISTANCE)
        underTest.attach()
        dispatchScrollState(RecyclerView.SCROLL_STATE_DRAGGING)

        dispatchScroll(-1)
        dispatchScrollState(RecyclerView.SCROLL_STATE_IDLE)

        verify(recyclerView).smoothScrollBy(-TARGET_DISTANCE, 0)
    }

    @Test
    fun `forward vertical slow drag scrolls down to last completely visible page`() {
        whenever(layoutManager.findLastCompletelyVisibleItemPosition()).thenReturn(TARGET_POSITION)
        whenever(layoutManager.findViewByPosition(TARGET_POSITION)).thenReturn(pageView)
        stubVerticalPageDistance(TARGET_DISTANCE)
        verticalUnderTest.attach()
        dispatchScrollState(RecyclerView.SCROLL_STATE_DRAGGING)

        dispatchScroll(dx = 0, dy = 1)
        dispatchScrollState(RecyclerView.SCROLL_STATE_IDLE)

        verify(recyclerView).smoothScrollBy(0, TARGET_DISTANCE)
    }

    @Test
    fun `backward vertical slow drag scrolls up to first completely visible page`() {
        whenever(layoutManager.findFirstCompletelyVisibleItemPosition()).thenReturn(TARGET_POSITION)
        whenever(layoutManager.findViewByPosition(TARGET_POSITION)).thenReturn(pageView)
        stubVerticalPageDistance(-TARGET_DISTANCE)
        verticalUnderTest.attach()
        dispatchScrollState(RecyclerView.SCROLL_STATE_DRAGGING)

        dispatchScroll(dx = 0, dy = -1)
        dispatchScrollState(RecyclerView.SCROLL_STATE_IDLE)

        verify(recyclerView).smoothScrollBy(0, -TARGET_DISTANCE)
    }

    @Test
    fun `multi page fling is reported as running until the scroll ends`() {
        stubHorizontalFlingTarget()
        whenever(layoutManager.findViewByPosition(TARGET_POSITION)).thenReturn(pageView)
        stubHorizontalPageDistance(TARGET_DISTANCE)
        underTest.attach()
        assertFalse(underTest.isScrollingToTarget)

        underTest.onFling(FAST_FLING_VELOCITY, 0)

        assertTrue(underTest.isScrollingToTarget)

        dispatchScrollState(RecyclerView.SCROLL_STATE_IDLE)

        assertFalse(underTest.isScrollingToTarget)
    }

    @Test
    fun `single page snap is not reported as a multi page fling`() {
        stubHorizontalFlingTarget()
        whenever(layoutManager.findLastCompletelyVisibleItemPosition()).thenReturn(CURRENT_POSITION)
        whenever(layoutManager.findViewByPosition(CURRENT_POSITION)).thenReturn(anchorView)
        underTest.attach()

        underTest.onFling(SLOW_FLING_VELOCITY, 0)

        assertFalse(underTest.isScrollingToTarget)
    }

    // RecyclerView notifies scroll listeners from the last registered to the first, so the helper
    // is only exercised realistically when the captured listeners are replayed in reverse.
    private fun dispatchScrollState(state: Int) {
        recyclerScrollState = state
        scrollListener.allValues.reversed().forEach { it.onScrollStateChanged(recyclerView, state) }
    }

    private fun dispatchScroll(dx: Int = 0, dy: Int = 0) {
        scrollListener.allValues.reversed().forEach { it.onScrolled(recyclerView, dx, dy) }
    }

    private fun stubHorizontalFlingTarget(anchorDistance: Int = 0) {
        whenever(layoutManager.itemCount).thenReturn(TARGET_POSITION + 1)
        whenever(layoutManager.childCount).thenReturn(1)
        whenever(layoutManager.getChildAt(0)).thenReturn(anchorView)
        whenever(layoutManager.getPosition(anchorView)).thenReturn(CURRENT_POSITION)
        whenever(layoutManager.computeScrollVectorForPosition(TARGET_POSITION)).thenReturn(PointF(1f, 0f))
        whenever(layoutManager.getDecoratedLeft(anchorView)).thenReturn(anchorDistance)
        whenever(layoutManager.getDecoratedRight(anchorView)).thenReturn(anchorDistance + PAGE_STEP)
        whenever(layoutManager.getDecoratedMeasuredWidth(anchorView)).thenReturn(PAGE_STEP)
        whenever(layoutManager.width).thenReturn(PAGE_STEP)
    }

    private fun stubVerticalFlingTarget(anchorDistance: Int = 0) {
        whenever(layoutManager.canScrollHorizontally()).thenReturn(false)
        whenever(layoutManager.canScrollVertically()).thenReturn(true)
        whenever(layoutManager.itemCount).thenReturn(TARGET_POSITION + 1)
        whenever(layoutManager.childCount).thenReturn(1)
        whenever(layoutManager.getChildAt(0)).thenReturn(anchorView)
        whenever(layoutManager.getPosition(anchorView)).thenReturn(CURRENT_POSITION)
        whenever(layoutManager.computeScrollVectorForPosition(TARGET_POSITION)).thenReturn(PointF(0f, 1f))
        whenever(layoutManager.getDecoratedTop(anchorView)).thenReturn(anchorDistance)
        whenever(layoutManager.getDecoratedBottom(anchorView)).thenReturn(anchorDistance + PAGE_STEP)
        whenever(layoutManager.getDecoratedMeasuredHeight(anchorView)).thenReturn(PAGE_STEP)
        whenever(layoutManager.height).thenReturn(PAGE_STEP)
    }

    private fun stubHorizontalPageDistance(distance: Int) {
        whenever(layoutManager.getDecoratedMeasuredWidth(pageView)).thenReturn(PAGE_STEP)
        whenever(layoutManager.getDecoratedLeft(pageView)).thenReturn(distance)
        whenever(layoutManager.width).thenReturn(PAGE_STEP)
    }

    private fun stubVerticalPageDistance(distance: Int) {
        whenever(layoutManager.canScrollHorizontally()).thenReturn(false)
        whenever(layoutManager.canScrollVertically()).thenReturn(true)
        whenever(layoutManager.getDecoratedMeasuredHeight(pageView)).thenReturn(PAGE_STEP)
        whenever(layoutManager.getDecoratedTop(pageView)).thenReturn(distance)
        whenever(layoutManager.height).thenReturn(PAGE_STEP)
    }

    private companion object {
        private const val PAGE_STEP = 100
        private const val MIN_FLING_VELOCITY = 50
        private const val SLOW_FLING_VELOCITY = MIN_FLING_VELOCITY + 1
        private const val FLING_VELOCITY = 2000
        private const val FAST_FLING_VELOCITY = 10000
        private const val CURRENT_POSITION = 1
        private const val TARGET_POSITION = 5
        private const val CURRENT_DISTANCE = 20
        private const val TARGET_DISTANCE = 300
        private const val FAST_TARGET_DURATION = 60
        private const val OFFSCREEN_TARGET_DISTANCE = 420
        private const val FAST_OFFSCREEN_TARGET_DURATION = 84
    }
}
