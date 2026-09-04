package com.yandex.div.core.view2.divs

import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import com.yandex.div2.DivContainer.LayoutMode
import com.yandex.div2.DivContainer.Orientation
import org.mockito.kotlin.mock
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BaseDivViewExtensionsReuseTest {

    private val path = DivStatePath.fromState(0)

    @Test
    fun `item builder data does not affect reuse when strict reuse is disabled`() {
        val oldItem = container(itemBuilderData = "old")
        val newItem = container(itemBuilderData = "new")

        assertTrue(oldItem.canReuseViewFor(newItem, strictItemBuilderViewReuseEnabled = false))
    }

    @Test
    fun `view is reused for the same item builder data`() {
        val oldItem = container(itemBuilderData = "same")
        val newItem = container(itemBuilderData = "same")

        assertTrue(oldItem.canReuseViewFor(newItem, strictItemBuilderViewReuseEnabled = true))
    }

    @Test
    fun `view is not reused for different item builder data`() {
        val oldItem = container(itemBuilderData = "old")
        val newItem = container(itemBuilderData = "new")

        assertFalse(oldItem.canReuseViewFor(newItem, strictItemBuilderViewReuseEnabled = true))
    }

    @Test
    fun `item builder view is not recycled by type`() {
        val oldItem = container(itemBuilderData = "old")
        val newItem = container(itemBuilderData = "new")

        assertFalse(oldItem.canRecycleViewFor(newItem, strictItemBuilderViewReuseEnabled = true))
    }

    @Test
    fun `regular view is still recycled by type`() {
        val oldItem = container(itemBuilderData = null)
        val newItem = container(itemBuilderData = null)

        assertTrue(oldItem.canRecycleViewFor(newItem, strictItemBuilderViewReuseEnabled = true))
    }

    @Test
    fun `container view is reused for container with the same layout kind`() {
        val oldItem = container(orientation = Orientation.HORIZONTAL)
        val newItem = container(orientation = Orientation.VERTICAL)

        assertTrue(oldItem.canReuseViewFor(newItem, strictItemBuilderViewReuseEnabled = false))
    }

    @Test
    fun `container view is not reused for container with different orientation`() {
        val oldItem = container(orientation = Orientation.OVERLAP)
        val newItem = container(orientation = Orientation.HORIZONTAL)

        assertFalse(oldItem.canReuseViewFor(newItem, strictItemBuilderViewReuseEnabled = false))
    }

    @Test
    fun `container view is not reused for wrap container`() {
        val oldItem = container(orientation = Orientation.HORIZONTAL)
        val newItem = container(orientation = Orientation.HORIZONTAL, layoutMode = LayoutMode.WRAP)

        assertFalse(oldItem.canReuseViewFor(newItem, strictItemBuilderViewReuseEnabled = false))
    }

    @Test
    fun `container view is not recycled for container with different orientation`() {
        val oldItem = container(orientation = Orientation.OVERLAP)
        val newItem = container(orientation = Orientation.HORIZONTAL)

        assertFalse(oldItem.canRecycleViewFor(newItem, strictItemBuilderViewReuseEnabled = true))
    }

    private fun container(
        itemBuilderData: String? = null,
        orientation: Orientation = Orientation.VERTICAL,
        layoutMode: LayoutMode = LayoutMode.NO_WRAP,
    ): DivBlock.Container {
        val resolver = ExpressionResolverImpl(mock(), mock(), mock(), mock(), itemBuilderData)
        val div = DivContainer(
            layoutMode = Expression.constant(layoutMode),
            orientation = Expression.constant(orientation),
        )
        return DivBlock.Container(Div.Container(div), resolver, path)
    }
}
