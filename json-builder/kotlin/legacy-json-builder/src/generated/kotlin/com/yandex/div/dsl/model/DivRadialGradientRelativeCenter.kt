// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivRadialGradientRelativeCenter internal constructor(
    @JsonIgnore val value: Property<Double>?,
) : DivRadialGradientCenter {

    @JsonProperty("type") override val type = "relative"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "value" to value,
        )
    }
}

fun <T> TemplateContext<T>.divRadialGradientRelativeCenter(): LiteralProperty<DivRadialGradientRelativeCenter> {
    return value(DivRadialGradientRelativeCenter(
        value = null,
    ))
}

fun <T> TemplateContext<T>.divRadialGradientRelativeCenter(
    value: Property<Double>? = null,
): LiteralProperty<DivRadialGradientRelativeCenter> {
    return value(DivRadialGradientRelativeCenter(
        value = value,
    ))
}

fun <T> TemplateContext<T>.divRadialGradientRelativeCenter(
    value: Double? = null,
): LiteralProperty<DivRadialGradientRelativeCenter> {
    return value(DivRadialGradientRelativeCenter(
        value = optionalValue(value),
    ))
}

fun CardContext.divRadialGradientRelativeCenter(
    value: ValueProperty<Double>,
): DivRadialGradientRelativeCenter {
    return DivRadialGradientRelativeCenter(
        value = value,
    )
}

fun CardContext.divRadialGradientRelativeCenter(
    value: Double,
): DivRadialGradientRelativeCenter {
    return DivRadialGradientRelativeCenter(
        value = value(value),
    )
}
