// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivSlideTransition internal constructor(
    @JsonIgnore val distance: Property<DivDimension>?,
    @JsonIgnore override val duration: Property<Int>?,
    @JsonIgnore val edge: Property<Edge>?,
    @JsonIgnore override val interpolator: Property<DivAnimationInterpolator>?,
    @JsonIgnore override val startDelay: Property<Int>?,
) : DivAppearanceTransition(), DivTransitionBase {

    @JsonProperty("type") override val type = "slide"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "distance" to distance,
            "duration" to duration,
            "edge" to edge,
            "interpolator" to interpolator,
            "start_delay" to startDelay,
        )
    }

    enum class Edge(@JsonValue val value: String) {
        LEFT("left"),
        TOP("top"),
        RIGHT("right"),
        BOTTOM("bottom"),
    }
}

fun <T> TemplateContext<T>.divSlideTransition(): LiteralProperty<DivSlideTransition> {
    return value(DivSlideTransition(
        distance = null,
        duration = null,
        edge = null,
        interpolator = null,
        startDelay = null,
    ))
}

fun <T> TemplateContext<T>.divSlideTransition(
    distance: Property<DivDimension>? = null,
    duration: Property<Int>? = null,
    edge: Property<DivSlideTransition.Edge>? = null,
    interpolator: Property<DivAnimationInterpolator>? = null,
    startDelay: Property<Int>? = null,
): LiteralProperty<DivSlideTransition> {
    return value(DivSlideTransition(
        distance = distance,
        duration = duration,
        edge = edge,
        interpolator = interpolator,
        startDelay = startDelay,
    ))
}

fun <T> TemplateContext<T>.divSlideTransition(
    distance: DivDimension? = null,
    duration: Int? = null,
    edge: DivSlideTransition.Edge? = null,
    interpolator: DivAnimationInterpolator? = null,
    startDelay: Int? = null,
): LiteralProperty<DivSlideTransition> {
    return value(DivSlideTransition(
        distance = optionalValue(distance),
        duration = optionalValue(duration),
        edge = optionalValue(edge),
        interpolator = optionalValue(interpolator),
        startDelay = optionalValue(startDelay),
    ))
}

fun CardContext.divSlideTransition(): DivSlideTransition {
    return DivSlideTransition(
        distance = null,
        duration = null,
        edge = null,
        interpolator = null,
        startDelay = null,
    )
}

fun CardContext.divSlideTransition(
    distance: ValueProperty<DivDimension>? = null,
    duration: ValueProperty<Int>? = null,
    edge: ValueProperty<DivSlideTransition.Edge>? = null,
    interpolator: ValueProperty<DivAnimationInterpolator>? = null,
    startDelay: ValueProperty<Int>? = null,
): DivSlideTransition {
    return DivSlideTransition(
        distance = distance,
        duration = duration,
        edge = edge,
        interpolator = interpolator,
        startDelay = startDelay,
    )
}

fun CardContext.divSlideTransition(
    distance: DivDimension? = null,
    duration: Int? = null,
    edge: DivSlideTransition.Edge? = null,
    interpolator: DivAnimationInterpolator? = null,
    startDelay: Int? = null,
): DivSlideTransition {
    return DivSlideTransition(
        distance = optionalValue(distance),
        duration = optionalValue(duration),
        edge = optionalValue(edge),
        interpolator = optionalValue(interpolator),
        startDelay = optionalValue(startDelay),
    )
}
