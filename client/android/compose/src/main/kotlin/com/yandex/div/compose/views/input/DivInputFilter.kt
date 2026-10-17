package com.yandex.div.compose.views.input

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div2.DivInputFilter

/** The predicate every filter of the list accepts a text with. */
@Composable
internal fun List<DivInputFilter>.observeFilter(): (String) -> Boolean {
    val regexes = mutableListOf<Regex>()
    val conditions = mutableListOf<Boolean>()
    forEach { filter ->
        when (filter) {
            is DivInputFilter.Regex -> {
                val pattern = filter.value.pattern.observedValue()
                rememberRegex(pattern)?.let { regexes += it }
            }
            is DivInputFilter.Expression -> {
                conditions += filter.value.condition.observedValue()
            }
        }
    }
    return remember(regexes, conditions) {
        { value -> regexes.all { it.matches(value) } && conditions.all { it } }
    }
}
