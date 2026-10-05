package com.yandex.div.lottie

import android.os.Looper
import android.view.ContextThemeWrapper
import androidx.test.core.app.ApplicationProvider
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieDrawable
import com.yandex.div.core.Disposable
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.animations.DivAnimationsEnabledController
import com.yandex.div.internal.extensions.lottie.LottieData
import com.yandex.div.internal.extensions.lottie.LottieExtensionParams
import com.yandex.div.internal.extensions.lottie.LottieRepeat
import com.yandex.div.internal.extensions.lottie.LottieRepeatMode
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import kotlin.test.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class LottiePlaybackControllerTest {

    private val divView = Div2View(Div2Context(
        ContextThemeWrapper(ApplicationProvider.getApplicationContext(), 0),
        DivConfiguration.Builder(mock()).enableBindOnAttach(false).build(),
    ))
    private val resolver = ExpressionResolver.EMPTY
    private val path = DivStatePath.fromState(0)
    private val viewController = mock<LottieController> {
        on { getRepeatMode() } doReturn LottieDrawable.RESTART
        on { getProgress() } doReturn 0f
    }
    private var animationsEnabled = true
    private val animationsSubscription = mock<Disposable>()
    private val animationsEnabledController = mock<DivAnimationsEnabledController> {
        on { isEnabled() } doAnswer { animationsEnabled }
        on { observe(any(), any()) } doReturn animationsSubscription
    }

    @Test
    fun `start before composition is applied when composition becomes ready`() {
        val stateController = LottiePlaybackStateController()
        val controller = createController(isPlaying = false)
        controller.onBind(stateController, path)

        stateController.start(path)
        verify(viewController, never()).playAnimation()
        controller.onCompositionReady(mock())

        verify(viewController).playAnimation()
    }

    @Test
    fun `stop moves action playback to the first frame`() {
        // Arrange
        val stateController = LottiePlaybackStateController()
        val controller = createController(isPlaying = true)
        controller.onBind(stateController, path)
        controller.onCompositionReady(mock())
        stateController.start(path)
        shadowOf(Looper.getMainLooper()).idle()
        clearInvocations(viewController)

        // Act
        stateController.stop(path)
        shadowOf(Looper.getMainLooper()).idle()

        // Assert
        verify(viewController).pauseAnimationAt(0f)
    }

    @Test
    fun `stop before composition prevents configured playback`() {
        val stateController = LottiePlaybackStateController()
        val controller = createController(isPlaying = true)
        controller.onBind(stateController, path)
        stateController.stop(path)

        controller.onCompositionReady(mock())

        verify(viewController).pauseAnimationAt(0f)
        verify(viewController, never()).playAnimation()
    }

    @Test
    fun `stopped playback stays stopped after rebind`() {
        // Arrange
        val stateController = LottiePlaybackStateController()
        val controller = createController(isPlaying = true)
        controller.onBind(stateController, path)
        stateController.stop(path)
        controller.onUnbind()

        // Act
        controller.onBind(stateController, path)
        controller.onCompositionReady(mock())

        // Assert
        verify(viewController).pauseAnimationAt(0f)
        verify(viewController, never()).playAnimation()
    }

    @Test
    fun `animation completion stops matching action sequence`() {
        val stateController = LottiePlaybackStateController()
        val controller = createController(isPlaying = false)
        controller.onBind(stateController, path)
        controller.onCompositionReady(mock())
        stateController.start(path)
        shadowOf(Looper.getMainLooper()).idle()

        lastEndListener().invoke()

        assertEquals(PlaybackState.Completed(1), stateController.states.value[path])
    }

    @Test
    fun `animation completion preserves start awaiting main queue`() {
        // Arrange
        val stateController = LottiePlaybackStateController()
        val controller = createController(isPlaying = false)
        controller.onBind(stateController, path)
        controller.onCompositionReady(mock())
        stateController.start(path)
        shadowOf(Looper.getMainLooper()).idle()
        stateController.start(path)

        // Act
        lastEndListener().invoke()

        // Assert
        assertEquals(PlaybackState.Started(2), stateController.states.value[path])
        shadowOf(Looper.getMainLooper()).idle()
        verify(viewController, times(2)).playAnimation()
    }

    @Test
    fun `configured animation completion is recorded without action sequence`() {
        val stateController = LottiePlaybackStateController()
        val controller = createController(isPlaying = true)
        controller.onBind(stateController, path)
        controller.onCompositionReady(mock())

        lastEndListener().invoke()

        assertEquals(PlaybackState.Completed(0), stateController.states.value[path])
    }

    @Test
    fun `repeated start restores first repeat`() {
        val repeats = listOf(
            LottieRepeat(count = 0, mode = LottieRepeatMode.RESTART, minFrame = 0, maxFrame = 9),
            LottieRepeat(count = 1, mode = LottieRepeatMode.REVERSE, minFrame = 60, maxFrame = 99),
        )
        val composition = mock<LottieComposition> {
            on { startFrame } doReturn 0f
            on { endFrame } doReturn 100f
        }
        val stateController = LottiePlaybackStateController()
        val controller = createController(isPlaying = false, repeats = repeats)
        controller.onBind(stateController, path)
        controller.onCompositionReady(composition)
        stateController.start(path)
        shadowOf(Looper.getMainLooper()).idle()
        lastEndListener().invoke()
        assertEquals(60, lastMinFrame())

        stateController.start(path)
        shadowOf(Looper.getMainLooper()).idle()

        assertEquals(0, lastMinFrame())
    }

    @Test
    fun `global animation setting pauses and resumes action playback`() {
        val observer = argumentCaptor<() -> Unit>()
        val stateController = LottiePlaybackStateController()
        val controller = createController(isPlaying = false)
        controller.onBind(stateController, path)
        controller.onCompositionReady(mock())
        stateController.start(path)
        shadowOf(Looper.getMainLooper()).idle()
        verify(animationsEnabledController).observe(eq(divView), observer.capture())
        whenever(viewController.isAnimating()).thenReturn(true, false)

        animationsEnabled = false
        observer.firstValue.invoke()
        animationsEnabled = true
        observer.firstValue.invoke()

        verify(viewController, atLeastOnce()).pauseAnimation()
        verify(viewController).resumeAnimation()
    }

    @Test
    fun `unbind closes playback binding once and ignores later changes`() {
        val stateController = LottiePlaybackStateController()
        val controller = createController(isPlaying = false)
        controller.onBind(stateController, path)
        controller.onCompositionReady(mock())

        controller.onUnbind()
        controller.onUnbind()
        stateController.start(path)
        shadowOf(Looper.getMainLooper()).idle()

        verify(viewController, never()).playAnimation()
        verify(animationsSubscription).close()
    }

    private fun createController(
        isPlaying: Boolean,
        repeats: List<LottieRepeat> = emptyList(),
    ): LottiePlaybackController {
        val params = LottieExtensionParams(
            data = LottieData.Json("{}"),
            isPlaying = Expression.constant(isPlaying),
            repeatCount = 0,
            repeatMode = LottieRepeatMode.RESTART,
            repeats = repeats,
            safeMode = false,
        )
        return LottiePlaybackController(
            viewController,
            divView,
            params,
            resolver,
            animationsEnabledController,
        )
    }

    private fun lastEndListener(): () -> Unit {
        val captor = argumentCaptor<() -> Unit>()
        verify(viewController, atLeastOnce()).addEndListener(captor.capture())
        return captor.lastValue
    }

    private fun lastMinFrame(): Int {
        val captor = argumentCaptor<Int>()
        verify(viewController, atLeastOnce()).setFrameRange(captor.capture(), any())
        return captor.lastValue
    }
}
