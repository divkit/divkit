package com.yandex.div.core.view2.prebinding

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.extension.DivExtensionActionHandler
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.core.images.BitmapSource
import com.yandex.div.core.images.DivCachedImage
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference
import com.yandex.div.core.player.DivPlayer
import com.yandex.div.core.player.DivPlayerFactory
import com.yandex.div.core.player.DivPlayerPreloader
import com.yandex.div.core.player.DivPlayerView
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div.core.widget.makeExactSpec
import com.yandex.div.core.widget.makeUnspecifiedSpec
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.internal.view.DivImageView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.testContextThemeWrapper
import com.yandex.div2.DivBase
import com.yandex.div2.DivData
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue
import org.json.JSONObject
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.annotation.LooperMode

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.INSTRUMENTATION_TEST)
class GalleryPrebindingTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val preparations = CopyOnWriteArrayList<Pair<Thread, View>>()
    private val imageCallbacks = CopyOnWriteArrayList<DivImageDownloadCallback>()
    private val player = mock<DivPlayer>()
    private val videoCleanupExtension = mock<DivExtensionHandler> {
        on { matches(any()) } doAnswer { it.getArgument<DivBase>(0).id == "video" }
    }
    private var failVideoUnbinding = false
    private lateinit var divView: Div2View

    @BeforeTest
    fun setup() = instrumentation.runOnMainSync {
        val extension = object : DivExtensionHandler {
            override fun matches(div: DivBase) = div.extensions?.any { it.id == "record-binding" } == true

            override fun beforeBindView(
                divView: Div2View,
                expressionResolver: ExpressionResolver,
                view: View,
                div: DivBase,
            ) {
                preparations += Thread.currentThread() to view
            }

            override fun bindView(
                divView: Div2View,
                expressionResolver: ExpressionResolver,
                view: View,
                div: DivBase,
            ) = Unit

            override fun unbindView(
                divView: Div2View,
                expressionResolver: ExpressionResolver,
                view: View,
                div: DivBase,
            ) {
                if (failVideoUnbinding && div.id == "video") {
                    throw IllegalStateException("Video extension cleanup failed")
                }
            }
        }
        val imageLoader = mock<DivImageLoader> {
            on { loadImage(any<String>(), any<DivImageDownloadCallback>()) } doAnswer {
                imageCallbacks += it.getArgument<DivImageDownloadCallback>(1)
                mock<LoadReference>()
            }
        }
        val playerFactory = mock<DivPlayerFactory> {
            on { makePreloader() } doReturn DivPlayerPreloader.STUB
            on { makePlayer(any(), any()) } doReturn player
            on { makePlayerView(any()) } doAnswer {
                object : DivPlayerView(it.getArgument(0)) {
                    private var attachedPlayer: DivPlayer? = null

                    override fun attach(player: DivPlayer) {
                        attachedPlayer = player
                    }

                    override fun detach() {
                        attachedPlayer = null
                    }

                    override fun getAttachedPlayer() = attachedPlayer
                }
            }
        }
        val configuration = DivConfiguration.Builder(imageLoader)
            .enableBindOnAttach(false)
            .enableDeferredVideoPlayerCreation(false)
            .divPlayerFactory(playerFactory)
            .extension(extension)
            .extension(videoCleanupExtension)
            .divErrorsReporter(mock())
            .build()
        divView = Div2View(Div2Context(testContextThemeWrapper(), configuration))
    }

    @AfterTest
    fun cleanup() = instrumentation.runOnMainSync { divView.cleanup() }

    @Test
    fun `first layout reuses the view bound once on a worker`() {
        bindAsync()
        instrumentation.runOnMainSync {
            layout()
            val displayed = divView.findViewWithTag<View>("first")

            assertEquals(
                listOf(false to displayed),
                preparations.filter { it.second.tag == "first" }.map { (thread, view) ->
                    (thread === Looper.getMainLooper().thread) to view
                },
            )
        }
    }

    @Test
    fun `synchronous binding keeps gallery items unbound until layout`() = instrumentation.runOnMainSync {
        divView.setData(document(), DivDataTag("gallery-prebinding"))

        assertTrue(preparations.isEmpty())
    }

    @Test
    fun `image resumes after media release before first layout`() {
        bindAsync()
        val drawable = ColorDrawable(Color.GREEN)
        instrumentation.runOnMainSync {
            divView.releaseMedia()
            divView.loadMedia()
            layout()
            imageCallbacks[1].onSuccess(DivCachedImage.Drawable(drawable, BitmapSource.MEMORY))

            assertSame(drawable, divView.findViewWithTag<DivImageView>("image")?.drawable)
        }
    }

    @Test
    fun `release before first layout frees the prepared video player`() {
        bindAsync()
        instrumentation.runOnMainSync { divView.releaseMedia() }

        verify(player).release()
    }

    @Test
    fun `prepared video player is released when extension cleanup fails before first layout`() {
        bindAsync()
        failVideoUnbinding = true

        instrumentation.runOnMainSync { divView.releaseMedia() }

        verify(player).release()
    }

    @Test
    fun `later video extension is unbound when earlier extension cleanup fails before first layout`() {
        bindAsync()
        failVideoUnbinding = true
        val video = preparations.single { it.second.tag == "video" }.second

        instrumentation.runOnMainSync { divView.releaseMedia() }

        verify(videoCleanupExtension).unbindView(eq(divView), any(), eq(video), argThat { id == "video" })
    }

    @Test
    fun `video extension is unbound when action cleanup fails before first layout`() {
        val actionHandler = mock<DivExtensionActionHandler> {
            on { onViewUnbind(any(), any(), any()) } doThrow IllegalStateException("Video action cleanup failed")
        }
        whenever(videoCleanupExtension.actionHandler).thenReturn(actionHandler)
        bindAsync()
        val video = preparations.single { it.second.tag == "video" }.second

        instrumentation.runOnMainSync { divView.releaseMedia() }

        verify(videoCleanupExtension).unbindView(eq(divView), any(), eq(video), argThat { id == "video" })
    }

    @Test
    fun `prepared item remains visible after recycling away and back`() {
        bindAsync()
        instrumentation.runOnMainSync {
            layout()
            val gallery = requireNotNull(divView.findViewWithTag<DivRecyclerView>("gallery"))
            gallery.setItemViewCacheSize(0)
            gallery.itemAnimator = null
            val manager = gallery.layoutManager as LinearLayoutManager
            manager.scrollToPositionWithOffset(5, 0)
            layout()
            manager.scrollToPositionWithOffset(0, 0)
            layout()

            assertEquals("Card 0", divView.findViewWithTag<TextView>("first")?.text?.toString())
        }
    }

    private fun bindAsync() {
        val completed = CountDownLatch(1)
        var successful = false
        instrumentation.runOnMainSync {
            divView.setDataAsync(document(), DivDataTag("gallery-prebinding")) {
                successful = it
                completed.countDown()
            }
        }
        check(completed.await(5, TimeUnit.SECONDS) && successful) { "Async gallery binding failed" }
    }

    private fun layout() {
        divView.measure(makeExactSpec(300), makeUnspecifiedSpec())
        divView.layout(0, 0, divView.measuredWidth, divView.measuredHeight)
    }

    private fun document(): DivData = DivData(
        DivParsingEnvironment(logger = { throw AssertionError("Invalid gallery fixture", it) }),
        JSONObject(DOCUMENT).getJSONObject("card"),
    )

    private companion object {
        const val DOCUMENT = """
            {
              "card": {
                "log_id": "gallery-prebinding",
                "states": [{
                  "state_id": 0,
                  "div": {
                    "type": "gallery", "id": "gallery", "item_spacing": 0,
                    "items": [
                      {
                        "type": "text", "id": "first", "text": "Card 0",
                        "width": {"type": "fixed", "value": 120, "unit": "px"},
                        "height": {"type": "fixed", "value": 48, "unit": "px"},
                        "extensions": [{"id": "record-binding"}]
                      },
                      {
                        "type": "image", "id": "image", "image_url": "https://example.com/prepared.png",
                        "width": {"type": "fixed", "value": 120, "unit": "px"},
                        "height": {"type": "fixed", "value": 48, "unit": "px"},
                        "extensions": [{"id": "record-binding"}]
                      },
                      {
                        "type": "video", "id": "video",
                        "video_sources": [{
                          "type": "video_source", "url": "https://example.com/prepared.mp4",
                          "mime_type": "video/mp4"
                        }],
                        "width": {"type": "fixed", "value": 120, "unit": "px"},
                        "height": {"type": "fixed", "value": 48, "unit": "px"},
                        "extensions": [{"id": "record-binding"}]
                      },
                      {
                        "type": "text", "text": "Card 3",
                        "width": {"type": "fixed", "value": 120, "unit": "px"},
                        "height": {"type": "fixed", "value": 48, "unit": "px"}
                      },
                      {
                        "type": "text", "text": "Card 4",
                        "width": {"type": "fixed", "value": 120, "unit": "px"},
                        "height": {"type": "fixed", "value": 48, "unit": "px"}
                      },
                      {
                        "type": "text", "text": "Card 5",
                        "width": {"type": "fixed", "value": 120, "unit": "px"},
                        "height": {"type": "fixed", "value": 48, "unit": "px"}
                      }
                    ]
                  }
                }]
              }
            }
        """
    }
}
