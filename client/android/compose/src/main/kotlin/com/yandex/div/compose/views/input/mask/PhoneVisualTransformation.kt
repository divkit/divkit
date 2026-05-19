package com.yandex.div.compose.views.input.mask

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.yandex.div.compose.utils.variables.mutableStateFromVariable
import com.yandex.div.compose.views.input.DivInputState
import com.yandex.div2.DivPhoneInputMask
import com.yandex.div2.PhoneMasks
import org.json.JSONObject

@Composable
internal fun DivPhoneInputMask.rememberPhoneInputState(textVariable: String): DivInputState {
    val raw = mutableStateFromVariable(rawTextVariable, defaultValue = "")
    val display = mutableStateFromVariable(textVariable, defaultValue = "")
    val patternState = remember { mutableStateOf("".phoneMaskPattern) }
    val pendingSanitizedWriteback = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(raw.value) {
        val current = raw.value
        val sanitized = sanitizePhoneRaw(current)
        val isOurWriteback = pendingSanitizedWriteback.value == current
        pendingSanitizedWriteback.value = null

        if (!isOurWriteback) {
            val newPattern = current.phoneMaskPattern
            if (newPattern != patternState.value) {
                patternState.value = newPattern
            }
        }

        if (current != sanitized) {
            pendingSanitizedWriteback.value = sanitized
            raw.value = sanitized
            return@LaunchedEffect
        }

        val formatted = buildMaskedText(
            sanitized,
            patternState.value,
            isDynamic = { it == DYNAMIC_CHAR },
        ).text.text
        if (display.value != formatted) {
            display.value = formatted
        }
    }

    val transformation = remember(patternState.value) {
        PhoneVisualTransformation(patternState.value)
    }

    return remember(raw, transformation) {
        DivInputState(
            source = raw,
            visualTransformation = transformation,
            sanitize = ::sanitizePhoneRaw,
        )
    }
}

private class PhoneVisualTransformation(
    private val pattern: String,
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText =
        buildMaskedText(text.text, pattern, isDynamic = { it == DYNAMIC_CHAR })
}

private fun sanitizePhoneRaw(input: String): String = input.filter(Char::isDigit)

private val String.phoneMaskPattern: String
    get() {
        if (isBlank()) return UNIVERSAL_MASK
        var current: JSONObject = PhoneMasks.VALUE_DEFAULT_VALUE
        var idx = 0
        while (!current.has("value")) {
            if (idx >= length) {
                current = current.get(COUNTRY_CODE_END_MARKER) as JSONObject
                break
            }
            val digit = get(idx++).toString()
            current = current.get(if (current.has(digit)) digit else COUNTRY_CODE_END_MARKER) as JSONObject
        }
        return current.getString("value") + PhoneMasks.EXTRA_NUMBERS
    }

private const val DYNAMIC_CHAR = '0'
private const val COUNTRY_CODE_END_MARKER = "*"
private const val UNIVERSAL_MASK = "000000000000000"
