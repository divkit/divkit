package com.yandex.div.core.view2.divs.gallery

import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.DefaultCollectionItemBinding
import com.yandex.div.internal.core.toBlock
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.container
import com.yandex.div.test.testContextThemeWrapper
import org.junit.runner.RunWith
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.doReturn
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivGalleryAdapterTest {

    private val observer = mock<RecyclerView.AdapterDataObserver>()

    @Test
    fun `old first item is rebound when a range is inserted at start`() {
        val adapter = adapter(itemCount = 1)

        adapter.addItems(0, listOf(item(), item()))

        inOrder(observer).apply {
            verify(observer).onItemRangeInserted(0, 2)
            verify(observer).onItemRangeChanged(2, 1, null)
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `old last item is rebound when a range is inserted at end`() {
        val adapter = adapter(itemCount = 1)

        adapter.addItems(1, listOf(item(), item()))

        inOrder(observer).apply {
            verify(observer).onItemRangeInserted(1, 2)
            verify(observer).onItemRangeChanged(0, 1, null)
            verifyNoMoreInteractions()
        }
    }

    @Test
    fun `existing items are not rebound when a range is inserted in the middle`() {
        val adapter = adapter(itemCount = 2)

        adapter.addItems(1, listOf(item()))

        verify(observer).onItemRangeInserted(1, 1)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `existing items are not rebound in a multi column gallery`() {
        val adapter = adapter(itemCount = 1)
        adapter.columnCount = 2

        adapter.addItems(0, listOf(item(), item()))

        verify(observer).onItemRangeInserted(0, 2)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `first range insertion does not rebind a nonexistent edge item`() {
        val adapter = adapter(itemCount = 0)

        adapter.addItems(0, listOf(item(), item()))

        verify(observer).onItemRangeInserted(0, 2)
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun `holder reads current adapter orientation`() {
        val adapter = adapter(itemCount = 0)
        val layout = holderLayout(adapter)
        adapter.orientation = RecyclerView.VERTICAL

        assertEquals(RecyclerView.VERTICAL, layout.orientation())
    }

    @Test
    fun `holder reads current adapter column count`() {
        val adapter = adapter(itemCount = 0)
        val layout = holderLayout(adapter)
        adapter.columnCount = 3

        assertEquals(3, layout.columnCount())
    }

    @Test
    fun `holder reads current adapter cross spacing`() {
        val adapter = adapter(itemCount = 0)
        val layout = holderLayout(adapter)
        adapter.crossSpacing = 12f

        assertEquals(12f, layout.crossSpacing())
    }

    private fun holderLayout(adapter: DivGalleryAdapter): DivGalleryItemLayout {
        val context = Div2Context(testContextThemeWrapper(), DivConfiguration.Builder(mock()).build())
        val divView = mock<Div2View> { on { this.context } doReturn context }
        return DivGalleryViewHolder.create(adapter, divView, mock(), mock()).itemView as DivGalleryItemLayout
    }

    private fun adapter(itemCount: Int) = DivGalleryAdapter(
        items = List(itemCount) { item() },
        itemBinding = DefaultCollectionItemBinding(createHolder = { mock() }),
    ).apply {
        registerAdapterDataObserver(observer)
    }

    private fun item() = container().toBlock(
        ExpressionResolver.EMPTY,
        DivStatePath.fromState(0),
    )
}
