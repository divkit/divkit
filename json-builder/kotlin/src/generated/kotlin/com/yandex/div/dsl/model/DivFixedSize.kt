// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivFixedSize internal constructor(
    @JsonIgnore val unit: Property<DivSizeUnit>?,
    @JsonIgnore val value: Property<Int>?,
) : DivSize() {

    @JsonProperty("type") override val type = "fixed"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "unit" to unit,
            "value" to value,
        )
    }
}

fun <T> TemplateContext<T>.divFixedSize(): LiteralProperty<DivFixedSize> {
    return value(DivFixedSize(
        unit = null,
        value = null,
    ))
}

fun <T> TemplateContext<T>.divFixedSize(
    value: Property<Int>? = null,
    unit: Property<DivSizeUnit>? = null,
): LiteralProperty<DivFixedSize> {
    return value(DivFixedSize(
        unit = unit,
        value = value,
    ))
}

fun <T> TemplateContext<T>.divFixedSize(
    value: Int? = null,
    unit: DivSizeUnit? = null,
): LiteralProperty<DivFixedSize> {
    return value(DivFixedSize(
        unit = optionalValue(unit),
        value = optionalValue(value),
    ))
}

fun CardContext.divFixedSize(
    value: ValueProperty<Int>,
    unit: ValueProperty<DivSizeUnit>? = null,
): DivFixedSize {
    return DivFixedSize(
        unit = unit,
        value = value,
    )
}

fun CardContext.divFixedSize(
    value: Int,
    unit: DivSizeUnit? = null,
): DivFixedSize {
    return DivFixedSize(
        unit = optionalValue(unit),
        value = value(value),
    )
}
