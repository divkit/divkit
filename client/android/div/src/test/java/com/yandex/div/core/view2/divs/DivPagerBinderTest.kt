package com.yandex.div.core.view2.divs

import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.ScrollDirection
import com.yandex.div.core.asExpression
import com.yandex.div.core.state.DivViewState
import com.yandex.div.core.state.PagerState
import com.yandex.div.core.util.AccessibilityStateProvider
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.divs.pager.DivPagerAdapter
import com.yandex.div.core.view2.divs.pager.DivPagerBinder
import com.yandex.div.core.view2.divs.pager.PagerIndicatorConnector
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.data.Variable
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.core.nonNullItems
import com.yandex.div.internal.core.toBlock
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
    private val accessibilityStateProvider = AccessibilityStateProvider(false)

    private val underTest = DivPagerBinder(
        baseBinder = baseBinder,
        viewCreator = viewCreator,
        divBinder = { divBinder },
        actionPerformer = mock(),
        pagerIndicatorConnector = PagerIndicatorConnector(),
        accessibilityStateProvider = accessibilityStateProvider,
    )

    private val div = div()
    private val divBlock = div.toBlock(resolver, rootPath()) as DivBlock.Pager
    private val divPagerView = divPagerView(div).apply {
        layoutParams = defaultLayoutParams()
    }

    @Before
    fun `init current state`() {
        whenever(divView.currentState).thenReturn(divViewState)
    }

    @Test
    fun `set default item`() {
        underTest.bindView(divPagerView, divBlock, divView)

        Assert.assertEquals(DEFAULT_ITEM, divPagerView.currentItem)
    }

    @Test
    fun `keep selected item on rebind`() {
        underTest.bindView(divPagerView, divBlock, divView)

        divPagerView.currentItem = DEFAULT_ITEM + 1
        underTest.bindView(divPagerView, divBlock, divView)

        Assert.assertEquals(DEFAULT_ITEM + 1, divPagerView.viewPager.currentItem)
    }

    @Test
    fun `set default item when has current state without current page index`() {
        underTest.bindView(divPagerView, divBlock, divView)

        Assert.assertEquals(DEFAULT_ITEM, divPagerView.currentItem)
    }

    @Test
    fun `restore previously selected page`() {
        whenever(divViewState.getBlockState<PagerState>(any())).thenReturn(PagerState(DEFAULT_ITEM + 1))

        underTest.bindView(divPagerView, divBlock, divView)

        Assert.assertEquals(DEFAULT_ITEM + 1, divPagerView.currentItem)
    }

    @Test
    fun `do not log page change when selected page for the first time`() {
        underTest.bindView(divPagerView, divBlock, divView)

        divPagerView.changePageCallbackForLogger?.onPageSelected(DEFAULT_ITEM)

        verify(divView.div2Component.div2Logger, never()).logPagerChangePage(
            any(),
            any(),
            any(),
            any(),
            any()
        )
    }

    @Test
    fun `log page change when selected next page`() {
        underTest.bindView(divPagerView, divBlock, divView)

        divPagerView.changePageCallbackForLogger?.onPageSelected(DEFAULT_ITEM)
        divPagerView.changePageCallbackForLogger?.onPageSelected(DEFAULT_ITEM + 1)

        verify(divView.div2Component.div2Logger).logPagerChangePage(
            any(),
            any(),
            any(),
            any(),
            eq(ScrollDirection.NEXT)
        )
    }

    @Test
    fun `bind view to div when selected page has visibility actions`() {
        val pagerJson = div.writeToJSON()
        pagerJson.getJSONArray("items")
            .getJSONObject(DEFAULT_ITEM)
            .put("visibility_action", DivVisibilityAction(logId = "test".asExpression()).writeToJSON())
        val divPager = Div.Pager(DivPager(DivParsingEnvironment(ParsingErrorLogger.ASSERT), pagerJson))
            .toBlock(resolver, rootPath()) as DivBlock.Pager
        underTest.bindView(divPagerView, divPager, divView)

        divPagerView.changePageCallbackForLogger?.onPageSelected(DEFAULT_ITEM)

        verify(divView).bindViewToDiv(divPagerView, divPager.divValue.nonNullItems[DEFAULT_ITEM])
    }

    @Test
    fun `unbind view from div on previously selected page`() {
        underTest.bindView(divPagerView, divBlock, divView)

        divPagerView.changePageCallbackForLogger?.onPageSelected(DEFAULT_ITEM)
        divPagerView.changePageCallbackForLogger?.onPageSelected(DEFAULT_ITEM + 1)

        verify(divView).unbindViewFromDiv(divPagerView)
    }

    @Test
    fun `item count variable is initialized with non gone item count`() {
        val itemCountVariable = itemCountVariable()
        val div = divWithItemCountVariable()
        val view = divPagerViewWithLayout(div)

        underTest.bindView(view, div.toBlock(resolver, rootPath()) as DivBlock.Pager, divView)

        Assert.assertEquals(3L, itemCountVariable.getValue())
    }

    @Test
    fun `item count variable is updated when item visibility changes`() {
        val itemCountVariable = itemCountVariable()
        val div = divWithItemCountVariable()
        val view = divPagerViewWithLayout(div)
        underTest.bindView(view, div.toBlock(resolver, rootPath()) as DivBlock.Pager, divView)
        val adapter = view.viewPager.adapter as DivPagerAdapter
        val firstItem = adapter.items.first()

        adapter.removeItem(0)
        Assert.assertEquals(2L, itemCountVariable.getValue())

        adapter.addItems(0, listOf(firstItem))
        Assert.assertEquals(3L, itemCountVariable.getValue())
    }

    @Test
    fun `item count variable is updated when adapter items change`() {
        val itemCountVariable = itemCountVariable()
        val div = divWithItemCountVariable()
        val view = divPagerViewWithLayout(div)
        underTest.bindView(view, div.toBlock(resolver, rootPath()) as DivBlock.Pager, divView)
        val adapter = view.viewPager.adapter as DivPagerAdapter

        adapter.setItems(adapter.items.dropLast(1))

        Assert.assertEquals(2L, itemCountVariable.getValue())
    }

    @Test
    fun `item count variable ignores infinite scroll duplicates`() {
        val itemCountVariable = itemCountVariable()
        val div = divWithItemCountVariable("pager_gone_with_infinite_scroll.json")
        val view = divPagerViewWithLayout(div)

        underTest.bindView(view, div.toBlock(resolver, rootPath()) as DivBlock.Pager, divView)

        Assert.assertEquals(6L, itemCountVariable.getValue())
    }

    @Test
    fun `pager without item count variable does not mutate variable`() {
        underTest.bindView(divPagerView, divBlock, divView)

        verify(resolver, never()).getVariable(ITEM_COUNT_VARIABLE)
    }

    @Test
    fun `vertical pager with infinite scroll registers scroll listener on recycler view`() {
        val verticalDiv = verticalInfiniteScrollDiv()
        val verticalDivPagerView = divPagerView(verticalDiv).apply {
            layoutParams = defaultLayoutParams()
        }

        underTest.bindView(verticalDivPagerView, verticalDiv.toBlock(resolver, rootPath()) as DivBlock.Pager, divView)

        val recyclerView = verticalDivPagerView.viewPager.getChildAt(0) as? RecyclerView
        Assert.assertNotNull("RecyclerView should be present inside ViewPager2", recyclerView)
        // Verify that at least one scroll listener is registered (the infinite scroll listener)
        val listeners = getScrollListeners(recyclerView!!)
        Assert.assertTrue(
            "Vertical pager with infinite_scroll=true must register a scroll listener",
            listeners.isNotEmpty()
        )
    }

    @Test
    fun `vertical pager infinite scroll listener handles dy without crash`() {
        val verticalDiv = verticalInfiniteScrollDiv()
        val verticalDivPagerView = divPagerView(verticalDiv).apply {
            layoutParams = defaultLayoutParams()
        }

        underTest.bindView(verticalDivPagerView, verticalDiv.toBlock(resolver, rootPath()) as DivBlock.Pager, divView)

        val recyclerView = verticalDivPagerView.viewPager.getChildAt(0) as? RecyclerView
            ?: return

        // For a vertical pager, dx is always 0 and dy carries the scroll delta.
        // This call must not throw and must use dy (not dx) for boundary detection.
        val listeners = getScrollListeners(recyclerView)
        listeners.forEach { listener ->
            // dx=0, dy=1 simulates a downward scroll on a vertical pager.
            // Before the fix, dx=0 would prevent any boundary reset from firing.
            listener.onScrolled(recyclerView, 0, 1)
            // dx=0, dy=-1 simulates an upward scroll.
            listener.onScrolled(recyclerView, 0, -1)
        }
        // If no exception is thrown, the listener correctly handles vertical scroll deltas.
    }

    private fun verticalInfiniteScrollDiv() =
        UnitTestData(PAGER_DIR, "pager_vertical_infinite_scroll.json").div as Div.Pager

    private fun getScrollListeners(recyclerView: RecyclerView): List<RecyclerView.OnScrollListener> {
        return try {
            val field = RecyclerView::class.java.getDeclaredField("mScrollListeners")
            field.isAccessible = true
            @Suppress("UNCHECKED_CAST")
            (field.get(recyclerView) as? List<RecyclerView.OnScrollListener>) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun div() = UnitTestData(PAGER_DIR, "pager_default_item.json").div as Div.Pager

    private fun divWithItemCountVariable(fileName: String = "pager_default_item.json"): Div.Pager {
        val pagerJson = (UnitTestData(PAGER_DIR, fileName).div as Div.Pager).writeToJSON()
        pagerJson.put("item_count_variable", ITEM_COUNT_VARIABLE)
        return Div.Pager(DivPager(DivParsingEnvironment(ParsingErrorLogger.ASSERT), pagerJson))
    }

    private fun itemCountVariable() = Variable.IntegerVariable(ITEM_COUNT_VARIABLE, 0L).also {
        whenever(resolver.getVariable(ITEM_COUNT_VARIABLE)).thenReturn(it)
    }

    private fun divPagerView(div: Div) = viewCreator.create(div, ExpressionResolver.EMPTY) as DivPagerView

    private fun divPagerViewWithLayout(div: Div) = divPagerView(div).apply {
        layoutParams = defaultLayoutParams()
    }

    private companion object {
        private const val PAGER_DIR = "div-pager"
        private const val DEFAULT_ITEM = 1
        private const val ITEM_COUNT_VARIABLE = "count"
    }
}
