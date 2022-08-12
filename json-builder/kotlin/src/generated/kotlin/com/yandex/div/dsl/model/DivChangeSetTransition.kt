// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivChangeSetTransition internal constructor(
    @JsonIgnore val items: Property<List<DivChangeTransition>>?,
) : DivChangeTransition() {

    @JsonProperty("type") override val type = "set"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "items" to items,
        )
    }
}

fun <T> TemplateContext<T>.divChangeSetTransition(): LiteralProperty<DivChangeSetTransition> {
    return value(DivChangeSetTransition(
        items = null,
    ))
}

fun <T> TemplateContext<T>.divChangeSetTransition(
    items: Property<List<DivChangeTransition>>? = null,
): LiteralProperty<DivChangeSetTransition> {
    return value(DivChangeSetTransition(
        items = items,
    ))
}

fun <T> TemplateContext<T>.divChangeSetTransition(
    items: List<DivChangeTransition>? = null,
): LiteralProperty<DivChangeSetTransition> {
    return value(DivChangeSetTransition(
        items = optionalValue(items),
    ))
}

fun CardContext.divChangeSetTransition(
    items: ValueProperty<List<DivChangeTransition>>,
): DivChangeSetTransition {
    return DivChangeSetTransition(
        items = items,
    )
}

fun CardContext.divChangeSetTransition(
    items: List<DivChangeTransition>,
): DivChangeSetTransition {
    return DivChangeSetTransition(
        items = value(items),
    )
}
