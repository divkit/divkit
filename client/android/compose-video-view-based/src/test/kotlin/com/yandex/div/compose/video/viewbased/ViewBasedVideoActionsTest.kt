package com.yandex.div.compose.video.viewbased

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.DivView
import com.yandex.div.compose.actions.DivActionData
import com.yandex.div.compose.actions.DivActionHandlingContext
import com.yandex.div.compose.actions.DivExternalActionHandler
import com.yandex.div.core.player.DivPlayer
import com.yandex.div.core.player.DivPlayerFactory
import com.yandex.div.core.player.DivPlayerView
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.video
import com.yandex.div.test.data.videoSource
import com.yandex.div2.DivVideoScale
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class ViewBasedVideoActionsTest {

    @get:Rule
    val rule = createComposeRule()

    private val player = mock<DivPlayer>()
    private var emitPlayOnScaleChange = false
    private val playerView = object : DivPlayerView(getApplicationContext()) {
        override fun setScale(videoScale: DivVideoScale) {
            if (emitPlayOnScaleChange) {
                emitPlayOnScaleChange = false
                observer.firstValue.onPlay()
            }
        }
    }
    private val playerFactory = mock<DivPlayerFactory> {
        on { makePlayer(any(), any()) } doReturn player
        on { makePlayerView(any()) } doReturn playerView
    }
    private val observer = argumentCaptor<DivPlayer.Observer>()
    private val actions = mutableListOf<String>()
    private val errors = mutableListOf<String>()
    private val visible = mutableStateOf(true)
    private val divData = mutableStateOf(data(video(
        id = "video",
        videoSources = listOf(videoSource()),
        resumeActions = listOf(action(id = "resume", url = "https://divkit.tech/resume")),
        pauseActions = listOf(action(id = "pause", url = "https://divkit.tech/pause")),
        bufferingActions = listOf(action(id = "buffering", url = "https://divkit.tech/buffering")),
        endActions = listOf(action(id = "end", url = "https://divkit.tech/end")),
        fatalActions = listOf(action(id = "fatal", url = "https://divkit.tech/fatal")),
    )))
    private val divContext = DivContext(
        baseContext = getApplicationContext(),
        configuration = DivConfiguration(
            playerFactory = ViewBasedDivVideoPlayerFactory(playerFactory),
            actionHandler = object : DivExternalActionHandler {
                override fun handle(context: DivActionHandlingContext, action: DivActionData) {
                    actions.add(requireNotNull(action.id))
                }
            },
            reporter = object : DivReporter() {
                override fun reportError(message: String) {
                    errors.add(message)
                }
            },
        ),
    )

    @Test
    fun `resume action is invoked after buffering without another ready callback`() {
        setContent()
        rule.runOnIdle { observer.firstValue.onPlay() }
        rule.runOnIdle { observer.firstValue.onBuffering() }

        rule.runOnIdle { observer.firstValue.onPlay() }
        rule.waitForIdle()

        assertEquals(listOf("resume", "buffering", "resume"), actions)
    }

    @Test
    fun `buffering action is invoked on every loading cycle`() {
        setContent()
        rule.runOnIdle { observer.firstValue.onPlay() }
        rule.runOnIdle { observer.firstValue.onBuffering() }
        rule.runOnIdle { observer.firstValue.onPlay() }

        rule.runOnIdle { observer.firstValue.onBuffering() }
        rule.waitForIdle()

        assertEquals(listOf("resume", "buffering", "resume", "buffering"), actions)
    }

    @Test
    fun `repeated buffering callbacks in one turn invoke both actions`() {
        setContent()

        rule.runOnIdle {
            observer.firstValue.onBuffering()
            observer.firstValue.onBuffering()
        }
        rule.waitForIdle()

        assertEquals(listOf("buffering", "buffering"), actions)
    }

    @Test
    fun `actions preserve callback order in one turn`() {
        setContent()

        rule.runOnIdle {
            observer.firstValue.onPlay()
            observer.firstValue.onBuffering()
            observer.firstValue.onPlay()
            observer.firstValue.onPause()
            observer.firstValue.onEnd()
        }
        rule.waitForIdle()

        assertEquals(listOf("resume", "buffering", "resume", "pause", "end"), actions)
    }

    @Test
    @Suppress("DEPRECATION")
    fun `fatal action is invoked without an error cause`() {
        setContent()

        rule.runOnIdle { observer.firstValue.onFatal() }
        rule.waitForIdle()

        assertEquals(listOf("fatal"), actions)
        assertEquals(emptyList(), errors)
    }

    @Test
    fun `fatal action is invoked for repeated identical errors`() {
        setContent()
        val error = IllegalStateException("boom")

        rule.runOnIdle {
            observer.firstValue.onFatal(error)
            observer.firstValue.onFatal(error)
        }
        rule.waitForIdle()

        assertEquals(listOf("fatal", "fatal"), actions)
        assertEquals(listOf(
            "Playback in div with id 'video' encountered an error: boom",
            "Playback in div with id 'video' encountered an error: boom",
        ), errors)
    }

    @Test
    fun `callbacks during observer attachment are delivered`() {
        doAnswer {
            val attachedObserver = it.getArgument<DivPlayer.Observer>(0)
            attachedObserver.onPlay()
            attachedObserver.onBuffering()
            null
        }.whenever(player).addObserver(any())

        setContent()

        assertEquals(listOf("resume", "buffering"), actions)
    }

    @Test
    fun `end callback does not synthesize a pause action`() {
        setContent()
        rule.runOnIdle { observer.firstValue.onPlay() }

        rule.runOnIdle { observer.firstValue.onEnd() }
        rule.waitForIdle()

        assertEquals(listOf("resume", "end"), actions)
    }

    @Test
    fun `explicit pause callback is delivered after end`() {
        setContent()
        rule.runOnIdle { observer.firstValue.onEnd() }

        rule.runOnIdle { observer.firstValue.onPause() }
        rule.waitForIdle()

        assertEquals(listOf("end", "pause"), actions)
    }

    @Test
    fun `late callbacks do not invoke actions after disposal`() {
        setContent()
        rule.runOnIdle { visible.value = false }

        rule.runOnIdle { observer.firstValue.onPlay() }
        rule.waitForIdle()

        assertEquals(emptyList(), actions)
    }

    @Test
    fun `rebind uses new actions without replaying previous callbacks`() {
        setContent()
        rule.runOnIdle { observer.firstValue.onPlay() }
        rule.runOnIdle {
            divData.value = data(video(
                id = "video",
                videoSources = listOf(videoSource()),
                resumeActions = listOf(action(id = "updated", url = "https://divkit.tech/resume")),
            ))
        }

        rule.runOnIdle { observer.firstValue.onPlay() }
        rule.waitForIdle()

        assertEquals(listOf("resume", "updated"), actions)
    }

    @Test
    fun `callback during view update uses new actions without losing the event`() {
        setContent()

        rule.runOnIdle {
            emitPlayOnScaleChange = true
            divData.value = data(video(
                id = "video",
                videoSources = listOf(videoSource()),
                scale = constant(DivVideoScale.FILL),
                resumeActions = listOf(action(id = "updated", url = "https://divkit.tech/resume")),
            ))
        }
        rule.waitForIdle()

        assertEquals(listOf("updated"), actions)
    }

    @Test
    fun `initial source error invokes fatal action once`() {
        divData.value = data(video(
            id = "video",
            playerSettingsPayload = constant(org.json.JSONObject()),
            fatalActions = listOf(action(id = "fatal", url = "https://divkit.tech/fatal")),
        ))
        val error = IllegalStateException("No sources")
        lateinit var attachedObserver: DivPlayer.Observer
        doAnswer {
            attachedObserver = it.getArgument(0)
            attachedObserver.onFatal(error)
            null
        }.whenever(player).addObserver(any())
        doAnswer {
            attachedObserver.onFatal(error)
            null
        }.whenever(player).setSource(any(), any())

        setContent()

        assertEquals(listOf("fatal"), actions)
    }

    @Test
    fun `source configuration changes still dispatch callbacks after initialization`() {
        setContent()
        doAnswer {
            observer.firstValue.onPlay()
            null
        }.whenever(player).setSource(any(), any())

        for (autoplay in listOf(true, false)) {
            rule.runOnIdle {
                divData.value = data(video(
                    videoSources = listOf(videoSource()),
                    autostart = constant(autoplay),
                    resumeActions = listOf(action(id = "resume", url = "https://divkit.tech/resume")),
                ))
            }
            rule.waitForIdle()
        }

        assertEquals(listOf("resume", "resume"), actions)
    }

    private fun setContent() {
        rule.setContent {
            CompositionLocalProvider(LocalContext provides divContext) {
                if (visible.value) DivView(data = divData.value)
            }
        }
        rule.waitForIdle()
        verify(player).addObserver(observer.capture())
    }
}
