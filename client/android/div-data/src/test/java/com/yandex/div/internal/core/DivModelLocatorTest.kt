package com.yandex.div.internal.core

import com.yandex.div.core.state.DivStatePath
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.container
import com.yandex.div.test.data.custom
import com.yandex.div.test.data.data
import com.yandex.div.test.data.state
import com.yandex.div.test.data.text
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class DivModelLocatorTest {
    private val resolver = ExpressionResolver.EMPTY

    @Test
    fun `siblings without ids retain their original indices in model paths`() {
        val target = text(text = "Target")
        val root = container(id = "root", items = listOf(text(text = "First"), target))
        val locator = DivBlockLocator(
            DivModelSearchEnvironment(data(root).states.single(), resolver),
        )

        val result = locator.findTarget(null, null, resolver, matches = { it.div === target })

        assertEquals("0/root/child#1", result.getOrThrow().path.fullPath)
    }

    @Test
    fun `scope selects the matching model among duplicate ids`() {
        val target = text(id = "target", text = "Right")
        val root = container(items = listOf(
            container(id = "left", items = listOf(text(id = "target", text = "Left"))),
            container(id = "right", items = listOf(target)),
        ))
        val locator = DivBlockLocator(
            DivModelSearchEnvironment(data(root).states.single(), resolver),
        )

        val result = locator.findTarget("target", "right", resolver, matches = { true })

        assertSame(target, result.getOrThrow().div)
    }

    @Test
    fun `model traversal enters nested custom elements`() {
        val target = text(id = "target", text = "Target")
        val root = custom(type = "outer", items = listOf(custom(type = "inner", items = listOf(target))))
        val locator = DivBlockLocator(
            DivModelSearchEnvironment(data(root).states.single(), resolver),
        )

        val result = locator.findTarget("target", null, resolver, matches = { true })

        assertSame(target, result.getOrThrow().div)
    }

    @Test
    fun `active state selects one of the matching model targets`() {
        val target = text(id = "target", text = "Selected")
        val root = state(id = "switcher", states = listOf(
            state(stateId = "other", div = text(id = "target", text = "Other")),
            state(stateId = "selected", div = target),
        ))
        val locator = DivBlockLocator(
            DivModelSearchEnvironment(
                data(root).states.single(),
                resolver,
                activeStates = setOf(DivStatePath.parse("0/switcher/selected")),
            ),
        )

        val result = locator.findTarget("target", null, resolver, matches = { true })

        assertSame(target, result.getOrThrow().div)
    }
}
