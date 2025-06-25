package com.yandex.div.core.util.bitmap.blur

import android.graphics.Bitmap
import android.graphics.HardwareRenderer
import android.graphics.PixelFormat
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.Shader
import android.hardware.HardwareBuffer
import android.media.ImageReader
import android.os.Build
import androidx.annotation.RequiresApi
import com.yandex.div.core.util.bitmap.BitmapEffectHelper
import com.yandex.div.core.util.bitmap.blur.BlurUtils.isBlurParamsValid

@RequiresApi(Build.VERSION_CODES.S)
internal class HighApiBitmapEffectHelper : BitmapEffectHelper() {
    private var cachedHardwareRenderer: HardwareRenderer? = null
    private fun getOrCreateHardwareRenderer(): HardwareRenderer {
        return cachedHardwareRenderer ?: HardwareRenderer().also {
            cachedHardwareRenderer = it
        }
    }

    private var cachedRenderNode: RenderNode? = null
    private fun getOrCreateRenderNode(): RenderNode {
        return cachedRenderNode ?: RenderNode("BlurEffect").also {
            cachedRenderNode = it
        }
    }

    override fun blurBitmap(
        bitmap: Bitmap,
        radius: Float
    ): Bitmap {
        if (!isBlurParamsValid(bitmap, radius)) return bitmap

        return blur(bitmap, radius.toFloat(), isShadow = false)
    }

    override fun blurShadow(
        bitmap: Bitmap,
        coercedRadius: Float
    ): Bitmap {
        if (!isBlurParamsValid(bitmap, coercedRadius)) return bitmap

        return blur(bitmap, coercedRadius, isShadow = true)
    }

    override fun getBitmapScale(radius: Float) = 1f

    override fun getCoercedBlurRadius(radius: Float) = radius

    override fun release() {
        cachedRenderNode?.discardDisplayList()
        cachedRenderNode = null

        cachedHardwareRenderer?.destroy()
        cachedHardwareRenderer = null
    }

    private fun blur(bitmap: Bitmap, radius: Float, isShadow: Boolean): Bitmap {
        val hardwareRenderer = getOrCreateHardwareRenderer()
        val renderNode = getOrCreateRenderNode()

        val imageReader = ImageReader.newInstance(
            /* width = */ bitmap.width, /* height = */ bitmap.height,
            /* format = */ PixelFormat.RGBA_8888, /* maxImages = */ MAX_BLURRED_IMAGES,
            /* usage = */ HardwareBuffer.USAGE_GPU_SAMPLED_IMAGE or HardwareBuffer.USAGE_GPU_COLOR_OUTPUT
        )

        hardwareRenderer.setSurface(imageReader.surface)
        hardwareRenderer.setContentRoot(renderNode)
        renderNode.setPosition(0, 0, imageReader.width, imageReader.height)

        // RenderEffect makes shadows darker and images more blurred than RenderScript with
        // the same blur radius value. Here we artificially lower it in order to achieve the same
        // blur effect as in the old APIs. The value selected experimentally.
        val coercedRadius = radius / BLUR_COMPATIBILITY_DIVIDER
        val treatment = if (isShadow) Shader.TileMode.DECAL else Shader.TileMode.MIRROR
        val blurRenderEffect = RenderEffect.createBlurEffect(
            /* radiusX = */ coercedRadius,
            /* radiusY = */ coercedRadius,
            /* edgeTreatment = */ treatment)

        renderNode.setRenderEffect(blurRenderEffect)

        val renderCanvas = renderNode.beginRecording()
        renderCanvas.drawBitmap(bitmap, 0f, 0f, null)
        renderNode.endRecording()

        hardwareRenderer.createRenderRequest()
            .setWaitForPresent(true)
            .syncAndDraw()

        val image = imageReader.acquireNextImage() ?: return bitmap
        val hardwareBuffer = image.hardwareBuffer ?: return bitmap

        val result = try {
            val wrapped = Bitmap.wrapHardwareBuffer(hardwareBuffer, null) ?: return bitmap

            when {
                isShadow && wrapped.config != Bitmap.Config.ALPHA_8 -> {
                    wrapped.copy(Bitmap.Config.ALPHA_8, false)
                }
                wrapped.config != bitmap.config -> {
                    wrapped.copy(bitmap.config, false)
                }
                else -> {
                    wrapped.copy(wrapped.config, false)
                }
            }.also {
                wrapped.recycle()
            }
        } finally {
            hardwareBuffer.close()
            image.close()
        }

        return result
    }

    private companion object {
        const val MAX_BLURRED_IMAGES = 1
        const val BLUR_COMPATIBILITY_DIVIDER = 1.5f
    }
}
