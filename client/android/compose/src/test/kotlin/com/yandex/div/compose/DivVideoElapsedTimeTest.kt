package com.yandex.div.compose

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.views.video.DivVideoView
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.variable
import com.yandex.div.test.data.video
import com.yandex.div.test.data.videoSource
import com.yandex.div2.Div
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivVideoElapsedTimeTest {

    @get:Rule
    val rule = createComposeRule()

    private val variables = DivVariableController()
    private val playerFactory = TestDivVideoPlayerFactory()
    private val visible = mutableStateOf(true)
    private val context = DivContext(
        baseContext = getApplicationContext(),
        configuration = divConfiguration {
            playerFactory = this@DivVideoElapsedTimeTest.playerFactory
            variableController = variables
            reporter = TestReporter()
        },
    )
    private val content = mutableStateOf(video(elapsedTimeVariable = "elapsed", videoSources = listOf(videoSource())))
    private val viewContext = context.getViewContext(data(content.value))
    private val localComponent = mutableStateOf(viewContext.rootLocalComponent)
    private val player get() = requireNotNull(playerFactory.lastCreatedPlayer)

    @Test
    fun `initial elapsed variable seeks without being overwritten`() {
        val elapsed = Variable.IntegerVariable("elapsed", 7_500L)
        variables.declare(elapsed)

        setContent(video(elapsedTimeVariable = "elapsed", videoSources = listOf(videoSource())))

        assertEquals(listOf(7_500L), player.seekRequests)
        assertEquals(7_500L, elapsed.getValue())
    }

    @Test
    fun `elapsed position survives leaving composition`() {
        val elapsed = Variable.IntegerVariable("elapsed", 0L)
        variables.declare(elapsed)
        setContent(video(elapsedTimeVariable = "elapsed", videoSources = listOf(videoSource())))
        rule.runOnIdle { player.currentTimeMsState.value = 8_000L }
        rule.waitForIdle()

        reenterComposition()

        assertEquals(listOf(8_000L), player.seekRequests)
        assertEquals(8_000L, elapsed.getValue())
    }

    @Test
    fun `player progress does not seek back into the player`() {
        val elapsed = Variable.IntegerVariable("elapsed", 0L)
        variables.declare(elapsed)
        setContent(video(elapsedTimeVariable = "elapsed", videoSources = listOf(videoSource())))

        rule.runOnIdle { player.currentTimeMsState.value = 1_000L }
        rule.waitForIdle()

        assertEquals(1_000L, elapsed.getValue())
        assertEquals(listOf(0L), player.seekRequests)
    }

    @Test
    fun `external elapsed change seeks once without waiting for acknowledgement`() {
        val elapsed = Variable.IntegerVariable("elapsed", 0L)
        variables.declare(elapsed)
        setContent(video(elapsedTimeVariable = "elapsed", videoSources = listOf(videoSource())))
        player.acknowledgeSeek = false

        rule.runOnIdle { elapsed.set(9_000L) }
        rule.waitForIdle()

        assertEquals(listOf(0L, 9_000L), player.seekRequests)
        assertEquals(9_000L, elapsed.getValue())
    }

    @Test
    fun `changing elapsed variable seeks to the new variable`() {
        variables.declare(Variable.IntegerVariable("elapsed", 1_000L))
        variables.declare(Variable.IntegerVariable("other", 6_000L))
        setVideoView()

        rule.runOnIdle { content.value = video(elapsedTimeVariable = "other", videoSources = listOf(videoSource())) }
        rule.waitForIdle()

        assertEquals(listOf(1_000L, 6_000L), player.seekRequests)
    }

    @Test
    fun `position clamped by the player updates the newly bound variable`() {
        variables.declare(Variable.IntegerVariable("elapsed", 1_000L))
        val other = Variable.IntegerVariable("other", 9_000L)
        variables.declare(other)
        setVideoView()

        rule.runOnIdle {
            player.durationMs = 5_000L
            content.value = video(elapsedTimeVariable = "other", videoSources = listOf(videoSource()))
        }
        rule.waitForIdle()

        assertEquals(5_000L, other.getValue())
        assertEquals(listOf(1_000L, 9_000L), player.seekRequests)
    }

    @Test
    fun `progress writes only the newly bound elapsed variable`() {
        val elapsed = Variable.IntegerVariable("elapsed", 1_000L)
        val other = Variable.IntegerVariable("other", 6_000L)
        variables.declare(elapsed)
        variables.declare(other)
        setVideoView()
        rule.runOnIdle { content.value = video(elapsedTimeVariable = "other", videoSources = listOf(videoSource())) }
        rule.waitForIdle()

        rule.runOnIdle { player.currentTimeMsState.value = 9_000L }
        rule.waitForIdle()

        assertEquals(1_000L, elapsed.getValue())
        assertEquals(9_000L, other.getValue())
    }

    @Test
    fun `same variable name in a new local scope restores its own position`() {
        variables.declare(Variable.IntegerVariable("elapsed", 1_000L))
        setVideoView()
        val scopedComponent = viewContext.getLocalComponent(
            data = container(variables = listOf(variable("elapsed", 12_000L))).value(),
            parentComponent = viewContext.rootLocalComponent,
        )

        rule.runOnIdle { localComponent.value = scopedComponent }
        rule.waitForIdle()

        assertEquals(listOf(1_000L, 12_000L), player.seekRequests)
    }

    private fun setContent(div: Div) {
        rule.setContent {
            CompositionLocalProvider(LocalContext provides context) {
                if (visible.value) DivView(data = data(div))
            }
        }
        rule.waitForIdle()
    }

    /** Composes [DivVideoView] directly, so that tests can swap its model and local component in place. */
    private fun setVideoView() {
        rule.setContent {
            CompositionLocalProvider(
                LocalContext provides context,
                LocalDivViewContext provides viewContext,
                LocalComponent provides localComponent.value,
            ) {
                DivVideoView(Modifier, (content.value as Div.Video).value)
            }
        }
        rule.waitForIdle()
    }

    private fun reenterComposition() {
        rule.runOnIdle { visible.value = false }
        rule.waitForIdle()
        rule.runOnIdle { visible.value = true }
        rule.waitForIdle()
    }
}
