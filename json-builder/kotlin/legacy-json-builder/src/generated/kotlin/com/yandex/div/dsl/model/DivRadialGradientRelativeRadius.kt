// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivRadialGradientRelativeRadius internal constructor(
    @JsonIgnore val value: Property<Value>?,
) : DivRadialGradientRadius {

    @JsonProperty("type") override val type = "relative"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "value" to value,
        )
    }

    enum class Value(@JsonValue val value: String) {
        NEAREST_CORNER("nearest_corner"),
        FARTHEST_CORNER("farthest_corner"),
        NEAREST_SIDE("nearest_side"),
        FARTHEST_SIDE("farthest_side"),
    }
}

fun <T> TemplateContext<T>.divRadialGradientRelativeRadius(): LiteralProperty<DivRadialGradientRelativeRadius> {
    return value(DivRadialGradientRelativeRadius(
        value = null,
    ))
}

fun <T> TemplateContext<T>.divRadialGradientRelativeRadius(
    value: Property<DivRadialGradientRelativeRadius.Value>? = null,
): LiteralProperty<DivRadialGradientRelativeRadius> {
    return value(DivRadialGradientRelativeRadius(
        value = value,
    ))
}

fun <T> TemplateContext<T>.divRadialGradientRelativeRadius(
    value: DivRadialGradientRelativeRadius.Value? = null,
): LiteralProperty<DivRadialGradientRelativeRadius> {
    return value(DivRadialGradientRelativeRadius(
        value = optionalValue(value),
    ))
}

fun CardContext.divRadialGradientRelativeRadius(
    value: ValueProperty<DivRadialGradientRelativeRadius.Value>,
): DivRadialGradientRelativeRadius {
    return DivRadialGradientRelativeRadius(
        value = value,
    )
}

fun CardContext.divRadialGradientRelativeRadius(
    value: DivRadialGradientRelativeRadius.Value,
): DivRadialGradientRelativeRadius {
    return DivRadialGradientRelativeRadius(
        value = value(value),
    )
}
