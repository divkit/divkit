package com.yandex.div.compose.pager

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.lazy.LazyListState
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class DivPagerStateStorageTest {

    private val storage = DivPagerStateStorage()

    @Test
    fun `get returns null for unknown id`() {
        assertNull(storage.get("missing"))
    }

    @Test
    fun `put publishes state under the given id`() {
        val state = pagerState()

        storage.put("p1", state)

        assertSame(state, storage.get("p1"))
    }

    @Test
    fun `put overwrites the entry under the same id`() {
        val first = pagerState()
        val second = pagerState()
        storage.put("p1", first)

        storage.put("p1", second)

        assertSame(second, storage.get("p1"))
    }

    @Test
    fun `put for a new id does not affect other ids`() {
        val first = pagerState()
        val second = pagerState()

        storage.put("p1", first)
        storage.put("p2", second)

        assertSame(first, storage.get("p1"))
        assertSame(second, storage.get("p2"))
    }

    private fun pagerState(): DivPagerState = DivPagerState(
        pageCount = 3,
        listState = LazyListState(),
        snapPosition = SnapPosition.Start,
    )
}
