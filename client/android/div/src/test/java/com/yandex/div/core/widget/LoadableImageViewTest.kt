package com.yandex.div.core.widget

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View.MeasureSpec
import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.internal.view.DivImageView
import com.yandex.div.internal.widget.AspectImageView.Scale
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div.internal.widget.FrameContainerLayout
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
@Config(qualifiers = "xxhdpi")
class LoadableImageViewTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    private val view = LoadableImageView(context).apply {
        layoutParams = LayoutParams(300, 300)
    }

    private val animation = AnimatedDrawable()

    @Test
    fun `animated drawable uses display density when scale is no scale`() {
        view.setImageDrawable(animation)

        assertEquals(120 to 60, view.drawable.intrinsicWidth to view.drawable.intrinsicHeight)
    }

    @Test
    fun `animated drawable uses display density when view wraps content`() {
        view.imageScale = Scale.FIT
        view.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

        view.setImageDrawable(animation)

        assertEquals(120 to 60, view.drawable.intrinsicWidth to view.drawable.intrinsicHeight)
    }

    @Test
    fun `animated drawable is unchanged when fitting fixed size view`() {
        view.imageScale = Scale.FIT

        view.setImageDrawable(animation)

        assertSame(animation, view.drawable)
    }

    @Test
    fun `animated drawable receives unscaled bounds when view lays out`() {
        view.setImageDrawable(animation)

        view.layout(0, 0, 300, 300)

        assertEquals(Rect(0, 0, 40, 20), animation.bounds)
    }

    @Test
    fun `animation invalidation reaches displayed drawable`() {
        view.setImageDrawable(animation)

        val callback = mock<Drawable.Callback>()
        view.drawable.callback = callback

        animation.invalidateSelf()

        verify(callback).invalidateDrawable(view.drawable)
    }

    @Test
    fun `fixed size image redraws without layout only when optimization is enabled`() = withMeasuredView { _, _ ->
        view.skipRedundantDrawableLayout = true
        view.setImageDrawable(image(80, 160))

        assertFalse(view.isLayoutRequested)
        view.draw(Canvas(Bitmap.createBitmap(200, 100, Bitmap.Config.ARGB_8888)))
        val matrix = FloatArray(9)
        view.imageMatrix.getValues(matrix)
        assertEquals(0.625f, matrix[Matrix.MSCALE_X])
        assertEquals(0.625f, matrix[Matrix.MSCALE_Y])

        view.skipRedundantDrawableLayout = false
        view.setImageDrawable(image(80, 80))

        assertTrue(view.isLayoutRequested)
    }

    @Test
    fun `aspect image skips drawable layout but forwards callback layout`() = withMeasuredView(
        aspectRatio = 2f,
        heightMode = MeasureSpec.AT_MOST
    ) { _, _ ->
        view.skipRedundantDrawableLayout = true
        view.setImageDrawable(image(80, 160))

        assertFalse(view.isLayoutRequested)
        assertEquals(100, view.measuredHeight)

        view.setImageChangeCallback { view.requestLayout() }
        view.setImageDrawable(image(80, 80))

        assertTrue(view.isLayoutRequested)
    }

    @Test
    fun `drawable dependent height still changes with optimization enabled`() = withMeasuredView(
        heightMode = MeasureSpec.AT_MOST
    ) { widthSpec, heightSpec ->
        assertEquals(200, view.measuredHeight)
        view.skipRedundantDrawableLayout = true
        view.setImageDrawable(image(80, 160))

        assertTrue(view.isLayoutRequested)
        view.measure(widthSpec, heightSpec)
        assertEquals(400, view.measuredHeight)
    }

    @Test
    @Config(sdk = [28, 35])
    fun `drawable change resizes parent after an exact second measure pass`() {
        val activity = Robolectric.buildActivity(Activity::class.java).setup().visible()
        try {
            val parent = FrameContainerLayout(activity.get())
            activity.get().setContentView(parent)
            val imageView = DivImageView(activity.get()).apply {
                imageScale = Scale.FIT
                setImageDrawable(image(80, 80))
            }
            parent.addView(
                imageView,
                DivLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            )
            val widthSpec = MeasureSpec.makeMeasureSpec(600, MeasureSpec.AT_MOST)
            val heightSpec = MeasureSpec.makeMeasureSpec(80, MeasureSpec.EXACTLY)
            parent.measure(widthSpec, heightSpec)
            parent.layout(0, 0, parent.measuredWidth, parent.measuredHeight)
            assertEquals(80, parent.measuredWidth)
            assertEquals(80, imageView.measuredWidth)
            assertTrue(imageView.isAttachedToWindow)
            assertFalse(parent.isLayoutRequested)

            imageView.skipRedundantDrawableLayout = true
            imageView.setImageDrawable(image(160, 80))

            assertTrue(parent.isLayoutRequested)
            parent.measure(widthSpec, heightSpec)
            assertEquals(160, parent.measuredWidth)
            assertEquals(160, imageView.measuredWidth)
        } finally {
            activity.pause().stop().destroy()
        }
    }

    @Test
    fun `exact remeasure does not hide a cached drawable dependent height`() = withMeasuredView(
        heightMode = MeasureSpec.AT_MOST
    ) { widthSpec, heightSpec ->
        view.measure(widthSpec, MeasureSpec.makeMeasureSpec(100, MeasureSpec.EXACTLY))
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        assertEquals(100, view.measuredHeight)
        view.skipRedundantDrawableLayout = true

        view.setImageDrawable(image(80, 160))

        assertTrue(view.isLayoutRequested)
        view.measure(widthSpec, heightSpec)
        assertEquals(400, view.measuredHeight)
    }

    private fun withMeasuredView(
        aspectRatio: Float = 0f,
        heightMode: Int = MeasureSpec.EXACTLY,
        block: (Int, Int) -> Unit
    ) {
        val activity = Robolectric.buildActivity(Activity::class.java).setup().visible()
        try {
            val parent = FrameLayout(activity.get())
            activity.get().setContentView(parent)
            parent.addView(view)
            view.aspectRatio = aspectRatio
            view.imageScale = Scale.FIT
            view.adjustViewBounds = true
            view.setImageDrawable(image(80, 80))
            val widthSpec = MeasureSpec.makeMeasureSpec(200, MeasureSpec.EXACTLY)
            val heightSpec = MeasureSpec.makeMeasureSpec(if (heightMode == MeasureSpec.EXACTLY) 100 else 600, heightMode)
            view.measure(widthSpec, heightSpec)
            view.layout(0, 0, view.measuredWidth, view.measuredHeight)
            assertTrue(view.isAttachedToWindow)
            assertFalse(view.isLayoutRequested)
            block(widthSpec, heightSpec)
        } finally {
            activity.pause().stop().destroy()
        }
    }

    private fun image(width: Int, height: Int) = GradientDrawable().apply {
        setSize(width, height)
        setColor(Color.GREEN)
    }

    private class AnimatedDrawable : GradientDrawable(), Animatable {
        init {
            setSize(40, 20)
        }

        override fun start() = Unit
        override fun stop() = Unit
        override fun isRunning() = false
    }
}
