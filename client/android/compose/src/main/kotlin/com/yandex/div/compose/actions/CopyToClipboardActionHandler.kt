package com.yandex.div.compose.actions

import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.getSystemService
import com.yandex.div.compose.DivReporter
import com.yandex.div.internal.actions.resolveClipData
import com.yandex.div2.DivActionCopyToClipboard
import javax.inject.Inject

internal class CopyToClipboardActionHandler @Inject constructor(
    private val context: Context,
    private val reporter: DivReporter,
) {

    fun handle(context: DivActionHandlingContext, action: DivActionCopyToClipboard) {
        val clipboard = this.context.getSystemService<ClipboardManager>()
        if (clipboard == null) {
            reporter.reportError("Failed to access clipboard manager")
            return
        }

        clipboard.setPrimaryClip(action.content.resolveClipData(context.expressionResolver))
    }
}
