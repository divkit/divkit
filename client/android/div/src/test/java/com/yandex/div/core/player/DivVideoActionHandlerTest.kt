package com.yandex.div.core.player

import android.app.Activity
import com.yandex.div.DivDataTag
import com.yandex.div.core.Disposable
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivVideoViewState
import com.yandex.div.core.view2.DivViewState
import com.yandex.div.core.view2.DivViewStateStore
import com.yandex.div.core.view2.disableAssertions
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.video
import com.yandex.div2.Div
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

private const val VIDEO_ID = "video"
private const val SCOPE_ID = "scope"

@RunWith(RobolectricTestRunner::class)
class DivVideoActionHandlerTest {

    private val activity = Robolectric.buildActivity(Activity::class.java).get()
    private val context = Div2Context(activity, DivConfiguration.Builder(mock()).build())
    private val underTest = DivVideoActionHandler()

    @Test
    fun `start action stores playing state`() {
        val viewStateStore = RecordingViewStateStore()
        val divView = createDivView(video(id = VIDEO_ID), viewStateStore)

        val isHandled = underTest.handleAction(divView, VIDEO_ID, null, "start")

        assertTrue(isHandled)
        assertEquals(DivVideoViewState(DivVideoPlaybackState.PLAYING), viewStateStore.singleState)
    }

    @Test
    fun `pause action stores paused state`() {
        val viewStateStore = RecordingViewStateStore()
        val divView = createDivView(video(id = VIDEO_ID), viewStateStore)

        val isHandled = underTest.handleAction(divView, VIDEO_ID, null, "pause")

        assertTrue(isHandled)
        assertEquals(DivVideoViewState(DivVideoPlaybackState.PAUSED), viewStateStore.singleState)
    }

    @Test
    fun `unknown action is not handled`() = disableAssertions {
        val viewStateStore = RecordingViewStateStore()
        val divView = createDivView(video(id = VIDEO_ID), viewStateStore)

        val isHandled = underTest.handleAction(divView, VIDEO_ID, null, "unknown")

        assertFalse(isHandled)
        assertTrue(viewStateStore.states.isEmpty())
    }

    @Test
    fun `missing video is not handled`() {
        val viewStateStore = RecordingViewStateStore()
        val divView = createDivView(container(), viewStateStore)

        val isHandled = underTest.handleAction(divView, VIDEO_ID, null, "start")

        assertFalse(isHandled)
        assertTrue(viewStateStore.states.isEmpty())
    }

    @Test
    fun `duplicate video is not handled without scope`() {
        val viewStateStore = RecordingViewStateStore()
        val content = container(items = listOf(video(id = VIDEO_ID), video(id = VIDEO_ID)))
        val divView = createDivView(content, viewStateStore)

        val isHandled = underTest.handleAction(divView, VIDEO_ID, null, "start")

        assertFalse(isHandled)
        assertTrue(viewStateStore.states.isEmpty())
    }

    @Test
    fun `scope disambiguates duplicate video ids`() {
        val viewStateStore = RecordingViewStateStore()
        val content = container(
            items = listOf(
                video(id = VIDEO_ID),
                container(id = SCOPE_ID, items = listOf(video(id = VIDEO_ID))),
            )
        )
        val divView = createDivView(content, viewStateStore)

        val isHandled = underTest.handleAction(divView, VIDEO_ID, SCOPE_ID, "start")

        assertTrue(isHandled)
        assertEquals(DivVideoViewState(DivVideoPlaybackState.PLAYING), viewStateStore.singleState)
    }

    @Test
    fun `missing scope falls back to globally unique video`() {
        val viewStateStore = RecordingViewStateStore()
        val divView = createDivView(video(id = VIDEO_ID), viewStateStore)

        val isHandled = underTest.handleAction(divView, VIDEO_ID, SCOPE_ID, "start")

        assertTrue(isHandled)
        assertEquals(DivVideoViewState(DivVideoPlaybackState.PLAYING), viewStateStore.singleState)
    }

    private fun createDivView(content: Div, viewStateStore: DivViewStateStore): Div2View =
        Div2View(context).apply {
            setData(data(content), DivDataTag("test"))
            this.viewStateStore = viewStateStore
        }
}

private class RecordingViewStateStore : DivViewStateStore {

    val states = mutableListOf<DivViewState>()
    val singleState: DivViewState
        get() = states.single()

    override fun get(divPath: String): DivViewState? = null

    override fun put(divPath: String, state: DivViewState) {
        states += state
    }

    override fun getOrPut(divPath: String, defaultValue: () -> DivViewState): DivViewState = defaultValue()

    override fun observe(divPath: String, observer: (DivViewState) -> Unit): Disposable = Disposable.NULL
}
