package com.yandex.div.core.view2.drawable

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Picture
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class TintablePictureDrawableTest {

    private val canvasBitmap = Bitmap.createBitmap(30, 30, Bitmap.Config.ARGB_8888)

    private val drawable = TintablePictureDrawable(
        Picture().apply {
            beginRecording(10, 10).drawColor(Color.BLACK)
            endRecording()
        }
    ).apply {
        setBounds(0, 0, 10, 10)
    }

    @Test
    fun `picture tinted inside bounds`() {
        drawable.colorFilter = PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)

        drawable.draw(Canvas(canvasBitmap))

        assertEquals(Color.RED, canvasBitmap.getPixel(5, 5))
    }

    @Test
    fun `area outside bounds stays transparent when tint mode colors transparent pixels`() {
        drawable.colorFilter = PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SCREEN)

        drawable.draw(Canvas(canvasBitmap))

        assertEquals(Color.TRANSPARENT, canvasBitmap.getPixel(20, 20))
    }
}
