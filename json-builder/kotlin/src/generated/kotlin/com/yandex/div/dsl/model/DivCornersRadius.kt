// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivCornersRadius internal constructor(
    @JsonIgnore val bottomLeft: Property<Int>?,
    @JsonIgnore val bottomRight: Property<Int>?,
    @JsonIgnore val topLeft: Property<Int>?,
    @JsonIgnore val topRight: Property<Int>?,
) {

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "bottom-left" to bottomLeft,
            "bottom-right" to bottomRight,
            "top-left" to topLeft,
            "top-right" to topRight,
        )
    }
}

fun <T> TemplateContext<T>.divCornersRadius(): LiteralProperty<DivCornersRadius> {
    return value(DivCornersRadius(
        bottomLeft = null,
        bottomRight = null,
        topLeft = null,
        topRight = null,
    ))
}

fun <T> TemplateContext<T>.divCornersRadius(
    bottomLeft: Property<Int>? = null,
    bottomRight: Property<Int>? = null,
    topLeft: Property<Int>? = null,
    topRight: Property<Int>? = null,
): LiteralProperty<DivCornersRadius> {
    return value(DivCornersRadius(
        bottomLeft = bottomLeft,
        bottomRight = bottomRight,
        topLeft = topLeft,
        topRight = topRight,
    ))
}

fun <T> TemplateContext<T>.divCornersRadius(
    bottomLeft: Int? = null,
    bottomRight: Int? = null,
    topLeft: Int? = null,
    topRight: Int? = null,
): LiteralProperty<DivCornersRadius> {
    return value(DivCornersRadius(
        bottomLeft = optionalValue(bottomLeft),
        bottomRight = optionalValue(bottomRight),
        topLeft = optionalValue(topLeft),
        topRight = optionalValue(topRight),
    ))
}

fun CardContext.divCornersRadius(): DivCornersRadius {
    return DivCornersRadius(
        bottomLeft = null,
        bottomRight = null,
        topLeft = null,
        topRight = null,
    )
}

fun CardContext.divCornersRadius(
    bottomLeft: ValueProperty<Int>? = null,
    bottomRight: ValueProperty<Int>? = null,
    topLeft: ValueProperty<Int>? = null,
    topRight: ValueProperty<Int>? = null,
): DivCornersRadius {
    return DivCornersRadius(
        bottomLeft = bottomLeft,
        bottomRight = bottomRight,
        topLeft = topLeft,
        topRight = topRight,
    )
}

fun CardContext.divCornersRadius(
    bottomLeft: Int? = null,
    bottomRight: Int? = null,
    topLeft: Int? = null,
    topRight: Int? = null,
): DivCornersRadius {
    return DivCornersRadius(
        bottomLeft = optionalValue(bottomLeft),
        bottomRight = optionalValue(bottomRight),
        topLeft = optionalValue(topLeft),
        topRight = optionalValue(topRight),
    )
}
