// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivTransform internal constructor(
    @JsonIgnore val pivotX: Property<DivPivot>?,
    @JsonIgnore val pivotY: Property<DivPivot>?,
    @JsonIgnore val rotation: Property<Double>?,
) {

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "pivot_x" to pivotX,
            "pivot_y" to pivotY,
            "rotation" to rotation,
        )
    }
}

fun <T> TemplateContext<T>.divTransform(): LiteralProperty<DivTransform> {
    return value(DivTransform(
        pivotX = null,
        pivotY = null,
        rotation = null,
    ))
}

fun <T> TemplateContext<T>.divTransform(
    pivotX: Property<DivPivot>? = null,
    pivotY: Property<DivPivot>? = null,
    rotation: Property<Double>? = null,
): LiteralProperty<DivTransform> {
    return value(DivTransform(
        pivotX = pivotX,
        pivotY = pivotY,
        rotation = rotation,
    ))
}

fun <T> TemplateContext<T>.divTransform(
    pivotX: DivPivot? = null,
    pivotY: DivPivot? = null,
    rotation: Double? = null,
): LiteralProperty<DivTransform> {
    return value(DivTransform(
        pivotX = optionalValue(pivotX),
        pivotY = optionalValue(pivotY),
        rotation = optionalValue(rotation),
    ))
}

fun CardContext.divTransform(): DivTransform {
    return DivTransform(
        pivotX = null,
        pivotY = null,
        rotation = null,
    )
}

fun CardContext.divTransform(
    pivotX: ValueProperty<DivPivot>? = null,
    pivotY: ValueProperty<DivPivot>? = null,
    rotation: ValueProperty<Double>? = null,
): DivTransform {
    return DivTransform(
        pivotX = pivotX,
        pivotY = pivotY,
        rotation = rotation,
    )
}

fun CardContext.divTransform(
    pivotX: DivPivot? = null,
    pivotY: DivPivot? = null,
    rotation: Double? = null,
): DivTransform {
    return DivTransform(
        pivotX = optionalValue(pivotX),
        pivotY = optionalValue(pivotY),
        rotation = optionalValue(rotation),
    )
}
