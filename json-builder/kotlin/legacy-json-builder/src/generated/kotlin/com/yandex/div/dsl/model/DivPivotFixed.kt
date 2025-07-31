// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivPivotFixed internal constructor(
    @JsonIgnore val unit: Property<DivSizeUnit>?,
    @JsonIgnore val value: Property<Int>?,
) : DivPivot {

    @JsonProperty("type") override val type = "pivot-fixed"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "unit" to unit,
            "value" to value,
        )
    }
}

fun <T> TemplateContext<T>.divPivotFixed(): LiteralProperty<DivPivotFixed> {
    return value(DivPivotFixed(
        unit = null,
        value = null,
    ))
}

fun <T> TemplateContext<T>.divPivotFixed(
    unit: Property<DivSizeUnit>? = null,
    value: Property<Int>? = null,
): LiteralProperty<DivPivotFixed> {
    return value(DivPivotFixed(
        unit = unit,
        value = value,
    ))
}

fun <T> TemplateContext<T>.divPivotFixed(
    unit: DivSizeUnit? = null,
    value: Int? = null,
): LiteralProperty<DivPivotFixed> {
    return value(DivPivotFixed(
        unit = optionalValue(unit),
        value = optionalValue(value),
    ))
}

fun CardContext.divPivotFixed(): DivPivotFixed {
    return DivPivotFixed(
        unit = null,
        value = null,
    )
}

fun CardContext.divPivotFixed(
    unit: ValueProperty<DivSizeUnit>? = null,
    value: ValueProperty<Int>? = null,
): DivPivotFixed {
    return DivPivotFixed(
        unit = unit,
        value = value,
    )
}

fun CardContext.divPivotFixed(
    unit: DivSizeUnit? = null,
    value: Int? = null,
): DivPivotFixed {
    return DivPivotFixed(
        unit = optionalValue(unit),
        value = optionalValue(value),
    )
}
