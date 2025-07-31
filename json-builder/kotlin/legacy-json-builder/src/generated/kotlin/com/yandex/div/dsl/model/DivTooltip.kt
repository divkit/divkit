// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivTooltip internal constructor(
    @JsonIgnore val animationIn: Property<DivAnimation>?,
    @JsonIgnore val animationOut: Property<DivAnimation>?,
    @JsonIgnore val div: Property<Div>?,
    @JsonIgnore val duration: Property<Int>?,
    @JsonIgnore val id: Property<String>?,
    @JsonIgnore val offset: Property<DivPoint>?,
    @JsonIgnore val position: Property<Position>?,
) {

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "animation_in" to animationIn,
            "animation_out" to animationOut,
            "div" to div,
            "duration" to duration,
            "id" to id,
            "offset" to offset,
            "position" to position,
        )
    }

    enum class Position(@JsonValue val value: String) {
        LEFT("left"),
        TOP_LEFT("top-left"),
        TOP("top"),
        TOP_RIGHT("top-right"),
        RIGHT("right"),
        BOTTOM_RIGHT("bottom-right"),
        BOTTOM("bottom"),
        BOTTOM_LEFT("bottom-left"),
    }
}

fun <T> TemplateContext<T>.divTooltip(): LiteralProperty<DivTooltip> {
    return value(DivTooltip(
        animationIn = null,
        animationOut = null,
        div = null,
        duration = null,
        id = null,
        offset = null,
        position = null,
    ))
}

fun <T> TemplateContext<T>.divTooltip(
    div: Property<Div>? = null,
    id: Property<String>? = null,
    position: Property<DivTooltip.Position>? = null,
    animationIn: Property<DivAnimation>? = null,
    animationOut: Property<DivAnimation>? = null,
    duration: Property<Int>? = null,
    offset: Property<DivPoint>? = null,
): LiteralProperty<DivTooltip> {
    return value(DivTooltip(
        animationIn = animationIn,
        animationOut = animationOut,
        div = div,
        duration = duration,
        id = id,
        offset = offset,
        position = position,
    ))
}

fun <T> TemplateContext<T>.divTooltip(
    div: Div? = null,
    id: String? = null,
    position: DivTooltip.Position? = null,
    animationIn: DivAnimation? = null,
    animationOut: DivAnimation? = null,
    duration: Int? = null,
    offset: DivPoint? = null,
): LiteralProperty<DivTooltip> {
    return value(DivTooltip(
        animationIn = optionalValue(animationIn),
        animationOut = optionalValue(animationOut),
        div = optionalValue(div),
        duration = optionalValue(duration),
        id = optionalValue(id),
        offset = optionalValue(offset),
        position = optionalValue(position),
    ))
}

fun CardContext.divTooltip(
    div: ValueProperty<Div>,
    id: ValueProperty<String>,
    position: ValueProperty<DivTooltip.Position>,
    animationIn: ValueProperty<DivAnimation>? = null,
    animationOut: ValueProperty<DivAnimation>? = null,
    duration: ValueProperty<Int>? = null,
    offset: ValueProperty<DivPoint>? = null,
): DivTooltip {
    return DivTooltip(
        animationIn = animationIn,
        animationOut = animationOut,
        div = div,
        duration = duration,
        id = id,
        offset = offset,
        position = position,
    )
}

fun CardContext.divTooltip(
    div: Div,
    id: String,
    position: DivTooltip.Position,
    animationIn: DivAnimation? = null,
    animationOut: DivAnimation? = null,
    duration: Int? = null,
    offset: DivPoint? = null,
): DivTooltip {
    return DivTooltip(
        animationIn = optionalValue(animationIn),
        animationOut = optionalValue(animationOut),
        div = value(div),
        duration = optionalValue(duration),
        id = value(id),
        offset = optionalValue(offset),
        position = value(position),
    )
}
