package com.yandex.div.compose.views.text

/**
 * Result of resolving safe end truncation for Compose text.
 *
 * Native rendering is retained when Compose already chose a safe boundary. This preserves its
 * standard layout and ellipsis behavior without rebuilding the annotated text unnecessarily.
 * Prepared text is rendered only to correct an unsafe boundary or apply custom truncation.
 */
internal sealed interface TextEllipsisResult {

    /** Keep the source text and let Compose render its native ellipsis. */
    data object UseNativeRendering : TextEllipsisResult

    /** Render the prepared text with clipping, including when that text is empty. */
    data class Render(
        val text: AnnotatedText,
    ) : TextEllipsisResult
}
