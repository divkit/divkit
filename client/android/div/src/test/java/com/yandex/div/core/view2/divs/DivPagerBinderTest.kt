package com.yandex.div.core.view2.divs

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.Disposable
import com.yandex.div.core.ScrollDirection
import com.yandex.div.core.asExpression
import com.yandex.div.core.state.DivViewState
import com.yandex.div.core.state.PagerState
import com.yandex.div.core.util.AccessibilityStateProvider
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.divs.pager.DivPagerAdapter
import com.yandex.div.core.view2.divs.pager.DivPagerBinder
import com.yandex.div.core.view2.divs.pager.DivPagerBinder.Companion.VIRTUAL_ITEM_COUNT
import com.yandex.div.core.view2.divs.pager.DivPagerBinder.Companion.VIRTUAL_ITEM_COUNT_EXTENDED
import com.yandex.div.core.view2.divs.pager.PagerIndicatorConnector
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.core.VariableMutationHandler
import com.yandex.div.internal.core.nonNullItems
import com.yandex.div.internal.core.toBlock
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivCollectionItemBuilder
import com.yandex.div2.DivVisibilityAction
import org.json.JSONArray
import org.junit.runner.RunWith
import org.mockito.kotlin.KArgumentCaptor
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivPagerBinderTest : DivBinderTest() {

    private val divViewState = mock<DivViewState>()
    private val divBinder = mock<DivBinder>()
    private val accessibilityStateProvider = AccessibilityStateProvider(false)
    private val variableMutationHandler = mock<VariableMutationHandler>()

    private val underTest = DivPagerBinder(
        baseBinder = baseBinder,
        viewCreator = viewCreator,
        divBinder = { divBinder },
        actionPerformer = mock(),
        pagerIndicatorConnector = PagerIndicatorConnector(),
        accessibilityStateProvider = accessibilityStateProvider,
        variableMutationHandler = variableMutationHandler,
    )

    private val div = div()
    private val divBlock = div.toBlock(resolver, rootPath()) as DivBlock.Pager
    private val divPagerView = divPagerView(div).apply {
        layoutParams = defaultLayoutParams()
    }

    @BeforeTest
    fun `init current state`() {
        whenever(divView.currentState).thenReturn(divViewState)
    }

    @Test
    fun `keep selected item on rebind`() {
        underTest.bindView(divPagerView, divBlock, divView)

        divPagerView.currentItem = DEFAULT_ITEM + 1
        underTest.bindView(divPagerView, divBlock, divView)

        assertEquals(DEFAULT_ITEM + 1, divPagerView.viewPager.currentItem)
    }

    @Test
    fun `default item is selected when current state has no page index`() {
        underTest.bindView(divPagerView, divBlock, divView)

        assertEquals(DEFAULT_ITEM, divPagerView.currentItem)
    }

    @Test
    fun `stored page is restored when current state has page index`() {
        whenever(divViewState.getBlockState<PagerState>(any())).thenReturn(PagerState(DEFAULT_ITEM + 1))

        underTest.bindView(divPagerView, divBlock, divView)

        assertEquals(DEFAULT_ITEM + 1, divPagerView.currentItem)
    }

    @Test
    fun `do not log page change when selected page for the first time`() {
        val logger = divView.div2Component.div2Logger
        underTest.bindView(divPagerView, divBlock, divView)

        divPagerView.changePageCallbackForLogger?.onPageSelected(DEFAULT_ITEM)

        verifyNoInteractions(logger)
    }

    @Test
    fun `log page change when selected next page`() {
        val logger = divView.div2Component.div2Logger
        underTest.bindView(divPagerView, divBlock, divView)

        divPagerView.changePageCallbackForLogger?.onPageSelected(DEFAULT_ITEM)
        divPagerView.changePageCallbackForLogger?.onPageSelected(DEFAULT_ITEM + 1)

        verify(logger).logPagerChangePage(
            any(),
            any(),
            any(),
            eq(DEFAULT_ITEM + 1),
            eq(ScrollDirection.NEXT)
        )
        verifyNoMoreInteractions(logger)
    }

    @Test
    fun `bind view to div when selected page has visibility actions`() {
        val items = div.value.items!!.toMutableList()
        val selectedItem = items[DEFAULT_ITEM] as Div.Text
        items[DEFAULT_ITEM] = Div.Text(
            selectedItem.value.copy(visibilityAction = DivVisibilityAction(logId = "test".asExpression()))
        )
        val divPager = Div.Pager(div.value.copy(items = items))
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
        val div = divWithItemCountVariable()
        val view = divPagerViewWithLayout(div)

        underTest.bindView(view, div.toBlock(resolver, rootPath()) as DivBlock.Pager, divView)

        verifyItemCountVariableChange(3)
    }

    @Test
    fun `item count variable is updated when an item is removed`() {
        val div = divWithItemCountVariable()
        val view = divPagerViewWithLayout(div)
        underTest.bindView(view, div.toBlock(resolver, rootPath()) as DivBlock.Pager, divView)
        val adapter = view.viewPager.adapter as DivPagerAdapter
        clearInvocations(variableMutationHandler)

        adapter.removeItem(0)

        verifyItemCountVariableChange(2)
    }

    @Test
    fun `item count variable is updated when an item is inserted`() {
        val baseDiv = divWithItemCountVariable()
        val div = Div.Pager(baseDiv.value.copy(items = baseDiv.value.items!!.take(2)))
        val view = divPagerViewWithLayout(div)
        underTest.bindView(view, div.toBlock(resolver, rootPath()) as DivBlock.Pager, divView)
        val adapter = view.viewPager.adapter as DivPagerAdapter
        val firstItem = adapter.items.first()
        clearInvocations(variableMutationHandler)

        adapter.addItems(2, listOf(firstItem))

        verifyItemCountVariableChange(3)
    }

    @Test
    fun `item count variable is updated when adapter items change`() {
        val div = divWithItemCountVariable()
        val view = divPagerViewWithLayout(div)
        underTest.bindView(view, div.toBlock(resolver, rootPath()) as DivBlock.Pager, divView)
        val adapter = view.viewPager.adapter as DivPagerAdapter
        clearInvocations(variableMutationHandler)

        adapter.setItems(adapter.items.dropLast(1))

        verifyItemCountVariableChange(2)
    }

    @Test
    fun `item count variable is updated once when insertion creates virtual ranges`() {
        val baseDiv = divWithItemCountVariable()
        val div = Div.Pager(
            baseDiv.value.copy(
                items = baseDiv.value.items!!.take(1),
                infiniteScroll = true.asExpression(),
                multiPageScroll = true.asExpression(),
            )
        )
        val view = divPagerViewWithLayout(div)
        underTest.bindView(view, div.toBlock(resolver, rootPath()) as DivBlock.Pager, divView)
        val adapter = view.viewPager.adapter as DivPagerAdapter
        val firstItem = adapter.items.first()
        clearInvocations(variableMutationHandler)

        adapter.addItems(1, listOf(firstItem))

        verifyItemCountVariableChange(2)
    }

    @Test
    fun `item count variable ignores infinite scroll duplicates`() {
        val div = divWithItemCountVariable("pager_gone_with_infinite_scroll.json")
        val view = divPagerViewWithLayout(div)

        underTest.bindView(view, div.toBlock(resolver, rootPath()) as DivBlock.Pager, divView)

        verifyItemCountVariableChange(6)
    }

    @Test
    fun `infinite scroll uses two virtual items on each side`() {
        val div = divWithScroll(infiniteScroll = true, multiPageScroll = false)
        val view = divPagerViewWithLayout(div)

        underTest.bindView(view, div.toBlock(resolver, rootPath()) as DivBlock.Pager, divView)

        val adapter = view.viewPager.adapter as DivPagerAdapter
        assertEquals(VIRTUAL_ITEM_COUNT, adapter.virtualItemCount)
    }

    @Test
    fun `multi page infinite scroll uses extended virtual range`() {
        val div = divWithScroll(infiniteScroll = true, multiPageScroll = true)
        val view = divPagerViewWithLayout(div)

        underTest.bindView(view, div.toBlock(resolver, rootPath()) as DivBlock.Pager, divView)

        val adapter = view.viewPager.adapter as DivPagerAdapter
        assertEquals(VIRTUAL_ITEM_COUNT_EXTENDED, adapter.virtualItemCount)
    }

    @Test
    fun `virtual range expands when multi page scroll becomes enabled`() {
        val observer = argumentCaptor<(Boolean) -> Unit>()
        val multiPageScroll = observableBoolean(initialValue = false, observer)
        val div = Div.Pager(
            div().value.copy(
                infiniteScroll = true.asExpression(),
                multiPageScroll = multiPageScroll,
            )
        )
        val view = divPagerViewWithLayout(div)
        underTest.bindView(view, div.toBlock(resolver, rootPath()) as DivBlock.Pager, divView)
        val adapter = view.viewPager.adapter as DivPagerAdapter

        observer.firstValue(true)

        assertEquals(VIRTUAL_ITEM_COUNT_EXTENDED, adapter.virtualItemCount)
    }

    @Test
    fun `virtual range shrinks when multi page scroll becomes disabled`() {
        val observer = argumentCaptor<(Boolean) -> Unit>()
        val multiPageScroll = observableBoolean(initialValue = true, observer)
        val div = Div.Pager(
            div().value.copy(
                infiniteScroll = true.asExpression(),
                multiPageScroll = multiPageScroll,
            )
        )
        val view = divPagerViewWithLayout(div)
        underTest.bindView(view, div.toBlock(resolver, rootPath()) as DivBlock.Pager, divView)
        val adapter = view.viewPager.adapter as DivPagerAdapter

        observer.firstValue(false)

        assertEquals(VIRTUAL_ITEM_COUNT, adapter.virtualItemCount)
    }

    @Test
    fun `multi page scroll does not create virtual items when infinite scroll is disabled`() {
        val div = divWithScroll(infiniteScroll = false, multiPageScroll = true)
        val view = divPagerViewWithLayout(div)

        underTest.bindView(view, div.toBlock(resolver, rootPath()) as DivBlock.Pager, divView)

        val adapter = view.viewPager.adapter as DivPagerAdapter
        assertEquals(0, adapter.virtualItemCount)
    }

    @Test
    fun `virtual range disappears when infinite scroll becomes disabled`() {
        val observer = argumentCaptor<(Boolean) -> Unit>()
        val infiniteScroll = observableBoolean(initialValue = true, observer)
        val div = Div.Pager(
            div().value.copy(
                infiniteScroll = infiniteScroll,
                multiPageScroll = true.asExpression(),
            )
        )
        val view = divPagerViewWithLayout(div)
        underTest.bindView(view, div.toBlock(resolver, rootPath()) as DivBlock.Pager, divView)
        val adapter = view.viewPager.adapter as DivPagerAdapter

        observer.firstValue(false)

        assertEquals(0, adapter.virtualItemCount)
    }

    @Test
    fun `virtual range appears when infinite scroll becomes enabled`() {
        val observer = argumentCaptor<(Boolean) -> Unit>()
        val infiniteScroll = observableBoolean(initialValue = false, observer)
        val div = Div.Pager(
            div().value.copy(
                infiniteScroll = infiniteScroll,
                multiPageScroll = true.asExpression(),
            )
        )
        val view = divPagerViewWithLayout(div)
        underTest.bindView(view, div.toBlock(resolver, rootPath()) as DivBlock.Pager, divView)
        val adapter = view.viewPager.adapter as DivPagerAdapter

        observer.firstValue(true)

        assertEquals(VIRTUAL_ITEM_COUNT_EXTENDED, adapter.virtualItemCount)
    }

    @Test
    fun `item count variable is not updated when only virtual range changes`() {
        val observer = argumentCaptor<(Boolean) -> Unit>()
        val multiPageScroll = observableBoolean(initialValue = false, observer)
        val baseDiv = divWithItemCountVariable()
        val div = Div.Pager(
            baseDiv.value.copy(
                infiniteScroll = true.asExpression(),
                multiPageScroll = multiPageScroll,
            )
        )
        val view = divPagerViewWithLayout(div)
        underTest.bindView(view, div.toBlock(resolver, rootPath()) as DivBlock.Pager, divView)
        clearInvocations(variableMutationHandler)

        observer.firstValue(true)

        verifyNoInteractions(variableMutationHandler)
    }

    @Test
    fun `item count variable is not updated when virtual range shrinks`() {
        val observer = argumentCaptor<(Boolean) -> Unit>()
        val multiPageScroll = observableBoolean(initialValue = true, observer)
        val baseDiv = divWithItemCountVariable()
        val div = Div.Pager(
            baseDiv.value.copy(
                infiniteScroll = true.asExpression(),
                multiPageScroll = multiPageScroll,
            )
        )
        val view = divPagerViewWithLayout(div)
        underTest.bindView(view, div.toBlock(resolver, rootPath()) as DivBlock.Pager, divView)
        clearInvocations(variableMutationHandler)

        observer.firstValue(false)

        verifyNoInteractions(variableMutationHandler)
    }

    @Test
    fun `pager without item count variable does not mutate variable`() {
        underTest.bindView(divPagerView, divBlock, divView)

        verifyNoInteractions(variableMutationHandler)
    }

    @Test
    fun `item builder subscriptions are disposed on rebind`() {
        val data = mock<Expression<JSONArray>>()
        val selector = mock<Expression<Boolean>>()
        val dataSubscription = mock<Disposable>()
        val selectorSubscription = mock<Disposable>()
        whenever(data.evaluate(resolver)).thenReturn(JSONArray())
        whenever(data.observe(any(), any())).thenReturn(dataSubscription)
        whenever(selector.observe(any(), any())).thenReturn(selectorSubscription)
        val itemBuilder = DivCollectionItemBuilder(
            data = data,
            prototypes = listOf(
                DivCollectionItemBuilder.Prototype(div.value.items!!.first(), null, selector),
            ),
        )
        val itemBuilderDiv = Div.Pager(div.value.copy(itemBuilder = itemBuilder))
        val itemBuilderBlock = itemBuilderDiv.toBlock(resolver, rootPath()) as DivBlock.Pager
        val reboundBlock = Div.Pager(itemBuilderDiv.value.copy())
            .toBlock(resolver, rootPath()) as DivBlock.Pager
        val view = divPagerViewWithLayout(itemBuilderDiv)

        underTest.bindView(view, itemBuilderBlock, divView)
        underTest.bindView(view, reboundBlock, divView)

        verify(dataSubscription).close()
        verify(selectorSubscription).close()
    }

    @Test
    fun `infinite scroll expression subscriptions are disposed on rebind`() {
        val multiPageObserver = argumentCaptor<(Boolean) -> Unit>()
        val infiniteObserver = argumentCaptor<(Boolean) -> Unit>()
        val multiPageSubscription = mock<Disposable>()
        val infiniteSubscription = mock<Disposable>()
        val div = Div.Pager(
            div().value.copy(
                infiniteScroll = observableBoolean(true, infiniteObserver, infiniteSubscription),
                multiPageScroll = observableBoolean(true, multiPageObserver, multiPageSubscription),
            )
        )
        val view = divPagerViewWithLayout(div)
        val block = div.toBlock(resolver, rootPath()) as DivBlock.Pager
        val reboundBlock = Div.Pager(div.value.copy()).toBlock(resolver, rootPath()) as DivBlock.Pager

        underTest.bindView(view, block, divView)
        underTest.bindView(view, reboundBlock, divView)

        verify(multiPageSubscription).close()
        verify(infiniteSubscription).close()
    }

    private fun div() = UnitTestData(PAGER_DIR, "pager_default_item.json").div as Div.Pager

    private fun divWithItemCountVariable(fileName: String = "pager_default_item.json"): Div.Pager {
        val div = UnitTestData(PAGER_DIR, fileName).div as Div.Pager
        return Div.Pager(div.value.copy(itemCountVariable = ITEM_COUNT_VARIABLE))
    }

    private fun divWithScroll(infiniteScroll: Boolean, multiPageScroll: Boolean): Div.Pager {
        return Div.Pager(
            div().value.copy(
                infiniteScroll = infiniteScroll.asExpression(),
                multiPageScroll = multiPageScroll.asExpression(),
            )
        )
    }

    private fun divPagerView(div: Div) = viewCreator.create(div, ExpressionResolver.EMPTY) as DivPagerView

    private fun divPagerViewWithLayout(div: Div) = divPagerView(div).apply {
        layoutParams = defaultLayoutParams()
    }

    private fun verifyItemCountVariableChange(value: Long) {
        verify(variableMutationHandler).setVariable(
            eq(ITEM_COUNT_VARIABLE),
            eq(value.toString()),
            any(),
            any(),
        )
        verifyNoMoreInteractions(variableMutationHandler)
    }

    private fun observableBoolean(
        initialValue: Boolean,
        observer: KArgumentCaptor<(Boolean) -> Unit>,
        subscription: Disposable = Disposable.NULL,
    ): Expression<Boolean> {
        val expression = mock<Expression<Boolean>>()
        whenever(expression.evaluate(resolver)).thenReturn(initialValue)
        whenever(expression.observeAndGet(eq(resolver), any())).thenCallRealMethod()
        whenever(expression.observe(eq(resolver), observer.capture())).thenReturn(subscription)
        return expression
    }

    private companion object {
        private const val PAGER_DIR = "div-pager"
        private const val DEFAULT_ITEM = 1
        private const val ITEM_COUNT_VARIABLE = "count"
    }
}
