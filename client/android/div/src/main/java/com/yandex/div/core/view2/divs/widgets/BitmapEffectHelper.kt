@file:Suppress("DEPRECATION")

package com.yandex.div.core.view2.divs.widgets

import android.graphics.Bitmap
import android.graphics.Matrix
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.DisplayMetrics
import androidx.annotation.Px
import com.yandex.div.core.dagger.DivScope
import javax.inject.Inject

private const val RADIUS_MAX_VALUE_PX = 25

@DivScope
internal class BitmapEffectHelper @Inject constructor(
    private val renderScript: RenderScript
) {

    fun blurBitmap(
        bitmap: Bitmap,
        @Px radius: Int
    ): Bitmap {
        if (radius == 0) {
            return bitmap
        }

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

        val input = Allocation.createFromBitmap(renderScript, result)
        val output = Allocation.createTyped(renderScript, input.type)
        ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript)).apply {
            setRadius(coercedRadius.toFloat())
            setInput(input)
            forEach(output)
        }
        output.copyTo(result)
        input.destroy()
        output.destroy()
        return result
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
}
