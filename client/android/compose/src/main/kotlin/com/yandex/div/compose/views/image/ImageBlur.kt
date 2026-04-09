package com.yandex.div.compose.views.image

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.core.graphics.scale
import coil3.transform.Transformation
import coil3.size.Size as CoilSize

/**
 * Coil Transformation that applies a Gaussian blur to the bitmap.
 * Uses RenderScript on API < 31 with iterative downscale+blur for large radii
 * (RenderScript max radius is 25).
 */
@Suppress("DEPRECATION")
internal class BlurTransformation(
    private val context: Context,
    private val radiusDp: Int,
    density: Float,
) : Transformation() {

    override val cacheKey: String = "blur_${radiusDp}_$density"

    val maxRadiusDp = 25f

    override suspend fun transform(input: Bitmap, size: CoilSize): Bitmap {
        if (radiusDp <= 0 || input.width <= 0 || input.height <= 0) {
            return input
        }

        val mutableBitmap = input.copy(Bitmap.Config.ARGB_8888, true) ?: return input

        if (radiusDp <= maxRadiusDp) {
            return applyRenderScriptBlur(mutableBitmap, radiusDp.toFloat())
        }

        val scaledBitmap = scaleBitmap(mutableBitmap)
        val blurredScaled = applyRenderScriptBlur(scaleBitmap(mutableBitmap), maxRadiusDp)

        val result = blurredScaled.scale(mutableBitmap.width, mutableBitmap.height)

        if (scaledBitmap !== blurredScaled) {
            scaledBitmap.recycle()
        }
        if (blurredScaled !== result) {
            blurredScaled.recycle()
        }
        mutableBitmap.recycle()

        return result
    }

    private fun applyRenderScriptBlur(bitmap: Bitmap, radius: Float): Bitmap {
        val renderScript = RenderScript.create(context)
        val input = Allocation.createFromBitmap(renderScript, bitmap)
        val output = Allocation.createTyped(renderScript, input.type)

        val scriptBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        scriptBlur.setRadius(radius.coerceIn(0.1f, 25f))
        scriptBlur.setInput(input)
        scriptBlur.forEach(output)

        output.copyTo(bitmap)

        input.destroy()
        output.destroy()
        scriptBlur.destroy()
        renderScript.destroy()

        return bitmap
    }

    private fun scaleBitmap(bitmap: Bitmap): Bitmap {
        val scaleFactor = maxRadiusDp / radiusDp
        val scaledWidth = (bitmap.width * scaleFactor).toInt().coerceAtLeast(1)
        val scaledHeight = (bitmap.height * scaleFactor).toInt().coerceAtLeast(1)
        return bitmap.scale(scaledWidth, scaledHeight)
    }
}
