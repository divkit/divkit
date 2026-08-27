package com.yandex.div.core.view2.divs

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.Disposable
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
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf
import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

@RunWith(AndroidJUnit4::class)
internal class DivVideoBinderTest : DivBinderTest() {

    private val playerWorkThreads = CopyOnWriteArrayList<Thread>()
    private val playerView = TestPlayerView(context) { playerWorkThreads += Thread.currentThread() }
    private val player = mock<DivPlayer>()
    private val videoView = DivVideoView(context)
    private val createdSource = argumentCaptor<List<DivVideoSource>>()
    private val createdConfig = argumentCaptor<DivPlayerPlaybackConfig>()
    private val playerFactory = mock<DivPlayerFactory> {
        on { makePlayerView(any()) } doAnswer {
            playerWorkThreads += Thread.currentThread()
            playerView
        }
        on { makePlayer(createdSource.capture(), createdConfig.capture()) } doAnswer {
            playerWorkThreads += Thread.currentThread()
            player
        }
    }
    private val executorService = QueuedExecutorService()
    private val underTest = createBinder(deferredVideoPlayerCreationEnabled = true)
    private val immediateUnderTest = createBinder(deferredVideoPlayerCreationEnabled = false)

    @BeforeTest
    fun setUpBindingDispatcher() {
        val mainHandler = Handler(Looper.getMainLooper())
        divView.viewComponent.bindingDispatcher.apply {
            whenever { postMainThreadAction(any()) } doAnswer { invocation ->
                mainHandler.post(invocation.getArgument<() -> Unit>(0))
                null
            }
        }
    }

    private fun createBinder(
        deferredVideoPlayerCreationEnabled: Boolean,
        playerFactory: DivPlayerFactory = this.playerFactory,
    ) = DivVideoBinder(
        baseBinder = baseBinder,
        variableBinder = mock(),
        actionPerformer = actionPerformer,
        executorService = executorService,
        playerFactory = playerFactory,
        deferredVideoPlayerCreationEnabled = deferredVideoPlayerCreationEnabled,
    )

    @Test
    fun `video expressions are evaluated on binding thread`() {
        val preview = MutableTestExpression("preview")

        val bindingThread = bindOffMain(DivVideo(videoSources = emptyList(), preview = preview))

        assertEquals(listOf(bindingThread), preview.evaluationThreads)
    }

    @Test
    fun `player work waits for main looper`() {
        bindOffMain(videoWithConstantSource())

        verifyNoInteractions(playerFactory)
    }

    @Test
    fun `player work runs on main thread`() {
        bindOffMain(videoWithConstantSource())

        shadowOf(Looper.getMainLooper()).idle()

        assertEquals(List(3) { Looper.getMainLooper().thread }, playerWorkThreads)
    }

