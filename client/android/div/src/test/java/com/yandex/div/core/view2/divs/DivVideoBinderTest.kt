package com.yandex.div.core.view2.divs

import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.yandex.div.core.Disposable
import com.yandex.div.core.expression.variables.TwoWayIntegerVariableBinder
import com.yandex.div.core.player.DivPlayer
import com.yandex.div.core.player.DivPlayerFactory
import com.yandex.div.core.player.DivPlayerPlaybackConfig
import com.yandex.div.core.player.DivPlayerView
import com.yandex.div.core.player.DivVideoResolution
import com.yandex.div.core.player.DivVideoSource
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.divs.widgets.DivVideoView
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivVideo
import com.yandex.div2.DivVideoSource as Div2VideoSource
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

@RunWith(RobolectricTestRunner::class)
internal class DivVideoBinderTest : DivBinderTest() {

    @Test(timeout = 5_000)
    fun `video expressions are evaluated on binding thread`() {
        val preview = MutableTestExpression("preview")
        val players = PlayerMocks()

        val bindingThread = bindOffMain(
            DivVideo(videoSources = emptyList(), preview = preview),
            players,
        )

        assertEquals(listOf(bindingThread), preview.evaluationThreads)
    }

    @Test(timeout = 5_000)
    fun `player work waits for main looper`() {
        val players = PlayerMocks()

        bindOffMain(videoWithConstantSource(), players)

        verifyNoInteractions(players.factory)
    }

    @Test(timeout = 5_000)
    fun `player work runs on main thread`() {
        val callThreads = CopyOnWriteArrayList<Thread>()
        val players = PlayerMocks(
            onMakePlayerView = { callThreads += Thread.currentThread() },
            onMakePlayer = { callThreads += Thread.currentThread() },
            onAttach = { callThreads += Thread.currentThread() },
        )
        bindOffMain(videoWithConstantSource(), players)

        shadowOf(Looper.getMainLooper()).idle()

        assertEquals(List(3) { Looper.getMainLooper().thread }, callThreads)
    }

    @Test(timeout = 5_000)
    fun `each dynamic source field keeps its current value`() {
        val cases = dynamicSourceCases()

        val actual = cases.associate { case ->
            val players = PlayerMocks()
            bindOffMain(DivVideo(videoSources = listOf(case.source)), players)
            case.update()
            shadowOf(Looper.getMainLooper()).idle()
            case.name to players.source.get().single()
        }

        assertEquals(cases.associate { it.name to it.expected }, actual)
    }

    @Test(timeout = 5_000)
    fun `each dynamic config field keeps its current value`() {
        val cases = dynamicConfigCases()

        val actual = cases.associate { case ->
            val players = PlayerMocks()
            bindOffMain(case.video, players)
            case.update()
            shadowOf(Looper.getMainLooper()).idle()
            case.name to players.config.get()
        }

        assertEquals(cases.associate { it.name to it.expected }, actual)
    }

    private fun bindOffMain(div: DivVideo, players: PlayerMocks): Thread {
        val mainHandler = Handler(Looper.getMainLooper())
        val bindingDispatcher = divView.viewComponent.bindingDispatcher
        doAnswer { invocation ->
            mainHandler.post(invocation.getArgument<() -> Unit>(0))
            null
        }.whenever(bindingDispatcher).postMainThreadAction(any())

        val binder = DivVideoBinder(
            baseBinder = baseBinder,
            variableBinder = mock<TwoWayIntegerVariableBinder>(),
            actionPerformer = actionPerformer,
            executorService = mock<ExecutorService>(),
            playerFactory = players.factory,
        )
        val divBlock = DivBlock.Video(Div.Video(div), resolver, DivStatePath(0))
        val view = DivVideoView(context)
        val failure = AtomicReference<Throwable>()
        val finished = CountDownLatch(1)
        val bindingThread = Thread {
            try {
                binder.loadVideo(view, divBlock, divView)
            } catch (error: Throwable) {
                failure.set(error)
            } finally {
                finished.countDown()
            }
        }
        bindingThread.start()

        if (!finished.await(2, TimeUnit.SECONDS)) {
            throw AssertionError("Video binding did not finish")
        }
        failure.get()?.let { throw AssertionError("Video binding failed", it) }
        return bindingThread
    }

