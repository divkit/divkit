package com.yandex.div.compose.actions

import com.yandex.div.compose.system.ClipboardManager
import com.yandex.div2.DivActionCopyToClipboard
import com.yandex.div2.DivActionCopyToClipboardContent
import dev.zacsweers.metro.Inject

internal class CopyToClipboardActionHandler @Inject constructor(
    private val clipboardManager: ClipboardManager,
) {

    fun handle(context: DivActionHandlingContext, action: DivActionCopyToClipboard) {
        val expressionResolver = context.expressionResolver
        when (val content = action.content) {
            is DivActionCopyToClipboardContent.ContentTextCase ->
                clipboardManager.putText(content.value.value.evaluate(expressionResolver))

            is DivActionCopyToClipboardContent.ContentUrlCase -> {
                clipboardManager.putUri(content.value.value.evaluate(expressionResolver))
            }
        }
    }
}
