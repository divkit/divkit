// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivAnimation internal constructor(
    @JsonIgnore val duration: Property<Int>?,
    @JsonIgnore val endValue: Property<Double>?,
    @JsonIgnore val interpolator: Property<DivAnimationInterpolator>?,
    @JsonIgnore val items: Property<List<DivAnimation>>?,
    @JsonIgnore val name: Property<Name>?,
    @JsonIgnore val repeat: Property<DivCount>?,
    @JsonIgnore val startDelay: Property<Int>?,
    @JsonIgnore val startValue: Property<Double>?,
) {

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "duration" to duration,
            "end_value" to endValue,
            "interpolator" to interpolator,
            "items" to items,
            "name" to name,
            "repeat" to repeat,
            "start_delay" to startDelay,
            "start_value" to startValue,
        )
    }

    enum class Name(@JsonValue val value: String) {
        FADE("fade"),
        TRANSLATE("translate"),
        SCALE("scale"),
        NATIVE("native"),
        SET("set"),
        NO_ANIMATION("no_animation"),
    }
}

fun <T> TemplateContext<T>.divAnimation(): LiteralProperty<DivAnimation> {
    return value(DivAnimation(
        duration = null,
        endValue = null,
        interpolator = null,
        items = null,
        name = null,
        repeat = null,
        startDelay = null,
        startValue = null,
    ))
}

fun <T> TemplateContext<T>.divAnimation(
    name: Property<DivAnimation.Name>? = null,
    duration: Property<Int>? = null,
    endValue: Property<Double>? = null,
    interpolator: Property<DivAnimationInterpolator>? = null,
    items: Property<List<DivAnimation>>? = null,
    repeat: Property<DivCount>? = null,
    startDelay: Property<Int>? = null,
    startValue: Property<Double>? = null,
): LiteralProperty<DivAnimation> {
    return value(DivAnimation(
        duration = duration,
        endValue = endValue,
        interpolator = interpolator,
        items = items,
        name = name,
        repeat = repeat,
        startDelay = startDelay,
        startValue = startValue,
    ))
}

fun <T> TemplateContext<T>.divAnimation(
    name: DivAnimation.Name? = null,
    duration: Int? = null,
    endValue: Double? = null,
    interpolator: DivAnimationInterpolator? = null,
    items: List<DivAnimation>? = null,
    repeat: DivCount? = null,
    startDelay: Int? = null,
    startValue: Double? = null,
): LiteralProperty<DivAnimation> {
    return value(DivAnimation(
        duration = optionalValue(duration),
        endValue = optionalValue(endValue),
        interpolator = optionalValue(interpolator),
        items = optionalValue(items),
        name = optionalValue(name),
        repeat = optionalValue(repeat),
        startDelay = optionalValue(startDelay),
        startValue = optionalValue(startValue),
    ))
}

fun CardContext.divAnimation(
    name: ValueProperty<DivAnimation.Name>,
    duration: ValueProperty<Int>? = null,
    endValue: ValueProperty<Double>? = null,
    interpolator: ValueProperty<DivAnimationInterpolator>? = null,
    items: ValueProperty<List<DivAnimation>>? = null,
    repeat: ValueProperty<DivCount>? = null,
    startDelay: ValueProperty<Int>? = null,
    startValue: ValueProperty<Double>? = null,
): DivAnimation {
    return DivAnimation(
        duration = duration,
        endValue = endValue,
        interpolator = interpolator,
        items = items,
        name = name,
        repeat = repeat,
        startDelay = startDelay,
        startValue = startValue,
    )
}

fun CardContext.divAnimation(
    name: DivAnimation.Name,
    duration: Int? = null,
    endValue: Double? = null,
    interpolator: DivAnimationInterpolator? = null,
    items: List<DivAnimation>? = null,
    repeat: DivCount? = null,
    startDelay: Int? = null,
    startValue: Double? = null,
): DivAnimation {
    return DivAnimation(
        duration = optionalValue(duration),
        endValue = optionalValue(endValue),
        interpolator = optionalValue(interpolator),
        items = optionalValue(items),
        name = value(name),
        repeat = optionalValue(repeat),
        startDelay = optionalValue(startDelay),
        startValue = optionalValue(startValue),
    )
}
