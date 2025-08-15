package com.yandex.div.core.widget

import android.graphics.drawable.Drawable
import android.view.View
import com.yandex.div.core.annotations.PublicApi

@PublicApi
interface DivViewDelegate {

    fun unscheduleDrawable(who: Drawable?)

    /**
     * @return drawable to invalidate
     */
    fun invalidateDrawable(dr: Drawable): Drawable
    /**
     * @return true if there's no need to call super.buildDrawingCache()
     */
    fun onVisibilityChanged(changedView: View, visibility: Int): Boolean
    fun onAttachedToWindow()
    fun onDetachedFromWindow()
    fun buildDrawingCache(autoScale: Boolean)
}