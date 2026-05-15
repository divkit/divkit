package com.yandex.div.compose.views.input

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div2.DivInputFilter

@Composable
internal fun rememberFilteredState(
    source: MutableState<String>,
    filters: List<DivInputFilter>?,
): MutableState<String> {
    if (filters.isNullOrEmpty()) return source
    val filter = filters.buildFilter()

    val accepted = remember { mutableStateOf(source.value) }
    if (filter(source.value)) {
        accepted.value = source.value
    }

    return remember(accepted, source, filter) {
        FilteringMutableState(accepted, source, filter)
    }
}

@Composable
private fun List<DivInputFilter>.buildFilter(): (String) -> Boolean {
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

private class FilteringMutableState(
    private val accepted: MutableState<String>,
    private val source: MutableState<String>,
    private val filter: (String) -> Boolean,
) : MutableState<String> {
    override var value: String
        get() = accepted.value
        set(newValue) {
            if (!filter(newValue)) return
            accepted.value = newValue
            source.value = newValue
        }

    override fun component1(): String = value
    override fun component2(): (String) -> Unit = { value = it }
}
