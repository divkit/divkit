package com.yandex.div.zoom

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.yandex.div.internal.KLog
import kotlin.math.abs

internal class ZoomTouchListener(
    private val touchController: ZoomTouchController,
    private val baseTouchListener: View.OnTouchListener?,
) : View.OnTouchListener {

    private val viewLocation = IntArray(2)
    private val rootLocation = IntArray(2)

    private var lastUnconsumedDownEvent: DownEvent? = null
    private var skipDownEvent = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        KLog.d(TAG) { "onTouch(event = $event)" }

        when (event.actionMasked) {

            MotionEvent.ACTION_DOWN -> {
                if (skipDownEvent) {
                    skipDownEvent = false
                    return false
                }

                lastUnconsumedDownEvent =
                    if (view.handleTouchEvent(event)) null else DownEvent(event.downTime, event.x, event.y)
                return true
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                when {
                    event.pointerCount < 2 -> view.handleTouchEvent(event)
                    touchController.isInZoom -> Unit
                    else -> {
                        view.sendCancelEvent(event)
                        touchController.startZoomTouch(view, event)
                    }
                }
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (touchController.isInZoom) {
                    touchController.updateZoomTouch(event)
                    return true
                }
                return view.tryDispatchGesture(event)
            }

            MotionEvent.ACTION_POINTER_UP -> {
                when {
                    !touchController.isInZoom -> return view.tryDispatchGesture(event)
                    event.pointerCount <= 2 -> touchController.finishZoomTouch(view, event)
                }
                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (touchController.isInZoom) {
                    touchController.finishZoomTouch(view, event)
                    return true
                }
                return view.tryDispatchGesture(event)
            }
        }

        return false
    }

    private fun View.sendCancelEvent(event: MotionEvent) {
        val cancelEvent = MotionEvent.obtain(event)
        cancelEvent.action = MotionEvent.ACTION_CANCEL
        handleTouchEvent(cancelEvent)
        cancelEvent.recycle()
    }

    private fun View.tryDispatchGesture(event: MotionEvent): Boolean {
        val down = lastUnconsumedDownEvent ?: return handleTouchEvent(event)

        if (skipMoveEvent(event, down)) return handleTouchEvent(event)

        lastUnconsumedDownEvent = null
        skipDownEvent = true

        val rootView = rootView
        getLocationInWindow(viewLocation)
        rootView.getLocationInWindow(rootLocation)
        val dx = offset(0)
        val dy = offset(1)

        rootView.dispatchEvent(
            downTime = down.time,
            eventTime = down.time,
            action = MotionEvent.ACTION_DOWN,
            x = down.x + dx,
            y = down.y + dy,
        )
        rootView.dispatchEvent(
            downTime = down.time,
            eventTime = event.eventTime,
            action = event.action,
            x = event.x + dx,
            y = event.y + dy,
        )
        return true
    }

    private fun View.handleTouchEvent(event: MotionEvent) =
        baseTouchListener?.onTouch(this, event) == true || onTouchEvent(event)

    private fun View.skipMoveEvent(event: MotionEvent, down: DownEvent): Boolean {
        if (event.actionMasked != MotionEvent.ACTION_MOVE) return false

        val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
        return abs(event.x - down.x) < touchSlop && abs(event.y - down.y) < touchSlop
    }

    private fun View.dispatchEvent(downTime: Long, eventTime: Long, action: Int, x: Float, y: Float) {
        val event = MotionEvent.obtain(downTime, eventTime, action, x, y, 0)
        dispatchTouchEvent(event)
        try {
            event.recycle()
        } catch (_: IllegalStateException) {
            // ignore
        }
    }

    private fun offset(index: Int) = (viewLocation[index] - rootLocation[index]).toFloat()

    private class DownEvent(val time: Long, val x: Float, val y: Float)

    companion object {
        private const val TAG = "ZoomTouchListener"
    }
}
