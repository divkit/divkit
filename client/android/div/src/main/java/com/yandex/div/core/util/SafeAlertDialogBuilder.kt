// Copyright 2022 Yandex LLC. All rights reserved.

package com.yandex.div.core.util

import android.content.Context
import android.content.DialogInterface
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog

/**
 * Wrapper for [AlertDialog.Builder], which can build SafeAlertDialog
 * with tapjacking protection.
 * @see [SafeAlertDialog]
 */
class SafeAlertDialogBuilder(context: Context) {
    private val alertDialogBuilder: AlertDialog.Builder

    init {
        alertDialogBuilder = AlertDialog.Builder(context)
    }

    // Delegation of control to alertDialogBuilder

    /**
     * Call [AlertDialog.Builder.setView].
     */
    fun setView(view: View?): SafeAlertDialogBuilder {
        view?.filterTouchesWhenObscured = true
        alertDialogBuilder.setView(view)
        return this
    }

    /**
     * Call [AlertDialog.Builder.setTitle].
     */
    fun setTitle(@StringRes titleId: Int): SafeAlertDialogBuilder {
        alertDialogBuilder.setTitle(titleId)
        return this
    }

    /**
     * Call [AlertDialog.Builder.setTitle].
     */
    fun setTitle(title: CharSequence?): SafeAlertDialogBuilder {
        alertDialogBuilder.setTitle(title)
        return this
    }

    /**
     * Call [AlertDialog.Builder.setMessage].
     */
    fun setMessage(@StringRes messageId: Int): SafeAlertDialogBuilder {
        alertDialogBuilder.setMessage(messageId)
        return this
    }

    /**
     * Call [AlertDialog.Builder.setMessage].
     */
    fun setMessage(message: CharSequence?): SafeAlertDialogBuilder {
        alertDialogBuilder.setMessage(message)
        return this
    }

    /**
     * Call [AlertDialog.Builder.setPositiveButton].
     */
    fun setPositiveButton(@StringRes textId: Int, listener: DialogInterface.OnClickListener?
    ): SafeAlertDialogBuilder {
        alertDialogBuilder.setPositiveButton(textId, listener)
        return this
    }

    /**
     * Call [AlertDialog.Builder.setPositiveButton].
     */
    fun setPositiveButton(text: String, listener: DialogInterface.OnClickListener?
    ): SafeAlertDialogBuilder {
        alertDialogBuilder.setPositiveButton(text, listener)
        return this
    }

    /**
     * Call [AlertDialog.Builder.setNegativeButton].
     */
    fun setNegativeButton(@StringRes textId: Int, listener: DialogInterface.OnClickListener?
    ): SafeAlertDialogBuilder {
        alertDialogBuilder.setNegativeButton(textId, listener)
        return this
    }

    /**
     * Call [AlertDialog.Builder.setNegativeButton].
     */
    fun setNegativeButton(text: String, listener: DialogInterface.OnClickListener?
    ): SafeAlertDialogBuilder {
        alertDialogBuilder.setNegativeButton(text, listener)
        return this
    }

    /**
     * Call [AlertDialog.Builder.setOnCancelListener].
     */
    fun setOnCancelListener(onCancelListener: DialogInterface.OnCancelListener?
    ): SafeAlertDialogBuilder {
        alertDialogBuilder.setOnCancelListener(onCancelListener)
        return this
    }

    /**
     * Call [AlertDialog.Builder.setOnDismissListener].
     */
    fun setOnDismissListener(listener: DialogInterface.OnDismissListener?
    ): SafeAlertDialogBuilder {
        alertDialogBuilder.setOnDismissListener(listener)
        return this
    }

    /**
     * Call [AlertDialog.Builder.create].
     */
    fun create(): SafeAlertDialog {
        val alertDialog = alertDialogBuilder.create()
        return SafeAlertDialog(alertDialog)
    }

    /**
     * Create SafeAlertDialog by [create]
     * and call [SafeAlertDialog.show].
     */
    fun show(): SafeAlertDialog {
        return create().apply { show() }
    }
}
