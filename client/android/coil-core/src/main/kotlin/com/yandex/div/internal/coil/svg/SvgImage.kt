package com.yandex.div.internal.coil.svg

import android.graphics.Canvas
import android.graphics.Picture
import coil3.Image
import java.lang.ref.WeakReference
import coil3.svg.SvgImage as CoilSvgImage

internal class SvgImage(
    private val image: CoilSvgImage,
    private val viewportWidth: Float,
    private val viewportHeight: Float,
    override val width: Int,
    override val height: Int,
) : Image by image {

    // A displayed drawable owns its Picture. Keep it reusable without retaining embedded
    // bitmaps for the lifetime of Coil's much smaller SVG cache entry.
    private var pictureRef: WeakReference<Picture>? = null

    @Synchronized
    fun asPicture(maxBitmapSize: Int): Picture {
        val scale = minOf(1.0, maxBitmapSize.toDouble() / maxOf(width, height))
        val pictureWidth = (width * scale).toInt().coerceAtLeast(1)
        val pictureHeight = (height * scale).toInt().coerceAtLeast(1)
        pictureRef?.get()?.let { picture ->
            if (picture.width == pictureWidth && picture.height == pictureHeight) {
                return picture
            }
        }

        val picture = Picture()
        val canvas = picture.beginRecording(pictureWidth, pictureHeight)
        try {
            canvas.scale(pictureWidth.toFloat() / width, pictureHeight.toFloat() / height)
            draw(canvas)
        } finally {
            picture.endRecording()
        }
        pictureRef = WeakReference(picture)
        return picture
    }

    override fun draw(canvas: Canvas) {
        val saveCount = canvas.save()
        try {
            canvas.scale(width / viewportWidth, height / viewportHeight)
            canvas.clipRect(0f, 0f, viewportWidth, viewportHeight)
            image.draw(canvas)
        } finally {
            canvas.restoreToCount(saveCount)
        }
    }
}
