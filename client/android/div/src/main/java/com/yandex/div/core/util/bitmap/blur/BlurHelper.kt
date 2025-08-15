package com.yandex.div.core.util.bitmap.blur

import android.graphics.Bitmap

internal interface BlurHelper {
    fun blurBitmap(
        bitmap: Bitmap,
        radius: Float,
    ): Bitmap

    /**
     * @param coercedRadius Due to the preprocessing required to draw the shadow,
     * takes adjusted size of the blur radius from [getCoercedBlurRadius] method.
     */
    fun blurShadow(
        bitmap: Bitmap,
        coercedRadius: Float,
    ): Bitmap

    /**
     * Because in old API ScriptIntrinsicBlur can't apply blur more then 25,
     * we downscale bitmap, blur it and upscale to previous size.
     */
    fun getBitmapScale(radius: Float): Float

    /**
     * The RenderScript used in the old API does not support a bluer value above 25 units.
     * This method returns the adjusted radius allowed by current API.
     */
    fun getCoercedBlurRadius(radius: Float): Float

    /**
     * In order to clear the renderer data when we do not plan to use the blur in the near future.
     * Warning: after calling release, the next bluer call will recreate the renderers,
     * which may take a considerable time.
     */
    fun release()
}
