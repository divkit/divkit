package com.yandex.div.core.view2.animations

import android.content.Context
import android.graphics.Rect
import android.view.View
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class TranslationYClipBoundsTest {

    private val context = RuntimeEnvironment.application
    private val view = ClipSavingView(context).apply {
        right = 200
        bottom = 100
    }
    private val underTest = VerticalTranslation.TranslationYClipBounds(view)

    @Test
    fun `appear from top`() {
        underTest.set(view, -1f)
        underTest.set(view, -0.5f)
        underTest.set(view, 0f)

        Assert.assertArrayEquals(arrayOf(
            Rect(0, 99, 200, 100),
            Rect(0, 49, 200, 100),
            Rect(0, 0, 200, 100)
        ), view.clipRects.toTypedArray())
    }

    @Test
    fun `appear from bottom`() {
        underTest.set(view, 1f)
        underTest.set(view, 0.5f)
        underTest.set(view, 0.0001f)
        underTest.set(view, 0f)

        Assert.assertArrayEquals(arrayOf(
            Rect(0, 0, 200, 1),
            Rect(0, 0, 200, 51),
            Rect(0, 0, 200, 100),
            Rect(0, 0, 200, 100)
        ), view.clipRects.toTypedArray())
    }

    @Test
    fun `disappear to top`() {
        underTest.set(view, 1f)
        underTest.set(view, 0.5f)
        underTest.set(view, 0.0001f)
        underTest.set(view, 0f)

        Assert.assertArrayEquals(arrayOf(
            Rect(0, 0, 200, 1),
            Rect(0, 0, 200, 51),
            Rect(0, 0, 200, 100),
            Rect(0, 0, 200, 100)
        ), view.clipRects.toTypedArray())
    }

    private class ClipSavingView(context: Context) : View(context) {
        val clipRects = mutableListOf<Rect>()

        override fun setClipBounds(clipBounds: Rect) {
            clipRects.add(Rect(clipBounds))
        }
    }
}

