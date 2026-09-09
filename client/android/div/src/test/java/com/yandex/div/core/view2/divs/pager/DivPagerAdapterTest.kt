package com.yandex.div.core.view2.divs.pager

import android.util.SparseArray
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.Disposable
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.divs.pager.DivPagerBinder.Companion.VIRTUAL_ITEM_COUNT
import com.yandex.div.core.view2.divs.pager.DivPagerBinder.Companion.VIRTUAL_ITEM_COUNT_EXTENDED
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.core.toBlock
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.container
import com.yandex.div.test.data.text
import com.yandex.div2.DivVisibility
import org.junit.runner.RunWith
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.eq
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivPagerAdapterTest {

    private val pagerView = mock<DivPagerView>()
    private val observer = mock<RecyclerView.AdapterDataObserver>()

    @Test
    fun `extended virtual range wraps items at both real range boundaries`() {
        val first = item("first")
        val second = item("second")
        val third = item("third")
        val fourth = item("fourth")
        val fifth = item("fifth")
        val adapter = adapter(listOf(first, second, third, fourth, fifth))
        adapter.requestedVirtualItemCount = VIRTUAL_ITEM_COUNT_EXTENDED

        assertEquals(
            listOf(first, second, fifth, first, fifth, first, fifth),
            listOf(
                adapter.itemsToShow[0],
                adapter.itemsToShow[1],
                adapter.itemsToShow[VIRTUAL_ITEM_COUNT_EXTENDED - 1],
                adapter.itemsToShow[VIRTUAL_ITEM_COUNT_EXTENDED],
                adapter.itemsToShow[VIRTUAL_ITEM_COUNT_EXTENDED + 4],
                adapter.itemsToShow[VIRTUAL_ITEM_COUNT_EXTENDED + 5],
                adapter.itemsToShow[VIRTUAL_ITEM_COUNT_EXTENDED * 2 + 4],
            ),
        )
    }

    @Test
    fun `second visible item insertion emits virtual and real ranges`() {
        val adapter = adapter(listOf(item("first")))
        adapter.requestedVirtualItemCount = VIRTUAL_ITEM_COUNT_EXTENDED
        clearInvocations(observer)

        adapter.addItems(1, listOf(item("second")))

        inOrder(observer).apply {
            verify(observer).onItemRangeInserted(0, VIRTUAL_ITEM_COUNT_EXTENDED)
            verify(observer).onItemRangeInserted(VIRTUAL_ITEM_COUNT_EXTENDED + 1, 1)
            verify(observer).onItemRangeInserted(VIRTUAL_ITEM_COUNT_EXTENDED + 2, VIRTUAL_ITEM_COUNT_EXTENDED)
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `virtual range request with one visible item emits no notifications`() {
        val adapter = adapter(listOf(item("first")))
        clearInvocations(observer)

        adapter.requestedVirtualItemCount = VIRTUAL_ITEM_COUNT_EXTENDED

        verifyNoInteractions(observer)
    }

    @Test
    fun `second visible item insertion preserves selected real item`() {
        val adapter = adapter(listOf(item("first")))
        adapter.requestedVirtualItemCount = VIRTUAL_ITEM_COUNT_EXTENDED
        clearInvocations(pagerView)
        whenever(pagerView.currentItem).thenReturn(0)

        adapter.addItems(1, listOf(item("second")))

        verify(pagerView).currentItem = VIRTUAL_ITEM_COUNT_EXTENDED
    }

    @Test
    fun `last visible item removal emits virtual and real ranges`() {
        val adapter = adapter(listOf(item("first"), item("second")))
        adapter.requestedVirtualItemCount = VIRTUAL_ITEM_COUNT_EXTENDED
        clearInvocations(observer)

        adapter.removeItem(1)

        inOrder(observer).apply {
            verify(observer).onItemRangeRemoved(VIRTUAL_ITEM_COUNT_EXTENDED + 2, VIRTUAL_ITEM_COUNT_EXTENDED)
            verify(observer).onItemRangeRemoved(VIRTUAL_ITEM_COUNT_EXTENDED + 1, 1)
            verify(observer).onItemRangeRemoved(0, VIRTUAL_ITEM_COUNT_EXTENDED)
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `last visible item removal preserves selected real item`() {
        val adapter = adapter(listOf(item("first"), item("second")))
        adapter.requestedVirtualItemCount = VIRTUAL_ITEM_COUNT_EXTENDED
        clearInvocations(pagerView)
        whenever(pagerView.currentItem).thenReturn(VIRTUAL_ITEM_COUNT_EXTENDED)

        adapter.removeItem(1)

        verify(pagerView).currentItem = 0
    }

    @Test
    fun `virtual range expansion emits inserted edge ranges`() {
        val adapter = adapter(listOf(item("first"), item("second"), item("third")))
        adapter.requestedVirtualItemCount = VIRTUAL_ITEM_COUNT
        clearInvocations(observer)

        adapter.requestedVirtualItemCount = VIRTUAL_ITEM_COUNT_EXTENDED

        inOrder(observer).apply {
            verify(observer).onItemRangeInserted(0, VIRTUAL_ITEM_COUNT_EXTENDED - VIRTUAL_ITEM_COUNT)
            verify(observer).onItemRangeInserted(25, VIRTUAL_ITEM_COUNT_EXTENDED - VIRTUAL_ITEM_COUNT)
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `virtual range expansion preserves selected real item`() {
        val adapter = adapter(listOf(item("first"), item("second"), item("third")))
        adapter.requestedVirtualItemCount = VIRTUAL_ITEM_COUNT
        clearInvocations(pagerView)
        whenever(pagerView.currentItem).thenReturn(VIRTUAL_ITEM_COUNT + 1)

        adapter.requestedVirtualItemCount = VIRTUAL_ITEM_COUNT_EXTENDED

        verify(pagerView).currentItem = VIRTUAL_ITEM_COUNT_EXTENDED + 1
    }

    @Test
    fun `virtual range shrink emits removed edge ranges`() {
        val adapter = adapter(listOf(item("first"), item("second"), item("third")))
        adapter.requestedVirtualItemCount = VIRTUAL_ITEM_COUNT_EXTENDED
        clearInvocations(observer)

        adapter.requestedVirtualItemCount = VIRTUAL_ITEM_COUNT

        inOrder(observer).apply {
            verify(observer).onItemRangeRemoved(0, VIRTUAL_ITEM_COUNT_EXTENDED - VIRTUAL_ITEM_COUNT)
            verify(observer).onItemRangeRemoved(7, VIRTUAL_ITEM_COUNT_EXTENDED - VIRTUAL_ITEM_COUNT)
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `virtual range shrink preserves selected real item`() {
        val adapter = adapter(List(5) { item("item_$it") })
        adapter.requestedVirtualItemCount = VIRTUAL_ITEM_COUNT_EXTENDED
        clearInvocations(pagerView)
        whenever(pagerView.currentItem).thenReturn(VIRTUAL_ITEM_COUNT_EXTENDED + 1)

        adapter.requestedVirtualItemCount = VIRTUAL_ITEM_COUNT

        verify(pagerView).currentItem = VIRTUAL_ITEM_COUNT + 1
    }

    @Test
    fun `virtual range removal emits removed edge ranges`() {
        val adapter = adapter(listOf(item("first"), item("second"), item("third")))
        adapter.requestedVirtualItemCount = VIRTUAL_ITEM_COUNT_EXTENDED
        clearInvocations(observer)

        adapter.requestedVirtualItemCount = 0

        inOrder(observer).apply {
            verify(observer).onItemRangeRemoved(0, VIRTUAL_ITEM_COUNT_EXTENDED)
            verify(observer).onItemRangeRemoved(3, VIRTUAL_ITEM_COUNT_EXTENDED)
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `virtual range removal preserves selected real item`() {
        val adapter = adapter(listOf(item("first"), item("second"), item("third")))
        adapter.requestedVirtualItemCount = VIRTUAL_ITEM_COUNT_EXTENDED
        clearInvocations(pagerView)
        whenever(pagerView.currentItem).thenReturn(VIRTUAL_ITEM_COUNT_EXTENDED + 1)

        adapter.requestedVirtualItemCount = 0

        verify(pagerView).currentItem = 1
    }

    @Test
    fun `real item insertion refreshes both virtual ranges`() {
        val adapter = adapter(listOf(item("first"), item("second")))
        adapter.requestedVirtualItemCount = VIRTUAL_ITEM_COUNT_EXTENDED
        clearInvocations(observer)

        adapter.addItems(2, listOf(item("third")))

        inOrder(observer).apply {
            verify(observer).onItemRangeInserted(VIRTUAL_ITEM_COUNT_EXTENDED + 2, 1)
            verify(observer).onItemRangeChanged(0, VIRTUAL_ITEM_COUNT_EXTENDED, null)
            verify(observer).onItemRangeChanged(
                VIRTUAL_ITEM_COUNT_EXTENDED + 3,
                VIRTUAL_ITEM_COUNT_EXTENDED,
                null,
            )
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `real item insertion without virtual range emits only real range`() {
        val adapter = adapter(listOf(item("first"), item("second")))

        adapter.addItems(2, listOf(item("third")))

        verify(observer).onItemRangeInserted(2, 1)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `real item removal refreshes both virtual ranges`() {
        val adapter = adapter(listOf(item("first"), item("second"), item("third")))
        adapter.requestedVirtualItemCount = VIRTUAL_ITEM_COUNT_EXTENDED
        clearInvocations(observer)

        adapter.removeItem(1)

        inOrder(observer).apply {
            verify(observer).onItemRangeRemoved(VIRTUAL_ITEM_COUNT_EXTENDED + 1, 1)
            verify(observer).onItemRangeChanged(0, VIRTUAL_ITEM_COUNT_EXTENDED, null)
            verify(observer).onItemRangeChanged(VIRTUAL_ITEM_COUNT_EXTENDED + 2, VIRTUAL_ITEM_COUNT_EXTENDED, null)
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `real item removal without virtual range emits only real range`() {
        val adapter = adapter(listOf(item("first"), item("second"), item("third")))

        adapter.removeItem(1)

        verify(observer).onItemRangeRemoved(1, 1)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `first visible range insertion emits one complete range`() {
        val adapter = adapter(emptyList())
        adapter.requestedVirtualItemCount = VIRTUAL_ITEM_COUNT_EXTENDED
        clearInvocations(observer)

        adapter.addItems(0, listOf(item("first"), item("second")))

        verify(observer).onItemRangeInserted(0, VIRTUAL_ITEM_COUNT_EXTENDED * 2 + 2)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `gone item becoming visible emits virtual and real ranges`() {
        val visibilityObserver = argumentCaptor<(DivVisibility) -> Unit>()
        val visibility = mock<Expression<DivVisibility>>()
        whenever(visibility.evaluate(ExpressionResolver.EMPTY)).thenReturn(DivVisibility.GONE)
        whenever(visibility.observe(eq(ExpressionResolver.EMPTY), visibilityObserver.capture()))
            .thenReturn(Disposable.NULL)
        val dynamicItem = text(text = "second", visibility = visibility).toBlock(
            ExpressionResolver.EMPTY,
            DivStatePath.fromState(0),
        )
        val adapter = adapter(listOf(item("first"), dynamicItem))
        adapter.requestedVirtualItemCount = VIRTUAL_ITEM_COUNT_EXTENDED
        clearInvocations(observer)

        visibilityObserver.firstValue(DivVisibility.VISIBLE)

        inOrder(observer).apply {
            verify(observer).onItemRangeInserted(0, VIRTUAL_ITEM_COUNT_EXTENDED)
            verify(observer).onItemRangeInserted(VIRTUAL_ITEM_COUNT_EXTENDED + 1, 1)
            verify(observer).onItemRangeInserted(VIRTUAL_ITEM_COUNT_EXTENDED + 2, VIRTUAL_ITEM_COUNT_EXTENDED)
            verifyNoMoreInteractions()
        }
    }

    private fun adapter(items: List<DivBlock>) = DivPagerAdapter(
        items = items,
        divView = mock(),
        divBinder = mock(),
        pageTranslations = SparseArray(),
        viewCreator = mock(),
        pagerView = pagerView,
    ).apply {
        registerAdapterDataObserver(observer)
    }

    private fun item(id: String) = container(id = id).toBlock(
        ExpressionResolver.EMPTY,
        DivStatePath.fromState(0),
    )

}
