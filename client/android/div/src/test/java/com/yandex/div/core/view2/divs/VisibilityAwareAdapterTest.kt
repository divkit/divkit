package com.yandex.div.core.view2.divs

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.Disposable
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.core.toBlock
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.text
import com.yandex.div2.DivVisibility
import org.junit.runner.RunWith
import org.mockito.kotlin.KArgumentCaptor
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

@RunWith(AndroidJUnit4::class)
class VisibilityAwareAdapterTest {

    private val resolver = mock<ExpressionResolver>()
    private val path = rootPath()

    private val observer = mock<RecyclerView.AdapterDataObserver>()

    @Test
    fun `invisible items reserve a slot in the adapter`() {
        val visibleItem = item(DivVisibility.VISIBLE)
        val invisibleItem = item(DivVisibility.INVISIBLE)

        val adapter = adapter(listOf(visibleItem, invisibleItem))

        assertEquals(2, adapter.itemCount)
        assertEquals(listOf(visibleItem, invisibleItem), adapter.visibleItems)
    }

    @Test
    fun `gone items are excluded from the adapter`() {
        val visibleItem = item(DivVisibility.VISIBLE)
        val goneItem = item(DivVisibility.GONE)

        val adapter = adapter(listOf(visibleItem, goneItem))

        assertEquals(1, adapter.itemCount)
        assertEquals(listOf(visibleItem), adapter.visibleItems)
    }

    @Test
    fun `visible to invisible transition does not change adapter slots`() {
        val callback = argumentCaptor<(DivVisibility) -> Unit>()
        val item = item(observableVisibility(DivVisibility.VISIBLE, callback))

        val adapter = adapter(listOf(item))

        callback.firstValue(DivVisibility.INVISIBLE)

        assertEquals(1, adapter.itemCount)
        verifyNoInteractions(observer)
    }

    @Test
    fun `invisible to visible transition does not change adapter slots`() {
        val callback = argumentCaptor<(DivVisibility) -> Unit>()
        val item = item(observableVisibility(DivVisibility.INVISIBLE, callback))

        val adapter = adapter(listOf(item))

        callback.firstValue(DivVisibility.VISIBLE)

        assertEquals(1, adapter.itemCount)
        verifyNoInteractions(observer)
    }

    @Test
    fun `visible to gone transition removes the slot`() {
        val callback = argumentCaptor<(DivVisibility) -> Unit>()
        val item = item(observableVisibility(DivVisibility.VISIBLE, callback))

        val adapter = adapter(listOf(item))

        callback.firstValue(DivVisibility.GONE)

        assertEquals(0, adapter.itemCount)
        verifyRemoved(0)
    }

    @Test
    fun `invisible to gone transition removes the slot`() {
        val callback = argumentCaptor<(DivVisibility) -> Unit>()
        val item = item(observableVisibility(DivVisibility.INVISIBLE, callback))

        val adapter = adapter(listOf(item))

        callback.firstValue(DivVisibility.GONE)

        assertEquals(0, adapter.itemCount)
        verifyRemoved(0)
    }

    @Test
    fun `gone to invisible transition inserts a slot`() {
        val callback = argumentCaptor<(DivVisibility) -> Unit>()
        val item = item(observableVisibility(DivVisibility.GONE, callback))

        val adapter = adapter(listOf(item))

        callback.firstValue(DivVisibility.INVISIBLE)

        assertEquals(1, adapter.itemCount)
        verifyInserted(position = 0, count = 1)
    }

    @Test
    fun `gone to visible transition inserts a slot`() {
        val callback = argumentCaptor<(DivVisibility) -> Unit>()
        val item = item(observableVisibility(DivVisibility.GONE, callback))

        val adapter = adapter(listOf(item))

        callback.firstValue(DivVisibility.VISIBLE)

        assertEquals(1, adapter.itemCount)
        verifyInserted(position = 0, count = 1)
    }

    @Test
    fun `multiple non gone items are inserted as one visible range`() {
        val existingVisibleItem = item(DivVisibility.VISIBLE)
        val existingGoneItem = item(DivVisibility.GONE)
        val addedGoneItem = item(DivVisibility.GONE)
        val addedInvisibleItem = item(DivVisibility.INVISIBLE)
        val addedVisibleItem = item(DivVisibility.VISIBLE)
        val adapter = adapter(listOf(existingVisibleItem, existingGoneItem))

        adapter.addItems(2, listOf(addedGoneItem, addedInvisibleItem, addedVisibleItem))

        assertEquals(
            listOf(existingVisibleItem, addedInvisibleItem, addedVisibleItem),
            adapter.visibleItems,
        )
        verifyInserted(position = 1, count = 2)
    }

    @Test
    fun `all gone items are added without adapter notification`() {
        val adapter = adapter(listOf(item(DivVisibility.VISIBLE)))

        adapter.addItems(1, listOf(item(DivVisibility.GONE), item(DivVisibility.GONE)))

        assertEquals(3, adapter.items.size)
        verifyNoInteractions(observer)
    }

    @Test
    fun `visible position is computed relative to non gone items only`() {
        val gone = item(DivVisibility.GONE)
        val visible = item(DivVisibility.VISIBLE)
        val invisible = item(DivVisibility.INVISIBLE)
        val visibleEnd = item(DivVisibility.VISIBLE)

        val adapter = adapter(listOf(gone, visible, invisible, visibleEnd))

        assertEquals(3, adapter.itemCount)
        assertSame(visible, adapter.visibleItems[0])
        assertSame(invisible, adapter.visibleItems[1])
        assertSame(visibleEnd, adapter.visibleItems[2])
    }

    @Test
    fun `transition of an invisible item to gone removes the correct visible position`() {
        val callback = argumentCaptor<(DivVisibility) -> Unit>()
        val first = item(DivVisibility.VISIBLE)
        val target = item(observableVisibility(DivVisibility.INVISIBLE, callback))
        val last = item(DivVisibility.VISIBLE)

        val adapter = adapter(listOf(first, target, last))

        callback.firstValue(DivVisibility.GONE)

        assertEquals(2, adapter.itemCount)
        verifyRemoved(1)
    }

    private fun adapter(items: List<DivBlock>) = TestAdapter(items).apply {
        registerAdapterDataObserver(observer)
    }

    private fun verifyInserted(position: Int, count: Int) {
        verify(observer).onItemRangeInserted(position, count)
        verifyNoMoreInteractions(observer)
    }

    private fun verifyRemoved(position: Int) {
        verify(observer).onItemRangeRemoved(position, 1)
        verifyNoMoreInteractions(observer)
    }

    private fun item(visibility: DivVisibility) = item(constant(visibility))

    private fun item(expression: Expression<DivVisibility>) =
        text(text = "item", visibility = expression).toBlock(resolver, path)

    private fun observableVisibility(
        initialValue: DivVisibility,
        callback: KArgumentCaptor<(DivVisibility) -> Unit>,
    ): Expression<DivVisibility> {
        val expression = mock<Expression<DivVisibility>>()
        whenever(expression.evaluate(resolver)).thenReturn(initialValue)
        whenever(expression.observe(eq(resolver), callback.capture())).thenReturn(Disposable.NULL)
        return expression
    }

    private class TestAdapter(
        items: List<DivBlock>,
    ) : VisibilityAwareAdapter<RecyclerView.ViewHolder>(items) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = mock()

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = Unit
    }

}
