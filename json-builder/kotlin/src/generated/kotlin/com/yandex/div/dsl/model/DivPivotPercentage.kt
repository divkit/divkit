// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivPivotPercentage internal constructor(
    @JsonIgnore val value: Property<Double>?,
) : DivPivot {

    @JsonProperty("type") override val type = "pivot-percentage"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "value" to value,
        )
    }
}

fun <T> TemplateContext<T>.divPivotPercentage(): LiteralProperty<DivPivotPercentage> {
    return value(DivPivotPercentage(
        value = null,
    ))
}

fun <T> TemplateContext<T>.divPivotPercentage(
    value: Property<Double>? = null,
): LiteralProperty<DivPivotPercentage> {
    return value(DivPivotPercentage(
        value = value,
    ))
}

fun <T> TemplateContext<T>.divPivotPercentage(
    value: Double? = null,
): LiteralProperty<DivPivotPercentage> {
    return value(DivPivotPercentage(
        value = optionalValue(value),
    ))
}

fun CardContext.divPivotPercentage(
    value: ValueProperty<Double>,
): DivPivotPercentage {
    return DivPivotPercentage(
        value = value,
    )
}

fun CardContext.divPivotPercentage(
    value: Double,
): DivPivotPercentage {
    return DivPivotPercentage(
        value = value(value),
    )
}
