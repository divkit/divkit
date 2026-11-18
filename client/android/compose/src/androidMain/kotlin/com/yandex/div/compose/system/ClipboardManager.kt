package com.yandex.div.compose.system

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.core.content.getSystemService
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.di.DivContextScope
import dev.zacsweers.metro.Inject

@DivContextScope
internal class ClipboardManager @Inject constructor(
    private val context: Context,
    private val reporter: DivReporter,
) {
    private val clipboard by lazy {
        val clipboard = context.getSystemService<ClipboardManager>()
        if (clipboard == null) {
            reporter.reportError("Failed to access clipboard manager")
            return@lazy null
        }
        return@lazy clipboard
    }

    fun putText(text: String) {
        clipboard?.setPrimaryClip(
            ClipData(
                "Copied text",
                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                ClipData.Item(text)
            )
        )
    }

    fun putUri(uri: Uri) {
        clipboard?.setPrimaryClip(
            ClipData(
                "Copied url",
                arrayOf(
                    ClipDescription.MIMETYPE_TEXT_URILIST,
                    ClipDescription.MIMETYPE_TEXT_PLAIN,
                ),
                ClipData.Item(uri.toString(), null, uri)
            )
        )
    }
}
