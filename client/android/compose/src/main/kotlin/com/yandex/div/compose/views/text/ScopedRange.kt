package com.yandex.div.compose.views.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.yandex.div.compose.collection.observeBuilderDataElements
import com.yandex.div.compose.di.DivLocalComponent
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div2.DivText

/**
 * A character range with the scope its expressions are resolved in.
 */
internal data class ScopedRange(
    val range: DivText.Range,
    val component: DivLocalComponent,
)

/**
 * The ranges of the text: the ones its `range_builder` builds, or the static `ranges` otherwise.
 */
@Composable
internal fun DivText.observeSourceRanges(): List<ScopedRange> {
    val component = LocalComponent.current
    rangeBuilder?.let { return it.observeBuiltRanges(component) }
    return ranges.rememberScopedRanges()
}

/**
 * These ranges, each resolved in the scope of the text.
 */
@Composable
internal fun List<DivText.Range>?.rememberScopedRanges(): List<ScopedRange> {
    val component = LocalComponent.current
    return remember(this, component) { this?.map { ScopedRange(it, component) } ?: emptyList() }
}

/**
 * The ranges this builder builds, each with the scope of its `data` element.
 */
@Composable
private fun DivText.RangeBuilder.observeBuiltRanges(parent: DivLocalComponent): List<ScopedRange> {
    return observeBuilderDataElements(
        propertyName = "range_builder",
        elements = data.observedValue(),
        dataElementName = dataElementName,
        parent = parent,
    ) { element ->
        observeSelectedRange(element.component)?.let { ScopedRange(it, element.component) }
    }
}

@Composable
private fun DivText.RangeBuilder.observeSelectedRange(
    component: DivLocalComponent,
): DivText.Range? {
    for (prototype in prototypes) {
        if (prototype.selector.observedValue(component = component)) {
            return prototype.range
        }
    }
    return null
}
