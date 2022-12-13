package com.yandex.div.core.tooltip

import android.view.View
import com.yandex.div.core.util.SafePopupWindow

internal class DivTooltipWindow(
    contentView: View,
    width: Int = 0,
    height: Int = 0,
    focusable: Boolean = false
) : SafePopupWindow(contentView, width, height, focusable) {
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
