// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivWrapContentSize internal constructor(
    @JsonIgnore val constrained: Property<BoolInt>?,
) : DivSize {

    @JsonProperty("type") override val type = "wrap_content"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "constrained" to constrained,
        )
    }
}

fun <T> TemplateContext<T>.divWrapContentSize(): LiteralProperty<DivWrapContentSize> {
    return value(DivWrapContentSize(
        constrained = null,
    ))
}

fun <T> TemplateContext<T>.divWrapContentSize(
    constrained: Property<BoolInt>? = null,
): LiteralProperty<DivWrapContentSize> {
    return value(DivWrapContentSize(
        constrained = constrained,
    ))
}

fun <T> TemplateContext<T>.divWrapContentSize(
    constrained: BoolInt? = null,
): LiteralProperty<DivWrapContentSize> {
    return value(DivWrapContentSize(
        constrained = optionalValue(constrained),
    ))
}

fun CardContext.divWrapContentSize(): DivWrapContentSize {
    return DivWrapContentSize(
        constrained = null,
    )
}

fun CardContext.divWrapContentSize(
    constrained: ValueProperty<BoolInt>? = null,
): DivWrapContentSize {
    return DivWrapContentSize(
        constrained = constrained,
    )
}

fun CardContext.divWrapContentSize(
    constrained: BoolInt? = null,
): DivWrapContentSize {
    return DivWrapContentSize(
        constrained = optionalValue(constrained),
    )
}
