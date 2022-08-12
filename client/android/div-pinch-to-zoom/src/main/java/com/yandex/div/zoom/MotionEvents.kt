package com.yandex.div.zoom

import android.graphics.PointF
import android.view.MotionEvent

internal fun MotionEvent.focalPoint(outPoint: PointF) {
    val isPointerUpAction = actionMasked == MotionEvent.ACTION_POINTER_UP
    val activePointerCount = if (isPointerUpAction) pointerCount - 1 else pointerCount
    var sumX = 0.0f;
    var sumY = 0.0f;

    for (index in 0 until pointerCount) {
        if (isPointerUpAction && actionIndex == index) {
            continue
        }
        sumX += getX(index)
        sumY += getY(index)
    }

    outPoint.set(sumX / activePointerCount, sumY / activePointerCount)
}
