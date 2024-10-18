package com.yandex.div.core.view2.divs.widgets

import android.view.View
import android.view.ViewOutlineProvider
import com.yandex.div.core.view2.BindingContext
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

    override fun setBorder(bindingContext: BindingContext, border: DivBorder?, view: View) {
        if (borderDrawer == null && border != null) {
            borderDrawer = DivBorderDrawer(bindingContext.divView, view)
        }

        borderDrawer?.setBorder(border, bindingContext.expressionResolver)
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
