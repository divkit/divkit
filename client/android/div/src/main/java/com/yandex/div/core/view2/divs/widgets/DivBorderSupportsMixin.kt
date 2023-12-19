package com.yandex.div.core.view2.divs.widgets

import android.view.View
import android.view.ViewOutlineProvider
import com.yandex.div.core.view2.divs.isConstantlyEmpty
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
        if (border == borderDrawer?.border) {
            return
        }

        when {
            border == null -> {
                view.apply {
                    elevation = DivBorderDrawer.NO_ELEVATION
                    clipToOutline = false
                    outlineProvider = ViewOutlineProvider.BACKGROUND
                }
                releaseBorderDrawer()
                borderDrawer = null
            }
            borderDrawer != null -> this.borderDrawer?.setBorder(resolver, border)
            border.isConstantlyEmpty() -> view.apply {
                elevation = DivBorderDrawer.NO_ELEVATION
                clipToOutline = needClipping
                outlineProvider = ViewOutlineProvider.BOUNDS
            }
            else -> {
                borderDrawer = DivBorderDrawer(view.resources.displayMetrics, view, resolver, border)
            }
        }
        borderDrawer?.needClipping = needClipping
    }
}
