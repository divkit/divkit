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
import android.util.Log
import android.view.PixelCopy
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.core.graphics.createBitmap
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.yandex.test.util.performOnMain
import kotlinx.coroutines.android.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal object ViewRasterizer {

    private const val TAG = "ViewRasterizer"
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

    private val uiDevice by lazy {
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    fun rasterize(view: View): Bitmap {
        if (!view.isRenderable) {
            return emptyViewBitmap(view)
        }

        val bitmap = createBitmap(view.width, view.height)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun pixelCopy(window: Window, view: View): Bitmap {
        if (!view.isRenderable) {
            return emptyViewBitmap(view)
        }

        val viewRect = view.captureRect(onScreen = false)
        val bitmap = createBitmap(viewRect.width(), viewRect.height())
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

    fun screenCopy(view: View): Bitmap {
        val rect = performOnMain { view.takeIf { it.isRenderable }?.captureRect(onScreen = true) }
            ?: return performOnMain { emptyViewBitmap(view) }
        val screen = checkNotNull(uiDevice.takeScreenshot()) { "Can not take screenshot of $view" }
        check(rect.intersect(0, 0, screen.width, screen.height)) { "$view is outside the screen" }
        // UiAutomation may return a hardware bitmap.
        val bitmap = if (screen.isHardware) screen.copy(Bitmap.Config.ARGB_8888, false) else screen
        return Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height())
    }

    private fun emptyViewBitmap(view: View): Bitmap {
        Log.w(
            TAG,
            "View is not renderable: width=${view.width}, height=${view.height}, " +
                "isLayoutRequested=${view.isLayoutRequested}, " +
                "isAttachedToWindow=${view.isAttachedToWindow}"
        )
        val bitmap = createBitmap(EMPTY_VIEW_WIDTH, EMPTY_VIEW_HEIGHT)
        val canvas = Canvas(bitmap)
        emptyViewTextLayout.draw(canvas)
        return bitmap
    }

    // https://issuetracker.google.com/issues/189446951#comment8
    private val View.isRenderable
        get() = !isLayoutRequested && width > 0 && height > 0

    private val Bitmap.isHardware
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && config == Bitmap.Config.HARDWARE

    private fun View.captureRect(onScreen: Boolean): Rect {
        val location = IntArray(2).apply {
            if (onScreen) getLocationOnScreen(this) else getLocationInWindow(this)
        }
        val elevationInset = -elevation.toInt()
        return Rect(0, 0, width, height).apply {
            offsetTo(location[0], location[1])
            inset(elevationInset, elevationInset)
        }
    }
}
