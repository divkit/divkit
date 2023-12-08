package com.yandex.div.core.view2.divs

import androidx.test.core.app.ApplicationProvider
import androidx.viewpager2.widget.ViewPager2
import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import com.yandex.div2.DivPager
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.InOrderOnType
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argThat
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PagerSelectedActionsDispatcherTest {

    private val divActionBinder = mock<DivActionBinder>()
    private val divView = divView(logId = CARD_ID, divTag = CARD_ID)
    private val bulkActionsArgumentCaptor = argumentCaptor<() -> Unit>()

    private val div = UnitTestData(PAGER_DIR, "pager_selected_actions.json").div
    private val divPager = div.value() as DivPager

    private val underTest = PagerSelectedActionsDispatcher(divView, divPager, ArrayList(divPager.items), divActionBinder)

    @Before
    fun setUp() {
        underTest.whenAttached()
    }

    @Test
    fun `no actions dispatched when selected item has no selected actions`() {
        val divPager = UnitTestData(PAGER_DIR, "pager_default_item.json").div.value() as DivPager

        val underTest = PagerSelectedActionsDispatcher(divView, divPager, ArrayList(divPager.items), divActionBinder)
        underTest.whenAttached()
        underTest.whenPageSelected(0)

        verify(divView, never()).bulkActions(any())
    }

    @Test
    fun `dispatch selected actions when page selected initially`() {
        underTest.whenPageSelected(0)

        whenBulkActionsRun()
        bulkActionsArgumentCaptor.firstValue.invoke()

        verifyActionHandled(urlStr = "div-action://set_state?state_id=0/description/0")
    }

    @Test
    fun `dispatch multiple selected actions when page selected and scroll is idle`() {
        underTest.whenPageSelected(0)
        underTest.whenPageSelected(1)
        underTest.whenScrollIsIdle()
        whenBulkActionsRun(2)

        bulkActionsArgumentCaptor.lastValue.invoke()

        verifyActionHandled(urlStr = "div-action://set_state?state_id=0/description/1")
        verifyActionHandled(urlStr = "some-action://authority")
    }

    @Test
    fun `do not dispatch selected actions when scroll is not idle`() {
        underTest.whenPageSelected(0)
        underTest.whenScrollIsDragging()
        underTest.whenPageSelected(1)
        underTest.whenPageSelected(2)

        whenBulkActionsRun()
        bulkActionsArgumentCaptor.firstValue.invoke()

        verifyActionHandled(urlStr = "div-action://set_state?state_id=0/description/0")
    }

    @Test
    fun `dispatch all selected pages when scroll is idle after a while`() {
        underTest.whenPageSelected(0)
        underTest.whenScrollIsDragging()
        underTest.whenPageSelected(1)
        underTest.whenScrollIsDragging()
        underTest.whenPageSelected(2)
        underTest.whenScrollIsDragging()
        underTest.whenPageSelected(1)
        underTest.whenScrollIsIdle()

        whenBulkActionsRun(times = 4)

        divActionBinder.inOrder {
            bulkActionsArgumentCaptor.firstValue.invoke()
            verifyActionHandled(divView, "div-action://set_state?state_id=0/description/0")

            bulkActionsArgumentCaptor.secondValue.invoke()
            verifyActionHandled(divView, "div-action://set_state?state_id=0/description/1")
            verifyActionHandled(divView, "some-action://authority")

            bulkActionsArgumentCaptor.thirdValue.invoke()
            verifyActionHandled(divView, "div-action://set_state?state_id=0/description/2")

            bulkActionsArgumentCaptor.allValues[3].invoke()
            verifyActionHandled(divView, "div-action://set_state?state_id=0/description/1")
            verifyActionHandled(divView, "some-action://authority")
        }
    }

    @Test
    fun `dispatch selected page once`() {
        underTest.whenPageSelected(0)
        underTest.whenScrollIsDragging()
        underTest.whenPageSelected(1)
        underTest.whenPageSelected(1)
        underTest.whenScrollIsIdle()

        whenBulkActionsRun(times = 2)

        bulkActionsArgumentCaptor.firstValue.invoke()
        bulkActionsArgumentCaptor.secondValue.invoke()

        verifyActionHandled("div-action://set_state?state_id=0/description/0")
        verifyActionHandled(urlStr = "div-action://set_state?state_id=0/description/1")
        verifyActionHandled(urlStr = "some-action://authority")
    }

    private fun PagerSelectedActionsDispatcher.whenAttached() {
        attach(ViewPager2(ApplicationProvider.getApplicationContext()))
    }

    private fun PagerSelectedActionsDispatcher.whenPageSelected(page: Int) {
        pageSelectionTracker!!.onPageSelected(page)
    }

    private fun PagerSelectedActionsDispatcher.whenScrollIsIdle() {
        pageSelectionTracker!!.onPageScrollStateChanged(ViewPager2.SCROLL_STATE_IDLE)
    }

    private fun PagerSelectedActionsDispatcher.whenScrollIsDragging() {
        pageSelectionTracker!!.onPageScrollStateChanged(ViewPager2.SCROLL_STATE_DRAGGING)
    }

    private fun whenBulkActionsRun(times: Int = 1) {
        verify(divView, times(times)).bulkActions(bulkActionsArgumentCaptor.capture())
    }

    private fun verifyActionHandled(urlStr: String, times: Int = 1) {
        verify(divActionBinder, times(times)).handleAction(eq(divView), urlEq(urlStr), anyOrNull(), anyOrNull())
    }

    private fun InOrderOnType<DivActionBinder>.verifyActionHandled(div2View: Div2View, url: String) {
        verify().handleAction(eq(div2View), urlEq(url), anyOrNull(), anyOrNull())
    }

    private companion object {
        private const val PAGER_DIR = "div-pager"
        private const val CARD_ID = "div_pager_card"

        private fun urlEq(urlStr: String) = argThat<DivAction> { url?.evaluate(ExpressionResolver.EMPTY).toString() == urlStr }
    }
}
