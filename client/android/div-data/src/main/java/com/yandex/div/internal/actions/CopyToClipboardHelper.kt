package com.yandex.div.internal.actions

import android.content.ClipData
import android.content.ClipDescription
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivActionCopyToClipboardContent

@InternalApi
fun DivActionCopyToClipboardContent.resolveClipData(resolver: ExpressionResolver): ClipData =
    when (this) {
        is DivActionCopyToClipboardContent.ContentTextCase -> ClipData(
            "Copied text",
            arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
            ClipData.Item(value.value.evaluate(resolver))
        )

        is DivActionCopyToClipboardContent.ContentUrlCase -> {
            val uri = value.value.evaluate(resolver)
            ClipData(
                "Copied url",
                arrayOf(ClipDescription.MIMETYPE_TEXT_URILIST, ClipDescription.MIMETYPE_TEXT_PLAIN),
                ClipData.Item(uri.toString(), null, uri)
            )
        }
    }
