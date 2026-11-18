package com.yandex.div.compose.views.tabs

import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div2.DivTabs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.math.abs
import kotlin.math.sign
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DivTabsStateTest {

    @get:Rule
    val rule = createComposeRule().apply {
        mainClock.autoAdvance = false
    }

    private val state = DivTabsState(initialIndex = 0, initialTabCount = 3)
    private lateinit var scope: CoroutineScope

    @Test
    fun `tiny offset on the first page keeps its exact position`() {
        assertEquals(0f, position(page = 0, offset = 0.000001f))
    }

    @Test
    fun `tiny positive offset keeps the current page position`() {
        assertEquals(1f, position(page = 1, offset = 0.000001f))
    }

    @Test
    fun `tiny negative offset keeps the current page position`() {
        assertEquals(1f, position(page = 1, offset = -0.000001f))
    }

    @Test
    fun `forward swipe preserves its fractional position`() {
        assertEquals(1.25f, position(page = 1, offset = 0.25f))
    }

    @Test
    fun `backward swipe preserves its fractional position`() {
        assertEquals(0.75f, position(page = 1, offset = -0.25f))
    }

    @Test
    fun `selected tab switches before the pager arrives`() {
        setContent()

        select(2, slide(durationMillis = 1000))
        rule.mainClock.advanceTimeByFrame()

        assertEquals(2, state.selectedIndex)
        assertEquals(0, state.pagerState.currentPage)
    }

    @Test
    fun `slide indicator moves from the selected tab to the target over animation_duration`() {
        setContent()

        select(2, slide(durationMillis = 1000))
        rule.mainClock.advanceTimeBy(500)

        assertEquals(0, state.indicatorFromIndex)
        assertEquals(2, state.indicatorToIndex)
        val midway = state.indicatorProgress
        assertTrue(midway > 0f && midway < 1f, "Indicator progress at 500 of 1000 ms: $midway")

        rule.mainClock.advanceTimeBy(600)

        assertEquals(2, state.pagerState.currentPage)
        assertEquals(2, state.indicatorFromIndex)
        assertEquals(2, state.indicatorToIndex)
        assertEquals(0f, state.indicatorProgress)
    }

    @Test
    fun `none indicator snaps to the target`() {
        setContent()

        select(2, TabTitleAnimation(DivTabs.TabTitleStyle.AnimationType.NONE, durationMillis = 1000))
        rule.mainClock.advanceTimeByFrame()

        assertEquals(2, state.indicatorToIndex)
        assertEquals(1f, state.indicatorProgress)
        assertEquals(0, state.pagerState.currentPage)
    }

    @Test
    fun `user scroll cancels the selection and hands the indicator back to the pager`() {
        setContent()

        select(2, slide(durationMillis = 1000))
        rule.mainClock.advanceTimeBy(100)
        scope.launch {
            state.pagerState.scroll(MutatePriority.UserInput) { scrollBy(10f) }
        }
        rule.mainClock.advanceTimeByFrame()

        val pager = state.pagerState
        val fraction = pager.currentPageOffsetFraction
        assertTrue(fraction != 0f, "Pager offset after a 10 px user scroll: $fraction")
        assertEquals(pager.currentPage, state.selectedIndex)
        assertEquals(pager.currentPage, state.indicatorFromIndex)
        assertEquals(pager.currentPage + fraction.sign.toInt(), state.indicatorToIndex)
        assertEquals(abs(fraction), state.indicatorProgress)
    }

    @Test
    fun `selecting the selected tab settles a displaced pager without a transition`() {
        setContent()

        scope.launch {
            state.pagerState.scroll(MutatePriority.UserInput) { scrollBy(30f) }
        }
        rule.mainClock.advanceTimeByFrame()
        assertTrue(state.indicatorProgress > 0f, "Pager offset after a 30 px user scroll: ${state.indicatorProgress}")

        select(0, slide(durationMillis = 1000))
        rule.mainClock.advanceTimeBy(1000)

        assertEquals(0, state.pagerState.currentPage)
        assertEquals(0f, state.pagerState.currentPageOffsetFraction)
        assertEquals(0, state.selectedIndex)
        assertEquals(0f, state.indicatorProgress)
    }

    @Test
    fun `selection without animation jumps`() {
        setContent()

        select(2, animation = null)
        rule.mainClock.advanceTimeByFrame()

        assertEquals(2, state.pagerState.currentPage)
        assertEquals(2, state.selectedIndex)
        assertEquals(0f, state.indicatorProgress)
    }

    private fun position(page: Int, offset: Float): Float {
        return PagerState(
            currentPage = page,
            currentPageOffsetFraction = offset,
            pageCount = { 3 },
        ).logicalPosition
    }

    private fun setContent() {
        rule.setContent {
            scope = rememberCoroutineScope()
            HorizontalPager(state = state.pagerState, modifier = Modifier.size(100.dp)) {
                Box(Modifier.fillMaxSize())
            }
        }
        rule.mainClock.advanceTimeByFrame()
    }

    private fun select(index: Int, animation: TabTitleAnimation?) {
        scope.launch { state.selectTab(index, animation) }
    }

    private fun slide(durationMillis: Int) =
        TabTitleAnimation(DivTabs.TabTitleStyle.AnimationType.SLIDE, durationMillis)
}
