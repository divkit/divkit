package com.yandex.div.core.view2.divs

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Picture
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Build
import com.yandex.div.core.asExpression
import com.yandex.div.core.images.BitmapSource
import com.yandex.div.core.images.DivCachedImage
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.ImageRepresentation
import com.yandex.div.core.view2.DivPlaceholderLoader
import com.yandex.div.core.view2.animations.DivAnimationsEnabledController
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.core.toBlock
import com.yandex.div.internal.view.DivImageView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivBlendMode
import com.yandex.div2.DivBlur
import com.yandex.div2.DivFadeTransition
import com.yandex.div2.DivFilter
import com.yandex.div2.DivFilterRtlMirror
import com.yandex.div2.DivImage
import com.yandex.div2.DivImageScale
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
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
class DivImageBinderTest : DivBinderTest() {

    private val placeholderLoader = mock<DivPlaceholderLoader>()
    private val path = DivStatePath.fromState(0)
    private val animationsEnabledController = mock<DivAnimationsEnabledController> {
        on { isEnabled() } doReturn true
    }
    private val binder = DivImageBinder(baseBinder, imageLoader, placeholderLoader, animationsEnabledController)

    @Before
    fun setUp() {
        whenever(imageLoader.loadImage(any(), any<DivImageDownloadCallback>())).thenReturn(mock())
    }

    @Test
    fun `layout optimization is reset when identical data is rebound with flag disabled`() {
        val (view, divImage) = createTestDiv("with_action.json")
        val optimizedBinder = DivImageBinder(
            baseBinder, imageLoader, placeholderLoader, animationsEnabledController,
            imageLayoutOptimizationEnabled = true
        )

        optimizedBinder.bindView(view, divImage, divView)
        Assert.assertTrue(view.skipRedundantDrawableLayout)

        binder.bindView(view, divImage, divView)

        Assert.assertFalse(view.skipRedundantDrawableLayout)
    }

    @Test
    fun `url action applied`() {
        val (view, divImage) = createTestDiv("with_action.json")

        binder.bindView(view, divImage, divView)

        assertActionApplied(view, Expected.ACTION_URI, resolver, divView)
    }

    @Test
    fun `state action applied`() {
        val (view, divImage) = createTestDiv("with_set_state_action.json")

        binder.bindView(view, divImage, divView)

        assertActionApplied(view, Expected.STATE_ACTION_URI, resolver, divView)
    }

    @Test
    fun `bind image when image wasn't previously bound`() {
        val (view, _) = createTestDiv("with_action.json")
        val divImage = createTestDiv(preview = PREVIEW, highPriorityPreviewShow = true)

        binder.bindView(view, divImage, divView)

        verify(placeholderLoader).applyPlaceholder(any(), any(), anyOrNull(), any(), any(), any(), any())
        verify(imageLoader).loadImage(
            eq(divImage.divValue.imageUrl?.evaluate(ExpressionResolver.EMPTY).toString()),
            any<DivImageDownloadCallback>()
        )
    }

    @Test
    fun `do not rebind image when imageUrl did not change and bitmap was not loaded`() {
        val (view, _) = createTestDiv("with_action.json")
        val divImage = createTestDiv(preview = PREVIEW, highPriorityPreviewShow = true)

        binder.bindView(view, divImage, divView)

        val nextDivImage = createTestDiv(preview = PREVIEW, highPriorityPreviewShow = true)

        binder.bindView(view, nextDivImage, divView)

        verify(placeholderLoader).applyPlaceholder(any(), any(), anyOrNull(), any(), any(), any(), any())
        verify(imageLoader).loadImage(
            eq(divImage.divValue.imageUrl?.evaluate(ExpressionResolver.EMPTY).toString()),
            any<DivImageDownloadCallback>()
        )
    }

