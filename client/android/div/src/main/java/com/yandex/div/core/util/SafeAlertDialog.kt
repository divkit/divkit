package com.yandex.div.core.util

import android.content.DialogInterface
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog

/**
 * Wrapper for AlertDialog with Tapjacking protection.
 * You should pass [alertDialog] from [SafeAlertDialogBuilder].
 *
 * If you need more methods, then you can add them manually.
 * For methods with "View" params - you MUST set "filterTouchesWhenObscured" to true.
 * @see SafeAlertDialogBuilder
 */
class SafeAlertDialog(private val alertDialog: AlertDialog) {

    /**
     * Check equals of references wrapped alertDialog and dialog.
     */
    fun checkEqualReference(dialog: DialogInterface): Boolean {
        return alertDialog === dialog
    }

    /**
     * Set discarding of touches on the buttons when the view's window is obscured by
     * another visible window at the touched location.
     */
    private fun setupTapjackingProtection(vararg views: View?) {
        for (view in views) {
            view?.filterTouchesWhenObscured = true
        }
    }

    // Delegation of control to alertDialog

    /**
     * Call [AlertDialog.findViewById].
     */
    fun <T : View?> findViewById(@IdRes id: Int): T? {
        return alertDialog.findViewById(id)
    }

    /**
     * Call [AlertDialog.hide].
     */
    fun hide() {
        alertDialog.hide()
    }

    /**
     * Call [AlertDialog.dismiss].
     */
    fun dismiss() {
        alertDialog.dismiss()
    }

    /**
     * Call [AlertDialog.cancel].
     */
    fun cancel() {
        alertDialog.cancel()
    }

    /**
     * Call [AlertDialog.show].
     */
    fun show() {
        alertDialog.show()
        setupTapjackingProtection(
                alertDialog.listView,
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE),
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE),
                alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL)
        )
    }
}
