package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.Disposable
import com.yandex.div.core.player.DivPlayer
import com.yandex.div.core.player.DivPlayerView
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertSame

@RunWith(AndroidJUnit4::class)
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
