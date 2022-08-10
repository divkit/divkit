package com.yandex.div.zoom

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.Window
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import androidx.annotation.ColorInt

class DivPinchToZoomConfiguration private constructor(
    internal val context: Context,
    internal val imageHost: ImageHost,
    internal val dimColor: Int,
    internal val animationInterpolator: Interpolator
) {

   class Builder(private val activity: Activity) {

       private var host: ImageHost? = null
       private var dimColor: Int = Color.TRANSPARENT
       private var animationInterpolator: Interpolator? = null

       fun host(window: Window): Builder {
           host = WindowImageHost(window)
           return this
       }

       fun dimColor(@ColorInt color: Int): Builder {
           dimColor = color
           return this
       }

       fun animationInterpolator(interpolator: Interpolator): Builder {
           animationInterpolator = interpolator
           return this
       }

       fun build(): DivPinchToZoomConfiguration {
            return DivPinchToZoomConfiguration(
                context = activity,
                imageHost = host ?: WindowImageHost(activity.window),
                dimColor = dimColor,
                animationInterpolator = animationInterpolator ?: AccelerateDecelerateInterpolator()
            )
       }
   }
}
