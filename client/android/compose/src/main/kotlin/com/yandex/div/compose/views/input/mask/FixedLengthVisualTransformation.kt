package com.yandex.div.compose.views.input.mask

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.variables.mutableStateFromVariable
import com.yandex.div.compose.views.input.DivInputState
import com.yandex.div.compose.views.input.rememberRegex
import com.yandex.div2.DivFixedLengthInputMask

@Composable
internal fun DivFixedLengthInputMask.rememberFixedLengthInputState(
    textVariable: String
): DivInputState {
    val raw = mutableStateFromVariable(rawTextVariable, defaultValue = "")
    val display = mutableStateFromVariable(textVariable, defaultValue = "")
    val params = rememberFixedLengthMaskParams()
    val transformation = remember(params) { FixedLengthVisualTransformation(params) }

    SyncMaskedRaw(raw, display, sanitize = params::transformRaw, format = params::format)

    return remember(raw, params) {
        DivInputState(
            source = raw,
            visualTransformation = transformation,
            sanitize = params::transformRaw,
        )
    }
}

@Composable
private fun DivFixedLengthInputMask.rememberFixedLengthMaskParams(): FixedLengthMaskParams {
    val pattern = pattern.observedValue()
    val alwaysVisible = alwaysVisible.observedValue()
    val patternElements = patternElements
    val keys = patternElements.map { it.key.observedValue().firstOrNull() ?: Char.MIN_VALUE }
    val regexStrings = patternElements.map { it.regex?.observedValue() }
    val placeholders = patternElements.map { it.placeholder.observedValue().firstOrNull() }

    val regexes = regexStrings.map { it?.let { p -> rememberRegex(p) } }
    return remember(pattern, alwaysVisible, keys, regexes, placeholders) {
        val regexByKey: Map<Char, Regex?> = keys.zip(regexes).toMap()
        val placeholderByKey: Map<Char, Char?> = keys.zip(placeholders).toMap()

        val dynamicChars = regexByKey.keys
        val dynamicRegexes = pattern.filter { it in dynamicChars }.map { regexByKey[it] }

        FixedLengthMaskParams(
            pattern = pattern,
            dynamicChars = dynamicChars,
            dynamicRegexes = dynamicRegexes,
            placeholderByKey = placeholderByKey,
            alwaysVisible = alwaysVisible,
        )
    }
}

private class FixedLengthVisualTransformation(
    private val params: FixedLengthMaskParams,
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return buildMaskedText(
            raw = text.text,
            pattern = params.pattern,
            isDynamic = params::isDynamic,
            placeholderForPosition = if (params.alwaysVisible) params::placeholderAt else null,
        )
    }
}

private class FixedLengthMaskParams(
    val pattern: String,
    private val dynamicChars: Set<Char>,
    private val dynamicRegexes: List<Regex?>,
    private val placeholderByKey: Map<Char, Char?>,
    val alwaysVisible: Boolean,
) {
    val maxRawLength: Int get() = dynamicRegexes.size

    fun isDynamic(patternChar: Char): Boolean = patternChar in dynamicChars

    fun placeholderAt(patternIndex: Int): Char? {
        val ch = pattern.getOrNull(patternIndex) ?: return null
        return placeholderByKey[ch]
    }

    fun transformRaw(raw: String): String {
        val sb = StringBuilder()
        var slot = 0
        for (char in raw) {
            if (slot >= maxRawLength) break
            val regex = dynamicRegexes[slot]
            if (regex == null || regex.matches(char.toString())) {
                sb.append(char)
                slot++
            }
        }
        return sb.toString()
    }

    fun format(raw: String): String =
        buildMaskedText(raw, pattern, ::isDynamic, placeholderForPosition = null).text.text
}
