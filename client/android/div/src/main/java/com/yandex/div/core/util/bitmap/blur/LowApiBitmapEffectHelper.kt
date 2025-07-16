@file:Suppress("DEPRECATION")

package com.yandex.div.core.util.bitmap.blur

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import com.yandex.div.core.util.bitmap.BitmapEffectHelper
import com.yandex.div.core.util.bitmap.blur.BlurUtils.isBlurParamsValid

internal class LowApiBitmapEffectHelper(
    private val context: Context,
) : BitmapEffectHelper() {
    private var cachedRenderScript: RenderScript? = null
    private fun getOrCreateRenderScript(): RenderScript {
        return cachedRenderScript ?: run {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                return RenderScript.create(context)
            }
            return RenderScript.createMultiContext(
                context,
                RenderScript.ContextType.NORMAL,
                RenderScript.CREATE_FLAG_NONE,
                context.applicationInfo.targetSdkVersion,
            )
        }.also {
            cachedRenderScript = it
        }
    }

    override fun blurBitmap(
        bitmap: Bitmap,
        radius: Float
    ): Bitmap {
        if (!isBlurParamsValid(bitmap, radius)) return bitmap

        val renderScript = getOrCreateRenderScript()

        var sampling = 1f
        val coercedRadius = if (radius > MAX_BLUR) {
            sampling = radius * 1f / MAX_BLUR
            MAX_BLUR
        } else {
            radius
        }

        val result = if (sampling == 1f) {
            bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, /* isMutable = */ false)
        } else {
            bitmap.scale(
                width = (bitmap.width / sampling).toInt(),
                height = (bitmap.height / sampling).toInt(),
                filter = false,
            )
        }

        val input = Allocation.createFromBitmap(renderScript, result)
        val output = Allocation.createTyped(renderScript, input.type)
        val blur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript)).apply {
            setRadius(coercedRadius)
            setInput(input)
            forEach(output)
        }
        output.copyTo(result)
        input.destroy()
        output.destroy()
        blur.destroy()
        return result
    }

    override fun blurShadow(
        bitmap: Bitmap,
        coercedRadius: Float
    ): Bitmap {
        if (!isBlurParamsValid(bitmap, coercedRadius)) return bitmap

        val outBitmap = createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ALPHA_8)

        val renderScript = getOrCreateRenderScript()
        val scriptIntrinsicBlur = ScriptIntrinsicBlur
            .create(renderScript, Element.A_8(renderScript))

        val inAllocation = Allocation.createFromBitmap(renderScript, bitmap)
        val outAllocation = Allocation.createFromBitmap(renderScript, outBitmap)

        scriptIntrinsicBlur.apply {
            setRadius(coercedRadius)
            setInput(inAllocation)
            forEach(outAllocation)
        }

        outAllocation.copyTo(outBitmap)
        outAllocation.destroy()
        inAllocation.destroy()
        scriptIntrinsicBlur.destroy()

        return outBitmap
    }

    override fun getBitmapScale(radius: Float) = if (radius <= MAX_BLUR) 1f else MAX_BLUR / radius

    override fun getCoercedBlurRadius(radius: Float) = radius.coerceIn(MIN_BLUR, MAX_BLUR)

    override fun release() {
        cachedRenderScript?.destroy()
        cachedRenderScript = null
    }

    private companion object {
        /**
         * The minimum value, that ScriptIntrinsicBlur can use
         **/
        private const val MIN_BLUR = 1f
        /**
         * The maximum value, that ScriptIntrinsicBlur can use
         **/
        private const val MAX_BLUR = 25f
    }
}
