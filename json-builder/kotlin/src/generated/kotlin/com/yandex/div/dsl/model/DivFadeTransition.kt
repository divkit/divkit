// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivFadeTransition internal constructor(
    @JsonIgnore val alpha: Property<Double>?,
    @JsonIgnore override val duration: Property<Int>?,
    @JsonIgnore override val interpolator: Property<DivAnimationInterpolator>?,
    @JsonIgnore override val startDelay: Property<Int>?,
) : DivAppearanceTransition, DivTransitionBase {

    @JsonProperty("type") override val type = "fade"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "alpha" to alpha,
            "duration" to duration,
            "interpolator" to interpolator,
            "start_delay" to startDelay,
        )
    }
}

fun <T> TemplateContext<T>.divFadeTransition(): LiteralProperty<DivFadeTransition> {
    return value(DivFadeTransition(
        alpha = null,
        duration = null,
        interpolator = null,
        startDelay = null,
    ))
}

fun <T> TemplateContext<T>.divFadeTransition(
    alpha: Property<Double>? = null,
    duration: Property<Int>? = null,
    interpolator: Property<DivAnimationInterpolator>? = null,
    startDelay: Property<Int>? = null,
): LiteralProperty<DivFadeTransition> {
    return value(DivFadeTransition(
        alpha = alpha,
        duration = duration,
        interpolator = interpolator,
        startDelay = startDelay,
    ))
}

fun <T> TemplateContext<T>.divFadeTransition(
    alpha: Double? = null,
    duration: Int? = null,
    interpolator: DivAnimationInterpolator? = null,
    startDelay: Int? = null,
): LiteralProperty<DivFadeTransition> {
    return value(DivFadeTransition(
        alpha = optionalValue(alpha),
        duration = optionalValue(duration),
        interpolator = optionalValue(interpolator),
        startDelay = optionalValue(startDelay),
    ))
}

fun CardContext.divFadeTransition(): DivFadeTransition {
    return DivFadeTransition(
        alpha = null,
        duration = null,
        interpolator = null,
        startDelay = null,
    )
}

fun CardContext.divFadeTransition(
    alpha: ValueProperty<Double>? = null,
    duration: ValueProperty<Int>? = null,
    interpolator: ValueProperty<DivAnimationInterpolator>? = null,
    startDelay: ValueProperty<Int>? = null,
): DivFadeTransition {
    return DivFadeTransition(
        alpha = alpha,
        duration = duration,
        interpolator = interpolator,
        startDelay = startDelay,
    )
}

fun CardContext.divFadeTransition(
    alpha: Double? = null,
    duration: Int? = null,
    interpolator: DivAnimationInterpolator? = null,
    startDelay: Int? = null,
): DivFadeTransition {
    return DivFadeTransition(
        alpha = optionalValue(alpha),
        duration = optionalValue(duration),
        interpolator = optionalValue(interpolator),
        startDelay = optionalValue(startDelay),
    )
}
