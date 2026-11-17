package com.yandex.div.render.common.text

import platform.Foundation.NSCharacterSet

private val whitespaceCharacters = NSCharacterSet.whitespaceAndNewlineCharacterSet
private val combiningCharacters = NSCharacterSet.nonBaseCharacterSet

internal actual fun isTrimmableWhitespace(codePoint: Int): Boolean = when (codePoint) {
    // Java includes these separators, while Foundation also includes NEL and zero-width space.
    in FILE_SEPARATOR..UNIT_SEPARATOR -> true
    NEXT_LINE, NO_BREAK_SPACE, FIGURE_SPACE, ZERO_WIDTH_SPACE, NARROW_NO_BREAK_SPACE -> false
    else -> whitespaceCharacters.longCharacterIsMember(codePoint.toUInt())
}

internal actual fun isCombiningMark(codePoint: Int): Boolean =
    combiningCharacters.longCharacterIsMember(codePoint.toUInt())

private const val FILE_SEPARATOR = 0x001C
private const val UNIT_SEPARATOR = 0x001F
private const val NEXT_LINE = 0x0085
private const val NO_BREAK_SPACE = 0x00A0
private const val FIGURE_SPACE = 0x2007
private const val ZERO_WIDTH_SPACE = 0x200B
private const val NARROW_NO_BREAK_SPACE = 0x202F
