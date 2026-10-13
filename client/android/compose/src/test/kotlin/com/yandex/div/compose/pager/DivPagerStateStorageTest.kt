package com.yandex.div.compose.pager

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.lazy.LazyListState
import com.yandex.div.test.data.pager
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertSame

class DivPagerStateStorageTest {

    private val storage = DivPagerStateStorage()
    private val p1 = pager(id = "p1").value
    private val p2 = pager(id = "p2").value

    @Test
    fun `get returns null for unknown pager`() {
        assertNull(storage.get(pager(id = "missing").value))
    }

    @Test
    fun `put publishes state for the given pager`() {
        val state = pagerState()

        storage.put(p1, state)

        assertSame(state, storage.get(p1))
    }

    @Test
    fun `put overwrites the entry for the same pager`() {
        val first = pagerState()
        val second = pagerState()
        storage.put(p1, first)

        storage.put(p1, second)

        assertSame(second, storage.get(p1))
    }

    @Test
    fun `put for a new id does not affect other ids`() {
        val first = pagerState()
        val second = pagerState()

        storage.put(p1, first)
        storage.put(p2, second)

        assertSame(first, storage.get(p1))
        assertSame(second, storage.get(p2))
    }

    @Test
    fun `pagers with the same id retain separate states`() {
        val duplicate = pager(id = "p1").value
        val first = pagerState()
        val second = pagerState()
        storage.put(p1, first)

        storage.put(duplicate, second)

        assertSame(first, storage.get(p1))
        assertSame(second, storage.get(duplicate))
    }

    @Test
    fun `pagers without ids retain separate states`() {
        val first = pager().value
        val second = pager().value
        val firstState = pagerState()
        val secondState = pagerState()
        storage.put(first, firstState)

        storage.put(second, secondState)

        assertSame(firstState, storage.get(first))
        assertSame(secondState, storage.get(second))
    }

    @Test
    fun `retain removes state of a pager deleted by a patch`() {
        storage.put(p1, pagerState())

        storage.retain(setOf(p2))

        assertNull(storage.get(p1))
    }

    @Test
    fun `retain preserves state of an unchanged pager`() {
        val state = pagerState()
        storage.put(p1, state)

        storage.retain(setOf(p1))

        assertSame(state, storage.get(p1))
    }

    private fun pagerState(): DivPagerState = DivPagerState(
        pageCount = 3,
        listState = LazyListState(),
        snapPosition = SnapPosition.Start,
    )
}
