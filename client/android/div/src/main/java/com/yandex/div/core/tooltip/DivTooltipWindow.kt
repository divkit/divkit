package com.yandex.div.core.tooltip

import android.view.View
import android.widget.PopupWindow

internal class DivTooltipWindow(
    contentView: View,
    width: Int = 0,
    height: Int = 0,
    focusable: Boolean = false
) : PopupWindow(contentView, width, height, focusable) {
    override fun dismiss() {
        try {
            super.dismiss()
        } catch (e: IllegalStateException) {
            // ignore, appears when hiding a tip
        } catch (e: IllegalArgumentException) {
            // ignore, appears when hiding a tip
        }
    }
}