    @Test
    fun `each dynamic source field keeps its current value`() {
        val url = MutableTestExpression(DEFAULT_URL)
        val mimeType = MutableTestExpression(DEFAULT_MIME_TYPE)
        val bitrate = MutableTestExpression(DEFAULT_BITRATE)
        val width = MutableTestExpression(DEFAULT_WIDTH)
        val height = MutableTestExpression(DEFAULT_HEIGHT)
        val cases = listOf(
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

        val actual = cases.associate { case ->
            bindOffMain(DivVideo(videoSources = listOf(case.source)))
            case.update()
            shadowOf(Looper.getMainLooper()).idle()
            case.name to createdSource.lastValue.single()
        }

        assertEquals(cases.associate { it.name to it.expected }, actual)
    }

    @Test
    fun `each dynamic config field keeps its current value`() {
        val autostart = MutableTestExpression(false)
        val muted = MutableTestExpression(false)
        val repeatable = MutableTestExpression(false)
        val payload = MutableTestExpression(JSONObject().put("version", "old"))
        val currentPayload = JSONObject().put("version", "current")
        val playbackSpeed = MutableTestExpression(1.0)
        val cases = listOf(
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

        val actual = cases.associate { case ->
            bindOffMain(case.video)
            case.update()
            shadowOf(Looper.getMainLooper()).idle()
            case.name to createdConfig.lastValue
        }

        assertEquals(cases.associate { it.name to it.expected }, actual)
    }

    private fun bindOffMain(div: DivVideo): Thread {
        (playerView.parent as? android.view.ViewGroup)?.removeView(playerView)
        val divBlock = DivBlock.Video(Div.Video(div), resolver, DivStatePath(0))
        val view = spy(DivVideoView(context))
        whenever(view.isAttachedToWindow).thenReturn(true)
        return runOffMain { immediateUnderTest.loadVideo(view, divBlock, divView) }
    }

    private fun runOffMain(action: () -> Unit): Thread {
        val failure = AtomicReference<Throwable>()
        val bindingThread = Thread {
            try {
                action()
            } catch (error: Throwable) {
                failure.set(error)
            }
        }
        bindingThread.start()
        bindingThread.join()
        failure.get()?.let { throw AssertionError("Video binding failed", it) }
        return bindingThread
    }

    private fun videoWithConstantSource() = DivVideo(
        videoSources = listOf(Div2VideoSource(
            mimeType = Expression.constant(DEFAULT_MIME_TYPE),
            url = Expression.constant(DEFAULT_URL),
        )),
    )

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

    @Test
    fun `player creation waits until video view is attached`() {
        val attachmentStates = mutableListOf<Boolean>()
        val playerFactory = mock<DivPlayerFactory> {
            on { makePlayerView(any()) } doAnswer { playerView }
            on { makePlayer(any(), any()) } doAnswer {
                attachmentStates += videoView.isAttachedToWindow
                player
            }
        }
        val underTest = createBinder(true, playerFactory)

        underTest.loadVideo(videoView, createBlock(DivVideo(videoSources = emptyList())), divView)
        attach(videoView)

        assertEquals(listOf(true), attachmentStates)
    }

    @Test
    fun `player stays hidden and is not created while scrolling`() {
        attachToScrollingParent(videoView)

        underTest.loadVideo(videoView, createBlock(DivVideo(videoSources = emptyList())), divView)

        assertEquals(View.INVISIBLE, playerView.visibility)
        verify(playerFactory, never()).makePlayer(any(), any())
    }

    @Test
    fun `player is attached and visible when scrolling finishes`() {
        val recycler = attachToScrollingParent(videoView)
        underTest.loadVideo(videoView, createBlock(DivVideo(videoSources = emptyList())), divView)

        recycler.moveToIdle()

        assertEquals(player to View.VISIBLE, playerView.getAttachedPlayer() to playerView.visibility)
    }

    @Test
    fun `player creation does not wait for scroll idle when deferred creation is disabled`() {
        attachToScrollingParent(videoView)

        immediateUnderTest.loadVideo(
            videoView,
            createBlock(DivVideo(videoSources = emptyList())),
            divView,
        )

        assertSame(player, playerView.getAttachedPlayer())
    }

    @Test
    fun `disabled deferred creation preserves legacy rebind behavior`() {
        val firstPlayer = mock<DivPlayer>()
        val secondPlayer = mock<DivPlayer>()
        val underTest = createBinder(false, playerFactoryReturning(firstPlayer, secondPlayer))
        val div = DivVideo(
            videoSources = emptyList(),
            preview = Expression.constant(INVALID_PREVIEW),
        )

        underTest.loadVideo(videoView, createBlock(div), divView)
        executorService.runNext()
        shadowOf(Looper.getMainLooper()).idle()

        underTest.loadVideo(videoView, createBlock(div), divView)

        assertSame(secondPlayer, playerView.getAttachedPlayer())
        verify(firstPlayer, never()).release()
    }

    @Test
    fun `disabled deferred creation propagates player initialization failure`() {
        val expected = IllegalStateException("player creation failed")
        val underTest = createBinder(false, failingPlayerFactory(expected))

        val failure = runCatching {
            underTest.loadVideo(
                videoView,
                createBlock(DivVideo(videoSources = emptyList())),
                divView,
            )
        }.exceptionOrNull()

        assertSame(expected, failure)
    }

    @Test
    fun `deferred creation propagates player initialization failure`() {
        val expected = IllegalStateException("player creation failed")
        val underTest = createBinder(true, failingPlayerFactory(expected))
        val recyclerView = attachToScrollingParent(videoView)
        underTest.loadVideo(videoView, createBlock(DivVideo(videoSources = emptyList())), divView)

        val failure = runCatching { recyclerView.moveToIdle() }.exceptionOrNull()

        assertSame(expected, failure)
    }

    @Test
    fun `preview scale is applied during binding`() {
        val div = DivVideo(
            videoSources = emptyList(),
            scale = Expression.constant(DivVideoScale.NO_SCALE),
        )

        underTest.loadVideo(videoView, createBlock(div), divView)

        assertEquals(ImageView.ScaleType.CENTER, previewView(videoView).scaleType)
    }

    @Test
    fun `deferred preview uses latest dynamic scale on main thread`() {
        val scale = MutableTestExpression(DivVideoScale.FIT)
        val div = DivVideo(
            videoSources = emptyList(),
            scale = scale,
        )

        val bindingThread = runOffMain { underTest.loadVideo(videoView, createBlock(div), divView) }
        scale.value = DivVideoScale.NO_SCALE
        shadowOf(Looper.getMainLooper()).idle()

        assertEquals(
            ImageView.ScaleType.CENTER to listOf(bindingThread, Looper.getMainLooper().thread),
            previewView(videoView).scaleType to scale.evaluationThreads,
        )
    }

    @Test
    fun `rebind keeps current preview visible while replacement is decoded`() {
        attach(videoView)

        underTest.loadVideo(videoView, createBlock(DivVideo(videoSources = emptyList())), divView)
        val previewView = previewView(videoView)
        previewView.visibility = View.VISIBLE

        underTest.loadVideo(
            videoView,
            createBlock(DivVideo(
                videoSources = emptyList(),
                preview = Expression.constant("pending preview"),
            )),
            divView,
        )

        assertEquals(View.VISIBLE to View.INVISIBLE, previewView.visibility to playerView.visibility)
    }

    @Test
    fun `rebind keeps pending decode for unchanged preview`() {
        val div = DivVideo(
            videoSources = emptyList(),
            preview = Expression.constant("pending preview"),
            scale = Expression.constant(DivVideoScale.FIT),
        )

        underTest.loadVideo(videoView, createBlock(div), divView)
        underTest.loadVideo(videoView, createBlock(div), divView)

        assertEquals(1, executorService.pendingTaskCount)
    }

    @Test
    fun `rebind keeps player visible after unchanged applied preview becomes ready`() {
        val firstPlayer = mock<DivPlayer>()
        val secondPlayer = mock<DivPlayer>()
        val observer = argumentCaptor<DivPlayer.Observer>()
        val underTest = createBinder(true, playerFactoryReturning(firstPlayer, secondPlayer))
        val div = DivVideo(
            videoSources = emptyList(),
            preview = Expression.constant(VALID_PREVIEW),
            scale = Expression.constant(DivVideoScale.FIT),
        )
        attach(videoView)

        underTest.loadVideo(videoView, createBlock(div), divView)
        executorService.runNext()
        shadowOf(Looper.getMainLooper()).idle()
        val previewView = previewView(videoView)
        videoView.releaseMedia()

        underTest.loadVideo(videoView, createBlock(div), divView)

        verify(secondPlayer).addObserver(observer.capture())
        observer.firstValue.onReady()
        assertEquals(View.INVISIBLE to View.VISIBLE, previewView.visibility to playerView.visibility)
    }

    @Test
    fun `failed preview decode is retried on rebind`() {
        val div = DivVideo(
            videoSources = emptyList(),
            preview = Expression.constant(INVALID_PREVIEW),
            scale = Expression.constant(DivVideoScale.FIT),
        )

        underTest.loadVideo(videoView, createBlock(div), divView)
        executorService.runNext()
        shadowOf(Looper.getMainLooper()).idle()

        underTest.loadVideo(videoView, createBlock(div), divView)

        assertEquals(1, executorService.pendingTaskCount)
    }

    @Test
    fun `deferred player uses dynamic source from scroll idle`() {
        val sourceUrl = MutableTestExpression(Uri.parse("https://example.com/old.mp4"))
        val recyclerView = attachToScrollingParent(videoView)
        val div = DivVideo(
            videoSources = listOf(Div2VideoSource(
                mimeType = Expression.constant("video/mp4"),
                url = sourceUrl,
            )),
        )

        underTest.loadVideo(videoView, createBlock(div), divView)
        sourceUrl.value = Uri.parse("https://example.com/current.mp4")
        recyclerView.moveToIdle()

        assertEquals(
            listOf(DivVideoSource(url = sourceUrl.value, mimeType = "video/mp4")),
            createdSource.allValues.flatten(),
        )
    }

    @Test
    fun `deferred player uses dynamic config from scroll idle`() {
        val repeatable = MutableTestExpression(false)
        val recyclerView = attachToScrollingParent(videoView)
        val div = DivVideo(videoSources = emptyList(), repeatable = repeatable)

        underTest.loadVideo(videoView, createBlock(div), divView)
        repeatable.value = true
        recyclerView.moveToIdle()

        assertEquals(
            listOf(DivPlayerPlaybackConfig(repeatable = true)),
            createdConfig.allValues,
        )
    }

    @Test
    fun `deferred player uses latest playback state`() {
        val viewStateStore = DivViewStateStoreImpl()
        whenever(divView.viewStateStore).thenReturn(viewStateStore)
        val recyclerView = attachToScrollingParent(videoView)
        val divBlock = createBlock(DivVideo(
            videoSources = emptyList(),
            autostart = Expression.constant(true),
        ))

        underTest.loadVideo(videoView, divBlock, divView)
        viewStateStore.put(
            divBlock.path.fullPath,
            DivVideoViewState(DivVideoPlaybackState.PAUSED),
        )

        recyclerView.moveToIdle()

        assertEquals(
            listOf(DivPlayerPlaybackConfig(autoplay = false)),
            createdConfig.allValues,
        )
    }

    private fun createBlock(div: DivVideo) = DivBlock.Video(Div.Video(div), resolver, DivStatePath(0))

    private fun attach(view: View) {
        Robolectric.buildActivity(Activity::class.java).setup().get().setContentView(view)
    }

    private fun attachToScrollingParent(view: DivVideoView): TestRecyclerView {
        return TestRecyclerView(context, RecyclerView.SCROLL_STATE_SETTLING).apply {
            layoutManager = LinearLayoutManager(context)
            addView(view)
            attach(this)
        }
    }

    private fun previewView(view: DivVideoView): ImageView {
        return view.children.filterIsInstance<ImageView>().single()
    }

    private fun playerFactoryReturning(
        firstPlayer: DivPlayer,
        secondPlayer: DivPlayer,
    ) = mock<DivPlayerFactory> {
        on { makePlayerView(any()) }.thenReturn(playerView)
        on { makePlayer(any(), any()) }.thenReturn(firstPlayer, secondPlayer)
    }

    private fun failingPlayerFactory(error: Throwable) = mock<DivPlayerFactory> {
        on { makePlayerView(any()) }.thenReturn(playerView)
        on { makePlayer(any(), any()) }.thenThrow(error)
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

    private class TestPlayerView(
        context: Context,
        private val onAttach: () -> Unit,
    ) : DivPlayerView(context) {

        private var player: DivPlayer? = null

        override fun attach(player: DivPlayer) {
            this.player = player
            onAttach()
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

        private val listeners = linkedSetOf<OnScrollListener>()

        override fun getScrollState(): Int = state

        override fun addOnScrollListener(listener: OnScrollListener) {
            listeners += listener
        }

        override fun removeOnScrollListener(listener: OnScrollListener) {
            listeners -= listener
        }

        fun moveToIdle() {
            state = SCROLL_STATE_IDLE
            listeners.toList().forEach { it.onScrollStateChanged(this, state) }
        }
    }

    private class QueuedExecutorService : AbstractExecutorService() {
        private val tasks = ArrayDeque<Runnable>()
        private var shutdown = false

        val pendingTaskCount: Int
            get() = tasks.size

        override fun execute(command: Runnable) {
            check(!shutdown) { "Executor is shut down" }
            tasks += command
        }

        fun runNext() {
            tasks.removeFirst().run()
        }

        override fun shutdown() {
            shutdown = true
        }

        override fun shutdownNow(): MutableList<Runnable> {
            shutdown = true
            return tasks.toMutableList().also { tasks.clear() }
        }

        override fun isShutdown(): Boolean = shutdown

        override fun isTerminated(): Boolean = shutdown && tasks.isEmpty()

        override fun awaitTermination(timeout: Long, unit: java.util.concurrent.TimeUnit): Boolean = isTerminated
    }
}
