package com.yandex.div.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.actions.DivActionData
import com.yandex.div.compose.actions.DivActionHandlingContext
import com.yandex.div.compose.actions.DivExternalActionHandler
import com.yandex.div.compose.video.DivVideoPlayer
import com.yandex.div.compose.video.DivVideoPlayerConfig
import com.yandex.div.compose.video.DivVideoPlayerFactory
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.video
import com.yandex.div.test.data.videoSource
import com.yandex.div2.Div
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DivViewWithVideoTest {

    @get:Rule
    val rule = createComposeRule()

    private val reporter = TestReporter()
    private val variableController = DivVariableController()
    private val playerFactory = FakeDivVideoPlayerFactory()
    private val externalActionHandler = RecordingExternalActionHandler()

    private val configuration = DivComposeConfiguration(
        actionHandler = externalActionHandler,
        playerFactory = playerFactory,
        reporter = reporter,
        variableController = variableController,
    )

    @Test
    fun `error is reported if no sources and no payload`() {
        reporter.failOnError = false

        setContent(
            video(id = "video")
        )

        assertEquals(
            "Neither 'video_source' nor 'player_settings_payload' are specified for video with id 'video'",
            reporter.lastError
        )
    }

    @Test
    fun `no error reported when only player_settings_payload is set`() {
        setContent(
            video(playerSettingsPayload = constant(org.json.JSONObject()))
        )

        assertNull(reporter.lastError)
    }

    @Test
    fun `no error reported when only video_sources is set`() {
        setContent(
            video(videoSources = listOf(videoSource()))
        )

        assertNull(reporter.lastError)
    }

    @Test
    fun `release is called when video leaves composition`() {
        val visible = mutableStateOf(true)
        val divData = data(video(videoSources = listOf(videoSource())))
        rule.setContent {
            val divContext = DivContext(
                baseContext = LocalContext.current,
                configuration = configuration,
            )
            CompositionLocalProvider(LocalContext provides divContext) {
                Box {
                    if (visible.value) {
                        DivView(data = divData)
                    }
                }
            }
        }

        rule.waitForIdle()
        val player = playerFactory.lastCreatedPlayer!!
        assertFalse(player.released)

        visible.value = false
        rule.waitForIdle()

        assertTrue(player.released)
    }

    @Test
    fun `config sources reflect sources from data`() {
        setContent(
            video(videoSources = listOf(videoSource(mimeType = constant("video/webm"))))
        )

        rule.waitForIdle()

        val sources = playerFactory.lastCreatedPlayer!!.lastConfig!!.sources
        assertEquals(1, sources.size)
        assertEquals("video/webm", sources[0].mimeType)
    }

    @Test
    fun `config muted reflects muted value from data`() {
        setContent(
            video(
                muted = constant(true),
                videoSources = listOf(videoSource()),
            )
        )

        rule.waitForIdle()

        assertEquals(true, playerFactory.lastCreatedPlayer!!.lastConfig!!.muted)
    }

    @Test
    fun `config playbackSpeed reflects speed from data`() {
        setContent(
            video(
                playbackSpeed = constant(1.5),
                videoSources = listOf(videoSource()),
            )
        )

        rule.waitForIdle()

        assertEquals(1.5f, playerFactory.lastCreatedPlayer!!.lastConfig!!.playbackSpeed)
    }

    @Test
    fun `resume actions are invoked when player starts playing`() {
        setContent(
            video(
                resumeActions = listOf(action(id = "resume", url = "test://resume")),
                videoSources = listOf(videoSource()),
            )
        )

        rule.waitForIdle()
        playerFactory.lastCreatedPlayer!!.isPlayingState.value = true
        rule.waitForIdle()

        assertEquals("resume", externalActionHandler.lastAction?.id)
    }

    @Test
    fun `pause actions are invoked when player pauses without ending`() {
        setContent(
            video(
                pauseActions = listOf(action(id = "pause", url = "test://pause")),
                videoSources = listOf(videoSource()),
            )
        )

        rule.waitForIdle()
        val player = playerFactory.lastCreatedPlayer!!
        player.isPlayingState.value = true
        rule.waitForIdle()
        player.isPlayingState.value = false
        rule.waitForIdle()

        assertEquals("pause", externalActionHandler.lastAction?.id)
    }

    @Test
    fun `pause actions are not invoked when player has ended`() {
        setContent(
            video(
                pauseActions = listOf(action(id = "pause", url = "test://pause")),
                videoSources = listOf(videoSource()),
            )
        )

        rule.waitForIdle()
        val player = playerFactory.lastCreatedPlayer!!
        player.isPlayingState.value = true
        rule.waitForIdle()
        player.isEndedState.value = true
        player.isPlayingState.value = false
        rule.waitForIdle()

        assertNull(externalActionHandler.lastAction)
    }

    @Test
    fun `buffering actions are invoked when player buffers`() {
        setContent(
            video(
                bufferingActions = listOf(action(id = "buffer", url = "test://buffer")),
                videoSources = listOf(videoSource()),
            )
        )

        rule.waitForIdle()
        playerFactory.lastCreatedPlayer!!.isBufferingState.value = true
        rule.waitForIdle()

        assertEquals("buffer", externalActionHandler.lastAction?.id)
    }

    @Test
    fun `end actions are invoked when player ends`() {
        setContent(
            video(
                endActions = listOf(action(id = "end", url = "test://end")),
                videoSources = listOf(videoSource()),
            )
        )

        rule.waitForIdle()
        playerFactory.lastCreatedPlayer!!.isEndedState.value = true
        rule.waitForIdle()

        assertEquals("end", externalActionHandler.lastAction?.id)
    }

    @Test
    fun `fatal actions are invoked when player errors out`() {
        reporter.failOnError = false

        setContent(
            video(
                id = "video",
                fatalActions = listOf(action(id = "fatal", url = "test://fatal")),
                videoSources = listOf(videoSource()),
            )
        )

        rule.waitForIdle()
        playerFactory.lastCreatedPlayer!!.errorState.value = IllegalStateException("boom")
        rule.waitForIdle()

        assertEquals("fatal", externalActionHandler.lastAction?.id)
    }

    @Test
    fun `error is reported when player encounters an error`() {
        reporter.failOnError = false

        setContent(
            video(
                id = "video",
                videoSources = listOf(videoSource()),
            )
        )

        rule.waitForIdle()
        playerFactory.lastCreatedPlayer!!.errorState.value = IllegalStateException("boom")
        rule.waitForIdle()

        assertEquals(
            "Playback in div with id 'video' encountered an error: boom",
            reporter.lastError
        )
    }

    @Test
    fun `elapsed_time_variable is updated from player time`() {
        val elapsed = Variable.IntegerVariable("elapsed", 0)
        variableController.declare(elapsed)

        setContent(
            video(
                elapsedTimeVariable = "elapsed",
                videoSources = listOf(videoSource()),
            )
        )

        rule.waitForIdle()
        playerFactory.lastCreatedPlayer!!.currentTimeMsState.value = 5_000L
        rule.waitForIdle()

        assertEquals(5_000L, elapsed.getValue())
    }

    @Test
    fun `player seeks when elapsed_time_variable changes`() {
        val elapsed = Variable.IntegerVariable("elapsed", 0)
        variableController.declare(elapsed)

        setContent(
            video(
                elapsedTimeVariable = "elapsed",
                videoSources = listOf(videoSource()),
            )
        )

        rule.waitForIdle()
        elapsed.set(7_500L)
        rule.waitForIdle()

        assertEquals(7_500L, playerFactory.lastCreatedPlayer!!.currentTimeMs.value)
    }

    private fun setContent(content: Div) {
        rule.setContent(
            configuration = configuration,
            data = data(content)
        )
    }
}