    @Test
    fun `do not bind image when imageUrl did not change and image was loaded`() {
        val (view, divImage) = createTestDiv("with_action.json")

        binder.bindView(view, divImage, divView)
        reset(placeholderLoader)

        whenImageLoaded(divImage.divValue.imageUrl?.evaluate(ExpressionResolver.EMPTY).toString())

        val (_, nextDivImage) = createTestDiv("with_action.json")
        binder.bindView(view, nextDivImage, divView)
        verifyNoMoreInteractions(placeholderLoader)
        verifyNoMoreInteractions(imageLoader)
    }

    @Test
    fun `do apply preview when imageUrl did not change but preview did changed`() {
        val (view, _) = createTestDiv("with_action.json")
        val divImage = createTestDiv(imageUrl = "empty://", preview = PREVIEW, highPriorityPreviewShow = true)

        binder.bindView(view, divImage, divView)

        val nextDivImage = createTestDiv(imageUrl = "empty://", preview = PREVIEW_B, highPriorityPreviewShow = true)

        binder.bindView(view, nextDivImage, divView)

        verify(placeholderLoader, times(2)).applyPlaceholder(any(), any(), anyOrNull(), any(), any(), any(), any())
        verify(imageLoader).loadImage(
            eq(divImage.divValue.imageUrl?.evaluate(ExpressionResolver.EMPTY).toString()),
            any<DivImageDownloadCallback>()
        )
    }

    @Test
    fun `do not apply preview when both imageUrl and preview have not changed`() {
        val (view, _) = createTestDiv("with_action.json")
        val divImage = createTestDiv(imageUrl = "empty://", preview = PREVIEW, highPriorityPreviewShow = true)



        val nextDivImage = createTestDiv(imageUrl = "empty://", preview = PREVIEW, highPriorityPreviewShow = true)

        binder.bindView(view, nextDivImage, divView)

        verify(placeholderLoader).applyPlaceholder(any(), any(), anyOrNull(), any(), any(), any(), any())
        verify(imageLoader).loadImage(
            eq(divImage.divValue.imageUrl?.evaluate(ExpressionResolver.EMPTY).toString()),
            any<DivImageDownloadCallback>()
        )
    }

    @Test
    fun `bind image when image was loaded but imageUrl changed`() {
        val (view, _) = createTestDiv("with_action.json")
        val divImage = createTestDiv(preview = PREVIEW, highPriorityPreviewShow = true)

        binder.bindView(view, divImage, divView)

        whenImageLoaded(divImage.divValue.imageUrl?.evaluate(ExpressionResolver.EMPTY).toString())

        val nextDivImage = createTestDiv()
        binder.bindView(view, nextDivImage, divView)

        verify(placeholderLoader).applyPlaceholder(any(), any(), anyOrNull(), any(), any(), any(), any())
        verify(imageLoader).loadImage(
            eq(nextDivImage.divValue.imageUrl?.evaluate(ExpressionResolver.EMPTY).toString()),
            any<DivImageDownloadCallback>()
        )
    }

    @Test
    fun `ignore high priority preview show when image was already loaded`() {
        val (view, _) = createTestDiv("with_action.json")
        val divImage = createTestDiv(preview = PREVIEW, highPriorityPreviewShow = true)

        binder.bindView(view, divImage, divView)

        verify(placeholderLoader)
            .applyPlaceholder(any(), any(), anyOrNull(), any(), synchronous = eq(true), any(), any())

        whenImageLoaded(divImage.divValue.imageUrl?.evaluate(ExpressionResolver.EMPTY).toString())

        val nextDivImage = createTestDiv(
            imageUrl = "https://foo.bar/bar.png",
            preview = PREVIEW,
            highPriorityPreviewShow = true
        )

        binder.bindView(view, nextDivImage, divView)

        verify(placeholderLoader)
            .applyPlaceholder(any(), any(), eq(PREVIEW), any(), synchronous = eq(false), any(), any())
    }

