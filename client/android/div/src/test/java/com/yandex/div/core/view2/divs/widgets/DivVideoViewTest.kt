package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.Disposable
import com.yandex.div.core.player.DivPlayer
import com.yandex.div.core.player.DivPlayerView
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowLooper
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
internal class DivVideoViewTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val underTest = DivVideoView(context)

    @Test
    fun `getPlayerView returns attached player view`() {
        val playerView = TestPlayerView(context)
        underTest.addView(playerView)

        assertSame(playerView, underTest.getPlayerView())
    }

    @Test
    fun `getPlayerView returns null when player view is absent`() {
        assertNull(underTest.getPlayerView())
    }

    @Test
    fun `releaseMedia detaches and releases current player`() {
        val playerView = TestPlayerView(context)
        val player = mock<DivPlayer>()
        playerView.attach(player)
        underTest.addView(playerView)

        underTest.releaseMedia()

        assertNull(playerView.getAttachedPlayer())
        verify(player).release()
    }

    @Test
    fun `releaseMedia closes all video subscriptions`() {
        val firstSubscription = mock<Disposable>()
        val secondSubscription = mock<Disposable>()
        underTest.addVideoSubscription(firstSubscription)
        underTest.addVideoSubscription(secondSubscription)

        underTest.releaseMedia()

        verify(firstSubscription).close()
        verify(secondSubscription).close()
    }

    @Test
    fun `releaseMedia retries failed player release after detach`() {
        var releaseAttempts = 0
        val releaseFailure = IllegalStateException("Failed release")
        val player = object : DivPlayer {
            override fun release() {
                releaseAttempts++
                if (releaseAttempts == 1) {
                    throw releaseFailure
                }
            }
        }
        val playerView = TestPlayerView(context)
        playerView.attach(player)
        underTest.addView(playerView)

        assertSame(releaseFailure, assertFailsWith<IllegalStateException> { underTest.releaseMedia() })
        assertNull(playerView.getAttachedPlayer())

        underTest.releaseMedia()
        underTest.releaseMedia()

        assertEquals(2, releaseAttempts)
    }

    @Test
    fun `releaseMedia does not release player twice during reentrant cleanup`() {
        var releaseAttempts = 0
        val player = object : DivPlayer {
            override fun release() {
                releaseAttempts++
                underTest.releaseMedia()
            }
        }
        val playerView = TestPlayerView(context)
        playerView.attach(player)
        underTest.addView(playerView)

        underTest.releaseMedia()
        underTest.releaseMedia()

        assertEquals(1, releaseAttempts)
        assertNull(playerView.getAttachedPlayer())
    }

    @Test
    fun `releaseMedia retries pending release and releases a newly attached player`() {
        var releaseAttempts = 0
        val firstPlayer = object : DivPlayer {
            override fun release() {
                releaseAttempts++
                if (releaseAttempts == 1) {
                    error("Failed release")
                }
            }
        }
        val secondPlayer = mock<DivPlayer>()
        val playerView = TestPlayerView(context)
        playerView.attach(firstPlayer)
        underTest.addView(playerView)
        assertFailsWith<IllegalStateException> { underTest.releaseMedia() }
        playerView.attach(secondPlayer)

        underTest.releaseMedia()
        assertNull(playerView.getAttachedPlayer())
        underTest.releaseMedia()

        assertEquals(2, releaseAttempts)
        verify(secondPlayer).release()
        assertNull(playerView.getAttachedPlayer())
    }

    @Test
    fun `releaseMedia does not release a reattached pending player twice`() {
        var releaseAttempts = 0
        val player = object : DivPlayer {
            override fun release() {
                releaseAttempts++
                if (releaseAttempts == 1) {
                    error("Failed release")
                }
            }
        }
        val playerView = TestPlayerView(context)
        playerView.attach(player)
        underTest.addView(playerView)
        assertFailsWith<IllegalStateException> { underTest.releaseMedia() }
        playerView.attach(player)

        underTest.releaseMedia()
        assertNull(playerView.getAttachedPlayer())
        underTest.releaseMedia()

        assertEquals(2, releaseAttempts)
        assertNull(playerView.getAttachedPlayer())
    }

    @Test
    fun `releaseMedia cancels a binding started reentrantly`() = runTest {
        val idleGate = CompletableDeferred<Unit>()
        var replacementStarted = false
        var replacementCompleted = false

        underTest.launchVideoBinding {
            underTest.launchVideoBinding {
                replacementStarted = true
                idleGate.await()
                replacementCompleted = true
            }
        }
        assertTrue(replacementStarted)

        underTest.releaseMedia()
        idleGate.complete(Unit)
        ShadowLooper.idleMainLooper()

        assertFalse(replacementCompleted)
    }

    @Test
    fun `new binding cancels a binding started reentrantly`() = runTest {
        val idleGate = CompletableDeferred<Unit>()
        var staleBindingCompleted = false
        var currentBindingCompleted = false

        underTest.launchVideoBinding {
            underTest.launchVideoBinding {
                idleGate.await()
                staleBindingCompleted = true
            }
        }

        underTest.launchVideoBinding {
            currentBindingCompleted = true
        }
        idleGate.complete(Unit)
        ShadowLooper.idleMainLooper()

        assertTrue(currentBindingCompleted)
        assertFalse(staleBindingCompleted)
    }

    @Test
    fun `releaseMedia cancels a binding started by cancellation cleanup`() = runTest {
        val initialGate = CompletableDeferred<Unit>()
        val replacementGate = CompletableDeferred<Unit>()
        var replacementStarted = false
        var replacementCompleted = false

        underTest.launchVideoBinding {
            try {
                initialGate.await()
            } finally {
                underTest.launchVideoBinding {
                    replacementStarted = true
                    replacementGate.await()
                    replacementCompleted = true
                }
            }
        }

        underTest.cancelPendingVideoBinding()
        assertTrue(replacementStarted)
        underTest.releaseMedia()
        replacementGate.complete(Unit)
        ShadowLooper.idleMainLooper()

        assertFalse(replacementCompleted)
    }

    @Test
    fun `video binding starts and resumes immediately on main`() = runTest {
        val idleGate = CompletableDeferred<Unit>()
        val events = mutableListOf<String>()

        underTest.launchVideoBinding {
            events += "started"
            idleGate.await()
            events += "resumed"
        }
        assertEquals(listOf("started"), events)

        idleGate.complete(Unit)

        assertEquals(listOf("started", "resumed"), events)
    }

    private class TestPlayerView(context: Context) : DivPlayerView(context) {

        private var player: DivPlayer? = null

        override fun attach(player: DivPlayer) {
            this.player = player
        }

        override fun detach() {
            player = null
        }

        override fun getAttachedPlayer(): DivPlayer? = player
    }
}
