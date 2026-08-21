package com.yandex.div.core.view2.divs.widgets

import android.app.Activity
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import com.yandex.div.core.player.DivPlayer
import com.yandex.div.core.player.DivPlayerView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
internal class DivVideoViewTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun `player initialization runs immediately when recycler is idle`() {
        val view = DivVideoView(context)
        val recyclerView = TestRecyclerView(context, RecyclerView.SCROLL_STATE_IDLE)
        var initializationCount = 0
        attach(view, recyclerView)

        view.videoBindingController.initializePlayerWhenIdle(
            view.videoBindingController.beginVideoBinding(),
        ) {
            initializationCount++
        }

        assertEquals(1, initializationCount)
    }

    @Test
    fun `player initialization waits until recycler becomes idle`() {
        val view = DivVideoView(context)
        val recyclerView = TestRecyclerView(context, RecyclerView.SCROLL_STATE_SETTLING)
        var initializationCount = 0
        attach(view, recyclerView)

        view.videoBindingController.initializePlayerWhenIdle(
            view.videoBindingController.beginVideoBinding(),
        ) {
            initializationCount++
        }
        assertEquals(0, initializationCount)

        recyclerView.moveToIdle()

        assertEquals(1, initializationCount)
        assertEquals(1, recyclerView.removedListenerCount)
    }

    @Test
    fun `new binding cancels pending player initialization`() {
        val view = DivVideoView(context)
        val recyclerView = TestRecyclerView(context, RecyclerView.SCROLL_STATE_DRAGGING)
        var initializationCount = 0
        attach(view, recyclerView)

        view.videoBindingController.initializePlayerWhenIdle(
            view.videoBindingController.beginVideoBinding(),
        ) {
            initializationCount++
        }
        view.videoBindingController.beginVideoBinding()
        recyclerView.moveToIdle()

        assertEquals(0, initializationCount)
        assertEquals(1, recyclerView.removedListenerCount)
    }

    @Test
    fun `new binding releases attached player immediately`() {
        val view = DivVideoView(context)
        val playerView = TestPlayerView(context)
        val player = mock<DivPlayer>()
        playerView.attach(player)
        view.addView(playerView)

        view.videoBindingController.beginVideoBinding()

        assertNull(playerView.getAttachedPlayer())
        verify(player).release()
    }

    @Test
    fun `release cancels pending player initialization`() {
        val view = DivVideoView(context)
        val recyclerView = TestRecyclerView(context, RecyclerView.SCROLL_STATE_DRAGGING)
        var initializationCount = 0
        attach(view, recyclerView)

        view.videoBindingController.initializePlayerWhenIdle(
            view.videoBindingController.beginVideoBinding(),
        ) {
            initializationCount++
        }
        view.releaseMedia()
        recyclerView.moveToIdle()

        assertEquals(0, initializationCount)
        assertEquals(1, recyclerView.removedListenerCount)
    }

    @Test
    fun `player initialization waits for attachment and scrolling to finish`() {
        val view = DivVideoView(context)
        val recyclerView = TestRecyclerView(context, RecyclerView.SCROLL_STATE_SETTLING)
        var initializationCount = 0

        view.videoBindingController.initializePlayerWhenIdle(
            view.videoBindingController.beginVideoBinding(),
        ) {
            initializationCount++
        }
        assertEquals(0, initializationCount)

        attach(view, recyclerView)
        assertEquals(0, initializationCount)

        recyclerView.moveToIdle()

        assertEquals(1, initializationCount)
    }

    @Test
    fun `player initialization waits for all scrolling parent recyclers`() {
        val view = DivVideoView(context)
        val innerRecyclerView = TestRecyclerView(context, RecyclerView.SCROLL_STATE_SETTLING)
        val outerRecyclerView = TestRecyclerView(context, RecyclerView.SCROLL_STATE_DRAGGING)
        var initializationCount = 0
        attachNested(view, innerRecyclerView, outerRecyclerView)

        view.videoBindingController.initializePlayerWhenIdle(
            view.videoBindingController.beginVideoBinding(),
        ) {
            initializationCount++
        }

        innerRecyclerView.moveToIdle()
        assertEquals(0, initializationCount)

        outerRecyclerView.moveToIdle()

        assertEquals(1, initializationCount)
        assertEquals(1, innerRecyclerView.removedListenerCount)
        assertEquals(1, outerRecyclerView.removedListenerCount)
    }

    @Test
    fun `callback from unobserved recycler removes stale listener`() {
        val view = DivVideoView(context)
        val observedRecyclerView = TestRecyclerView(context, RecyclerView.SCROLL_STATE_SETTLING)
        val staleRecyclerView = TestRecyclerView(context, RecyclerView.SCROLL_STATE_IDLE)
        var initializationCount = 0
        attach(view, observedRecyclerView)

        view.videoBindingController.initializePlayerWhenIdle(
            view.videoBindingController.beginVideoBinding(),
        ) {
            initializationCount++
        }
        staleRecyclerView.addOnScrollListener(requireNotNull(observedRecyclerView.currentListener))

        staleRecyclerView.moveToIdle()

        assertEquals(0, initializationCount)
        assertEquals(1, staleRecyclerView.removedListenerCount)

        observedRecyclerView.moveToIdle()

        assertEquals(1, initializationCount)
    }

    private fun attach(view: DivVideoView, recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addView(view)
        Robolectric.buildActivity(Activity::class.java).setup().get().setContentView(recyclerView)
    }

    private fun attachNested(
        view: DivVideoView,
        innerRecyclerView: RecyclerView,
        outerRecyclerView: RecyclerView,
    ) {
        innerRecyclerView.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.HORIZONTAL,
            false,
        )
        innerRecyclerView.addView(view)
        outerRecyclerView.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false,
        )
        outerRecyclerView.addView(innerRecyclerView)
        Robolectric.buildActivity(Activity::class.java).setup().get().setContentView(outerRecyclerView)
    }

    private class TestRecyclerView(
        context: Context,
        private var state: Int,
    ) : RecyclerView(context) {

        private var listener: OnScrollListener? = null
        val currentListener: OnScrollListener?
            get() = listener
        var removedListenerCount = 0
            private set

        override fun getScrollState(): Int = state

        override fun addOnScrollListener(listener: OnScrollListener) {
            this.listener = listener
        }

        override fun removeOnScrollListener(listener: OnScrollListener) {
            if (this.listener === listener) {
                this.listener = null
                removedListenerCount++
            }
        }

        fun moveToIdle() {
            state = SCROLL_STATE_IDLE
            listener?.onScrollStateChanged(this, state)
        }
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