    @Test
    fun `reset flag loaded image when imageUrl changed`() {
        val (view, _) = createTestDiv("with_action.json")
        val divImage = createTestDiv(highPriorityPreviewShow = true)

        binder.bindView(view, divImage, divView)

        whenImageLoaded(divImage.divValue.imageUrl?.evaluate(ExpressionResolver.EMPTY).toString())

        val spyView = spy(view)

        val nextDivImage = createTestDiv(imageUrl = "https://foo.bar/bar.png", highPriorityPreviewShow = true)
        binder.bindView(spyView, nextDivImage, divView)

        verify(spyView).resetImageLoaded()
    }

    @Test
    fun `not reset flag loaded image when imageUrl changed`() {
        val (view, _) = createTestDiv("with_action.json")
        val divImage = createTestDiv(highPriorityPreviewShow = true)

        binder.bindView(view, divImage, divView)

        whenImageLoaded(divImage.divValue.imageUrl?.evaluate(ExpressionResolver.EMPTY).toString())

        val spyView = spy(view)

        val nextDivImage = createTestDiv(highPriorityPreviewShow = true)
        binder.bindView(spyView, nextDivImage, divView)

        verify(spyView, never()).resetImageLoaded()
    }

    @Test
    fun `tint color applied when drawable loaded`() {
        val (view, _) = createTestDiv("with_action.json")
        val divImage = createTestDiv(tintColor = "#ffffff")
        binder.bindView(view, divImage, divView)
        Assert.assertNull(view.colorFilter)
        whenDrawableLoaded(divImage)

        Assert.assertNotNull(view.colorFilter)
    }

    @Test
    fun `tint color applied to image after rebind`() {
        val (view, _) = createTestDiv("with_action.json")
        val divImage = createTestDiv(tintColor = "#ffffff")

        binder.bindView(view, divImage, divView)
        Assert.assertNull(view.colorFilter)
        whenImageLoaded(divImage.divValue.imageUrl?.evaluate(ExpressionResolver.EMPTY).toString())
        Assert.assertNotNull(view.colorFilter)

        // with the same image url
        val nextDivImage = createTestDiv(tintColor = "#ffffff")
        binder.bindView(view, nextDivImage, divView)
        Assert.assertNotNull(view.colorFilter)

        // with another image url
        val anotherDivImage = createTestDiv(imageUrl = "https://another_image.png", tintColor = "#ffffff")
        binder.bindView(view, anotherDivImage, divView)
        Assert.assertNull(view.colorFilter)
    }

    @Test
    fun `stale image load does not reapply tint after same url rebind`() {
        val (view, _) = createTestDiv("with_action.json")
        val divImage = createTestDiv(tintColor = "#ffffff")
        binder.bindView(view, divImage, divView)

        binder.bindView(view, createTestDiv(), divView)
        Assert.assertNull(view.colorFilter)

        whenDrawableLoaded(divImage)

        Assert.assertNull(view.colorFilter)
    }

