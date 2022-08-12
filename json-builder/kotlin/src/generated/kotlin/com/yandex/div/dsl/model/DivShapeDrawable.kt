// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivShapeDrawable internal constructor(
    @JsonIgnore val color: Property<Color>?,
    @JsonIgnore val shape: Property<DivShape>?,
    @JsonIgnore val stroke: Property<DivStroke>?,
) : DivDrawable() {

    @JsonProperty("type") override val type = "shape_drawable"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "color" to color,
            "shape" to shape,
            "stroke" to stroke,
        )
    }
}

fun <T> TemplateContext<T>.divShapeDrawable(): LiteralProperty<DivShapeDrawable> {
    return value(DivShapeDrawable(
        color = null,
        shape = null,
        stroke = null,
    ))
}

fun <T> TemplateContext<T>.divShapeDrawable(
    color: Property<Color>? = null,
    shape: Property<DivShape>? = null,
    stroke: Property<DivStroke>? = null,
): LiteralProperty<DivShapeDrawable> {
    return value(DivShapeDrawable(
        color = color,
        shape = shape,
        stroke = stroke,
    ))
}

fun <T> TemplateContext<T>.divShapeDrawable(
    color: Color? = null,
    shape: DivShape? = null,
    stroke: DivStroke? = null,
): LiteralProperty<DivShapeDrawable> {
    return value(DivShapeDrawable(
        color = optionalValue(color),
        shape = optionalValue(shape),
        stroke = optionalValue(stroke),
    ))
}

fun CardContext.divShapeDrawable(
    color: ValueProperty<Color>,
    shape: ValueProperty<DivShape>,
    stroke: ValueProperty<DivStroke>? = null,
): DivShapeDrawable {
    return DivShapeDrawable(
        color = color,
        shape = shape,
        stroke = stroke,
    )
}

fun CardContext.divShapeDrawable(
    color: Color,
    shape: DivShape,
    stroke: DivStroke? = null,
): DivShapeDrawable {
    return DivShapeDrawable(
        color = value(color),
        shape = value(shape),
        stroke = optionalValue(stroke),
    )
}
