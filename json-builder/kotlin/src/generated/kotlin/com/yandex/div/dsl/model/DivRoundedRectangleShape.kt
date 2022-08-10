// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivRoundedRectangleShape internal constructor(
    @JsonIgnore val cornerRadius: Property<DivFixedSize>?,
    @JsonIgnore val itemHeight: Property<DivFixedSize>?,
    @JsonIgnore val itemWidth: Property<DivFixedSize>?,
) : DivShape() {

    @JsonProperty("type") override val type = "rounded_rectangle"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "corner_radius" to cornerRadius,
            "item_height" to itemHeight,
            "item_width" to itemWidth,
        )
    }
}

fun <T> TemplateContext<T>.divRoundedRectangleShape(): LiteralProperty<DivRoundedRectangleShape> {
    return value(DivRoundedRectangleShape(
        cornerRadius = null,
        itemHeight = null,
        itemWidth = null,
    ))
}

fun <T> TemplateContext<T>.divRoundedRectangleShape(
    cornerRadius: Property<DivFixedSize>? = null,
    itemHeight: Property<DivFixedSize>? = null,
    itemWidth: Property<DivFixedSize>? = null,
): LiteralProperty<DivRoundedRectangleShape> {
    return value(DivRoundedRectangleShape(
        cornerRadius = cornerRadius,
        itemHeight = itemHeight,
        itemWidth = itemWidth,
    ))
}

fun <T> TemplateContext<T>.divRoundedRectangleShape(
    cornerRadius: DivFixedSize? = null,
    itemHeight: DivFixedSize? = null,
    itemWidth: DivFixedSize? = null,
): LiteralProperty<DivRoundedRectangleShape> {
    return value(DivRoundedRectangleShape(
        cornerRadius = optionalValue(cornerRadius),
        itemHeight = optionalValue(itemHeight),
        itemWidth = optionalValue(itemWidth),
    ))
}

fun CardContext.divRoundedRectangleShape(): DivRoundedRectangleShape {
    return DivRoundedRectangleShape(
        cornerRadius = null,
        itemHeight = null,
        itemWidth = null,
    )
}

fun CardContext.divRoundedRectangleShape(
    cornerRadius: ValueProperty<DivFixedSize>? = null,
    itemHeight: ValueProperty<DivFixedSize>? = null,
    itemWidth: ValueProperty<DivFixedSize>? = null,
): DivRoundedRectangleShape {
    return DivRoundedRectangleShape(
        cornerRadius = cornerRadius,
        itemHeight = itemHeight,
        itemWidth = itemWidth,
    )
}

fun CardContext.divRoundedRectangleShape(
    cornerRadius: DivFixedSize? = null,
    itemHeight: DivFixedSize? = null,
    itemWidth: DivFixedSize? = null,
): DivRoundedRectangleShape {
    return DivRoundedRectangleShape(
        cornerRadius = optionalValue(cornerRadius),
        itemHeight = optionalValue(itemHeight),
        itemWidth = optionalValue(itemWidth),
    )
}
