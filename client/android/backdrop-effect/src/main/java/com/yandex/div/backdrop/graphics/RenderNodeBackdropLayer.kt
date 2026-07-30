package com.yandex.div.backdrop.graphics

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.HardwareRenderer
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.Shader
import android.hardware.HardwareBuffer
import android.media.Image
import android.media.ImageReader
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.yandex.div.backdrop.util.getCoordinateOffset

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal class RenderNodeBackdropLayer(
    view: View
) : BackdropLayer(view) {

    private var cornerRadii: FloatArray = FloatArray(4)
    private var blurRadius: Float = DEFAULT_BLUR_RADIUS
    private var refractionHeight: Float = DEFAULT_REFRACTION_HEIGHT
    private var refractionStrength: Float = DEFAULT_REFRACTION_STRENGTH
    private var refractionChromaticAberration: Boolean = false
    private var colorBrightness: Float = DEFAULT_COLOR_BRIGHTNESS
    private var colorContrast: Float = DEFAULT_COLOR_CONTRAST
    private var colorSaturation: Float = DEFAULT_COLOR_SATURATION

    private val backdropRenderNode by lazy(LazyThreadSafetyMode.NONE) { RenderNode("backdrop") }
    private val hardwareRenderer by lazy(LazyThreadSafetyMode.NONE) { HardwareRenderer() }
    private var imageReader: ImageReader? = null
    private var backdropBitmap: Bitmap? = null

    override fun setCornerRadius(radius: Float) {
        cornerRadii.fill(radius)
    }

    override fun setCornerRadii(topLeft: Float, topRight: Float, bottomRight: Float, bottomLeft: Float) {
        cornerRadii[CORNER_TOP_LEFT] = topLeft
        cornerRadii[CORNER_TOP_RIGHT] = topRight
        cornerRadii[CORNER_BOTTOM_RIGHT] = bottomRight
        cornerRadii[CORNER_BOTTOM_LEFT] = bottomLeft
    }

    override fun setBlurEffect(radius: Float) {
        blurRadius = radius
    }

    override fun setRefractionEffect(height: Float, strength: Float, chromaticAberration: Boolean) {
        refractionHeight = height
        refractionStrength = strength
        refractionChromaticAberration = chromaticAberration
    }

    override fun setColorAdjustment(brightness: Float, contrast: Float, saturation: Float) {
        colorBrightness = brightness
        colorContrast = contrast
        colorSaturation = saturation
    }

    override fun capture(backdropView: View) {
        val width = view.width
        val height = view.height
        if (width <= 0 || height <= 0) return

        val imageReader = getImageReader(view) ?: return

        backdropRenderNode.setPosition(0, 0, width, height)
        val backdropCanvas = backdropRenderNode.beginRecording()
        try {
            val (dx, dy) = view.getCoordinateOffset(backdropView)
            backdropCanvas.translate(-dx.toFloat(), -dy.toFloat())
            backdropView.draw(backdropCanvas)
        } finally {
            backdropRenderNode.endRecording()
        }

        val adjustColors = colorBrightness != DEFAULT_COLOR_BRIGHTNESS ||
            colorContrast != DEFAULT_COLOR_CONTRAST ||
            colorSaturation != DEFAULT_COLOR_SATURATION
        val colorAdjustmentEffect = if (adjustColors) {
            RenderEffect.createColorFilterEffect(
                ColorFilters.colorControlColorFilter(colorBrightness, colorContrast, colorSaturation)
            )
        } else {
            null
        }

        val blurEffect = if (blurRadius > DEFAULT_BLUR_RADIUS) {
            RenderEffect.createBlurEffect(blurRadius, blurRadius, Shader.TileMode.CLAMP)
        } else {
            null
        }

        val refractionEffect = if (refractionHeight > DEFAULT_REFRACTION_HEIGHT) {
            val refractionShader = if (refractionChromaticAberration) {
                Shaders.roundedRectRefractionWithDispersion(
                    width = width.toFloat(),
                    height = height.toFloat(),
                    cornerRadii = cornerRadii.clone(),
                    refractionHeight = refractionHeight,
                    refractionAmount = refractionStrength,
                    chromaticAberration = true
                )
            } else {
                Shaders.roundedRectRefraction(
                    width = width.toFloat(),
                    height = height.toFloat(),
                    cornerRadii = cornerRadii.clone(),
                    refractionHeight = refractionHeight,
                    refractionAmount = refractionStrength
                )
            }
            RenderEffect.createRuntimeShaderEffect(refractionShader, "content")
        } else {
            null
        }

        val renderEffect = chain(refractionEffect, chain(blurEffect, colorAdjustmentEffect))
        backdropRenderNode.setRenderEffect(renderEffect)

        var image: Image? = null
        var hardwareBuffer: HardwareBuffer? = null
        try {
            val hardwareRenderer = this.hardwareRenderer.apply {
                setContentRoot(backdropRenderNode)
                setSurface(imageReader.surface)
                isOpaque = false
            }

            hardwareRenderer.createRenderRequest()
                .setWaitForPresent(true)
                .syncAndDraw()

            image = imageReader.acquireNextImage() ?: return
            hardwareBuffer = image.hardwareBuffer ?: return

            backdropBitmap = Bitmap.wrapHardwareBuffer(hardwareBuffer, null)
        } finally {
            hardwareRenderer.setSurface(null)
            hardwareBuffer?.close()
            image?.close()
        }
    }

    private fun getImageReader(view: View): ImageReader? {
        if (view.width == 0 || view.height == 0) return null

        val imageReader = imageReader ?: return createImageReader(view.width, view.height)

        if (imageReader.width != view.width || imageReader.height != view.height) {
            imageReader.close()
            return createImageReader(view.width, view.height)
        }

        return imageReader
    }

    private fun createImageReader(width: Int, height: Int): ImageReader {
        return ImageReader.newInstance(
            /* width = */ width,
            /* height = */ height,
            /* format = */ PixelFormat.RGBA_8888,
            /* maxImages = */ 1,
            /* usage = */ HardwareBuffer.USAGE_GPU_SAMPLED_IMAGE or HardwareBuffer.USAGE_GPU_COLOR_OUTPUT
        )
    }

    private fun chain(outer: RenderEffect?, inner: RenderEffect?): RenderEffect? {
        return when {
            outer != null && inner != null -> RenderEffect.createChainEffect(outer, inner)
            outer != null -> outer
            inner != null -> inner
            else -> null
        }
    }

    override fun draw(canvas: Canvas, paint: Paint) {
        backdropBitmap?.let { bitmap ->
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint)
        }
    }

    override fun recycle() {
        backdropRenderNode.discardDisplayList()
        backdropBitmap?.recycle()
        backdropBitmap = null
    }

    override fun close() {
        hardwareRenderer.destroy()
    }

    private companion object {
        const val DEFAULT_BLUR_RADIUS = 0.0f
        const val DEFAULT_REFRACTION_HEIGHT = 0.0f
        const val DEFAULT_REFRACTION_STRENGTH = 0.0f
        const val DEFAULT_COLOR_BRIGHTNESS = 0.0f
        const val DEFAULT_COLOR_CONTRAST = 1.0f
        const val DEFAULT_COLOR_SATURATION = 1.0f

        const val CORNER_TOP_LEFT = 0
        const val CORNER_TOP_RIGHT = 1
        const val CORNER_BOTTOM_RIGHT = 2
        const val CORNER_BOTTOM_LEFT = 3
    }
}
