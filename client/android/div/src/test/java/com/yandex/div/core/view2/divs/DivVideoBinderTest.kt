package com.yandex.div.core.view2.divs

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yandex.div.core.Disposable
import com.yandex.div.core.expression.variables.TwoWayIntegerVariableBinder
import com.yandex.div.core.player.DivPlayer
import com.yandex.div.core.player.DivPlayerFactory
import com.yandex.div.core.player.DivPlayerPlaybackConfig
import com.yandex.div.core.player.DivPlayerView
import com.yandex.div.core.player.DivVideoPlaybackState
import com.yandex.div.core.player.DivVideoResolution
import com.yandex.div.core.player.DivVideoSource
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.DivVideoViewState
import com.yandex.div.core.view2.DivViewStateStoreImpl
import com.yandex.div.core.view2.divs.widgets.DivVideoView
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivVideo
import com.yandex.div2.DivVideoSource as Div2VideoSource
import com.yandex.div2.DivVideoScale
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

@RunWith(RobolectricTestRunner::class)
internal class DivVideoBinderTest : DivBinderTest() {

    private val playerFactory = mock<DivPlayerFactory>()
    private val executorService = mock<ExecutorService>()
    private val binder = createBinder(deferredVideoPlayerCreationEnabled = true)

    private fun createBinder(deferredVideoPlayerCreationEnabled: Boolean) = DivVideoBinder(
        baseBinder = baseBinder,
        variableBinder = mock<TwoWayIntegerVariableBinder>(),
        actionPerformer = actionPerformer,
        executorService = executorService,
        playerFactory = playerFactory,
        deferredVideoPlayerCreationEnabled = deferredVideoPlayerCreationEnabled,
    )

    @Test
    fun `video expressions are evaluated on binding thread`() {
        val preview = MutableTestExpression("preview")
        val players = PlayerMocks()

        val bindingThread = bindOffMain(
            DivVideo(videoSources = emptyList(), preview = preview),
            players,
        )

        assertEquals(listOf(bindingThread), preview.evaluationThreads)
    }

    @Test
    fun `player work waits for main looper`() {
        val players = PlayerMocks()

        bindOffMain(videoWithConstantSource(), players)

        verifyNoInteractions(players.factory)
    }

    @Test
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

    @Test
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