private class RecordingExternalActionHandler : DivExternalActionHandler {
    var lastAction: DivActionData? = null
        private set

    override fun handle(context: DivActionHandlingContext, action: DivActionData) {
        lastAction = action
    }
}

private class FakeDivVideoPlayerFactory : DivVideoPlayerFactory {

    var lastCreatedPlayer: FakeDivVideoPlayer? = null
        private set

    override fun makePlayer(): DivVideoPlayer {
        return FakeDivVideoPlayer().also { lastCreatedPlayer = it }
    }
}

private class FakeDivVideoPlayer : DivVideoPlayer {
    val isReadyState = MutableStateFlow(false)
    val isPlayingState = MutableStateFlow(false)
    val isBufferingState = MutableStateFlow(false)
    val isEndedState = MutableStateFlow(false)
    val currentTimeMsState = MutableStateFlow(0L)
    val errorState = MutableStateFlow<Throwable?>(null)

    override val isReady: StateFlow<Boolean> = isReadyState
    override val isPlaying: StateFlow<Boolean> = isPlayingState
    override val isBuffering: StateFlow<Boolean> = isBufferingState
    override val isEnded: StateFlow<Boolean> = isEndedState
    override val currentTimeMs: StateFlow<Long> = currentTimeMsState
    override val error: StateFlow<Throwable?> = errorState

    var lastConfig: DivVideoPlayerConfig? = null
        private set
    var released: Boolean = false
        private set

    override fun play() = Unit
    override fun pause() = Unit

    override fun seek(timeMs: Long) {
        currentTimeMsState.value = timeMs
    }

    override fun release() {
        released = true
    }

    @Composable
    override fun Content(config: DivVideoPlayerConfig, modifier: Modifier) {
        lastConfig = config
    }
}
