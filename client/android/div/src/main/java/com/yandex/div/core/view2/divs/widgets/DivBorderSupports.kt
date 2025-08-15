package com.yandex.div.core.view2.divs.widgets

import android.graphics.Canvas
import android.view.View
import com.yandex.div.core.view2.BindingContext
import com.yandex.div2.DivBorder

internal interface DivBorderSupports {

    var needClipping: Boolean

    fun getDivBorderDrawer(): DivBorderDrawer?

    // This code is pretty hot since it's executed during binding for most of divs, so avoid creating
    // DivBorderDrawer or reuse it if possible.
    fun setBorder(bindingContext: BindingContext, border: DivBorder?, view: View)

    fun onBoundsChanged(width: Int, height: Int) {
        getDivBorderDrawer()?.onBoundsChanged(width, height)
    }

    fun releaseBorderDrawer() {
        getDivBorderDrawer()?.release()
    }

    fun invalidateBorder() {
        getDivBorderDrawer()?.invalidateBorder()
    }
}

internal inline fun DivBorderSupports.drawBorderClipped(
    canvas: Canvas,
    translationX: Int = 0,
    translationY: Int = 0,
    callback: (Canvas) -> Unit
) {
    val borderDrawer = getDivBorderDrawer()
    if (borderDrawer == null) {
        callback(canvas)
    } else if (translationX == 0 && translationY == 0) {
        borderDrawer.drawClipped(canvas, callback)
    } else {
        borderDrawer.drawClippedAndTranslated(canvas, translationX, translationY, callback)
    }
}
