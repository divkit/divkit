package com.yandex.div.core

import android.net.Uri
import com.yandex.div.core.extension.DivExtensionController
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.core.images.LoadReference
import com.yandex.div.core.player.DivPlayerPreloader
import com.yandex.div.core.preload.PreloadResult
import com.yandex.div.core.view2.DivImagePreloader
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import com.yandex.div2.DivCustom
import com.yandex.div2.DivExtension
import com.yandex.div2.DivInput
import com.yandex.div2.DivSeparator
import com.yandex.div2.DivVideo
import com.yandex.div2.DivVideoSource
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

/**
 * Tests for [DivPreloader].
 */
@RunWith(RobolectricTestRunner::class)
class DivPreloaderTest {

    private val divCustomContainerViewAdapter = mock<DivCustomContainerViewAdapter> {
        on { preload(any(), any()) } doReturn DivPreloader.PreloadReference.EMPTY
    }
    private val divImagePreloader = mock<DivImagePreloader>()

    private val extensionHandlers = listOf<DivExtensionHandler>(mock(), mock())
    private val extensionHandlersController = DivExtensionController(extensionHandlers)

    private val divText = text(text = "test")

    private val custom = DivCustom(customType = "test")
    private val divCustom = Div.Custom(custom)

    private val input = DivInput(textVariable = "test")
    private val divInput = Div.Input(input)

    private val separator = DivSeparator(extensions = listOf(DivExtension(id = "test1")))
    private val divSeparator = Div.Separator(separator)

    private val containerItems = listOf(divText, divInput, divCustom, divSeparator)

    private val container = DivContainer(items = containerItems, extensions = listOf(DivExtension(id = "test2")))
    private val divContainer = Div.Container(container)
    private val videoPreloader = mock<DivPlayerPreloader>()
    private val resolver = mockExpressionResolver()

    private val videoSourceUrl = Uri.parse("https://example.com/video.mp4")
    private val videoSource = DivVideoSource(
        mimeType = Expression.constant("video/mp4"),
        url = Expression.constant(videoSourceUrl)
    )
    private val videoWithPreload = DivVideo(
        videoSources = listOf(videoSource),
        preloadRequired = Expression.constant(true)
    )
    private val divVideoWithPreload = Div.Video(videoWithPreload)

    private val underTest: DivPreloader = DivPreloader(
        divImagePreloader,
        divCustomContainerViewAdapter,
        extensionHandlersController,
        videoPreloader,
        DivPreloader.PreloadFilter.ONLY_PRELOAD_REQUIRED_FILTER
    )

    @Test
    fun `preload div background`() {
        underTest.preload(divInput, resolver)

        verify(divImagePreloader).preloadImage(eq(divInput), any(), any(), any())
    }

    @Test
    fun `preload div items background in containers`() {
        underTest.preload(divContainer, resolver)

        verify(divImagePreloader, times(1)).preloadImage(eq(divContainer), any(), any(), any())
        verify(divImagePreloader, times(1)).preloadImage(eq(divInput), any(), any(), any())
        verify(divImagePreloader, times(1)).preloadImage(eq(divText), any(), any(), any())
        verify(divImagePreloader, times(1)).preloadImage(eq(divCustom), any(), any(), any())
    }

    @Test
    fun `preload div custom`() {
        underTest.preload(divCustom, resolver)

        verify(divCustomContainerViewAdapter).preload(eq(custom), any())
    }

    @Test
    fun `preload div custom in container`() {
        underTest.preload(divContainer, resolver)

        verify(divCustomContainerViewAdapter).preload(eq(custom), any())
    }

    @Test
    fun `loadReferences cancel called when preload cancelled`() {
        val containerLoadReference = mock<LoadReference>()
        val inputLoadReference = mock<LoadReference>()
        val textLoadReference = mock<LoadReference>()
        val customLoadReference = mock<LoadReference>()
        whenever(divImagePreloader.preloadImage(eq(divContainer), any(), any(), any()))
            .thenReturn(listOf(containerLoadReference))
        whenever(divImagePreloader.preloadImage(eq(divInput), any(), any(), any()))
            .thenReturn(listOf(inputLoadReference))
        whenever(divImagePreloader.preloadImage(eq(divText), any(), any(), any()))
            .thenReturn(listOf(textLoadReference))
        whenever(divImagePreloader.preloadImage(eq(divCustom), any(), any(), any()))
            .thenReturn(listOf(customLoadReference))
        val ticket = underTest.preload(divContainer, resolver)

        ticket.cancel()

        verify(containerLoadReference).cancel()
        verify(inputLoadReference).cancel()
        verify(textLoadReference).cancel()
        verify(customLoadReference).cancel()
    }

    @Test
    fun `preprocesses div extensions when extension handlers present`() {
        whenever(extensionHandlers[0].matches(eq(container))).thenReturn(true)
        whenever(extensionHandlers[1].matches(eq(separator))).thenReturn(true)

        underTest.preload(divContainer, resolver)

        verify(extensionHandlers[0], times(1)).preprocess(eq(container), any(), any())
        verify(extensionHandlers[1], times(1)).preprocess(eq(separator), any(), any())
    }

    @Test
    fun `preload div video calls video preloader when preload required`() {
        whenever(videoPreloader.preloadVideo(any(), any())).doAnswer { invocation ->
            (invocation.getArgument<(List<PreloadResult>) -> Unit>(1)).invoke(emptyList())
            DivPreloader.PreloadReference.EMPTY
        }

        underTest.preload(divVideoWithPreload, resolver)

        verify(videoPreloader).preloadVideo(eq(listOf(videoSourceUrl)), any())
    }

    @Test
    fun `video preload reference cancelled when ticket cancelled`() {
        val videoPreloadReference = mock<DivPreloader.PreloadReference>()
        whenever(videoPreloader.preloadVideo(any(), any())).doReturn(videoPreloadReference)

        val ticket = underTest.preload(divVideoWithPreload, resolver)

        ticket.cancel()

        verify(videoPreloadReference).cancel()
    }
}
