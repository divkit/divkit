package com.yandex.div.lottie

import android.app.Activity
import android.os.Looper
import android.provider.Settings
import android.view.View
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.yandex.div.core.Disposable
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.animations.DivAnimationsEnabledController
import com.yandex.div.core.widget.LoadableImageView
import com.yandex.div.internal.extensions.lottie.LottieData
import com.yandex.div.internal.extensions.lottie.LottieExtensionParams
import com.yandex.div.internal.extensions.lottie.LottieRepeat
import com.yandex.div.internal.extensions.lottie.LottieRepeatMode
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowChoreographer

@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class LottiePlaybackIntegrationTest {

    private val activityController = Robolectric.buildActivity(Activity::class.java).setup()
    private val activity = activityController.get()
    private val imageView = LoadableImageView(activity)
    private val viewController = LottieController(imageView, asyncUpdatesEnabled = false)
    private val divView = Div2View(Div2Context(
        activity,
        DivConfiguration.Builder(mock()).enableBindOnAttach(false).build(),
    ))
    private val path = DivStatePath.fromState(0)
    private val stateController = LottiePlaybackStateController()
    private val composition = requireNotNull(LottieCompositionFactory.fromJsonStringSync(COMPOSITION, null).value)
    private var animationsEnabled = true
    private var onAnimationsEnabledChanged: () -> Unit = {}
    private val animationsEnabledController = mock<DivAnimationsEnabledController> {
        on { isEnabled() } doAnswer { animationsEnabled }
        on { observe(any(), any()) } doAnswer { invocation ->
            onAnimationsEnabledChanged = invocation.getArgument(1)
            Disposable {}
        }
    }
    private var playbackController: LottiePlaybackController? = null

    private val drawable: LottieDrawable
        get() = imageView.imageTransformer?.transform(null) as LottieDrawable

    init {
        ShadowChoreographer.setPaused(true)
        ShadowChoreographer.setFrameDelay(Duration.ofMillis(16))
        Settings.Global.putFloat(activity.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
        imageView.delegate = viewController
        activity.setContentView(imageView)
        imageView.layout(0, 0, 240, 240)
        shadowOf(Looper.getMainLooper()).idleFor(16, TimeUnit.MILLISECONDS)
    }

    @After
    fun close() {
        playbackController?.close()
        viewController.cancelAnimation()
        activityController.pause().stop().destroy()
    }

    @Test
    fun `start rewinds a configured animation playing from the middle`() {
        bind(isPlaying = true)
        drawable.frame = 60

        start()

        assertEquals(0, drawable.frame)
        assertTrue(viewController.isAnimating())
    }

    @Test
    fun `repeated start rewinds an action animation playing from the middle`() {
        bind()
        start()
        drawable.frame = 60

        start()

        assertEquals(0, drawable.frame)
        assertTrue(viewController.isAnimating())
    }

    @Test
    fun `stop rewinds a playing animation and leaves it stopped`() {
        bind()
        start()
        drawable.frame = 60

        stop()

        assertEquals(0, drawable.frame)
        assertFalse(viewController.isAnimating())
    }

    @Test
    fun `start after stop begins a new playback from the first frame`() {
        bind()
        start()
        drawable.frame = 60
        stop()

        start()

        assertEquals(0, drawable.frame)
        assertTrue(viewController.isAnimating())
    }

    @Test
    fun `repeated stop keeps the first frame stopped`() {
        bind()
        start()
        drawable.frame = 60
        stop()

        stop()

        assertEquals(0, drawable.frame)
        assertFalse(viewController.isAnimating())
    }

    @Test
    fun `start during a reverse repeat restores forward playback from the first frame`() {
        // Arrange
        bind(repeatMode = LottieRepeatMode.REVERSE, repeatCount = LottieDrawable.INFINITE)
        start()
        advanceAnimationBy(Duration.ofSeconds(5))
        assertTrue(drawable.speed < 0f, playbackDescription())

        // Act
        start()

        // Assert
        assertEquals(0, drawable.frame)
        assertTrue(drawable.speed > 0f)
        assertTrue(viewController.isAnimating())
    }

    @Test
    fun `stop during a reverse repeat restores the first frame`() {
        // Arrange
        bind(repeatMode = LottieRepeatMode.REVERSE, repeatCount = LottieDrawable.INFINITE)
        start()
        advanceAnimationBy(Duration.ofSeconds(5))
        assertTrue(drawable.speed < 0f, playbackDescription())

        // Act
        stop()

        // Assert
        assertEquals(0, drawable.frame)
        assertFalse(viewController.isAnimating())
    }

    @Test
    fun `start from a later repeat restores the first configured range`() {
        // Arrange
        bind(repeats = REPEATS)
        start()
        advanceAnimationBy(Duration.ofSeconds(1))
        assertEquals(60f, drawable.minFrame, playbackDescription())

        // Act
        start()

        // Assert
        assertEquals(10, drawable.frame)
        assertEquals(10f, drawable.minFrame)
        assertTrue(viewController.isAnimating())
    }

    @Test
    fun `stop from a later repeat restores the first configured range`() {
        // Arrange
        bind(repeats = REPEATS)
        start()
        advanceAnimationBy(Duration.ofSeconds(1))
        assertEquals(60f, drawable.minFrame, playbackDescription())

        // Act
        stop()

        // Assert
        assertEquals(10, drawable.frame)
        assertEquals(10f, drawable.minFrame)
        assertFalse(viewController.isAnimating())
    }

    @Test
    fun `stop before composition shows the first frame without autoplay`() {
        val controller = bind(isPlaying = true, compositionReady = false)
        stateController.stop(path)

        controller.onCompositionReady(composition)
        shadowOf(Looper.getMainLooper()).idle()

        assertEquals(0, drawable.frame)
        assertFalse(viewController.isAnimating())
    }

    @Test
    fun `start before composition plays from the first frame after loading`() {
        val controller = bind(compositionReady = false)
        stateController.start(path)

        controller.onCompositionReady(composition)
        shadowOf(Looper.getMainLooper()).idle()

        assertEquals(0, drawable.frame)
        assertTrue(viewController.isAnimating())
    }

    @Test
    fun `stopped playback stays on the first frame after rebind`() {
        // Arrange
        val controller = bind(isPlaying = true)
        stop()
        controller.onUnbind()

        // Act
        controller.onBind(stateController, path)
        controller.onCompositionReady(composition)
        shadowOf(Looper.getMainLooper()).idle()

        // Assert
        assertEquals(0, drawable.frame)
        assertFalse(viewController.isAnimating())
    }

    @Test
    fun `stop clears a start deferred until the view becomes visible`() {
        // Arrange
        bind()
        imageView.visibility = View.GONE
        start()

        // Act
        stop()
        imageView.visibility = View.VISIBLE

        // Assert
        assertEquals(0, drawable.frame)
        assertFalse(viewController.isAnimating())
    }

    @Test
    fun `stop clears playback deferred until the view is attached again`() {
        // Arrange
        bind()
        start()
        activity.setContentView(View(activity))

        // Act
        stop()
        activity.setContentView(imageView)

        // Assert
        assertEquals(0, drawable.frame)
        assertFalse(viewController.isAnimating())
    }

    @Test
    fun `start while animations are disabled rewinds a reverse animation without playing`() {
        bind(repeatMode = LottieRepeatMode.REVERSE)
        drawable.frame = 60
        animationsEnabled = false

        start()

        assertEquals(0, drawable.frame)
        assertFalse(viewController.isAnimating())
    }

    @Test
    fun `enabling animations plays a deferred start from the first frame`() {
        bind()
        animationsEnabled = false
        start()

        animationsEnabled = true
        onAnimationsEnabledChanged()

        assertEquals(0, drawable.frame)
        assertTrue(viewController.isAnimating())
    }

    @Test
    fun `natural completion preserves the final frame`() {
        bind()
        start()

        advanceAnimationBy(Duration.ofSeconds(5))

        assertEquals(composition.endFrame.toInt(), drawable.frame, playbackDescription())
        assertFalse(viewController.isAnimating())
    }

    @Test
    fun `reverse mode without repeats preserves its forward completion frame`() {
        bind(repeatMode = LottieRepeatMode.REVERSE)
        start()

        advanceAnimationBy(Duration.ofSeconds(5))

        assertEquals(composition.endFrame.toInt(), drawable.frame)
        assertFalse(viewController.isAnimating())
    }

    @Test
    fun `reverse mode with one repeat preserves its backward completion frame`() {
        bind(repeatMode = LottieRepeatMode.REVERSE, repeatCount = 1)
        start()

        advanceAnimationBy(Duration.ofSeconds(9))

        assertEquals(0, drawable.frame)
        assertFalse(viewController.isAnimating())
    }

    @Test
    fun `stop after natural completion rewinds the final frame`() {
        bind()
        start()
        advanceAnimationBy(Duration.ofSeconds(5))

        stop()

        assertEquals(0, drawable.frame)
        assertFalse(viewController.isAnimating())
    }

    private fun bind(
        isPlaying: Boolean = false,
        repeatMode: LottieRepeatMode = LottieRepeatMode.RESTART,
        repeatCount: Int = 0,
        repeats: List<LottieRepeat> = emptyList(),
        compositionReady: Boolean = true,
    ): LottiePlaybackController {
        val params = LottieExtensionParams(
            data = LottieData.Json(COMPOSITION),
            isPlaying = Expression.constant(isPlaying),
            repeatCount = repeatCount,
            repeatMode = repeatMode,
            repeats = repeats,
            safeMode = false,
        )
        val controller = LottiePlaybackController(
            viewController,
            divView,
            params,
            ExpressionResolver.EMPTY,
            animationsEnabledController,
        )
        playbackController = controller
        controller.onBind(stateController, path)
        if (compositionReady) {
            controller.onCompositionReady(composition)
        }
        shadowOf(Looper.getMainLooper()).idle()
        return controller
    }

    private fun start() {
        stateController.start(path)
        shadowOf(Looper.getMainLooper()).idle()
    }

    private fun advanceAnimationBy(duration: Duration) {
        repeat((duration.toMillis() / 16).toInt()) {
            shadowOf(Looper.getMainLooper()).idleFor(16, TimeUnit.MILLISECONDS)
        }
    }

    private fun playbackDescription(): String =
        "frame=${drawable.frame}, animating=${viewController.isAnimating()}, shown=${imageView.isShown}, " +
            "visible=${drawable.isVisible}, state=${stateController.states.value[path]}"

    private fun stop() {
        stateController.stop(path)
        shadowOf(Looper.getMainLooper()).idle()
    }
}

private const val COMPOSITION = """
{
  "v": "5.5.7",
  "fr": 30,
  "ip": 0,
  "op": 120,
  "w": 240,
  "h": 240,
  "layers": []
}
"""

private val REPEATS = listOf(
    LottieRepeat(count = 0, mode = LottieRepeatMode.RESTART, minFrame = 10, maxFrame = 20),
    LottieRepeat(count = -1, mode = LottieRepeatMode.REVERSE, minFrame = 60, maxFrame = 99),
)
