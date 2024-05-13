package com.yandex.div.core.view2.divs.widgets

import android.view.View
import android.view.ViewOutlineProvider
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBorder

internal class DivBorderSupportsMixin: DivBorderSupports {

    private var borderDrawer: DivBorderDrawer? = null
    override var isDrawing = false
    override var needClipping = true
        set(value) {
            borderDrawer?.needClipping = value
            field = value
        }

    override fun getDivBorderDrawer() = borderDrawer

    override fun setBorder(border: DivBorder?, view: View, resolver: ExpressionResolver) {
        if (borderDrawer == null && border != null) {
            borderDrawer = DivBorderDrawer(view)
        }

        borderDrawer?.setBorder(border, resolver)
        borderDrawer?.needClipping = needClipping
        if (border == null) {
            view.apply {
                elevation = DivBorderDrawer.NO_ELEVATION
                clipToOutline = false
                outlineProvider = ViewOutlineProvider.BACKGROUND
            }
            releaseBorderDrawer()
            borderDrawer = null
        }
        view.invalidate()
    }
}
