// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class ColorVariable internal constructor(
    @JsonIgnore val name: Property<String>?,
    @JsonIgnore val value: Property<Color>?,
) : DivVariable() {

    @JsonProperty("type") override val type = "color"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "name" to name,
            "value" to value,
        )
    }
}

fun <T> TemplateContext<T>.colorVariable(): LiteralProperty<ColorVariable> {
    return value(ColorVariable(
        name = null,
        value = null,
    ))
}

fun <T> TemplateContext<T>.colorVariable(
    name: Property<String>? = null,
    value: Property<Color>? = null,
): LiteralProperty<ColorVariable> {
    return value(ColorVariable(
        name = name,
        value = value,
    ))
}

fun <T> TemplateContext<T>.colorVariable(
    name: String? = null,
    value: Color? = null,
): LiteralProperty<ColorVariable> {
    return value(ColorVariable(
        name = optionalValue(name),
        value = optionalValue(value),
    ))
}

fun CardContext.colorVariable(
    name: ValueProperty<String>,
    value: ValueProperty<Color>,
): ColorVariable {
    return ColorVariable(
        name = name,
        value = value,
    )
}

fun CardContext.colorVariable(
    name: String,
    value: Color,
): ColorVariable {
    return ColorVariable(
        name = value(name),
        value = value(value),
    )
}
