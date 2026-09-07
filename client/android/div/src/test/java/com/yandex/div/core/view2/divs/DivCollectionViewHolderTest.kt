package com.yandex.div.core.view2.divs

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.expression.local.RuntimeStore
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.divs.gallery.DivGalleryViewHolder
import com.yandex.div.core.view2.divs.widgets.DivHolderView
import com.yandex.div.core.view2.divs.widgets.DivLineHeightTextView
import com.yandex.div.core.view2.divs.widgets.DivLinearLayout
import com.yandex.div.core.widget.DivViewWrapper
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.core.toBlock
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import com.yandex.div2.DivContainer.Orientation
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DivCollectionViewHolderTest : DivBinderTest() {

    private val divBinder = mock<DivBinder> {
        on { bind(any(), any(), any()) } doAnswer {
            @Suppress("UNCHECKED_CAST")
            (it.arguments[0] as DivHolderView<DivBlock>).divBlock = it.arguments[1] as DivBlock
        }
    }
    private val viewWrapper = DivViewWrapper(context)
    private val underTest = DivGalleryViewHolder(viewWrapper, divBinder, viewCreator, divView)

    @BeforeTest
    fun setUp() {
        whenever(divView.runtimeStore) doReturn RuntimeStore.EMPTY
    }

    @Test
    fun `recreate child view when recycled for container with different orientation`() {
        underTest.bind(container(Orientation.OVERLAP), 0)

        underTest.bind(container(Orientation.HORIZONTAL), 1)

        assertTrue(viewWrapper.child is DivLinearLayout)
    }

    @Test
    fun `recreate child view when recycled for div of different type`() {
        underTest.bind(container(Orientation.HORIZONTAL), 0)

        underTest.bind(text(text = "text").toBlock(resolver, rootPath()), 1)

        assertTrue(viewWrapper.child is DivLineHeightTextView)
    }

    @Test
    fun `reuse child view when recycled for replaceable div`() {
        underTest.bind(container(Orientation.HORIZONTAL), 0)
        val childView = viewWrapper.child

        underTest.bind(container(Orientation.VERTICAL), 1)

        assertSame(childView, viewWrapper.child)
    }

    private fun container(orientation: Orientation) =
        Div.Container(DivContainer(orientation = Expression.constant(orientation))).toBlock(resolver, rootPath())
}
