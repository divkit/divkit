package com.yandex.div.core.view2.divs

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import com.yandex.div.core.asExpression
import com.yandex.div.core.images.CachedBitmap
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.util.ImageRepresentation
import com.yandex.div.core.view2.DivPlaceholderLoader
import com.yandex.div.core.view2.divs.widgets.DivImageView
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivImage
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.reset
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivImageBinderTest : DivBinderTest() {

    private val placeholderLoader = mock<DivPlaceholderLoader>()
    private val errorCollector = mock<ErrorCollectors> {
        on { getOrCreate(anyOrNull(), anyOrNull()) } doReturn mock()
    }
    private val binder = DivImageBinder(
            baseBinder, imageLoader, placeholderLoader, errorCollector
    )

    @Before
    fun setUp() {
        whenever(imageLoader.loadImage(any(), any<DivImageDownloadCallback>())).thenReturn(mock())
    }

    @Test
    fun `url action applied`() {
        val (view, divImage) = createTestDiv("with_action.json")

        binder.bindView(bindingContext, view, divImage)

        assertActionApplied(bindingContext, view, Expected.ACTION_URI)
    }

    @Test
    fun `state action applied`() {
        val (view, divImage) = createTestDiv("with_set_state_action.json")

        binder.bindView(bindingContext, view, divImage)

        assertActionApplied(bindingContext, view, Expected.STATE_ACTION_URI)
    }

    @Test
    fun `bind image when image wasn't previously bound`() {
        val (view, _) = createTestDiv("with_action.json")

        val divImage = DivImage(
            imageUrl = Uri.parse("https://foo.bar/foo.png").asExpression(),
            preview = PREVIEW.asExpression(),
            highPriorityPreviewShow = true.asExpression()
        )

        binder.bindView(bindingContext, view, divImage)

        verify(placeholderLoader).applyPlaceholder(any(), any(), anyOrNull(), any(), any(), any(), any())
        verify(imageLoader).loadImage(eq(divImage.imageUrl.evaluate(ExpressionResolver.EMPTY).toString()), any<DivImageDownloadCallback>())
    }

    @Test
    fun `do not rebind image when imageUrl did not change and bitmap was not loaded`() {
        val (view, _) = createTestDiv("with_action.json")

        val divImage = DivImage(
            imageUrl = Uri.parse("https://foo.bar/foo.png").asExpression(),
            preview = PREVIEW.asExpression(),
            highPriorityPreviewShow = true.asExpression()
        )

        binder.bindView(bindingContext, view, divImage)

        val nextDivImage = DivImage(
            imageUrl = Uri.parse("https://foo.bar/foo.png").asExpression(),
            preview = PREVIEW.asExpression(),
            highPriorityPreviewShow = true.asExpression()
        )

        binder.bindView(bindingContext, view, nextDivImage)

        verify(placeholderLoader).applyPlaceholder(any(), any(), anyOrNull(), any(), any(), any(), any())
        verify(imageLoader).loadImage(eq(divImage.imageUrl.evaluate(ExpressionResolver.EMPTY).toString()), any<DivImageDownloadCallback>())
    }

    @Test
    fun `do not bind image when imageUrl did not change and bitmap was loaded`() {
        val (view, divImage) = createTestDiv("with_action.json")

        binder.bindView(bindingContext, view, divImage)
        reset(placeholderLoader)

        whenImageLoaded(divImage.imageUrl.evaluate(ExpressionResolver.EMPTY).toString())

        val (_, nextDivImage) = createTestDiv("with_action.json")
        binder.bindView(bindingContext, view, nextDivImage)
        verifyNoMoreInteractions(placeholderLoader)
        verifyNoMoreInteractions(imageLoader)
    }

    @Test
    fun `do apply preview when imageUrl did not change but preview did changed`() {
        val (view, _) = createTestDiv("with_action.json")

        val divImage = DivImage(
            imageUrl = Uri.parse("empty://").asExpression(),
            preview = PREVIEW.asExpression(),
            highPriorityPreviewShow = true.asExpression()
        )

        binder.bindView(bindingContext, view, divImage)

        val nextDivImage = DivImage(
            imageUrl = Uri.parse("empty://").asExpression(),
            preview = PREVIEW_B.asExpression(),
            highPriorityPreviewShow = true.asExpression()
        )

        binder.bindView(bindingContext, view, nextDivImage)

        verify(placeholderLoader, times(2)).applyPlaceholder(any(), any(), anyOrNull(), any(), any(), any(), any())
        verify(imageLoader).loadImage(eq(divImage.imageUrl.evaluate(ExpressionResolver.EMPTY).toString()), any<DivImageDownloadCallback>())
    }

    @Test
    fun `do not apply preview when both imageUrl and preview did not not change`() {
        val (view, _) = createTestDiv("with_action.json")

        val divImage = DivImage(
            imageUrl = Uri.parse("empty://").asExpression(),
            preview = PREVIEW.asExpression(),
            highPriorityPreviewShow = true.asExpression()
        )

        binder.bindView(bindingContext, view, divImage)

        val nextDivImage = DivImage(
            imageUrl = Uri.parse("empty://").asExpression(),
            preview = PREVIEW.asExpression(),
            highPriorityPreviewShow = true.asExpression()
        )

        binder.bindView(bindingContext, view, nextDivImage)

        verify(placeholderLoader).applyPlaceholder(any(), any(), anyOrNull(), any(), any(), any(), any())
        verify(imageLoader).loadImage(eq(divImage.imageUrl.evaluate(ExpressionResolver.EMPTY).toString()), any<DivImageDownloadCallback>())
    }

        @Test
    fun `bind image when bitmap was loaded but imageUrl changed`() {
        val (view, _) = createTestDiv("with_action.json")

        val divImage = DivImage(
            imageUrl = Uri.parse("https://foo.bar/foo.png").asExpression(),
            preview = PREVIEW.asExpression(),
            highPriorityPreviewShow = true.asExpression()
        )

        binder.bindView(bindingContext, view, divImage)

        whenImageLoaded(divImage.imageUrl.evaluate(ExpressionResolver.EMPTY).toString())

        val nextDivImage = DivImage(imageUrl = Uri.parse("https://foo.bar/foo.png").asExpression())
        binder.bindView(bindingContext, view, nextDivImage)

        verify(placeholderLoader).applyPlaceholder(any(), any(), anyOrNull(), any(), any(), any(), any())
        verify(imageLoader).loadImage(eq(nextDivImage.imageUrl.evaluate(ExpressionResolver.EMPTY).toString()), any<DivImageDownloadCallback>())
    }

    @Test
    fun `ignore high priority preview show when bitmap was already loaded`() {
        val (view, _) = createTestDiv("with_action.json")

        val divImage = DivImage(
                imageUrl = Uri.parse("https://foo.bar/foo.png").asExpression(),
                preview = PREVIEW.asExpression(),
                highPriorityPreviewShow = true.asExpression()
        )
        binder.bindView(bindingContext, view, divImage)
        verify(placeholderLoader).applyPlaceholder(
                any(), any(), anyOrNull(), any(),
                synchronous = eq(true), any(), any()
        )

        whenImageLoaded(divImage.imageUrl.evaluate(ExpressionResolver.EMPTY).toString())

        val nextDivImage = DivImage(
                imageUrl = Uri.parse("https://foo.bar/bar.png").asExpression(),
                preview = PREVIEW.asExpression(),
                highPriorityPreviewShow = true.asExpression()
        )
        binder.bindView(bindingContext, view, nextDivImage)

        verify(placeholderLoader).applyPlaceholder(
                any(), any(), eq(PREVIEW), any(),
                synchronous = eq(false), any(), any()
        )
    }

    @Test
    fun `reset flag loaded image when imageUrl changed`() {
        val (view, _) = createTestDiv("with_action.json")

        val divImage = DivImage(
                imageUrl = Uri.parse("https://foo.bar/foo.png").asExpression(),
                highPriorityPreviewShow = true.asExpression()
        )
        binder.bindView(bindingContext, view, divImage)

        whenImageLoaded(divImage.imageUrl.evaluate(ExpressionResolver.EMPTY).toString())

        val spyView = spy(view)

        val nextDivImage = DivImage(
                imageUrl = Uri.parse("https://foo.bar/bar.png").asExpression(),
                highPriorityPreviewShow = true.asExpression()
        )
        binder.bindView(bindingContext, spyView, nextDivImage)

        verify(spyView).resetImageLoaded()
    }

    @Test
    fun `not reset flag loaded image when imageUrl changed`() {
        val (view, _) = createTestDiv("with_action.json")

        val divImage = DivImage(
                imageUrl = Uri.parse("https://foo.bar/foo.png").asExpression(),
                highPriorityPreviewShow = true.asExpression()
        )
        binder.bindView(bindingContext, view, divImage)

        whenImageLoaded(divImage.imageUrl.evaluate(ExpressionResolver.EMPTY).toString())

        val spyView = spy(view)

        val nextDivImage = DivImage(
                imageUrl = Uri.parse("https://foo.bar/foo.png").asExpression(),
                highPriorityPreviewShow = true.asExpression()
        )
        binder.bindView(bindingContext, spyView, nextDivImage)

        verify(spyView, never()).resetImageLoaded()
    }

    @Test
    fun `tint color applied to image after rebind`() {
        val (view, _) = createTestDiv("with_action.json")
        val divImage = DivImage(
                imageUrl = Uri.parse("https://foo.bar/foo.png").asExpression(),
                tintColor = Color.parseColor("#ffffff").asExpression())

        binder.bindView(bindingContext, view, divImage)
        Assert.assertNull(view.colorFilter)
        whenImageLoaded(divImage.imageUrl.evaluate(ExpressionResolver.EMPTY).toString())
        Assert.assertNotNull(view.colorFilter)

        // with the same image url
        val nextDivImage = DivImage(
                imageUrl = Uri.parse("https://foo.bar/foo.png").asExpression(),
                tintColor = Color.parseColor("#ffffff").asExpression())
        binder.bindView(bindingContext, view, nextDivImage)
        Assert.assertNotNull(view.colorFilter)

        // with another image url
        val anotherDivImage = DivImage(
                imageUrl = Uri.parse("https://another_image.png").asExpression(),
                tintColor = Color.parseColor("#ffffff").asExpression())
        binder.bindView(bindingContext, view, anotherDivImage)
        Assert.assertNull(view.colorFilter)
    }

    @Test
    fun `tint color applied to preview`() {
        val (view, _) = createTestDiv("with_action.json")
        val divImage = DivImage(
                imageUrl = Uri.parse("https://foo.bar/foo.png").asExpression(),
                preview = PREVIEW.asExpression(),
                tintColor = Color.parseColor("#ffffff").asExpression())

        binder.bindView(bindingContext, view, divImage)
        Assert.assertNull(view.colorFilter)
        whenPreviewLoaded()
        Assert.assertNotNull(view.colorFilter)
    }

    private fun whenImageLoaded(imageUrl: String) {
        val imageDownloadCallbackCaptor = argumentCaptor<DivImageDownloadCallback>()
        verify(imageLoader).loadImage(eq(imageUrl), imageDownloadCallbackCaptor.capture())
        val cachedBitmap = mock<CachedBitmap> {
            on { bitmap } doReturn mock()
        }
        imageDownloadCallbackCaptor.firstValue.onSuccess(cachedBitmap)
    }

    private fun whenPreviewLoaded() {
        val previewSetCallbackCaptor = argumentCaptor<(ImageRepresentation?) -> Unit>()
        val bitmap = mock<Bitmap>()
        verify(placeholderLoader).applyPlaceholder(any(), any(), anyOrNull(), any(), any(), any(), previewSetCallbackCaptor.capture())
        previewSetCallbackCaptor.firstValue.invoke(ImageRepresentation.Bitmap(bitmap))
    }

    private fun createTestDiv(fileName: String): Pair<DivImageView, DivImage> {
        val div = UnitTestData(IMAGE_DIR, fileName).div
        val divImage = div.value() as DivImage
        val view = viewCreator.create(div, ExpressionResolver.EMPTY) as DivImageView
        view.layoutParams = defaultLayoutParams()
        return view to divImage
    }

    companion object {
        private const val IMAGE_DIR = "div-image"
        private const val PREVIEW = "iVBORw0KGgoAAAANSUhEUgAAAEEAAABACAYAAABFqxrgAAAACXBIWXMAABYlAAAWJQFJUiTwAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAIsSURBVHgB7ZqNbcIwEIXdTsAI6QRtN2CDMgIblA1CJ2g3gE5QOkEYgQ3MBnSDV1s2KjKOf0gVfOY+yUImF8Kdz+aeDiEYhmEYhmEYhjwAJmq0anRqHGA42Plc1I5ycnbieB9SjUbUiF39VHSgnkRN6JVFPjoQE5HJvSiXzvPemxoPdwr1+qzGp3NdB2AhakCntWeVZz22y//IhuJQTiwcxzYRe/fgnIoMSt0OjTPfRuy/I/cHoRKEGPTT/whMUfTuOQ+6yH1yyHYoBsSLor6DsXUNBTVg6oEOabSwlaG9z5c1K0EF/OmBvtWPlco+JKiUz+qLTnG+j0+dX9ogLZDOdcpmZKo7mBT+CjjSuSupPycQsCPyWgHIUnfq9RXh1J8FntXYYHQoRUojX93tAtc/QK3MxWXqzkcHwr/n0uOQPsgae12Ln3XAeZ0ZdJUehqk7zQrUFR5GVndjkSugGme+jdgPUndjkRuE3FQmkfq5Qdg585eI/aMz3wvqwJS7qQdj6xqKWoBf8WmHG3u9T91pJGpolvRkQy4dqDdLkKfuQqxIBwPp6k4LrXXErg0FAyX3IpGh7qztBuGAzT3PqK8XaZ2SKcFA7b1IxLeTRD40u08wQksmOpmiVpeCIjDnRezwvI1eZCAYt9OLVB33vRpzca4+t5Fbq+xF/og8BqV/qUFgtQpWqwZcrlbp9CJj4DK1KlHbX/lAoRc5Bii5FzkmKLEXyTAMwzAMw9wQvznV1eV5FJ3aAAAAAElFTkSuQmCC"
        private const val PREVIEW_B = "iVBORw0KGgoAAAANSUhEUgAAAEEAAABACAYAAABFqxrgAAAACXBIWXMAABYlAAAWJQFJUiTwAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAIsSURBVHgB7ZqNbcIwEIXdTsAI6QRtN2CDMgIblI02kA1CJ2g3gE5QOkEYgQ3MBnSDV1s2KjKOf0gVfOY+yUImF8Kdz+aeDiEYhmEYhmEYhjwAJmq0anRqHGA42Plc1I5ycnbieB9SjUbUiF39VHSgnkRN6JVFPjoQE5HJvSiXzvPemxoPdwr1+qzGp3NdB2AhakCntWeVZz22y//IhuJQTiwcxzYRe/fgnIoMSt0OjTPfRuy/I/cHoRKEGPTT/whMUfTuOQ+6yH1yyHYoBsSLor6DsXUNBTVg6oEOabSwlaG9z5c1K0EF/OmBvtWPlco+JKiUz+qLTnG+j0+dX9ogLZDOdcpmZKo7mBT+CjjSuSupPycQsCPyWgHIUnfq9RXh1J8FntXYYHQoRUojX93tAtc/QK3MxWXqzkcHwr/n0uOQPsgae12Ln3XAeZ0ZdJUehqk7zQrUFR5GVndjkSugGme+jdgPUndjkRuE3FQmkfq5Qdg585eI/aMz3wvqwJS7qQdj6xqKWoBf8WmHG3u9T91pJGpolvRkQy4dqDdLkKfuQqxIBwPp6k4LrXXErg0FAyX3IpGh7qztBuGAzT3PqK8XaZ2SKcFA7b1IxLeTRD40u08wQksmOpmiVpeCIjDnRezwvI1eZCAYt9OLVB33vRpzca4+t5Fbq+xF/og8BqV/qUFgtQpWqwZcrlbp9CJj4DK1KlHbX/lAoRc5Bii5FzkmKLEXyTAMwzAMw9wQvznV1eV5FJ3aAAAAAElFTkSuQmCC"
    }
}
