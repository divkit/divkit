package com.yandex.div.zoom

import android.graphics.Matrix
import android.graphics.PointF
import androidx.annotation.FloatRange

internal class ZoomModel {

    private val location = PointF()
    private val pivotPoint = PointF()
    private val matrix = Matrix()

    private var scale = MIN_SCALE
    private var translateX = 0.0f
    private var translateY = 0.0f

    fun prepare(location: PointF, pivotPoint: PointF) {
        this.location.set(location)
        this.pivotPoint.set(pivotPoint)

        scale = MIN_SCALE
        translateX = 0.0f
        translateY = 0.0f
    }

    fun scaleBy(scaleFactor: Float) {
        scale = (scale * scaleFactor).coerceIn(MIN_SCALE, MAX_SCALE)
    }

    fun translateBy(dx: Float, dy: Float) {
        translateX += dx
        translateY += dy
    }

    fun scale(@FloatRange(from = 0.0, to = 1.0) progress: Float = 1.0f): Float {
        if (progress == 1.0f) {
            return scale
        }
        return MIN_SCALE + (scale - MIN_SCALE) * progress
    }

    fun transformMatrix(@FloatRange(from = 0.0, to = 1.0) progress: Float = 1.0f): Matrix {
        val scaleProgress = scale(progress)
        val translateSensitivity = ((scaleProgress - MIN_SCALE) / (PAN_SCALE - MIN_SCALE)).coerceAtMost(1.0f)

        matrix.setScale(scaleProgress, scaleProgress, pivotPoint.x, pivotPoint.y)
        matrix.postTranslate(
            location.x + translateX * translateSensitivity * progress,
            location.y + translateY * translateSensitivity * progress
        )
        return matrix
    }

    private companion object {
        private const val MIN_SCALE = 1.0f
        private const val MAX_SCALE = 4.0f
        private const val PAN_SCALE = 2.0f
    }
}
