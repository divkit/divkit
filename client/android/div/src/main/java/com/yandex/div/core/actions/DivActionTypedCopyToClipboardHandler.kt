package com.yandex.div.core.actions

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.Assert
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivActionCopyToClipboardContent
import com.yandex.div2.DivActionTyped
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DivActionTypedCopyToClipboardHandler @Inject constructor() : DivActionTypedHandler {

    override fun handleAction(
        scopeId: String?,
        action: DivActionTyped,
        view: Div2View,
        resolver: ExpressionResolver,
    ): Boolean = when (action) {

        is DivActionTyped.CopyToClipboard -> {
            handleCopyToClipboard(action.value.content, view, resolver)
            true
        }

        else -> false
    }

    private fun handleCopyToClipboard(
        content: DivActionCopyToClipboardContent,
        view: Div2View,
        resolver: ExpressionResolver,
    ) {
        val clipboardManager =
            view.context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                ?: run {
                    Assert.fail("Failed to access clipboard manager!")
                    return
                }

        val clipData = content.resolveClipData(resolver)
        clipboardManager.setPrimaryClip(clipData)
    }
}

private fun DivActionCopyToClipboardContent.resolveClipData(
    expressionResolver: ExpressionResolver,
): ClipData {
    return when (this) {
        is DivActionCopyToClipboardContent.ContentTextCase -> ClipData(
            "Copied text",
            arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
            ClipData.Item(value.value.evaluate(expressionResolver))
        )

        is DivActionCopyToClipboardContent.ContentUrlCase -> {
            val uri = value.value.evaluate(expressionResolver)
            ClipData(
                "Copied url",
                arrayOf(ClipDescription.MIMETYPE_TEXT_URILIST, ClipDescription.MIMETYPE_TEXT_PLAIN),
                ClipData.Item(uri.toString(), null, uri)
            )
        }
    }
}