    private fun videoWithConstantSource() = DivVideo(
        videoSources = listOf(Div2VideoSource(
            mimeType = Expression.constant(DEFAULT_MIME_TYPE),
            url = Expression.constant(DEFAULT_URL),
        )),
    )

    private fun dynamicSourceCases(): List<DynamicSourceCase> {
        val url = MutableTestExpression(DEFAULT_URL)
        val mimeType = MutableTestExpression(DEFAULT_MIME_TYPE)
        val bitrate = MutableTestExpression(DEFAULT_BITRATE)
        val width = MutableTestExpression(DEFAULT_WIDTH)
        val height = MutableTestExpression(DEFAULT_HEIGHT)

        return listOf(
            DynamicSourceCase(
                name = "url",
                source = source(url = url),
                update = { url.value = CURRENT_URL },
                expected = DivVideoSource(url = CURRENT_URL, mimeType = DEFAULT_MIME_TYPE),
            ),
            DynamicSourceCase(
                name = "mimeType",
                source = source(mimeType = mimeType),
                update = { mimeType.value = CURRENT_MIME_TYPE },
                expected = DivVideoSource(url = DEFAULT_URL, mimeType = CURRENT_MIME_TYPE),
            ),
            DynamicSourceCase(
                name = "bitrate",
                source = source(bitrate = bitrate),
                update = { bitrate.value = CURRENT_BITRATE },
                expected = DivVideoSource(
                    url = DEFAULT_URL,
                    mimeType = DEFAULT_MIME_TYPE,
                    bitrate = CURRENT_BITRATE,
                ),
            ),
            DynamicSourceCase(
                name = "resolution width",
                source = source(resolution = Div2VideoSource.Resolution(
                    height = Expression.constant(DEFAULT_HEIGHT),
                    width = width,
                )),
                update = { width.value = CURRENT_WIDTH },
                expected = DivVideoSource(
                    url = DEFAULT_URL,
                    mimeType = DEFAULT_MIME_TYPE,
                    resolution = DivVideoResolution(CURRENT_WIDTH.toInt(), DEFAULT_HEIGHT.toInt()),
                ),
            ),
            DynamicSourceCase(
                name = "resolution height",
                source = source(resolution = Div2VideoSource.Resolution(
                    height = height,
                    width = Expression.constant(DEFAULT_WIDTH),
                )),
                update = { height.value = CURRENT_HEIGHT },
                expected = DivVideoSource(
                    url = DEFAULT_URL,
                    mimeType = DEFAULT_MIME_TYPE,
                    resolution = DivVideoResolution(DEFAULT_WIDTH.toInt(), CURRENT_HEIGHT.toInt()),
                ),
            ),
        )
    }

