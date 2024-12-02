package com.yandex.div.core.view2.divs.widgets

import android.graphics.Canvas
import android.view.View
import com.yandex.div.core.view2.BindingContext
import com.yandex.div2.DivBorder

internal interface DivBorderSupports {

    var isDrawing: Boolean

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

internal inline fun DivBorderSupports.drawBorderClipped(canvas: Canvas, callback: (Canvas) -> Unit) {
    isDrawing = true
    getDivBorderDrawer()?.drawClipped(canvas, callback) ?: callback(canvas)
    isDrawing = false
}

internal inline fun DivBorderSupports.dispatchDrawBorderClipped(canvas: Canvas, callback: (Canvas) -> Unit) {
    if (isDrawing) {
        callback(canvas)
    } else {
        getDivBorderDrawer()?.drawClipped(canvas, callback) ?: callback(canvas)
    }
}

internal inline fun DivBorderSupports.drawBorderClippedAndTranslated(
    canvas: Canvas,
    translationX: Int,
    translationY: Int,
    callback: (Canvas) -> Unit
) {
    isDrawing = true
    getDivBorderDrawer()?.drawClippedAndTranslated(canvas, translationX, translationY, callback)
        ?: callback(canvas)
    isDrawing = false
}

internal inline fun DivBorderSupports.dispatchDrawBorderClippedAndTranslated(
    canvas: Canvas,
    translationX: Int,
    translationY: Int,
    callback: (Canvas) -> Unit
) {
    if (isDrawing) {
        callback(canvas)
    } else {
        getDivBorderDrawer()?.drawClippedAndTranslated(canvas, translationX, translationY, callback)
            ?: callback(canvas)
    }
}
