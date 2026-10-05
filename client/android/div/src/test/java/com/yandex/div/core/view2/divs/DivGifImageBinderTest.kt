package com.yandex.div.core.view2.divs

import android.graphics.Bitmap
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import com.yandex.div.core.Disposable
import com.yandex.div.core.asExpression
import com.yandex.div.core.images.BitmapSource
import com.yandex.div.core.images.DivCachedImage
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.DivPlaceholderLoader
import com.yandex.div.core.view2.animations.DivAnimationsEnabledController
import com.yandex.div.core.view2.divs.widgets.DivGifImageView
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.core.toBlock
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivGifImage
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivGifImageBinderTest : DivBinderTest() {

    private val placeholderLoader = mock<DivPlaceholderLoader>()
    private val animationsEnabledController = mock<DivAnimationsEnabledController> {
        on { isEnabled() } doReturn false
        on { observe(any(), any()) } doReturn Disposable.NULL
    }
    private val binder = DivGifImageBinder(
        baseBinder,
        imageLoader,
        placeholderLoader,
        animationsEnabledController,
    )

    @Before
    fun setUp() {
        whenever(imageLoader.loadAnimatedImage(any(), any<DivImageDownloadCallback>())).thenReturn(mock())
    }

    @Test
    fun `animation is started when image is updated and animations are enabled`() {
        whenever(animationsEnabledController.isEnabled()).thenReturn(true)
        val view = DivGifImageView(context)
        val drawable = mock<AnimationDrawable>()

        binder.bindView(view, createTestDiv(), divView)
        view.setImage(drawable)

        verify(drawable).start()
    }

    @Test
    fun `animation is not started when image is updated and animations are disabled`() {
        val view = DivGifImageView(context)
        val drawable = mock<AnimationDrawable>()

        binder.bindView(view, createTestDiv(), divView)
        view.setImage(drawable)

        verify(drawable, never()).start()
    }

    @Test
    fun `animation is started when animations become enabled`() {
        val observer = argumentCaptor<() -> Unit>()
        val view = DivGifImageView(context)
        val drawable = mock<AnimationDrawable>()

        binder.bindView(view, createTestDiv(), divView)
        view.setImage(drawable)
        verify(animationsEnabledController).observe(eq(divView), observer.capture())

        verify(drawable, never()).start()

        whenever(animationsEnabledController.isEnabled()).thenReturn(true)
        observer.firstValue.invoke()

        verify(drawable).start()
    }

    @Test
    fun `running animation is stopped when animations become disabled`() {
        val observer = argumentCaptor<() -> Unit>()
        whenever(animationsEnabledController.isEnabled()).thenReturn(true)
        val view = DivGifImageView(context)
        val drawable = mock<AnimationDrawable> {
            on { isRunning } doReturn true
        }

        binder.bindView(view, createTestDiv(), divView)
        view.setImage(drawable)
        verify(animationsEnabledController).observe(eq(divView), observer.capture())

        whenever(animationsEnabledController.isEnabled()).thenReturn(false)
        observer.firstValue.invoke()

        verify(drawable).stop()
    }

    @Test
    fun `animated image replaces preview loaded from preview url`() {
        val previewCallback = argumentCaptor<DivImageDownloadCallback>()
        val gifCallback = argumentCaptor<DivImageDownloadCallback>()
        whenever(imageLoader.loadImage(any(), previewCallback.capture())).thenReturn(mock())
        whenever(imageLoader.loadAnimatedImage(any(), gifCallback.capture())).thenReturn(mock())
        val view = DivGifImageView(context)
        val gifDrawable = ColorDrawable()

        binder.bindView(view, createTestDiv(previewUrl = "https://foo.bar/preview.png"), divView)
        val preview = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        previewCallback.firstValue.onSuccess(DivCachedImage.Bitmap(preview, BitmapSource.NETWORK))
        gifCallback.firstValue.onSuccess(DivCachedImage.Drawable(gifDrawable, BitmapSource.NETWORK))

        assertSame(gifDrawable, view.drawable)
        assertTrue(view.isImageLoaded)
    }

    private fun createTestDiv(previewUrl: String? = null): DivBlock.GifImage {
        return Div.GifImage(
            DivGifImage(
                gifUrl = Uri.parse("https://foo.bar/animated.webp").asExpression(),
                previewUrl = previewUrl?.let { Uri.parse(it).asExpression() },
            ),
        ).toBlock(ExpressionResolver.EMPTY, DivStatePath.fromState(0)) as DivBlock.GifImage
    }
}
