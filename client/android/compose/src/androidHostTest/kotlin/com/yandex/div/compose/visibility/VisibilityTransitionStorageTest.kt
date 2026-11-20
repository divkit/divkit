package com.yandex.div.compose.visibility

import com.yandex.div.test.data.text
import com.yandex.div2.DivVisibility
import kotlin.test.Test
import kotlin.test.assertEquals

class VisibilityTransitionStorageTest {
    private val storage = VisibilityTransitionStorage()

    @Test
    fun `elements with the same id keep independent visibility`() {
        val first = text(id = "item", text = "first").value()
        val second = text(id = "item", text = "second").value()
        storage.get(first, DivVisibility.VISIBLE).renderedVisibility = DivVisibility.GONE

        assertEquals(DivVisibility.VISIBLE, storage.get(second, DivVisibility.VISIBLE).renderedVisibility)
    }

    @Test
    fun `patched element keeps visibility after obsolete aliases are released`() {
        val original = text(id = "item", text = "original").value()
        val updated = text(id = "item", text = "updated").value()
        storage.get(original, DivVisibility.VISIBLE).renderedVisibility = DivVisibility.INVISIBLE
        storage.alias(original, updated)

        storage.retain(setOf(updated))

        assertEquals(DivVisibility.INVISIBLE, storage.get(updated, DivVisibility.VISIBLE).renderedVisibility)
    }

    @Test
    fun `removed builder element starts with its current visibility when reused`() {
        val item = text(text = "item").value()
        storage.get(item, DivVisibility.VISIBLE)

        storage.remove(setOf(item))

        assertEquals(DivVisibility.GONE, storage.get(item, DivVisibility.GONE).renderedVisibility)
    }

    @Test
    fun `gone element remains in layout while attached exit is pending`() {
        val state = storage.get(text(text = "item").value(), DivVisibility.VISIBLE)
        state.attach(DivVisibility.VISIBLE)

        assertEquals(DivVisibility.VISIBLE, state.renderedVisibility(DivVisibility.GONE))
    }

    @Test
    fun `detached element does not remain in layout for a pending exit`() {
        val state = storage.get(text(text = "item").value(), DivVisibility.VISIBLE)
        state.attach(DivVisibility.VISIBLE)

        state.detach()

        assertEquals(DivVisibility.GONE, state.renderedVisibility(DivVisibility.GONE))
    }

    @Test
    fun `element remains retained while another composition still renders it`() {
        val state = storage.get(text(text = "item").value(), DivVisibility.VISIBLE)
        state.attach(DivVisibility.VISIBLE)
        state.attach(DivVisibility.VISIBLE)

        state.detach()

        assertEquals(DivVisibility.VISIBLE, state.renderedVisibility(DivVisibility.GONE))
    }

    @Test
    fun `first observer replaces visibility left by a disposed composition`() {
        val state = storage.get(text(text = "item").value(), DivVisibility.VISIBLE)
        state.startObserving(DivVisibility.VISIBLE)
        state.stopObserving()

        state.startObserving(DivVisibility.GONE)

        assertEquals(DivVisibility.GONE, state.renderedVisibility)
    }

    @Test
    fun `later observer keeps visibility rendered by the current composition`() {
        val state = storage.get(text(text = "item").value(), DivVisibility.GONE)
        state.startObserving(DivVisibility.GONE)

        state.attach(DivVisibility.VISIBLE)

        assertEquals(DivVisibility.GONE, state.renderedVisibility)
    }
}
