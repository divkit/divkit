package com.yandex.div.zoom

import android.view.MotionEvent
import android.view.View
import com.yandex.div.internal.KLog

internal class ZoomTouchListener(
    private val touchController: ZoomTouchController
) : View.OnTouchListener {

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        KLog.d(TAG) { "onTouch(event = $event)" }

        when (event.actionMasked) {

            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount >= 2 && !touchController.isInZoom) {
                    view.sendCancelEvent(event)
                    return touchController.startZoomTouch(view, event)
                }
                view.onTouchEvent(event)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (touchController.isInZoom) {
                    touchController.updateZoomTouch(event)
                    return true
                }
                return view.onTouchEvent(event)
            }

            MotionEvent.ACTION_POINTER_UP -> {
                if (event.pointerCount <= 2 && touchController.isInZoom) {
                    touchController.finishZoomTouch(view, event)
                    return true
                }
                return view.onTouchEvent(event)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (touchController.isInZoom) {
                    touchController.finishZoomTouch(view, event)
                    return true
                }
                return view.onTouchEvent(event)
            }
        }

        return false
    }

    private fun View.sendCancelEvent(event: MotionEvent) {
        val cancelEvent = MotionEvent.obtain(event)
        cancelEvent.action = MotionEvent.ACTION_CANCEL
        onTouchEvent(cancelEvent)
        cancelEvent.recycle()
    }

    companion object {
        private const val TAG = "ZoomTouchListener"
    }
}
