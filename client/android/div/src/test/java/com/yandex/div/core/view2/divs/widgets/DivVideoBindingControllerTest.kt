package com.yandex.div.core.view2.divs.widgets

import android.app.Activity
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.player.DivPlayer
import com.yandex.div.core.player.DivPlayerView
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.Robolectric
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
internal class DivVideoBindingControllerTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val view = DivVideoView(context)
    private val underTest = view.videoBindingController

    @Test
    fun `initialization runs immediately when attached recycler is idle`() {
        attachToRecycler(RecyclerView.SCROLL_STATE_IDLE)
        var initialized = false

        underTest.initializePlayerWhenIdle(underTest.beginVideoBinding()) {
            initialized = true
        }

        assertTrue(initialized)
    }

    @Test
    fun `initialization waits until recycler becomes idle`() {
        val recycler = attachToRecycler(RecyclerView.SCROLL_STATE_SETTLING)
        var stateWhenInitialized: Int? = null
        underTest.initializePlayerWhenIdle(underTest.beginVideoBinding()) {
            stateWhenInitialized = recycler.scrollState
        }

        recycler.moveToIdle()

        assertEquals(RecyclerView.SCROLL_STATE_IDLE, stateWhenInitialized)
    }

    @Test
    fun `new binding cancels pending initialization`() {
        val recycler = attachToRecycler(RecyclerView.SCROLL_STATE_DRAGGING)
        var initialized = false
        underTest.initializePlayerWhenIdle(underTest.beginVideoBinding()) {
            initialized = true
        }

        underTest.beginVideoBinding()
        recycler.moveToIdle()

        assertFalse(initialized)
        assertEquals(0, recycler.listenerCount)
    }

    @Test
    fun `new binding releases attached player`() {
        val playerView = TestPlayerView(context)
        val player = mock<DivPlayer>()
        playerView.attach(player)
        view.addView(playerView)

        underTest.beginVideoBinding()

        assertNull(playerView.getAttachedPlayer())
        verify(player).release()
    }

    @Test
    fun `release cancels pending initialization`() {
        val recycler = attachToRecycler(RecyclerView.SCROLL_STATE_SETTLING)
        var initialized = false
        underTest.initializePlayerWhenIdle(underTest.beginVideoBinding()) {
            initialized = true
        }

        underTest.release()
        recycler.moveToIdle()

        assertFalse(initialized)
        assertEquals(0, recycler.listenerCount)
    }

    @Test
    fun `initialization waits for attachment and scrolling to finish`() {
        var scrollStateWhenInitialized: Int? = null
        underTest.initializePlayerWhenIdle(underTest.beginVideoBinding()) {
            scrollStateWhenInitialized = (view.parent as RecyclerView).scrollState
        }

        val recycler = attachToRecycler(RecyclerView.SCROLL_STATE_SETTLING)
        recycler.moveToIdle()

        assertEquals(RecyclerView.SCROLL_STATE_IDLE, scrollStateWhenInitialized)
    }

    @Test
    fun `initialization waits for every scrolling parent recycler`() {
        val innerRecycler = TestRecyclerView(context, RecyclerView.SCROLL_STATE_SETTLING).apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            addView(view)
        }
        val outerRecycler = TestRecyclerView(context, RecyclerView.SCROLL_STATE_DRAGGING).apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            addView(innerRecycler)
        }
        Robolectric.buildActivity(Activity::class.java).setup().get().setContentView(outerRecycler)
        var statesWhenInitialized: Pair<Int, Int>? = null
        underTest.initializePlayerWhenIdle(underTest.beginVideoBinding()) {
            statesWhenInitialized = innerRecycler.scrollState to outerRecycler.scrollState
        }

        innerRecycler.moveToIdle()
        outerRecycler.moveToIdle()

        assertEquals(
            RecyclerView.SCROLL_STATE_IDLE to RecyclerView.SCROLL_STATE_IDLE,
            statesWhenInitialized,
        )
    }

    @Test
    fun `callback from unobserved recycler removes stale listener`() {
        val observedRecycler = attachToRecycler(RecyclerView.SCROLL_STATE_SETTLING)
        val staleRecycler = TestRecyclerView(context, RecyclerView.SCROLL_STATE_IDLE)
        var observedStateWhenInitialized: Int? = null
        underTest.initializePlayerWhenIdle(underTest.beginVideoBinding()) {
            observedStateWhenInitialized = observedRecycler.scrollState
        }
        staleRecycler.addOnScrollListener(observedRecycler.listeners.single())

        staleRecycler.moveToIdle()
        observedRecycler.moveToIdle()

        assertEquals(
            RecyclerView.SCROLL_STATE_IDLE to 0,
            observedStateWhenInitialized to staleRecycler.listenerCount,
        )
    }

    private fun attachToRecycler(state: Int): TestRecyclerView {
        val recycler = TestRecyclerView(context, state)
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.addView(view)
        Robolectric.buildActivity(Activity::class.java).setup().get().setContentView(recycler)
        return recycler
    }

    private class TestRecyclerView(
        context: Context,
        private var state: Int,
    ) : RecyclerView(context) {

        val listeners = linkedSetOf<OnScrollListener>()
        val listenerCount: Int
            get() = listeners.size

        override fun getScrollState(): Int = state

        override fun addOnScrollListener(listener: OnScrollListener) {
            listeners += listener
        }

        override fun removeOnScrollListener(listener: OnScrollListener) {
            listeners -= listener
        }

        fun moveToIdle() {
            state = SCROLL_STATE_IDLE
            listeners.toList().forEach { it.onScrollStateChanged(this, state) }
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
