package com.yandex.div.lottie

import android.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.airbnb.lottie.LottieComposition
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.widget.LoadableImageView
import com.yandex.div.internal.extensions.lottie.LottieData
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivExtension
import com.yandex.div2.DivGifImage
import kotlin.test.assertIs
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame
import org.json.JSONObject
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivLottieExtensionHandlerTest {

    private val handler = DivLottieExtensionHandler(asyncUpdatesEnabled = false)
    private val resolver = ExpressionResolver.EMPTY
    private val path = DivStatePath.fromState(0)
    private val context = ContextThemeWrapper(ApplicationProvider.getApplicationContext(), 0)
    private val divView = Div2View(Div2Context(
        context,
        DivConfiguration.Builder(mock()).enableBindOnAttach(false).build(),
    ))
    private val viewController = mock<LottieController>()
    private val view = LoadableImageView(context).apply {
        delegate = viewController
    }
    private val div = createDiv(width = 1)

    @After
    fun unbindView() {
        handler.unbindView(divView, resolver, view, div)
    }

    @Test
    fun `bind stores playback controller in view tag`() {
        handler.bindView(divView, resolver, view, div)

        assertIs<LottiePlaybackController>(view.getTag(R.id.lottie_playback_controller))
    }

    @Test
    fun `bind with unchanged data keeps tagged controller`() {
        handler.bindView(divView, resolver, view, div)
        val controller = playbackController()
        whenever(viewController.data).thenReturn(LottieData.Json(lottieJson(width = 1).toString()))

        handler.bindView(divView, resolver, view, div)

        assertSame(controller, playbackController())
    }

    @Test
    fun `changed data replaces tagged controller`() {
        handler.bindView(divView, resolver, view, div)
        val previousController = playbackController()
        val changedDiv = createDiv(width = 2)

        handler.bindView(divView, resolver, view, changedDiv)

        assertNotSame(previousController, playbackController())
    }

    @Test
    fun `changed data closes previous playback controller`() {
        handler.bindView(divView, resolver, view, div)
        val previousController = playbackController()
        handler.actionHandler.onViewBind(divView, path, view)
        val changedDiv = createDiv(width = 2)

        handler.bindView(divView, resolver, view, changedDiv)

        previousController.onCompositionReady(mock())
        verify(viewController, never()).setComposition(any())
    }

    @Test
    fun `unbind clears playback controller tag`() {
        handler.bindView(divView, resolver, view, div)

        handler.unbindView(divView, resolver, view, div)

        assertNull(view.getTag(R.id.lottie_playback_controller))
    }

    @Test
    fun `unbind closes tagged playback controller`() {
        handler.bindView(divView, resolver, view, div)
        val controller = playbackController()
        handler.actionHandler.onViewBind(divView, path, view)

        handler.unbindView(divView, resolver, view, div)

        controller.onCompositionReady(mock())
        verify(viewController, never()).setComposition(any())
    }

    @Test
    fun `bind after unbind creates working playback controller`() {
        // Arrange
        handler.bindView(divView, resolver, view, div)
        val previousController = playbackController()
        handler.unbindView(divView, resolver, view, div)
        val composition = mock<LottieComposition>()

        // Act
        handler.bindView(divView, resolver, view, div)
        handler.actionHandler.onViewBind(divView, path, view)
        val controller = playbackController()
        controller.onCompositionReady(composition)

        // Assert
        assertNotSame(previousController, controller)
        verify(viewController).setComposition(composition)
    }

    private fun playbackController(): LottiePlaybackController =
        assertIs<LottiePlaybackController>(view.getTag(R.id.lottie_playback_controller))

    private fun createDiv(width: Int): DivGifImage {
        val params = JSONObject().put("lottie_json", lottieJson(width))
        return DivGifImage(extensions = listOf(DivExtension(id = "lottie", params = params)))
    }

    private fun lottieJson(width: Int): JSONObject = JSONObject(MINIMAL_LOTTIE).put("w", width)
}

private const val MINIMAL_LOTTIE = """
{
  "v": "5.5.7",
  "fr": 30,
  "ip": 0,
  "op": 1,
  "w": 1,
  "h": 1,
  "layers": []
}
"""
