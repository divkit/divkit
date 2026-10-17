package com.yandex.div.compose.views.input.mask

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.views.input.rememberRegex
import com.yandex.div2.DivFixedLengthInputMask

@Composable
internal fun DivFixedLengthInputMask.rememberFixedLengthInputMask(): InputMask {
    val pattern = pattern.observedValue()
    val alwaysVisible = alwaysVisible.observedValue()
    val slots = patternElements.map { element ->
        FixedLengthInputMask.Slot(
            key = element.key.observedValue().firstOrNull() ?: Char.MIN_VALUE,
            regex = element.regex?.observedValue()?.let { rememberRegex(it) },
            placeholder = element.placeholder.observedValue().firstOrNull(),
        )
    }
    return remember(pattern, alwaysVisible, slots) { FixedLengthInputMask(pattern, slots, alwaysVisible) }
}

internal class FixedLengthInputMask(
    private val pattern: String,
    slots: List<Slot>,
    private val alwaysVisible: Boolean,
) : InputMask() {

    data class Slot(val key: Char, val regex: Regex?, val placeholder: Char?)

    private val slotsByKey = slots.associateBy { it.key }
    private val slotRegexes = pattern.filter { it in slotsByKey }.map { slotsByKey.getValue(it).regex }

    override fun sanitize(raw: String): String = buildString {
        var slot = 0
        for (char in raw) {
            if (slot == slotRegexes.size) break
            val regex = slotRegexes[slot]
            if (regex == null || regex.matches(char.toString())) {
                append(char)
                slot++
            }
        }
    }

    override fun pattern(raw: String): String = pattern

    override fun isDynamic(patternChar: Char): Boolean = patternChar in slotsByKey

    override fun placeholder(patternIndex: Int): Char? {
        return if (alwaysVisible) slotsByKey[pattern[patternIndex]]?.placeholder else null
    }
}
