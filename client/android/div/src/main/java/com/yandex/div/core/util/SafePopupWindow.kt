package com.yandex.div.core.util

import android.view.View
import android.widget.PopupWindow

/**
 * A standard popup window [android.widget.PopupWindow] with Tapjacking protection.
 */
open class SafePopupWindow: PopupWindow {
    constructor(contentView: View, width: Int, height: Int, focusable: Boolean) : super(
        contentView,
        width,
        height,
        focusable
    )

    constructor(contentView: View, width: Int, height: Int) : super(contentView, width, height)

    /**
     * Overridden setContentView method where filterTouchesWhenObscured is true.
     * This is for Tapjacking protection.
     */
    override fun setContentView(contentView: View?) {
        contentView?.filterTouchesWhenObscured = true
        super.setContentView(contentView)
    }
}
