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
internal class DivActionTypedCopyToClipboardHandler @Inject constructor()
    : DivActionTypedHandler {

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

        val clipData = content.getClipData(resolver)
        clipboardManager.setPrimaryClip(clipData)
    }

    private fun DivActionCopyToClipboardContent.getClipData(resolver: ExpressionResolver) =
        when (this) {
            is DivActionCopyToClipboardContent.ContentTextCase -> getClipData(resolver)
            is DivActionCopyToClipboardContent.ContentUrlCase -> getClipData(resolver)
        }

    private fun DivActionCopyToClipboardContent.ContentUrlCase.getClipData(
        resolver: ExpressionResolver
    ): ClipData {
        val content = value.value.evaluate(resolver)
        return ClipData(
            "Copied url",
            arrayOf(ClipDescription.MIMETYPE_TEXT_URILIST),
            ClipData.Item(content)
        )
    }

    private fun DivActionCopyToClipboardContent.ContentTextCase.getClipData(
        resolver: ExpressionResolver
    ): ClipData {
        val content = value.value.evaluate(resolver)
        return ClipData(
            "Copied text",
            arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
            ClipData.Item(content)
        )
    }
}
