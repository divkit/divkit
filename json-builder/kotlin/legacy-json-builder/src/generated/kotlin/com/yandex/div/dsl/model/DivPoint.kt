// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivPoint internal constructor(
    @JsonIgnore val x: Property<DivDimension>?,
    @JsonIgnore val y: Property<DivDimension>?,
) {

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "x" to x,
            "y" to y,
        )
    }
}

fun <T> TemplateContext<T>.divPoint(): LiteralProperty<DivPoint> {
    return value(DivPoint(
        x = null,
        y = null,
    ))
}

fun <T> TemplateContext<T>.divPoint(
    x: Property<DivDimension>? = null,
    y: Property<DivDimension>? = null,
): LiteralProperty<DivPoint> {
    return value(DivPoint(
        x = x,
        y = y,
    ))
}

fun <T> TemplateContext<T>.divPoint(
    x: DivDimension? = null,
    y: DivDimension? = null,
): LiteralProperty<DivPoint> {
    return value(DivPoint(
        x = optionalValue(x),
        y = optionalValue(y),
    ))
}

fun CardContext.divPoint(
    x: ValueProperty<DivDimension>,
    y: ValueProperty<DivDimension>,
): DivPoint {
    return DivPoint(
        x = x,
        y = y,
    )
}

fun CardContext.divPoint(
    x: DivDimension,
    y: DivDimension,
): DivPoint {
    return DivPoint(
        x = value(x),
        y = value(y),
    )
}
