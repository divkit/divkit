package com.yandex.div.core

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.extension.DivExtensionController
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.core.images.LoadReference
import com.yandex.div.core.player.DivPlayerPreloader
import com.yandex.div.core.preload.PreloadResult
import com.yandex.div.core.preload.UriPreloadResult
import com.yandex.div.core.view2.DivImagePreloader
import com.yandex.div.internal.util.UiThreadHandler
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import com.yandex.div2.DivCustom
import com.yandex.div2.DivExtension
import com.yandex.div2.DivInput
import com.yandex.div2.DivSeparator
import com.yandex.div2.DivVideo
import com.yandex.div2.DivVideoSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivPreloaderTest {

    private val imagePreloader = mock<DivImagePreloader> {
        on { preloadImage(any(), any(), any(), any()) } doReturn emptyList()
    }
    private val customAdapter = mock<DivCustomContainerViewAdapter> {
        on { preload(any(), any()) } doReturn DivPreloader.PreloadReference.EMPTY
    }
    private val videoPreloader = mock<DivPlayerPreloader>()
    private val resolver = mockExpressionResolver()
    private val callback = mock<DivPreloader.Callback>()
    private val underTest = DivPreloader(
        imagePreloader,
        customAdapter,
        DivExtensionController(emptyList()),
        videoPreloader,
        DivPreloader.PreloadFilter.ONLY_PRELOAD_REQUIRED_FILTER,
    )

    @BeforeTest
    fun setUp() {
        UiThreadHandler.setInTestMode(true)
    }

    @AfterTest
    fun tearDown() {
        UiThreadHandler.setInTestMode(false)
    }

    @Test
    fun `preload visits root and every child exactly once`() {
        val visitedDivs = argumentCaptor<Div>()
        val firstChild = text(text = "first")
        val secondChild = Div.Input(DivInput(textVariable = "input"))
        val root = Div.Container(DivContainer(items = listOf(firstChild, secondChild)))

        underTest.preload(root, resolver)

        verify(imagePreloader, times(3)).preloadImage(visitedDivs.capture(), any(), any(), any())
        assertEquals(listOf(root, firstChild, secondChild), visitedDivs.allValues)
    }

    @Test
    fun `preload delegates custom content to custom adapter`() {
        val custom = DivCustom(customType = "map")

        underTest.preload(Div.Custom(custom), resolver)

        verify(customAdapter).preload(eq(custom), any())
    }

    @Test
    fun `preload runs only matching extension handlers for nested divs`() {
        val separator = DivSeparator(extensions = listOf(DivExtension(id = "extension")))
        val matchingHandler = mock<DivExtensionHandler> {
            on { matches(separator) } doReturn true
        }
        val unrelatedHandler = mock<DivExtensionHandler>()
        val underTest = DivPreloader(
            imagePreloader,
            customAdapter,
            DivExtensionController(listOf(matchingHandler, unrelatedHandler)),
            videoPreloader,
            DivPreloader.PreloadFilter.ONLY_PRELOAD_REQUIRED_FILTER,
        )

        underTest.preload(
            Div.Container(DivContainer(items = listOf(Div.Separator(separator)))),
            resolver,
        )

        verify(matchingHandler).preprocess(eq(separator), eq(resolver), any())
        verify(unrelatedHandler, never()).preprocess(any(), any(), any())
    }

    @Test
    fun `video content is preloaded when preload is required`() {
        val firstUrl = Uri.parse("https://example.com/first.mp4")
        val secondUrl = Uri.parse("https://example.com/second.mp4")
        whenever(videoPreloader.preloadVideo(any(), any())) doAnswer {
            it.getArgument<(List<PreloadResult>) -> Unit>(1).invoke(emptyList())
            DivPreloader.PreloadReference.EMPTY
        }

        underTest.preload(Div.Video(video(true, firstUrl, secondUrl)), resolver)

        verify(videoPreloader).preloadVideo(eq(listOf(firstUrl, secondUrl)), any())
    }

    @Test
    fun `video content is not preloaded when preload is not required`() {
        underTest.preload(
            Div.Video(video(false, Uri.parse("https://example.com/video.mp4"))),
            resolver,
        )

        verify(videoPreloader, never()).preloadVideo(any(), any())
    }

    @Test
    fun `ticket cancellation cancels every collected reference`() {
        val custom = DivCustom(customType = "map")
        val customDiv = Div.Custom(custom)
        val videoDiv = Div.Video(video(true, Uri.parse("https://example.com/video.mp4")))
        val root = Div.Container(DivContainer(items = listOf(customDiv, videoDiv)))
        val cancelled = mutableSetOf<String>()
        val rootImageReference = LoadReference { cancelled += "root image" }
        val customImageReference = LoadReference { cancelled += "custom image" }
        val videoImageReference = LoadReference { cancelled += "video image" }
        val customReference = DivPreloader.PreloadReference { cancelled += "custom" }
        val videoReference = DivPreloader.PreloadReference { cancelled += "video" }
        whenever(imagePreloader.preloadImage(eq(root), any(), any(), any()))
            .thenReturn(listOf(rootImageReference))
        whenever(imagePreloader.preloadImage(eq(customDiv), any(), any(), any()))
            .thenReturn(listOf(customImageReference))
        whenever(imagePreloader.preloadImage(eq(videoDiv), any(), any(), any()))
            .thenReturn(listOf(videoImageReference))
        whenever(customAdapter.preload(eq(custom), any())).thenReturn(customReference)
        whenever(videoPreloader.preloadVideo(any(), any())) doAnswer {
            it.getArgument<(List<PreloadResult>) -> Unit>(1).invoke(emptyList())
            videoReference
        }

        underTest.preload(root, resolver).cancel()

        assertEquals(
            setOf("root image", "custom image", "video image", "custom", "video"),
            cancelled,
        )
    }

    @Test
    fun `callback remains pending while a video preload is unfinished`() {
        val videoCallbacks = argumentCaptor<(List<PreloadResult>) -> Unit>()
        whenever(videoPreloader.preloadVideo(any(), videoCallbacks.capture()))
            .thenReturn(DivPreloader.PreloadReference.EMPTY)
        val root = Div.Container(DivContainer(items = listOf(
            Div.Video(video(true, Uri.parse("https://example.com/first.mp4"))),
            Div.Video(video(true, Uri.parse("https://example.com/second.mp4"))),
        )))

        underTest.preload(root, resolver, callback)
        videoCallbacks.firstValue.invoke(emptyList())

        verify(callback, never()).finish(any())
    }

    @Test
    fun `callback finishes after all video preloads`() {
        val videoCallbacks = argumentCaptor<(List<PreloadResult>) -> Unit>()
        whenever(videoPreloader.preloadVideo(any(), videoCallbacks.capture()))
            .thenReturn(DivPreloader.PreloadReference.EMPTY)
        val root = Div.Container(DivContainer(items = listOf(
            Div.Video(video(true, Uri.parse("https://example.com/first.mp4"))),
            Div.Video(video(true, Uri.parse("https://example.com/second.mp4"))),
        )))

        underTest.preload(root, resolver, callback)
        videoCallbacks.allValues.forEach { it.invoke(emptyList()) }

        verify(callback).finish(false)
    }

    @Test
    fun `callback reports an error from video preload`() {
        val videoCallback = argumentCaptor<(List<PreloadResult>) -> Unit>()
        val url = Uri.parse("https://example.com/video.mp4")
        whenever(videoPreloader.preloadVideo(eq(listOf(url)), videoCallback.capture()))
            .thenReturn(DivPreloader.PreloadReference.EMPTY)

        underTest.preload(Div.Video(video(true, url)), resolver, callback)
        videoCallback.firstValue.invoke(listOf(UriPreloadResult(url, IllegalStateException("decode failed"))))

        verify(callback).finish(true)
    }

    @Test
    fun `concurrent video completions finish preload once`() = runBlocking {
        val videos = List(100) {
            Div.Video(video(true, Uri.parse("https://example.com/video-$it.mp4")))
        }
        val completionDispatcher = Dispatchers.Default.limitedParallelism(32)
        val completionJobs = mutableListOf<Job>()
        whenever(videoPreloader.preloadVideo(any(), any())) doAnswer {
            val videoCallback = it.getArgument<(List<PreloadResult>) -> Unit>(1)
            completionJobs += launch(completionDispatcher) { videoCallback(emptyList()) }
            DivPreloader.PreloadReference.EMPTY
        }

        underTest.preload(Div.Container(DivContainer(items = videos)), resolver, callback)
        completionJobs.joinAll()

        verify(callback, times(1)).finish(false)
    }

    private fun video(preloadRequired: Boolean, vararg urls: Uri) = DivVideo(
        videoSources = urls.map { url ->
            DivVideoSource(
                mimeType = Expression.constant("video/mp4"),
                url = Expression.constant(url),
            )
        },
        preloadRequired = Expression.constant(preloadRequired),
    )
}
