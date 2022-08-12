package com.yandex.div.zoom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import android.view.ViewGroup.LayoutParams
import android.view.animation.Interpolator
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.animation.doOnEnd
import com.yandex.div.core.util.KAssert
import com.yandex.div.util.ArgbEvaluatorCompat

internal class ZoomViewController(
    private val configuration: DivPinchToZoomConfiguration
) {

    var state = ZoomState.IDLE

    private val context: Context
        get() = configuration.context

    private val imageHost: ImageHost
        get() = configuration.imageHost

    private val animationInterpolator: Interpolator
        get() = configuration.animationInterpolator

    private val dimColor: Int
        get() = configuration.dimColor

    private val zoomView: ImageView by lazy {
        AppCompatImageView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            scaleType = ImageView.ScaleType.MATRIX
        }
    }

    private val argbEvaluator = ArgbEvaluatorCompat()
    private val zoomModel = ZoomModel()

    private var dimFactor: Float = 0.0f
        set(value) {
            field = value.coerceIn(0.0f, 1.0f)
            zoomView.setBackgroundColor(argbEvaluator.evaluate(field, Color.TRANSPARENT, dimColor))
        }

    fun showImage(location: PointF, pivotPoint: PointF, imageBitmap: Bitmap) {
        if (state != ZoomState.IDLE) {
            KAssert.fail { "Unable to show image in $state state" }
            return
        }

        zoomModel.prepare(location, pivotPoint)
        zoomView.setImageBitmap(imageBitmap)
        zoomView.imageMatrix = zoomModel.transformMatrix()
        imageHost.addImage(zoomView)
        dimFactor = 0.0f

        state = ZoomState.ACTIVE
    }

    fun translateImageBy(dx: Float, dy: Float) {
        if (state != ZoomState.ACTIVE) {
            KAssert.fail { "Unable to translate image in $state state" }
            return
        }

        zoomModel.translateBy(dx, dy)
        zoomView.imageMatrix = zoomModel.transformMatrix()
    }

    fun scaleImageBy(scaleFactor: Float) {
        if (state != ZoomState.ACTIVE) {
            KAssert.fail { "Unable to scale image in $state state" }
            return
        }

        zoomModel.scaleBy(scaleFactor)
        zoomView.imageMatrix = zoomModel.transformMatrix()
        dimFactor = (zoomModel.scale() - MIN_DIM_SCALE) / (MAX_DIM_SCALE - MIN_DIM_SCALE)
    }

    fun hideImage(animated: Boolean = true, callback: () -> Unit) {
        if (state != ZoomState.ACTIVE) {
            KAssert.fail { "Unable to hide image in $state state" }
            return
        }

        if (animated) {
            state = ZoomState.EXITING
            ValueAnimator.ofFloat(1.0f, 0.0f).apply {
                duration = ANIMATION_DURATION
                interpolator = animationInterpolator
                addUpdateListener {
                    val progress = animatedValue as Float
                    zoomView.imageMatrix = zoomModel.transformMatrix(progress)
                    dimFactor = (zoomModel.scale(progress) - MIN_DIM_SCALE) / (MAX_DIM_SCALE - MIN_DIM_SCALE)
                }
                doOnEnd {
                    exit()
                    callback()
                }
            }.start()
        } else {
            exit()
            callback()
        }
    }

    private fun exit() {
        imageHost.removeImage(zoomView)
        zoomView.setImageDrawable(null)
        zoomView.setBackgroundColor(Color.TRANSPARENT)
        zoomView.imageMatrix = null
        state = ZoomState.IDLE
    }

    private companion object {
        private const val MIN_DIM_SCALE = 1.0f
        private const val MAX_DIM_SCALE = 2.0f
        private const val ANIMATION_DURATION = 200L
    }
}