    @Test
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
        val binder = DivVideoBinder(
            baseBinder = baseBinder,
            variableBinder = mock<TwoWayIntegerVariableBinder>(),
            actionPerformer = actionPerformer,
            executorService = mock<ExecutorService>(),
            playerFactory = players.factory,
            deferredVideoPlayerCreationEnabled = false,
        )
        val divBlock = DivBlock.Video(Div.Video(div), resolver, DivStatePath(0))
        val view = spy(DivVideoView(context))
        whenever(view.isAttachedToWindow).thenReturn(true)
        return runOffMain { binder.loadVideo(view, divBlock, divView) }
    }

    private fun runOffMain(action: () -> Unit): Thread {
        val mainHandler = Handler(Looper.getMainLooper())
        val bindingDispatcher = divView.viewComponent.bindingDispatcher
        doAnswer { invocation ->
            mainHandler.post(invocation.getArgument<() -> Unit>(0))
            null
        }.whenever(bindingDispatcher).postMainThreadAction(any())

        val failure = AtomicReference<Throwable>()
        val finished = CountDownLatch(1)
        val bindingThread = Thread {
            try {
                action()
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

    @Test
    fun `player creation waits until video view is attached`() {
        val playerView = TestPlayerView(context)
        val player = mock<DivPlayer>()
        whenever(playerFactory.makePlayerView(any())).thenReturn(playerView)
        whenever(playerFactory.makePlayer(any(), any())).thenReturn(player)
        val view = DivVideoView(context)

        binder.loadVideo(view, createBlock(DivVideo(videoSources = emptyList())), divView)

        verify(playerFactory, never()).makePlayer(any(), any())

        Robolectric.buildActivity(Activity::class.java).setup().get().setContentView(view)

        verify(playerFactory).makePlayer(any(), any())
        assertSame(player, playerView.getAttachedPlayer())
    }

    @Test
    fun `player stays hidden without preview until deferred creation finishes`() {
        val playerView = TestPlayerView(context)
        val player = mock<DivPlayer>()
        whenever(playerFactory.makePlayerView(any())).thenReturn(playerView)
        whenever(playerFactory.makePlayer(any(), any())).thenReturn(player)
        val view = DivVideoView(context)
        val recyclerView = TestRecyclerView(context, RecyclerView.SCROLL_STATE_SETTLING)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addView(view)
        Robolectric.buildActivity(Activity::class.java).setup().get().setContentView(recyclerView)

        binder.loadVideo(view, createBlock(DivVideo(videoSources = emptyList())), divView)

        assertEquals(View.INVISIBLE, playerView.visibility)
        verify(playerFactory, never()).makePlayer(any(), any())

        recyclerView.moveToIdle()

        assertSame(player, playerView.getAttachedPlayer())
        assertEquals(View.VISIBLE, playerView.visibility)
    }

    @Test
    fun `player creation does not wait for scroll idle when deferred creation is disabled`() {
        val playerView = TestPlayerView(context)
        val player = mock<DivPlayer>()
        whenever(playerFactory.makePlayerView(any())).thenReturn(playerView)
        whenever(playerFactory.makePlayer(any(), any())).thenReturn(player)
        val view = DivVideoView(context)
        val recyclerView = TestRecyclerView(context, RecyclerView.SCROLL_STATE_SETTLING)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addView(view)
        Robolectric.buildActivity(Activity::class.java).setup().get().setContentView(recyclerView)

        createBinder(deferredVideoPlayerCreationEnabled = false).loadVideo(
            view,
            createBlock(DivVideo(videoSources = emptyList())),
            divView,
        )

        verify(playerFactory).makePlayer(any(), any())
        assertSame(player, playerView.getAttachedPlayer())
    }

    @Test
    fun `disabled deferred creation preserves legacy rebind behavior`() {
        val playerView = TestPlayerView(context)
        val firstPlayer = mock<DivPlayer>()
        val secondPlayer = mock<DivPlayer>()
        whenever(playerFactory.makePlayerView(any())).thenReturn(playerView)
        whenever(playerFactory.makePlayer(any(), any())).thenReturn(firstPlayer, secondPlayer)
        val view = DivVideoView(context)
        val immediateBinder = createBinder(deferredVideoPlayerCreationEnabled = false)
        val div = DivVideo(
            videoSources = emptyList(),
            preview = Expression.constant(INVALID_PREVIEW),
        )

        immediateBinder.loadVideo(view, createBlock(div), divView)
        argumentCaptor<Runnable>().apply {
            verify(executorService).submit(capture())
            firstValue.run()
        }
        shadowOf(Looper.getMainLooper()).idle()

        immediateBinder.loadVideo(view, createBlock(div), divView)

        verify(firstPlayer, never()).release()
        verify(executorService, times(2)).submit(any<Runnable>())
        assertSame(secondPlayer, playerView.getAttachedPlayer())
        assertEquals(View.VISIBLE, playerView.visibility)
    }

    @Test
    fun `disabled deferred creation propagates player initialization failure`() {
        val playerView = TestPlayerView(context)
        val expected = IllegalStateException("player creation failed")
        whenever(playerFactory.makePlayerView(any())).thenReturn(playerView)
        whenever(playerFactory.makePlayer(any(), any())).thenThrow(expected)
        val view = DivVideoView(context)

        val failure = runCatching {
            createBinder(deferredVideoPlayerCreationEnabled = false).loadVideo(
                view,
                createBlock(DivVideo(videoSources = emptyList())),
                divView,
            )
        }.exceptionOrNull()

        assertSame(expected, failure)
    }

    @Test
    fun `deferred creation propagates player initialization failure`() {
        val playerView = TestPlayerView(context)
        val expected = IllegalStateException("player creation failed")
        whenever(playerFactory.makePlayerView(any())).thenReturn(playerView)
        whenever(playerFactory.makePlayer(any(), any())).thenThrow(expected)
        val view = DivVideoView(context)
        val recyclerView = TestRecyclerView(context, RecyclerView.SCROLL_STATE_SETTLING)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addView(view)
        Robolectric.buildActivity(Activity::class.java).setup().get().setContentView(recyclerView)
        binder.loadVideo(view, createBlock(DivVideo(videoSources = emptyList())), divView)

        val failure = runCatching { recyclerView.moveToIdle() }.exceptionOrNull()

        assertSame(expected, failure)
    }

    @Test
    fun `preview scale is applied before player creation`() {
        val playerView = TestPlayerView(context)
        whenever(playerFactory.makePlayerView(any())).thenReturn(playerView)
        val view = DivVideoView(context)
        val div = DivVideo(
            videoSources = emptyList(),
            scale = Expression.constant(DivVideoScale.NO_SCALE),
        )

        binder.loadVideo(view, createBlock(div), divView)

        val previewView = view.getChildAt(1) as ImageView
        assertEquals(ImageView.ScaleType.CENTER, previewView.scaleType)
        verify(playerFactory, never()).makePlayer(any(), any())
    }

    @Test
    fun `deferred preview uses latest dynamic scale on main thread`() {
        val scale = MutableTestExpression(DivVideoScale.FIT)
        val playerView = TestPlayerView(context)
        whenever(playerFactory.makePlayerView(any())).thenReturn(playerView)
        val view = DivVideoView(context)
        val div = DivVideo(
            videoSources = emptyList(),
            scale = scale,
        )

        val bindingThread = runOffMain { binder.loadVideo(view, createBlock(div), divView) }
        scale.value = DivVideoScale.NO_SCALE
        shadowOf(Looper.getMainLooper()).idle()

        val previewView = view.getChildAt(1) as ImageView
        assertEquals(ImageView.ScaleType.CENTER, previewView.scaleType)
        assertEquals(listOf(bindingThread, Looper.getMainLooper().thread), scale.evaluationThreads)
    }

    @Test
    fun `rebind keeps current preview visible while replacement is decoded`() {
        val playerView = TestPlayerView(context)
        val player = mock<DivPlayer>()
        whenever(playerFactory.makePlayerView(any())).thenReturn(playerView)
        whenever(playerFactory.makePlayer(any(), any())).thenReturn(player)
        val view = DivVideoView(context)
        Robolectric.buildActivity(Activity::class.java).setup().get().setContentView(view)

        binder.loadVideo(view, createBlock(DivVideo(videoSources = emptyList())), divView)
        val previewView = view.getChildAt(1)
        previewView.visibility = View.VISIBLE

        binder.loadVideo(
            view,
            createBlock(DivVideo(
                videoSources = emptyList(),
                preview = Expression.constant("pending preview"),
            )),
            divView,
        )

        assertEquals(View.VISIBLE, previewView.visibility)
        assertEquals(View.INVISIBLE, playerView.visibility)
    }

    @Test
    fun `rebind keeps pending decode for unchanged preview`() {
        val playerView = TestPlayerView(context)
        whenever(playerFactory.makePlayerView(any())).thenReturn(playerView)
        val view = DivVideoView(context)
        val div = DivVideo(
            videoSources = emptyList(),
            preview = Expression.constant("pending preview"),
            scale = Expression.constant(DivVideoScale.FIT),
        )

        binder.loadVideo(view, createBlock(div), divView)
        binder.loadVideo(view, createBlock(div), divView)

        argumentCaptor<Runnable>().apply {
            verify(executorService, times(1)).submit(capture())
        }
    }

    @Test
    fun `rebind keeps player visible after unchanged applied preview becomes ready`() {
        val playerView = TestPlayerView(context)
        val firstPlayer = mock<DivPlayer>()
        val secondPlayer = mock<DivPlayer>()
        whenever(playerFactory.makePlayerView(any())).thenReturn(playerView)
        whenever(playerFactory.makePlayer(any(), any())).thenReturn(firstPlayer, secondPlayer)
        val view = DivVideoView(context)
        val div = DivVideo(
            videoSources = emptyList(),
            preview = Expression.constant(VALID_PREVIEW),
            scale = Expression.constant(DivVideoScale.FIT),
        )
        Robolectric.buildActivity(Activity::class.java).setup().get().setContentView(view)

        binder.loadVideo(view, createBlock(div), divView)
        argumentCaptor<Runnable>().apply {
            verify(executorService).submit(capture())
            firstValue.run()
        }
        shadowOf(Looper.getMainLooper()).idle()
        val previewView = view.getChildAt(1) as ImageView
        view.releaseMedia()

        binder.loadVideo(view, createBlock(div), divView)

        verify(executorService, times(1)).submit(any<Runnable>())
        assertEquals(View.VISIBLE, previewView.visibility)
        assertEquals(View.VISIBLE, playerView.visibility)

        argumentCaptor<DivPlayer.Observer>().apply {
            verify(secondPlayer).addObserver(capture())
            firstValue.onReady()
        }
        assertEquals(View.INVISIBLE, previewView.visibility)
        assertEquals(View.VISIBLE, playerView.visibility)
    }

    @Test
    fun `failed preview decode is retried on rebind`() {
        val playerView = TestPlayerView(context)
        whenever(playerFactory.makePlayerView(any())).thenReturn(playerView)
        val view = DivVideoView(context)
        val div = DivVideo(
            videoSources = emptyList(),
            preview = Expression.constant(INVALID_PREVIEW),
            scale = Expression.constant(DivVideoScale.FIT),
        )

        binder.loadVideo(view, createBlock(div), divView)
        argumentCaptor<Runnable>().apply {
            verify(executorService).submit(capture())
            firstValue.run()
        }
        shadowOf(Looper.getMainLooper()).idle()

        binder.loadVideo(view, createBlock(div), divView)

        verify(executorService, times(2)).submit(any<Runnable>())
    }

    @Test
    fun `deferred player uses dynamic source and config from scroll idle`() {
        val sourceUrl = MutableTestExpression(Uri.parse("https://example.com/old.mp4"))
        val repeatable = MutableTestExpression(false)
        val playerView = TestPlayerView(context)
        val player = mock<DivPlayer>()
        val playerSource = AtomicReference<List<DivVideoSource>>()
        val playerConfig = AtomicReference<DivPlayerPlaybackConfig>()
        whenever(playerFactory.makePlayerView(any())).thenReturn(playerView)
        whenever(playerFactory.makePlayer(any(), any())).thenAnswer { invocation ->
            playerSource.set(invocation.getArgument(0))
            playerConfig.set(invocation.getArgument(1))
            player
        }
        val view = DivVideoView(context)
        val recyclerView = TestRecyclerView(context, RecyclerView.SCROLL_STATE_SETTLING)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addView(view)
        Robolectric.buildActivity(Activity::class.java).setup().get().setContentView(recyclerView)
        val div = DivVideo(
            videoSources = listOf(Div2VideoSource(
                mimeType = Expression.constant("video/mp4"),
                url = sourceUrl,
            )),
            repeatable = repeatable,
        )

        binder.loadVideo(view, createBlock(div), divView)
        verify(playerFactory, never()).makePlayer(any(), any())

        sourceUrl.value = Uri.parse("https://example.com/current.mp4")
        repeatable.value = true
        recyclerView.moveToIdle()

        assertEquals(sourceUrl.value, playerSource.get().single().url)
        assertTrue(playerConfig.get().repeatable)
    }

    @Test
    fun `deferred player uses latest playback state`() {
        val playerView = TestPlayerView(context)
        val player = mock<DivPlayer>()
        val playerConfig = AtomicReference<DivPlayerPlaybackConfig>()
        val viewStateStore = DivViewStateStoreImpl()
        whenever(divView.viewStateStore).thenReturn(viewStateStore)
        whenever(playerFactory.makePlayerView(any())).thenReturn(playerView)
        whenever(playerFactory.makePlayer(any(), any())).thenAnswer { invocation ->
            playerConfig.set(invocation.getArgument(1))
            player
        }
        val view = DivVideoView(context)
        val recyclerView = TestRecyclerView(context, RecyclerView.SCROLL_STATE_SETTLING)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addView(view)
        Robolectric.buildActivity(Activity::class.java).setup().get().setContentView(recyclerView)
        val divBlock = createBlock(DivVideo(
            videoSources = emptyList(),
            autostart = Expression.constant(true),
        ))

        binder.loadVideo(view, divBlock, divView)
        viewStateStore.put(
            divBlock.path.fullPath,
            DivVideoViewState(DivVideoPlaybackState.PAUSED),
        )

        recyclerView.moveToIdle()

        assertFalse(playerConfig.get().autoplay)
    }

    private fun createBlock(div: DivVideo) = DivBlock.Video(Div.Video(div), resolver, DivStatePath(0))

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

    private class TestRecyclerView(
        context: Context,
        private var state: Int,
    ) : RecyclerView(context) {

        private var listener: OnScrollListener? = null

        override fun getScrollState(): Int = state

        override fun addOnScrollListener(listener: OnScrollListener) {
            this.listener = listener
        }

        override fun removeOnScrollListener(listener: OnScrollListener) {
            if (this.listener === listener) {
                this.listener = null
            }
        }

        fun moveToIdle() {
            state = SCROLL_STATE_IDLE
            listener?.onScrollStateChanged(this, state)
        }
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
        const val VALID_PREVIEW =
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII="
        const val INVALID_PREVIEW = "data:image/svg+xml;base64,bm90IHN2Zw=="
    }
}