    @Test
    fun `tint applied to drawable loaded after same url rebind`() {
        val (view, _) = createTestDiv("with_action.json")
        val divImage = createTestDiv(tintColor = "#ff0000", tintMode = DivBlendMode.MULTIPLY)
        binder.bindView(view, divImage, divView)
        val tinted = createTestDiv(tintColor = "#ff0000", tintMode = DivBlendMode.MULTIPLY)
        binder.bindView(view, tinted, divView)

        whenDrawableLoaded(divImage)

        val expected = PorterDuffColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY)
        Assert.assertEquals(expected, view.colorFilter)
    }

    @Test
    fun `current tint applied to bitmap loaded after same url rebind`() {
        val (view, _) = createTestDiv("with_action.json")
        val divImage = createTestDiv(tintColor = "#ff0000", tintMode = DivBlendMode.MULTIPLY)
        binder.bindView(view, divImage, divView)
        val tinted = createTestDiv(tintColor = "#0000ff", tintMode = DivBlendMode.SCREEN)
        binder.bindView(view, tinted, divView)

        whenImageLoaded(divImage.divValue.imageUrl?.evaluate(ExpressionResolver.EMPTY).toString())

        val expected = PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.SCREEN)
        Assert.assertEquals(expected, view.colorFilter)
    }

    @Test
    fun `current filters applied to bitmap loaded after same url rebind`() {
        val (view, _) = createTestDiv("with_action.json")
        val divImage = createTestDiv()
        binder.bindView(view, divImage, divView)
        val blurred = createTestDiv(filters = listOf(DivFilter.Blur(DivBlur(5L.asExpression()))))
        binder.bindView(view, blurred, divView)
        val bitmap = mock<Bitmap>()

        whenImageLoaded(
            divImage.divValue.imageUrl?.evaluate(ExpressionResolver.EMPTY).toString(),
            mock<BitmapDrawable> { on { this.bitmap } doReturn bitmap },
        )

        Assert.assertSame(bitmap, view.currentBitmapWithoutFilters)
    }

    @Test
    fun `current alpha applied to image loaded after same url rebind`() {
        val (view, _) = createTestDiv("with_action.json")
        val divImage = createTestDiv()
        binder.bindView(view, divImage, divView)
        binder.bindView(view, createTestDiv(alpha = 0.5), divView)

        whenDrawableLoaded(divImage)

        Assert.assertEquals(0.5f, view.alpha)
    }

    @Test
    fun `current appearance animation applied to image loaded after same url rebind`() {
        val (view, _) = createTestDiv("with_action.json")
        val divImage = createTestDiv()
        binder.bindView(view, divImage, divView)
        val faded = createTestDiv(appearanceAnimation = DivFadeTransition(0.2.asExpression()))
        binder.bindView(view, faded, divView)

        whenImageLoaded(
            divImage.divValue.imageUrl?.evaluate(ExpressionResolver.EMPTY).toString(),
            source = BitmapSource.NETWORK,
        )

        Assert.assertEquals(0.2f, view.alpha)
    }

    @Test
    fun `svg kept vector with tint of current div after same url rebind`() {
        val (view, _) = createTestDiv("with_action.json")
        val divImage = createTestDiv()
        binder.bindView(view, divImage, divView)
        val tinted = createTestDiv(tintColor = "#ff0000", tintMode = DivBlendMode.MULTIPLY)
        binder.bindView(view, tinted, divView)
        val picture = Picture().apply {
            beginRecording(10, 10)
            endRecording()
        }

        whenImageLoaded(
            divImage.divValue.imageUrl?.evaluate(ExpressionResolver.EMPTY).toString(),
            PictureDrawable(picture),
        )

        val expected = PorterDuffColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY)
        Assert.assertEquals(expected, view.drawable.colorFilter)
    }

    @Test
    @Config(sdk = [Build.VERSION_CODES.O_MR1])
    fun `tinted svg rasterized before android p`() {
        val (view, _) = createTestDiv("with_action.json")
        val divImage = createTestDiv(tintColor = "#ff0000")
        binder.bindView(view, divImage, divView)
        val picture = Picture().apply {
            beginRecording(10, 10)
            endRecording()
        }

        whenImageLoaded(
            divImage.divValue.imageUrl?.evaluate(ExpressionResolver.EMPTY).toString(),
            PictureDrawable(picture),
        )

        Assert.assertTrue(view.drawable is BitmapDrawable)
    }

    @Test
    fun `svg kept vector without tint of current div after same url rebind`() {
        val (view, _) = createTestDiv("with_action.json")
        val divImage = createTestDiv(tintColor = "#ff0000", tintMode = DivBlendMode.MULTIPLY)
        binder.bindView(view, divImage, divView)
        binder.bindView(view, createTestDiv(), divView)
        val picture = Picture().apply {
            beginRecording(10, 10)
            endRecording()
        }

        whenImageLoaded(
            divImage.divValue.imageUrl?.evaluate(ExpressionResolver.EMPTY).toString(),
            PictureDrawable(picture),
        )

        Assert.assertFalse(view.drawable is BitmapDrawable)
        Assert.assertNull(view.colorFilter)
    }

    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    fun `svg rasterized without tint when filters added on rebind`() {
        val (view, _) = createTestDiv("with_action.json")
        val divImage = createTestDiv(tintColor = "#ff0000")
        binder.bindView(view, divImage, divView)
        val picture = Picture().apply {
            beginRecording(10, 10).drawColor(Color.BLACK)
            endRecording()
        }
        whenImageLoaded(
            divImage.divValue.imageUrl?.evaluate(ExpressionResolver.EMPTY).toString(),
            PictureDrawable(picture),
        )

        val filtered = createTestDiv(tintColor = "#ff0000", filters = listOf(DivFilter.RtlMirror(DivFilterRtlMirror())))
        binder.bindView(view, filtered, divView)

        Assert.assertEquals(Color.BLACK, view.currentBitmapWithoutFilters?.getPixel(5, 5))
    }

    @Test
    @Config(qualifiers = "xxhdpi")
    fun `svg rasterized at picture size when filters added on rebind`() {
        val (view, _) = createTestDiv("with_action.json")
        val divImage = createTestDiv(tintColor = "#ff0000", scale = DivImageScale.NO_SCALE)
        binder.bindView(view, divImage, divView)
        val picture = Picture().apply {
            beginRecording(10, 10)
            endRecording()
        }
        whenImageLoaded(
            divImage.divValue.imageUrl?.evaluate(ExpressionResolver.EMPTY).toString(),
            PictureDrawable(picture),
        )

        val filtered = createTestDiv(
            tintColor = "#ff0000",
            scale = DivImageScale.NO_SCALE,
            filters = listOf(DivFilter.RtlMirror(DivFilterRtlMirror())),
        )
        binder.bindView(view, filtered, divView)

        Assert.assertEquals(10, view.currentBitmapWithoutFilters?.width)
    }

    @Test
    fun `tint color applied to drawable after loading`() {
        val (view, _) = createTestDiv("with_action.json")
        val divImage = createTestDiv(tintColor = "#ffffff")

        binder.bindView(view, divImage, divView)
        Assert.assertNull(view.colorFilter)
        whenDrawableLoaded(divImage.divValue.imageUrl?.evaluate(ExpressionResolver.EMPTY).toString())

        Assert.assertNotNull(view.colorFilter)
    }

    @Test
    fun `tint color applied to preview`() {
        val (view, _) = createTestDiv("with_action.json")
        val divImage = createTestDiv(preview = PREVIEW, tintColor = "#ffffff")

        binder.bindView(view, divImage, divView)
        Assert.assertNull(view.colorFilter)
        whenPreviewLoaded()
        Assert.assertNotNull(view.colorFilter)
    }

    @Test
    fun `tint color and mode applied to asynchronously loaded drawable`() {
        val (view, _) = createTestDiv("with_action.json")
        val divImage = createTestDiv(tintColor = "#ff0000", tintMode = DivBlendMode.MULTIPLY)

        binder.bindView(view, divImage, divView)
        Assert.assertNull(view.colorFilter)

        whenDrawableLoaded(divImage)

        Assert.assertEquals(PorterDuffColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY), view.colorFilter)
    }

    @Test
    fun `tint color and mode updated and cleared on drawable rebind`() {
        val (view, _) = createTestDiv("with_action.json")
        val divImage = createTestDiv(tintColor = "#ff0000", tintMode = DivBlendMode.MULTIPLY)
        binder.bindView(view, divImage, divView)
        whenDrawableLoaded(divImage)

        val nextDivImage = createTestDiv(tintColor = "#0000ff", tintMode = DivBlendMode.SCREEN)
        binder.bindView(view, nextDivImage, divView)
        Assert.assertEquals(PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.SCREEN), view.colorFilter)

        binder.bindView(view, createTestDiv(), divView)
        Assert.assertNull(view.colorFilter)
    }

    @Test
    fun `tint reapplied to asynchronously loaded drawable when image url changes`() {
        val (view, _) = createTestDiv("with_action.json")
        val divImage = createTestDiv(tintColor = "#ff0000", tintMode = DivBlendMode.MULTIPLY)
        binder.bindView(view, divImage, divView)
        whenDrawableLoaded(divImage)

        val nextDivImage = createTestDiv(
            imageUrl = "https://another_image.png",
            tintColor = "#ff0000",
            tintMode = DivBlendMode.MULTIPLY,
        )
        binder.bindView(view, nextDivImage, divView)
        Assert.assertNull(view.colorFilter)

        whenDrawableLoaded(nextDivImage)

        Assert.assertEquals(PorterDuffColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY), view.colorFilter)
    }

    private fun whenDrawableLoaded(divImage: DivBlock.Image) {
        whenImageLoaded(
            divImage.divValue.imageUrl?.evaluate(ExpressionResolver.EMPTY).toString(),
            ColorDrawable(Color.WHITE),
        )
    }

    private fun whenImageLoaded(
        imageUrl: String,
        drawable: Drawable = mock<BitmapDrawable> {
            on { bitmap } doReturn mock()
        },
        source: BitmapSource = BitmapSource.MEMORY,
    ) {
        val imageDownloadCallbackCaptor = argumentCaptor<DivImageDownloadCallback>()
        verify(imageLoader).loadImage(eq(imageUrl), imageDownloadCallbackCaptor.capture())
        val cachedImage = DivCachedImage.Drawable(drawable, source)
        imageDownloadCallbackCaptor.firstValue.onSuccess(cachedImage)
    }

    private fun whenDrawableLoaded(imageUrl: String) {
        val imageDownloadCallbackCaptor = argumentCaptor<DivImageDownloadCallback>()
        verify(imageLoader).loadImage(eq(imageUrl), imageDownloadCallbackCaptor.capture())
        val cachedImage = DivCachedImage.Drawable(ColorDrawable(Color.BLACK), BitmapSource.MEMORY)
        imageDownloadCallbackCaptor.firstValue.onSuccess(cachedImage)
    }

    private fun whenPreviewLoaded() {
        val previewSetCallbackCaptor = argumentCaptor<(ImageRepresentation?) -> Unit>()
        val bitmap = mock<Bitmap>()
        verify(placeholderLoader).applyPlaceholder(any(), any(), anyOrNull(), any(), any(), any(), previewSetCallbackCaptor.capture())
        previewSetCallbackCaptor.firstValue.invoke(ImageRepresentation.Bitmap(bitmap))
    }

    private fun createTestDiv(fileName: String): Pair<DivImageView, DivBlock.Image> {
        val div = UnitTestData(IMAGE_DIR, fileName).div.toBlock(resolver, path) as DivBlock.Image
        val view = viewCreator.create(div.div, ExpressionResolver.EMPTY) as DivImageView
        view.layoutParams = defaultLayoutParams()
        return view to div
    }

    private fun createTestDiv(
        imageUrl: String = "https://foo.bar/foo.png",
        preview: String? = null,
        highPriorityPreviewShow: Boolean = false,
        tintColor: String? = null,
        tintMode: DivBlendMode = DivBlendMode.SOURCE_IN,
        alpha: Double = 1.0,
        filters: List<DivFilter>? = null,
        appearanceAnimation: DivFadeTransition? = null,
        scale: DivImageScale = DivImageScale.FILL,
    ): DivBlock.Image {
        return Div.Image(DivImage(
            alpha = alpha.asExpression(),
            appearanceAnimation = appearanceAnimation,
            filters = filters,
            imageUrl = Uri.parse(imageUrl).asExpression(),
            preview = preview?.asExpression(),
            scale = scale.asExpression(),
            highPriorityPreviewShow = highPriorityPreviewShow.asExpression(),
            tintColor = tintColor?.let { Color.parseColor(it).asExpression() },
            tintMode = tintMode.asExpression(),
        )).toBlock(resolver, path) as DivBlock.Image
    }

    companion object {
        private const val IMAGE_DIR = "div-image"
        private const val PREVIEW = "iVBORw0KGgoAAAANSUhEUgAAAEEAAABACAYAAABFqxrgAAAACXBIWXMAABYlAAAWJQFJUiTwAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAIsSURBVHgB7ZqNbcIwEIXdTsAI6QRtN2CDMgIblA1CJ2g3gE5QOkEYgQ3MBnSDV1s2KjKOf0gVfOY+yUImF8Kdz+aeDiEYhmEYhmEYhjwAJmq0anRqHGA42Plc1I5ycnbieB9SjUbUiF39VHSgnkRN6JVFPjoQE5HJvSiXzvPemxoPdwr1+qzGp3NdB2AhakCntWeVZz22y//IhuJQTiwcxzYRe/fgnIoMSt0OjTPfRuy/I/cHoRKEGPTT/whMUfTuOQ+6yH1yyHYoBsSLor6DsXUNBTVg6oEOabSwlaG9z5c1K0EF/OmBvtWPlco+JKiUz+qLTnG+j0+dX9ogLZDOdcpmZKo7mBT+CjjSuSupPycQsCPyWgHIUnfq9RXh1J8FntXYYHQoRUojX93tAtc/QK3MxWXqzkcHwr/n0uOQPsgae12Ln3XAeZ0ZdJUehqk7zQrUFR5GVndjkSugGme+jdgPUndjkRuE3FQmkfq5Qdg585eI/aMz3wvqwJS7qQdj6xqKWoBf8WmHG3u9T91pJGpolvRkQy4dqDdLkKfuQqxIBwPp6k4LrXXErg0FAyX3IpGh7qztBuGAzT3PqK8XaZ2SKcFA7b1IxLeTRD40u08wQksmOpmiVpeCIjDnRezwvI1eZCAYt9OLVB33vRpzca4+t5Fbq+xF/og8BqV/qUFgtQpWqwZcrlbp9CJj4DK1KlHbX/lAoRc5Bii5FzkmKLEXyTAMwzAMw9wQvznV1eV5FJ3aAAAAAElFTkSuQmCC"
        private const val PREVIEW_B = "iVBORw0KGgoAAAANSUhEUgAAAEEAAABACAYAAABFqxrgAAAACXBIWXMAABYlAAAWJQFJUiTwAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAIsSURBVHgB7ZqNbcIwEIXdTsAI6QRtN2CDMgIblI02kA1CJ2g3gE5QOkEYgQ3MBnSDV1s2KjKOf0gVfOY+yUImF8Kdz+aeDiEYhmEYhmEYhjwAJmq0anRqHGA42Plc1I5ycnbieB9SjUbUiF39VHSgnkRN6JVFPjoQE5HJvSiXzvPemxoPdwr1+qzGp3NdB2AhakCntWeVZz22y//IhuJQTiwcxzYRe/fgnIoMSt0OjTPfRuy/I/cHoRKEGPTT/whMUfTuOQ+6yH1yyHYoBsSLor6DsXUNBTVg6oEOabSwlaG9z5c1K0EF/OmBvtWPlco+JKiUz+qLTnG+j0+dX9ogLZDOdcpmZKo7mBT+CjjSuSupPycQsCPyWgHIUnfq9RXh1J8FntXYYHQoRUojX93tAtc/QK3MxWXqzkcHwr/n0uOQPsgae12Ln3XAeZ0ZdJUehqk7zQrUFR5GVndjkSugGme+jdgPUndjkRuE3FQmkfq5Qdg585eI/aMz3wvqwJS7qQdj6xqKWoBf8WmHG3u9T91pJGpolvRkQy4dqDdLkKfuQqxIBwPp6k4LrXXErg0FAyX3IpGh7qztBuGAzT3PqK8XaZ2SKcFA7b1IxLeTRD40u08wQksmOpmiVpeCIjDnRezwvI1eZCAYt9OLVB33vRpzca4+t5Fbq+xF/og8BqV/qUFgtQpWqwZcrlbp9CJj4DK1KlHbX/lAoRc5Bii5FzkmKLEXyTAMwzAMw9wQvznV1eV5FJ3aAAAAAElFTkSuQmCC"
    }
}
