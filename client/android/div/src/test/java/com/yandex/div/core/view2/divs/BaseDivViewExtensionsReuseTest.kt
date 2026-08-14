package com.yandex.div.core.view2.divs

import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.internal.core.DivBlock
import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock

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

    private fun container(itemBuilderData: String?): DivBlock.Container {
        val resolver = ExpressionResolverImpl(mock(), mock(), mock(), mock(), itemBuilderData)
        return DivBlock.Container(Div.Container(DivContainer()), resolver, path)
    }
}
