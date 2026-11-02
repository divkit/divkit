package com.yandex.div.render.common.text

/**
 * Platform boundaries in UTF-16 offsets. [preceding] returns [DONE] before the first boundary.
 */
internal interface TextBoundaryIterator {
    fun isBoundary(offset: Int): Boolean
    fun preceding(offset: Int): Int

    companion object {
        const val DONE = -1
    }
}

// Preserve the platform Unicode tables and Java whitespace semantics, including non-breaking spaces.
internal expect fun isTrimmableWhitespace(codePoint: Int): Boolean

internal expect fun isCombiningMark(codePoint: Int): Boolean
