// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivRadialGradientFixedCenter internal constructor(
    @JsonIgnore val unit: Property<DivSizeUnit>?,
    @JsonIgnore val value: Property<Int>?,
) : DivRadialGradientCenter {

    @JsonProperty("type") override val type = "fixed"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "unit" to unit,
            "value" to value,
        )
    }
}

fun <T> TemplateContext<T>.divRadialGradientFixedCenter(): LiteralProperty<DivRadialGradientFixedCenter> {
    return value(DivRadialGradientFixedCenter(
        unit = null,
        value = null,
    ))
}

fun <T> TemplateContext<T>.divRadialGradientFixedCenter(
    value: Property<Int>? = null,
    unit: Property<DivSizeUnit>? = null,
): LiteralProperty<DivRadialGradientFixedCenter> {
    return value(DivRadialGradientFixedCenter(
        unit = unit,
        value = value,
    ))
}

fun <T> TemplateContext<T>.divRadialGradientFixedCenter(
    value: Int? = null,
    unit: DivSizeUnit? = null,
): LiteralProperty<DivRadialGradientFixedCenter> {
    return value(DivRadialGradientFixedCenter(
        unit = optionalValue(unit),
        value = optionalValue(value),
    ))
}

fun CardContext.divRadialGradientFixedCenter(
    value: ValueProperty<Int>,
    unit: ValueProperty<DivSizeUnit>? = null,
): DivRadialGradientFixedCenter {
    return DivRadialGradientFixedCenter(
        unit = unit,
        value = value,
    )
}

fun CardContext.divRadialGradientFixedCenter(
    value: Int,
    unit: DivSizeUnit? = null,
): DivRadialGradientFixedCenter {
    return DivRadialGradientFixedCenter(
        unit = optionalValue(unit),
        value = value(value),
    )
}
