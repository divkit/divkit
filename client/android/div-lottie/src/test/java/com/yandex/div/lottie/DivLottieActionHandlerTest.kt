package com.yandex.div.lottie

import android.view.ContextThemeWrapper
import android.view.View
import androidx.test.core.app.ApplicationProvider
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.expression.evaluation.DictEvaluator
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivData
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivLottieActionHandlerTest {

    private val handler = DivLottieActionHandler()
    private val divView = createCardView("card")
    private val path = DivStatePath.fromState(0)
    private val resolver = mock<ExpressionResolver>()

    @Test
    fun `start is delivered when target view binds`() {
        val view = View(ApplicationProvider.getApplicationContext())
        val controller = mock<LottiePlaybackController>()
        view.setTag(R.id.lottie_playback_controller, controller)

        assertTrue(handle("start", path))
        handler.onViewBind(divView, path, view)

        val stateController = boundStateController(controller)
        assertEquals(PlaybackState.Started(1), stateController.states.value[path])
    }

    @Test
    fun `stop is delivered when target view binds`() {
        val view = View(ApplicationProvider.getApplicationContext())
        val controller = mock<LottiePlaybackController>()
        view.setTag(R.id.lottie_playback_controller, controller)

        assertTrue(handle("stop", path))
        handler.onViewBind(divView, path, view)

        val stateController = boundStateController(controller)
        assertEquals(PlaybackState.Stopped(0), stateController.states.value[path])
    }

    @Test
    fun `start updates playback after target view binds`() {
        val view = View(ApplicationProvider.getApplicationContext())
        val controller = mock<LottiePlaybackController>()
        view.setTag(R.id.lottie_playback_controller, controller)
        handler.onViewBind(divView, path, view)
        val stateController = boundStateController(controller)

        assertTrue(handle("start", path))

        assertEquals(PlaybackState.Started(1), stateController.states.value[path])
    }

    @Test
    fun `stop updates playback after target view binds`() {
        val view = View(ApplicationProvider.getApplicationContext())
        val controller = mock<LottiePlaybackController>()
        view.setTag(R.id.lottie_playback_controller, controller)
        handler.onViewBind(divView, path, view)
        val stateController = boundStateController(controller)

        assertTrue(handle("stop", path))

        assertEquals(PlaybackState.Stopped(0), stateController.states.value[path])
    }

    @Test
    fun `view unbind releases playback binding`() {
        val view = View(ApplicationProvider.getApplicationContext())
        val controller = mock<LottiePlaybackController>()
        view.setTag(R.id.lottie_playback_controller, controller)

        handler.onViewUnbind(divView, path, view)

        verify(controller).onUnbind()
    }

    @Test
    fun `playback on one path does not activate another path`() {
        val otherPath = path.appendDiv("other")
        val view = View(ApplicationProvider.getApplicationContext())
        val controller = mock<LottiePlaybackController>()
        view.setTag(R.id.lottie_playback_controller, controller)

        assertTrue(handle("start", path))
        handler.onViewBind(divView, otherPath, view)

        val stateController = boundStateController(controller, otherPath)
        assertEquals(null, stateController.states.value[otherPath])
    }

    @Test
    fun `views with the same card tag share playback state`() {
        val firstCardView = createCardView("shared-card")
        val secondCardView = createCardView("shared-card")
        val view = View(ApplicationProvider.getApplicationContext())
        val controller = mock<LottiePlaybackController>()
        view.setTag(R.id.lottie_playback_controller, controller)

        assertTrue(handle("start", path, firstCardView))
        handler.onViewBind(secondCardView, path, view)

        val stateController = boundStateController(controller)
        assertEquals(PlaybackState.Started(1), stateController.states.value[path])
    }

    @Test
    fun `different card tags keep independent playback state`() {
        val firstCardView = createCardView("first-card")
        val secondCardView = createCardView("second-card")
        val view = View(ApplicationProvider.getApplicationContext())
        val controller = mock<LottiePlaybackController>()
        view.setTag(R.id.lottie_playback_controller, controller)

        assertTrue(handle("start", path, firstCardView))
        handler.onViewBind(secondCardView, path, view)

        val stateController = boundStateController(controller)
        assertEquals(null, stateController.states.value[path])
    }

    @Test
    fun `new card tag does not inherit state on the same Div2View`() {
        val cardView = createCardView("first-card")
        assertTrue(handle("start", path, cardView))
        cardView.setData(card("second"), DivDataTag("second-card"))
        val view = View(ApplicationProvider.getApplicationContext())
        val controller = mock<LottiePlaybackController>()
        view.setTag(R.id.lottie_playback_controller, controller)

        handler.onViewBind(cardView, path, view)

        val stateController = boundStateController(controller)
        assertEquals(null, stateController.states.value[path])
    }

    @Test
    fun `missing payload is not a Lottie command`() {
        assertFalse(handle(null, path))
    }

    @Test
    fun `unsupported playback commands are rejected`() {
        assertFalse(handle("play", path))
        assertFalse(handle("pause", path))
    }

    private fun boundStateController(
        controller: LottiePlaybackController,
        targetPath: DivStatePath = path,
    ): LottiePlaybackStateController {
        val captor = argumentCaptor<LottiePlaybackStateController>()
        verify(controller).onBind(captor.capture(), eq(targetPath))
        return captor.firstValue
    }

    private fun handle(
        command: String?,
        targetPath: DivStatePath,
        targetView: Div2View = divView,
    ): Boolean {
        val payload = command?.let { commandType ->
            mock<DictEvaluator>().also {
                whenever(it.get()).thenReturn(Result.success(mapOf("type" to commandType)))
            }
        }
        return handler.handleAction(targetView, targetPath, payload, resolver)
    }

    private fun card(logId: String): DivData {
        return DivData(
            DivParsingEnvironment(ParsingErrorLogger.ASSERT),
            JSONObject(MINIMAL_CARD.replace("card-id", logId)),
        )
    }

    private fun createCardView(tag: String): Div2View {
        val configuration = DivConfiguration.Builder(mock())
            .enableBindOnAttach(false)
            .build()
        val context = ContextThemeWrapper(ApplicationProvider.getApplicationContext(), 0)
        return Div2View(Div2Context(context, configuration)).also { cardView ->
            cardView.setData(card(tag), DivDataTag(tag))
        }
    }
}

private const val MINIMAL_CARD = """
{
  "log_id": "card-id",
  "states": [
    {
      "state_id": 0,
      "div": {
        "type": "text",
        "id": "target",
        "text": "Target"
      }
    }
  ]
}
"""
