@file:OptIn(
    kotlinx.cinterop.BetaInteropApi::class,
    kotlinx.cinterop.ExperimentalForeignApi::class,
)

package com.yandex.div.render.common.text

import com.yandex.div.core.annotations.InternalApi
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.useContents
import platform.CoreFoundation.CFRangeMake
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFStringTokenizerAdvanceToNextToken
import platform.CoreFoundation.CFStringTokenizerCreate
import platform.CoreFoundation.CFStringTokenizerGetCurrentTokenRange
import platform.CoreFoundation.kCFStringTokenizerTokenNone
import platform.CoreFoundation.kCFStringTokenizerUnitLineBreak
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSString
import platform.Foundation.create
import platform.Foundation.rangeOfComposedCharacterSequenceAtIndex

@InternalApi
public actual fun TextTruncation(text: String): TextTruncation {
    return TextTruncation(text) { character ->
        if (character) {
            ComposedCharacterIterator(text)
        } else {
            LineBreakIterator(lineBreakOffsets(NSString.create(string = text)))
        }
    }
}

private class ComposedCharacterIterator(private val text: String) : TextBoundaryIterator {
    private val string = NSString.create(string = text)

    override fun isBoundary(offset: Int): Boolean {
        return offset == 0 ||
                offset == text.length ||
                clusterStart(offset) == offset
    }

    override fun preceding(offset: Int): Int {
        return if (offset == 0) {
            TextBoundaryIterator.DONE
        } else {
            clusterStart(offset - 1)
        }
    }

    private fun clusterStart(index: Int): Int {
        // NSString treats CR and LF separately, but the pair is one grapheme boundary unit.
        if (index > 0 && text[index] == '\n' && text[index - 1] == '\r') {
            return index - 1
        }
        return string.rangeOfComposedCharacterSequenceAtIndex(index.toULong())
            .useContents { location.toInt() }
    }
}

private class LineBreakIterator(private val offsets: List<Int>) : TextBoundaryIterator {
    override fun isBoundary(offset: Int): Boolean {
        return offsets.binarySearch(offset) >= 0
    }

    override fun preceding(offset: Int): Int {
        val index = offsets.binarySearch(offset)
        val precedingIndex = if (index >= 0) index - 1 else -index - 2
        return offsets.getOrElse(precedingIndex) { TextBoundaryIterator.DONE }
    }
}

private fun lineBreakOffsets(text: NSString): List<Int> {
    val string = CFBridgingRetain(text)
    try {
        val tokenizer = requireNotNull(
            CFStringTokenizerCreate(
                alloc = null,
                string = string?.reinterpret(),
                range = CFRangeMake(0, text.length.toLong()),
                options = kCFStringTokenizerUnitLineBreak,
                locale = null,
            )
        )
        try {
            return buildList {
                add(0)
                while (
                    CFStringTokenizerAdvanceToNextToken(tokenizer) != kCFStringTokenizerTokenNone
                ) {
                    add(
                        CFStringTokenizerGetCurrentTokenRange(tokenizer)
                            .useContents { (location + length).toInt() }
                    )
                }
            }
        } finally {
            CFRelease(tokenizer)
        }
    } finally {
        CFRelease(string)
    }
}
