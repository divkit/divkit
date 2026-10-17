package com.yandex.div.compose.views.input.mask

import com.yandex.div2.PhoneMasks
import org.json.JSONObject

/** The mask of the country the leading digits of the number belong to; it has no parameters. */
internal object PhoneInputMask : InputMask() {

    override fun sanitize(raw: String): String {
        val digits = raw.filter(Char::isDigit)
        return digits.take(pattern(digits).count { it == DYNAMIC_CHAR })
    }

    override fun pattern(raw: String): String {
        if (raw.isBlank()) return UNIVERSAL_MASK
        var current: JSONObject = PhoneMasks.VALUE_DEFAULT_VALUE
        var index = 0
        while (!current.has("value")) {
            if (index >= raw.length) {
                current = current.get(COUNTRY_CODE_END_MARKER) as JSONObject
                break
            }
            val digit = raw[index++].toString()
            current = current.get(if (current.has(digit)) digit else COUNTRY_CODE_END_MARKER) as JSONObject
        }
        return current.getString("value") + PhoneMasks.EXTRA_NUMBERS
    }

    override fun isDynamic(patternChar: Char): Boolean = patternChar == DYNAMIC_CHAR
}

private const val DYNAMIC_CHAR = '0'
private const val COUNTRY_CODE_END_MARKER = "*"
private const val UNIVERSAL_MASK = "000000000000000"
