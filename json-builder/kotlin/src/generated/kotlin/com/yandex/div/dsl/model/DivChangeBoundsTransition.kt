// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivChangeBoundsTransition internal constructor(
    @JsonIgnore override val duration: Property<Int>?,
    @JsonIgnore override val interpolator: Property<DivAnimationInterpolator>?,
    @JsonIgnore override val startDelay: Property<Int>?,
) : DivChangeTransition(), DivTransitionBase {

    @JsonProperty("type") override val type = "change_bounds"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "duration" to duration,
            "interpolator" to interpolator,
            "start_delay" to startDelay,
        )
    }
}

fun <T> TemplateContext<T>.divChangeBoundsTransition(): LiteralProperty<DivChangeBoundsTransition> {
    return value(DivChangeBoundsTransition(
        duration = null,
        interpolator = null,
        startDelay = null,
    ))
}

fun <T> TemplateContext<T>.divChangeBoundsTransition(
    duration: Property<Int>? = null,
    interpolator: Property<DivAnimationInterpolator>? = null,
    startDelay: Property<Int>? = null,
): LiteralProperty<DivChangeBoundsTransition> {
    return value(DivChangeBoundsTransition(
        duration = duration,
        interpolator = interpolator,
        startDelay = startDelay,
    ))
}

fun <T> TemplateContext<T>.divChangeBoundsTransition(
    duration: Int? = null,
    interpolator: DivAnimationInterpolator? = null,
    startDelay: Int? = null,
): LiteralProperty<DivChangeBoundsTransition> {
    return value(DivChangeBoundsTransition(
        duration = optionalValue(duration),
        interpolator = optionalValue(interpolator),
        startDelay = optionalValue(startDelay),
    ))
}

fun CardContext.divChangeBoundsTransition(): DivChangeBoundsTransition {
    return DivChangeBoundsTransition(
        duration = null,
        interpolator = null,
        startDelay = null,
    )
}

fun CardContext.divChangeBoundsTransition(
    duration: ValueProperty<Int>? = null,
    interpolator: ValueProperty<DivAnimationInterpolator>? = null,
    startDelay: ValueProperty<Int>? = null,
): DivChangeBoundsTransition {
    return DivChangeBoundsTransition(
        duration = duration,
        interpolator = interpolator,
        startDelay = startDelay,
    )
}

fun CardContext.divChangeBoundsTransition(
    duration: Int? = null,
    interpolator: DivAnimationInterpolator? = null,
    startDelay: Int? = null,
): DivChangeBoundsTransition {
    return DivChangeBoundsTransition(
        duration = optionalValue(duration),
        interpolator = optionalValue(interpolator),
        startDelay = optionalValue(startDelay),
    )
}
