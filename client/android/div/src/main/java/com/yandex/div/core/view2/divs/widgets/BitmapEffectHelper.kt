@file:Suppress("DEPRECATION")

package com.yandex.div.core.view2.divs.widgets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.HardwareRenderer
import android.graphics.Matrix
import android.graphics.PixelFormat
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.Shader
import android.hardware.HardwareBuffer
import android.media.ImageReader
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.DisplayMetrics
import androidx.annotation.Px
import androidx.annotation.RequiresApi
import androidx.core.graphics.scale
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.dagger.Names
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider


private const val RADIUS_MAX_VALUE_PX = 25f
private const val MAX_BLURRED_IMAGES = 1

@DivScope
internal class BitmapEffectHelper @Inject constructor(
    private val renderScript: Provider<RenderScript>
) {

    fun blurBitmap(
        bitmap: Bitmap,
        @Px radius: Float,
        isShadow: Boolean = false,
    ): Bitmap {
        if (radius == 0f) {
            return bitmap
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            blurBitmapHighApi(bitmap, radius, isShadow)
        } else {
            blurBitmapLowApi(bitmap, radius, isShadow)
        }
    }

    internal fun mirrorBitmap(bitmap: Bitmap): Bitmap {
        val mirrorMatrix = Matrix()
        mirrorMatrix.preScale(-1f, 1f)

        val result = Bitmap.createBitmap(
            /* source = */ bitmap,
            /* x = */ 0,
            /* y = */ 0,
            /* width = */ bitmap.width,
            /* height = */ bitmap.height,
            /* m = */ mirrorMatrix,
            /* filter = */ false
        )
        result.density = DisplayMetrics.DENSITY_DEFAULT
        return result
    }

    private fun blurBitmapLowApi(bitmap: Bitmap, radius: Float, isShadow: Boolean = false): Bitmap {
        var sampling = 1f
        val coercedRadius = if (radius > RADIUS_MAX_VALUE_PX) {
            sampling = radius * 1f / RADIUS_MAX_VALUE_PX
            RADIUS_MAX_VALUE_PX
        } else {
            radius
        }

        val result = if (sampling == 1f) {
            bitmap.copy(bitmap.config, /* isMutable = */ false)
        } else {
            Bitmap.createScaledBitmap(
                /* src = */ bitmap,
                /* dstWidth = */ (bitmap.width / sampling).toInt(),
                /* dstHeight = */ (bitmap.height / sampling).toInt(),
                /* filter = */ false
            )
        }

        val input = Allocation.createFromBitmap(renderScript.get(), result)
        val output = Allocation.createTyped(renderScript.get(), input.type)
        val element = if (isShadow) Element.A_8(renderScript.get()) else Element.U8_4(renderScript.get())
        ScriptIntrinsicBlur.create(renderScript.get(), element).apply {
            setRadius(coercedRadius)
            setInput(input)
            forEach(output)
        }
        output.copyTo(result)
        input.destroy()
        output.destroy()

        return if (!isShadow || sampling == 1f) {
            result
        } else {
            result.scale(bitmap.width, bitmap.height)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun blurBitmapHighApi(bitmap: Bitmap, radius: Float, isShadow: Boolean): Bitmap {
        val imageReader = ImageReader.newInstance(
            /* width = */ bitmap.width, /* height = */ bitmap.height,
            /* format = */ PixelFormat.RGBA_8888, /* maxImages = */ MAX_BLURRED_IMAGES,
            /* usage = */ HardwareBuffer.USAGE_GPU_SAMPLED_IMAGE or HardwareBuffer.USAGE_GPU_COLOR_OUTPUT
        )
        val renderNode = RenderNode("BlurEffect")
        val hardwareRenderer = HardwareRenderer()

        hardwareRenderer.setSurface(imageReader.surface)
        hardwareRenderer.setContentRoot(renderNode)
        renderNode.setPosition(0, 0, imageReader.width, imageReader.height)
        val coercedRadius = radius / 2
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
        val blurredBitmap = Bitmap.wrapHardwareBuffer(hardwareBuffer, null) ?: return bitmap

        hardwareBuffer.close()
        image.close()
        imageReader.close()
        renderNode.discardDisplayList()
        hardwareRenderer.destroy()
        return if (isShadow) blurredBitmap.copy(Bitmap.Config.ALPHA_8, true) else blurredBitmap
    }
}
