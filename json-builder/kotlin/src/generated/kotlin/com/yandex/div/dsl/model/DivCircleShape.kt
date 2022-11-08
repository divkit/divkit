// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivCircleShape internal constructor(
    @JsonIgnore val radius: Property<DivFixedSize>?,
) : DivShape {

    @JsonProperty("type") override val type = "circle"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "radius" to radius,
        )
    }
}

fun <T> TemplateContext<T>.divCircleShape(): LiteralProperty<DivCircleShape> {
    return value(DivCircleShape(
        radius = null,
    ))
}

fun <T> TemplateContext<T>.divCircleShape(
    radius: Property<DivFixedSize>? = null,
): LiteralProperty<DivCircleShape> {
    return value(DivCircleShape(
        radius = radius,
    ))
}

fun <T> TemplateContext<T>.divCircleShape(
    radius: DivFixedSize? = null,
): LiteralProperty<DivCircleShape> {
    return value(DivCircleShape(
        radius = optionalValue(radius),
    ))
}

fun CardContext.divCircleShape(): DivCircleShape {
    return DivCircleShape(
        radius = null,
    )
}

fun CardContext.divCircleShape(
    radius: ValueProperty<DivFixedSize>? = null,
): DivCircleShape {
    return DivCircleShape(
        radius = radius,
    )
}

fun CardContext.divCircleShape(
    radius: DivFixedSize? = null,
): DivCircleShape {
    return DivCircleShape(
        radius = optionalValue(radius),
    )
}
