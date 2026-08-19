package com.yandex.div.core.view2

import com.yandex.div.core.player.DivVideoPlaybackState
import com.yandex.div.core.state.DivStatePath
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DivViewStateStoreImplTest {

    private val path = DivStatePath.parse("0/gallery/video")
    private val underTest = DivViewStateStoreImpl()

    @Test
    fun `stores view state by unique div path`() {
        val state = DivVideoViewState(DivVideoPlaybackState.PLAYING)

        underTest.put(path.fullPath, state)

        assertEquals(state, underTest.get(path.fullPath))
    }

    @Test
    fun `isolates equal div ids at different paths`() {
        val otherPath = DivStatePath.parse("0/other/video")
        val playing = DivVideoViewState(DivVideoPlaybackState.PLAYING)
        val paused = DivVideoViewState(DivVideoPlaybackState.PAUSED)

        underTest.put(path.fullPath, playing)
        underTest.put(otherPath.fullPath, paused)

        assertEquals(playing, underTest.get(path.fullPath))
        assertEquals(paused, underTest.get(otherPath.fullPath))
    }

    @Test
    fun `initializes missing value only once`() {
        var initializations = 0
        val state = DivVideoViewState(DivVideoPlaybackState.PLAYING)

        val first = underTest.getOrPut(path.fullPath) { state.also { initializations++ } }
        val second = underTest.getOrPut(path.fullPath) { state.also { initializations++ } }

        assertEquals(state, first)
        assertEquals(state, second)
        assertEquals(1, initializations)
    }

    @Test
    fun `empty store does not retain values`() {
        DivViewStateStore.EMPTY.put(path.fullPath, DivVideoViewState(DivVideoPlaybackState.PLAYING))

        assertNull(DivViewStateStore.EMPTY.get(path.fullPath))
    }

    @Test
    fun `notifies observer when value changes`() {
        val values = mutableListOf<DivViewState>()
        val playing = DivVideoViewState(DivVideoPlaybackState.PLAYING)
        val paused = DivVideoViewState(DivVideoPlaybackState.PAUSED)
        underTest.observe(path.fullPath, values::add)

        underTest.put(path.fullPath, playing)
        underTest.put(path.fullPath, paused)

        assertEquals(listOf(playing, paused), values)
    }

    @Test
    fun `notifies observer when same value is set again`() {
        val values = mutableListOf<DivViewState>()
        val state = DivVideoViewState(DivVideoPlaybackState.PLAYING)
        underTest.observe(path.fullPath, values::add)

        underTest.put(path.fullPath, state)
        underTest.put(path.fullPath, state)

        assertEquals(listOf(state, state), values)
    }

    @Test
    fun `does not notify observer after subscription is closed`() {
        val values = mutableListOf<DivViewState>()
        val subscription = underTest.observe(path.fullPath, values::add)
        subscription.close()

        underTest.put(path.fullPath, DivVideoViewState(DivVideoPlaybackState.PLAYING))

        assertEquals(emptyList<DivViewState>(), values)
    }

    @Test
    fun `reset clears stored values`() {
        underTest.put(path.fullPath, DivVideoViewState(DivVideoPlaybackState.PLAYING))

        underTest.reset()

        assertNull(underTest.get(path.fullPath))
    }
}
