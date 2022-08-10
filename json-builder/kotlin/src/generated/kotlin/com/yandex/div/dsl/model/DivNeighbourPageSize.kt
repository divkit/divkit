// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivNeighbourPageSize internal constructor(
    @JsonIgnore val neighbourPageWidth: Property<DivFixedSize>?,
) : DivPagerLayoutMode() {

    @JsonProperty("type") override val type = "fixed"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "neighbour_page_width" to neighbourPageWidth,
        )
    }
}

fun <T> TemplateContext<T>.divNeighbourPageSize(): LiteralProperty<DivNeighbourPageSize> {
    return value(DivNeighbourPageSize(
        neighbourPageWidth = null,
    ))
}

fun <T> TemplateContext<T>.divNeighbourPageSize(
    neighbourPageWidth: Property<DivFixedSize>? = null,
): LiteralProperty<DivNeighbourPageSize> {
    return value(DivNeighbourPageSize(
        neighbourPageWidth = neighbourPageWidth,
    ))
}

fun <T> TemplateContext<T>.divNeighbourPageSize(
    neighbourPageWidth: DivFixedSize? = null,
): LiteralProperty<DivNeighbourPageSize> {
    return value(DivNeighbourPageSize(
        neighbourPageWidth = optionalValue(neighbourPageWidth),
    ))
}

fun CardContext.divNeighbourPageSize(
    neighbourPageWidth: ValueProperty<DivFixedSize>,
): DivNeighbourPageSize {
    return DivNeighbourPageSize(
        neighbourPageWidth = neighbourPageWidth,
    )
}

fun CardContext.divNeighbourPageSize(
    neighbourPageWidth: DivFixedSize,
): DivNeighbourPageSize {
    return DivNeighbourPageSize(
        neighbourPageWidth = value(neighbourPageWidth),
    )
}
