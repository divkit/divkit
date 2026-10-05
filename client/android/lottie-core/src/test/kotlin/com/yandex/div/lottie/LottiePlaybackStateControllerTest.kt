package com.yandex.div.lottie

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

class LottiePlaybackStateControllerTest {

    private val playback = LottiePlaybackStateController()

    @Test
    fun `stop prevents configured playback on an idle key`() {
        playback.stop("animation")

        assertEquals(mapOf<Any, PlaybackState>("animation" to PlaybackState.Stopped(0)), playback.states.value)
    }

    @Test
    fun `stop deactivates only the target key`() {
        playback.start("animation")
        playback.start("other")

        playback.stop("animation")

        assertEquals(
            mapOf<Any, PlaybackState>(
                "animation" to PlaybackState.Stopped(1),
                "other" to PlaybackState.Started(1),
            ),
            playback.states.value,
        )
    }

    @Test
    fun `start after explicit stop creates a new playback`() {
        playback.start("animation")
        playback.stop("animation")

        playback.start("animation")

        assertEquals(PlaybackState.Started(2), playback.states.value["animation"])
    }

    @Test
    fun `start activates target key only`() {
        playback.start("animation")

        assertEquals(mapOf<Any, PlaybackState>("animation" to PlaybackState.Started(1)), playback.states.value)
    }

    @Test
    fun `completion deactivates the matching start`() {
        playback.start("animation")

        playback.onPlaybackFinished("animation", sequence = 1)

        assertEquals(PlaybackState.Completed(1), playback.states.value["animation"])
    }

    @Test
    fun `configured playback completion stays stopped until explicit start`() {
        playback.onAutoplayed("animation")

        assertEquals(PlaybackState.Completed(0), playback.states.value["animation"])
    }

    @Test
    fun `configured completion cannot stop an explicit start`() {
        playback.start("animation")

        playback.onAutoplayed("animation")

        assertEquals(PlaybackState.Started(1), playback.states.value["animation"])
    }

    @Test
    fun `late completion cannot finish a newer start`() {
        playback.start("animation")
        playback.start("animation")

        playback.onPlaybackFinished("animation", sequence = 1)

        assertEquals(PlaybackState.Started(2), playback.states.value["animation"])
    }

    @Test
    fun `start after completion creates a new playback`() {
        playback.start("animation")
        playback.onPlaybackFinished("animation", sequence = 1)

        playback.start("animation")

        assertEquals(PlaybackState.Started(2), playback.states.value["animation"])
    }

    @Test
    fun `explicit stop replaces completed playback with stopped playback`() {
        playback.start("animation")
        playback.onPlaybackFinished("animation", sequence = 1)

        playback.stop("animation")

        assertEquals(PlaybackState.Stopped(1), playback.states.value["animation"])
    }

    @Test
    fun `late completion cannot replace an explicit stop`() {
        playback.start("animation")
        playback.stop("animation")

        playback.onPlaybackFinished("animation", sequence = 1)

        assertEquals(PlaybackState.Stopped(1), playback.states.value["animation"])
    }

    @Test
    fun `published snapshots do not change after another start`() {
        playback.start("animation")
        val snapshot = playback.states.value

        playback.start("animation")

        assertEquals(mapOf<Any, PlaybackState>("animation" to PlaybackState.Started(1)), snapshot)
    }

    @Test
    fun `parallel starts retain every sequence increment`() {
        val executor = Executors.newFixedThreadPool(4)
        try {
            val starts = List(1000) { executor.submit { playback.start("animation") } }
            starts.forEach { it.get(5, TimeUnit.SECONDS) }

            assertEquals(PlaybackState.Started(1000), playback.states.value["animation"])
        } finally {
            executor.shutdownNow()
        }
    }

    @Test
    fun `key subscriber ignores changes to another key`() = runBlocking {
        // Arrange
        val observed = mutableListOf<PlaybackState>()
        val subscription = launch(start = CoroutineStart.UNDISPATCHED) {
            playback.states
                .map { it["animation"] ?: PlaybackState.Idle }
                .distinctUntilChanged()
                .collect { observed += it }
        }

        // Act
        playback.start("other")
        kotlinx.coroutines.yield()
        subscription.cancel()

        // Assert
        assertEquals(listOf<PlaybackState>(PlaybackState.Idle), observed)
    }
}
