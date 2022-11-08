// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivPageSize internal constructor(
    @JsonIgnore val pageWidth: Property<DivPercentageSize>?,
) : DivPagerLayoutMode {

    @JsonProperty("type") override val type = "percentage"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "page_width" to pageWidth,
        )
    }
}

fun <T> TemplateContext<T>.divPageSize(): LiteralProperty<DivPageSize> {
    return value(DivPageSize(
        pageWidth = null,
    ))
}

fun <T> TemplateContext<T>.divPageSize(
    pageWidth: Property<DivPercentageSize>? = null,
): LiteralProperty<DivPageSize> {
    return value(DivPageSize(
        pageWidth = pageWidth,
    ))
}

fun <T> TemplateContext<T>.divPageSize(
    pageWidth: DivPercentageSize? = null,
): LiteralProperty<DivPageSize> {
    return value(DivPageSize(
        pageWidth = optionalValue(pageWidth),
    ))
}

fun CardContext.divPageSize(
    pageWidth: ValueProperty<DivPercentageSize>,
): DivPageSize {
    return DivPageSize(
        pageWidth = pageWidth,
    )
}

fun CardContext.divPageSize(
    pageWidth: DivPercentageSize,
): DivPageSize {
    return DivPageSize(
        pageWidth = value(pageWidth),
    )
}
