// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivScaleTransition internal constructor(
    @JsonIgnore override val duration: Property<Int>?,
    @JsonIgnore override val interpolator: Property<DivAnimationInterpolator>?,
    @JsonIgnore val pivotX: Property<Double>?,
    @JsonIgnore val pivotY: Property<Double>?,
    @JsonIgnore val scale: Property<Double>?,
    @JsonIgnore override val startDelay: Property<Int>?,
) : DivAppearanceTransition(), DivTransitionBase {

    @JsonProperty("type") override val type = "scale"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "duration" to duration,
            "interpolator" to interpolator,
            "pivot_x" to pivotX,
            "pivot_y" to pivotY,
            "scale" to scale,
            "start_delay" to startDelay,
        )
    }
}

fun <T> TemplateContext<T>.divScaleTransition(): LiteralProperty<DivScaleTransition> {
    return value(DivScaleTransition(
        duration = null,
        interpolator = null,
        pivotX = null,
        pivotY = null,
        scale = null,
        startDelay = null,
    ))
}

fun <T> TemplateContext<T>.divScaleTransition(
    duration: Property<Int>? = null,
    interpolator: Property<DivAnimationInterpolator>? = null,
    pivotX: Property<Double>? = null,
    pivotY: Property<Double>? = null,
    scale: Property<Double>? = null,
    startDelay: Property<Int>? = null,
): LiteralProperty<DivScaleTransition> {
    return value(DivScaleTransition(
        duration = duration,
        interpolator = interpolator,
        pivotX = pivotX,
        pivotY = pivotY,
        scale = scale,
        startDelay = startDelay,
    ))
}

fun <T> TemplateContext<T>.divScaleTransition(
    duration: Int? = null,
    interpolator: DivAnimationInterpolator? = null,
    pivotX: Double? = null,
    pivotY: Double? = null,
    scale: Double? = null,
    startDelay: Int? = null,
): LiteralProperty<DivScaleTransition> {
    return value(DivScaleTransition(
        duration = optionalValue(duration),
        interpolator = optionalValue(interpolator),
        pivotX = optionalValue(pivotX),
        pivotY = optionalValue(pivotY),
        scale = optionalValue(scale),
        startDelay = optionalValue(startDelay),
    ))
}

fun CardContext.divScaleTransition(): DivScaleTransition {
    return DivScaleTransition(
        duration = null,
        interpolator = null,
        pivotX = null,
        pivotY = null,
        scale = null,
        startDelay = null,
    )
}

fun CardContext.divScaleTransition(
    duration: ValueProperty<Int>? = null,
    interpolator: ValueProperty<DivAnimationInterpolator>? = null,
    pivotX: ValueProperty<Double>? = null,
    pivotY: ValueProperty<Double>? = null,
    scale: ValueProperty<Double>? = null,
    startDelay: ValueProperty<Int>? = null,
): DivScaleTransition {
    return DivScaleTransition(
        duration = duration,
        interpolator = interpolator,
        pivotX = pivotX,
        pivotY = pivotY,
        scale = scale,
        startDelay = startDelay,
    )
}

fun CardContext.divScaleTransition(
    duration: Int? = null,
    interpolator: DivAnimationInterpolator? = null,
    pivotX: Double? = null,
    pivotY: Double? = null,
    scale: Double? = null,
    startDelay: Int? = null,
): DivScaleTransition {
    return DivScaleTransition(
        duration = optionalValue(duration),
        interpolator = optionalValue(interpolator),
        pivotX = optionalValue(pivotX),
        pivotY = optionalValue(pivotY),
        scale = optionalValue(scale),
        startDelay = optionalValue(startDelay),
    )
}
