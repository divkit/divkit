package com.yandex.div.core.view2.divs

import android.net.Uri
import com.yandex.div.core.Div2ImageStubProvider
import com.yandex.div.core.asExpression
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.view2.DivPlaceholderLoader
import com.yandex.div.core.view2.divs.widgets.DivImageView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivImage
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.Executors

@RunWith(RobolectricTestRunner::class)
class DivImageBinderTest : DivBinderTest() {

    private val placeholderLoader =
        spy(DivPlaceholderLoader(Div2ImageStubProvider.STUB, Executors.newFixedThreadPool(1)))
    private val binder = DivImageBinder(
        baseBinder, imageLoader, placeholderLoader,
    )

    @Before
    fun setUp() {
        whenever(imageLoader.loadImage(any(), any<DivImageDownloadCallback>())).thenReturn(mock())
    }

    @Test
    fun `url action applied`() {
        val (view, divContainer) = createTestDiv("with_action.json")

        binder.bindView(view, divContainer, divView)

        assertActionApplied(divView, view, Expected.ACTION_URI)
    }

    @Test
    fun `state action applied`() {
        val (view, divContainer) = createTestDiv("with_set_state_action.json")

        binder.bindView(view, divContainer, divView)

        assertActionApplied(divView, view, Expected.STATE_ACTION_URI)
    }

    @Test
    fun `bind image when image wasn't previously bound`() {
        val (view, divContainer) = createTestDiv("with_action.json")

        binder.bindView(view, divContainer, divView)

        verify(placeholderLoader).applyPlaceholder(any(), anyOrNull(), any(), any(), any())
        verify(imageLoader).loadImage(eq(divContainer.imageUrl.evaluate(ExpressionResolver.EMPTY).toString()), any<DivImageDownloadCallback>())
    }

    @Test
    fun `bind image when imageUrl did not change but bitmap was not loaded`() {
        val (view, divContainer) = createTestDiv("with_action.json")
        binder.bindView(view, divContainer, divView)

        val (_, nextDivContainer) = createTestDiv("with_action.json")
        binder.bindView(view, nextDivContainer, divView)

        verify(placeholderLoader, times(2)).applyPlaceholder(any(), anyOrNull(), any(), any(), any())
        verify(imageLoader, times(2)).loadImage(eq(divContainer.imageUrl.evaluate(ExpressionResolver.EMPTY).toString()), any<DivImageDownloadCallback>())
    }

    @Test
    fun `do not bind image when imageUrl did not change and bitmap was loaded`() {
        val (view, divContainer) = createTestDiv("with_action.json")

        binder.bindView(view, divContainer, divView)

        whenImageLoaded(divContainer.imageUrl.evaluate(ExpressionResolver.EMPTY).toString())

        val (_, nextDivContainer) = createTestDiv("with_action.json")
        binder.bindView(view, nextDivContainer, divView)
        verifyNoMoreInteractions(placeholderLoader)
        verifyNoMoreInteractions(imageLoader)
    }

    @Test
    fun `bind image when bitmap was loaded but imageUrl changed`() {
        val (view, divContainer) = createTestDiv("with_action.json")

        binder.bindView(view, divContainer, divView)

        whenImageLoaded(divContainer.imageUrl.evaluate(ExpressionResolver.EMPTY).toString())

        val nextDivContainer = DivImage(imageUrl = Uri.parse("https://foo.bar/foo.png").asExpression())
        binder.bindView(view, nextDivContainer, divView)

        verify(placeholderLoader, times(2)).applyPlaceholder(any(), anyOrNull(), any(), any(), any())
        verify(imageLoader).loadImage(eq(nextDivContainer.imageUrl.evaluate(ExpressionResolver.EMPTY).toString()), any<DivImageDownloadCallback>())
    }

    @Test
    fun `ignore high priority preview show when bitmap was already loaded`() {
        val (view, _) = createTestDiv("with_action.json")

        val divContainer = DivImage(
            imageUrl = Uri.parse("https://foo.bar/foo.png").asExpression(),
            highPriorityPreviewShow = true.asExpression()
        )
        binder.bindView(view, divContainer, divView)
        verify(placeholderLoader).applyPlaceholder(
            any(), anyOrNull(), any(),
            synchronous = eq(true), any()
        )

        whenImageLoaded(divContainer.imageUrl.evaluate(ExpressionResolver.EMPTY).toString())

        val nextDivContainer = DivImage(
            imageUrl = Uri.parse("https://foo.bar/bar.png").asExpression(),
            highPriorityPreviewShow = true.asExpression()
        )
        binder.bindView(view, nextDivContainer, divView)

        verify(placeholderLoader).applyPlaceholder(
            any(), anyOrNull(), any(),
            synchronous = eq(false), any()
        )
    }

    @Test
    fun `reset flag loaded image when imageUrl changed`() {
        val (view, _) = createTestDiv("with_action.json")

        val divContainer = DivImage(
            imageUrl = Uri.parse("https://foo.bar/foo.png").asExpression(),
            highPriorityPreviewShow = true.asExpression()
        )
        binder.bindView(view, divContainer, divView)

        whenImageLoaded(divContainer.imageUrl.evaluate(ExpressionResolver.EMPTY).toString())

        val spyView = spy(view)

        val nextDivContainer = DivImage(
            imageUrl = Uri.parse("https://foo.bar/bar.png").asExpression(),
            highPriorityPreviewShow = true.asExpression()
        )
        binder.bindView(spyView, nextDivContainer, divView)

        verify(spyView).resetImageLoaded()
    }

    @Test
    fun `not reset flag loaded image when imageUrl changed`() {
        val (view, _) = createTestDiv("with_action.json")

        val divContainer = DivImage(
            imageUrl = Uri.parse("https://foo.bar/foo.png").asExpression(),
            highPriorityPreviewShow = true.asExpression()
        )
        binder.bindView(view, divContainer, divView)

        whenImageLoaded(divContainer.imageUrl.evaluate(ExpressionResolver.EMPTY).toString())

        val spyView = spy(view)

        val nextDivContainer = DivImage(
            imageUrl = Uri.parse("https://foo.bar/foo.png").asExpression(),
            highPriorityPreviewShow = true.asExpression()
        )
        binder.bindView(spyView, nextDivContainer, divView)

        verify(spyView, never()).resetImageLoaded()
    }

    private fun whenImageLoaded(imageUrl: String) {
        val imageDownloadCallbackCaptor = argumentCaptor<DivImageDownloadCallback>()
        verify(placeholderLoader).applyPlaceholder(any(), anyOrNull(), any(), any(), any())
        verify(imageLoader).loadImage(eq(imageUrl), imageDownloadCallbackCaptor.capture())
        imageDownloadCallbackCaptor.firstValue.onSuccess(mock())
    }

    private fun createTestDiv(fileName: String): Pair<DivImageView, DivImage> {
        val div = UnitTestData(IMAGE_DIR, fileName).div
        val divContainer = div.value() as DivImage
        val view = viewCreator.create(div, ExpressionResolver.EMPTY) as DivImageView
        view.layoutParams = defaultLayoutParams()
        return view to divContainer
    }

    companion object {
        private const val IMAGE_DIR = "div-image"
    }
}
