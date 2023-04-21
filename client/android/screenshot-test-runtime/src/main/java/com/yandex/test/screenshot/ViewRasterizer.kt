package com.yandex.test.screenshot

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.view.PixelCopy
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi
import kotlinx.coroutines.android.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal object ViewRasterizer {

    private const val EMPTY_VIEW_WIDTH = 192
    private const val EMPTY_VIEW_HEIGHT = 48
    private const val EMPTY_VIEW_TEXT = "<empty view>"
    private const val EMPTY_VIEW_LINESPACING_MULTIPLIER = 1.0f
    private const val EMPTY_VIEW_LINESPACING_ADDITION = 0.0f

    private val textPaint = TextPaint().apply {
        textSize = 32.0f
    }

    @Suppress("DEPRECATION")
    private val emptyViewTextLayout = StaticLayout(
        EMPTY_VIEW_TEXT,
        textPaint,
        EMPTY_VIEW_WIDTH,
        Layout.Alignment.ALIGN_NORMAL,
        EMPTY_VIEW_LINESPACING_MULTIPLIER,
        EMPTY_VIEW_LINESPACING_ADDITION,
        true
    )

    private val backgroundThread = HandlerThread("ViewRasterizer").apply {
        start()
    }

    private val backgroundLooper: Looper
        get() = backgroundThread.looper

    fun rasterize(view: View): Bitmap {
        if (!view.isRenderable) return emptyViewBitmap()

        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun pixelCopy(window: Window, view: View): Bitmap {
        if (!view.isRenderable) return emptyViewBitmap()

        val viewRect = view.rectInWindow().apply {
            val elevationInset = -view.elevation.toInt()
            inset(elevationInset, elevationInset)
        }
        val bitmap = Bitmap.createBitmap(viewRect.width(), viewRect.height(), Bitmap.Config.ARGB_8888)
        val handler = Handler(backgroundLooper)

        return runBlocking(handler.asCoroutineDispatcher()) {
            suspendCoroutine { continuation ->
                PixelKopy.request(window, viewRect, bitmap, handler) { copyResult ->
                    if (copyResult == PixelCopy.SUCCESS) {
                        continuation.resume(bitmap)
                    } else {
                        continuation.resumeWithException(RuntimeException("Can not take pixel copy of $view"))
                    }
                }
            }
        }
    }

    private fun emptyViewBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(EMPTY_VIEW_WIDTH, EMPTY_VIEW_HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        emptyViewTextLayout.draw(canvas)
        return bitmap
    }

    // https://issuetracker.google.com/issues/189446951#comment8
    private val View.isRenderable
        get() = !isLayoutRequested && width > 0 && height > 0

    private fun View.rectInWindow(): Rect {
        val locationInWindow = IntArray(2).apply {
            getLocationInWindow(this)
        }
        return Rect(0, 0, width, height).apply {
            offsetTo(locationInWindow[0], locationInWindow[1])
        }
    }
}
