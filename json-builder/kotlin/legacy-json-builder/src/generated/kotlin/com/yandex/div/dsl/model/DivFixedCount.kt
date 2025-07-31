// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivFixedCount internal constructor(
    @JsonIgnore val value: Property<Int>?,
) : DivCount {

    @JsonProperty("type") override val type = "fixed"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "value" to value,
        )
    }
}

fun <T> TemplateContext<T>.divFixedCount(): LiteralProperty<DivFixedCount> {
    return value(DivFixedCount(
        value = null,
    ))
}

fun <T> TemplateContext<T>.divFixedCount(
    value: Property<Int>? = null,
): LiteralProperty<DivFixedCount> {
    return value(DivFixedCount(
        value = value,
    ))
}

fun <T> TemplateContext<T>.divFixedCount(
    value: Int? = null,
): LiteralProperty<DivFixedCount> {
    return value(DivFixedCount(
        value = optionalValue(value),
    ))
}

fun CardContext.divFixedCount(
    value: ValueProperty<Int>,
): DivFixedCount {
    return DivFixedCount(
        value = value,
    )
}

fun CardContext.divFixedCount(
    value: Int,
): DivFixedCount {
    return DivFixedCount(
        value = value(value),
    )
}