    private fun dynamicConfigCases(): List<DynamicConfigCase> {
        val autostart = MutableTestExpression(false)
        val muted = MutableTestExpression(false)
        val repeatable = MutableTestExpression(false)
        val payload = MutableTestExpression(JSONObject().put("version", "old"))
        val currentPayload = JSONObject().put("version", "current")
        val playbackSpeed = MutableTestExpression(1.0)

        return listOf(
            DynamicConfigCase(
                name = "autostart",
                video = videoWithConstantSource().copy(autostart = autostart),
                update = { autostart.value = true },
                expected = DivPlayerPlaybackConfig(autoplay = true),
            ),
            DynamicConfigCase(
                name = "muted",
                video = videoWithConstantSource().copy(muted = muted),
                update = { muted.value = true },
                expected = DivPlayerPlaybackConfig(isMuted = true),
            ),
            DynamicConfigCase(
                name = "repeatable",
                video = videoWithConstantSource().copy(repeatable = repeatable),
                update = { repeatable.value = true },
                expected = DivPlayerPlaybackConfig(repeatable = true),
            ),
            DynamicConfigCase(
                name = "player settings payload",
                video = videoWithConstantSource().copy(playerSettingsPayload = payload),
                update = { payload.value = currentPayload },
                expected = DivPlayerPlaybackConfig(payload = currentPayload),
            ),
            DynamicConfigCase(
                name = "playback speed",
                video = videoWithConstantSource().copy(playbackSpeed = playbackSpeed),
                update = { playbackSpeed.value = CURRENT_PLAYBACK_SPEED },
                expected = DivPlayerPlaybackConfig(playbackSpeed = CURRENT_PLAYBACK_SPEED.toFloat()),
            ),
        )
    }

    private fun source(
        bitrate: Expression<Long>? = null,
        mimeType: Expression<String> = Expression.constant(DEFAULT_MIME_TYPE),
        resolution: Div2VideoSource.Resolution? = null,
        url: Expression<Uri> = Expression.constant(DEFAULT_URL),
    ) = Div2VideoSource(
        bitrate = bitrate,
        mimeType = mimeType,
        resolution = resolution,
        url = url,
    )

    private data class DynamicSourceCase(
        val name: String,
        val source: Div2VideoSource,
        val update: () -> Unit,
        val expected: DivVideoSource,
    )

    private data class DynamicConfigCase(
        val name: String,
        val video: DivVideo,
        val update: () -> Unit,
        val expected: DivPlayerPlaybackConfig,
    )

    private class PlayerMocks(
        onMakePlayerView: () -> Unit = {},
        onMakePlayer: () -> Unit = {},
        onAttach: () -> Unit = {},
    ) {
        val view = mock<DivPlayerView>()
        val player = mock<DivPlayer>()
        val factory = mock<DivPlayerFactory>()
        val source = AtomicReference<List<DivVideoSource>>()
        val config = AtomicReference<DivPlayerPlaybackConfig>()

        init {
            whenever(factory.makePlayerView(any())).thenAnswer {
                onMakePlayerView()
                view
            }
            whenever(factory.makePlayer(any(), any())).thenAnswer { invocation ->
                onMakePlayer()
                source.set(invocation.getArgument(0))
                config.set(invocation.getArgument(1))
                player
            }
            doAnswer {
                onAttach()
                Unit
            }.whenever(view).attach(player)
        }
    }

    private class MutableTestExpression<T : Any>(
        @Volatile var value: T,
    ) : Expression<T>() {

        val evaluationThreads = CopyOnWriteArrayList<Thread>()

        override val rawValue: Any = Any()

        override fun evaluate(resolver: com.yandex.div.json.expressions.ExpressionResolver): T {
            evaluationThreads += Thread.currentThread()
            return value
        }

        override fun observe(
            resolver: com.yandex.div.json.expressions.ExpressionResolver,
            callback: (T) -> Unit,
        ): Disposable = Disposable.NULL
    }

    private companion object {
        val DEFAULT_URL: Uri = Uri.parse("https://example.com/old.mp4")
        val CURRENT_URL: Uri = Uri.parse("https://example.com/current.mp4")
        const val DEFAULT_MIME_TYPE = "video/mp4"
        const val CURRENT_MIME_TYPE = "video/webm"
        const val DEFAULT_BITRATE = 1_000L
        const val CURRENT_BITRATE = 2_000L
        const val DEFAULT_WIDTH = 320L
        const val CURRENT_WIDTH = 640L
        const val DEFAULT_HEIGHT = 180L
        const val CURRENT_HEIGHT = 360L
        const val CURRENT_PLAYBACK_SPEED = 1.5
    }
}
