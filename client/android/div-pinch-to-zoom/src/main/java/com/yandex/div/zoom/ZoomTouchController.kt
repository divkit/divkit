package com.yandex.div.zoom

import android.content.Context
import android.graphics.PointF
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import com.yandex.div.internal.widget.TransientView

internal class ZoomTouchController(
    private val configuration: DivPinchToZoomConfiguration,
    private val viewController: ZoomViewController
) {

    val isInZoom: Boolean
        get() = viewController.state != ZoomState.IDLE

    private val context: Context
        get() = configuration.context

    private val gestureDetector = ScaleGestureDetector(context, OnGestureListenerImpl())
    private val initialFocalPoint = PointF()
    private val focalPoint = PointF()

    private val tempPoint = PointF()
    private val tempIntArray = IntArray(2)

    fun startZoomTouch(sourceView: View, event: MotionEvent): Boolean {
        if (isInZoom) {
            return false
        }

        gestureDetector.onTouchEvent(event)
        event.focalPoint(initialFocalPoint)
        focalPoint.set(initialFocalPoint)

        sourceView.getLocationInWindow(tempIntArray)
        tempPoint.set(tempIntArray[INDEX_X].toFloat(), tempIntArray[INDEX_Y].toFloat())
        (sourceView as? TransientView)?.isTransient = true
        viewController.showImage(
            location = tempPoint,
            pivotPoint = initialFocalPoint,
            imageBitmap = sourceView.drawToBitmap()
        )
        (sourceView as? TransientView)?.isTransient = false

        sourceView.parent?.requestDisallowInterceptTouchEvent(true)
        sourceView.visibility = View.INVISIBLE

        return true
    }

    fun updateZoomTouch(event: MotionEvent) {
        gestureDetector.onTouchEvent(event)

        if (viewController.state != ZoomState.ACTIVE) {
            return
        }

        tempPoint.set(focalPoint)
        event.focalPoint(focalPoint)
        viewController.translateImageBy(focalPoint.x - tempPoint.x, focalPoint.y - tempPoint.y)
    }

    fun finishZoomTouch(sourceView: View, event: MotionEvent) {
        if (viewController.state != ZoomState.ACTIVE) {
            return
        }

        gestureDetector.onTouchEvent(event)
        viewController.hideImage {
            sourceView.visibility = View.VISIBLE
        }
    }

    private inner class OnGestureListenerImpl : ScaleGestureDetector.OnScaleGestureListener {


        override fun onScaleBegin(detector: ScaleGestureDetector) = true

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            if (viewController.state != ZoomState.ACTIVE) {
                return true
            }
            viewController.scaleImageBy(detector.scaleFactor)
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) = Unit
    }

    private companion object {
        private const val INDEX_X = 0
        private const val INDEX_Y = 1
    }
}
