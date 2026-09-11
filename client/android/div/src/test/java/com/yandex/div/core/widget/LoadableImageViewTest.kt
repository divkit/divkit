package com.yandex.div.core.widget

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.ViewGroup.LayoutParams
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.internal.widget.AspectImageView.Scale
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.annotation.Config
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

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

    private class AnimatedDrawable : GradientDrawable(), Animatable {
        init {
            setSize(40, 20)
        }

        override fun start() = Unit
        override fun stop() = Unit
        override fun isRunning() = false
    }
}
