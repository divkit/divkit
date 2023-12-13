package com.yandex.div.core.view2.divs

import androidx.core.view.get
import com.yandex.div.core.ScrollDirection
import com.yandex.div.core.state.DivViewState
import com.yandex.div.core.state.PagerState
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.internal.core.nonNullItems
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivPager
import com.yandex.div2.DivVisibilityAction
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivPagerBinderTest: DivBinderTest() {

    private val divViewState = mock<DivViewState>()
    private val divBinder = mock<DivBinder>()

    private val underTest = DivPagerBinder(
        baseBinder = baseBinder,
        viewCreator = viewCreator,
        divBinder = { divBinder },
        divPatchCache = mock(),
        divActionBinder = mock(),
        pagerIndicatorConnector = PagerIndicatorConnector()
    )

    private val div = div()
    private val divPager = divPager(div)
    private val divPagerView = divPagerView(div).apply {
        layoutParams = defaultLayoutParams()
    }

    @Before
    fun `init current state`() {
        whenever(divView.currentState).thenReturn(divViewState)
    }

    @Test
    fun `set default item`() {
        underTest.bindView(divPagerView, divPager, divView, rootPath())

        Assert.assertEquals(DEFAULT_ITEM, divPagerView.currentItem)
    }

    @Test
    fun `keep selected item on rebind`() {
        underTest.bindView(divPagerView, divPager, divView, rootPath())

        divPagerView.currentItem = DEFAULT_ITEM + 1
        underTest.bindView(divPagerView, divPager, divView, rootPath())

        Assert.assertEquals(DEFAULT_ITEM + 1, divPagerView.viewPager.currentItem)
    }

    @Test
    fun `set default item when has current state without current page index`() {
        underTest.bindView(divPagerView, divPager, divView, rootPath())

        Assert.assertEquals(DEFAULT_ITEM, divPagerView.currentItem)
    }

    @Test
    fun `restore previously selected page`() {
        whenever(divViewState.getBlockState<PagerState>(any())).thenReturn(PagerState(DEFAULT_ITEM + 1))

        underTest.bindView(divPagerView, divPager, divView, rootPath())

        Assert.assertEquals(DEFAULT_ITEM + 1, divPagerView.currentItem)
    }

    @Test
    fun `do not log page change when selected page for the first time`() {
        underTest.bindView(divPagerView, divPager, divView, rootPath())

        underTest.changePageCallbackForLogger?.onPageSelected(DEFAULT_ITEM)

        verify(divView.div2Component.div2Logger, never()).logPagerChangePage(
            any(),
            any(),
            any(),
            any()
        )
    }

    @Test
    fun `log page change when selected next page`() {
        underTest.bindView(divPagerView, divPager, divView, rootPath())

        underTest.changePageCallbackForLogger?.onPageSelected(DEFAULT_ITEM)
        underTest.changePageCallbackForLogger?.onPageSelected(DEFAULT_ITEM + 1)

        verify(divView.div2Component.div2Logger).logPagerChangePage(
            any(),
            any(),
            any(),
            eq(ScrollDirection.NEXT)
        )
    }

    @Test
    fun `bind view to div when selected page has visibility actions`() {
        val pagerJson = divPager.writeToJSON()
        pagerJson.getJSONArray("items")
            .getJSONObject(DEFAULT_ITEM)
            .put("visibility_action", DivVisibilityAction(logId = "test").writeToJSON())
        val divPager = DivPager(DivParsingEnvironment(ParsingErrorLogger.ASSERT), pagerJson)
        underTest.bindView(divPagerView, divPager, divView, rootPath())

        underTest.changePageCallbackForLogger?.onPageSelected(DEFAULT_ITEM)

        verify(divView).bindViewToDiv(divPagerView.viewPager[0], divPager.nonNullItems[DEFAULT_ITEM])
    }

    @Test
    fun `unbind view from div on previously selected page`() {
        underTest.bindView(divPagerView, divPager, divView, rootPath())

        underTest.changePageCallbackForLogger?.onPageSelected(DEFAULT_ITEM)
        underTest.changePageCallbackForLogger?.onPageSelected(DEFAULT_ITEM + 1)

        verify(divView).unbindViewFromDiv(divPagerView.viewPager[0])
    }

    private fun div() = UnitTestData(PAGER_DIR, "pager_default_item.json").div

    private fun divPager(div: Div) = div.value() as DivPager

    private fun divPagerView(div: Div) = viewCreator.create(div, ExpressionResolver.EMPTY) as DivPagerView

    private companion object {
        private const val PAGER_DIR = "div-pager"
        private const val DEFAULT_ITEM = 1
    }
}
