// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivAppearanceSetTransition internal constructor(
    @JsonIgnore val items: Property<List<DivAppearanceTransition>>?,
) : DivAppearanceTransition {

    @JsonProperty("type") override val type = "set"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "items" to items,
        )
    }
}

fun <T> TemplateContext<T>.divAppearanceSetTransition(): LiteralProperty<DivAppearanceSetTransition> {
    return value(DivAppearanceSetTransition(
        items = null,
    ))
}

fun <T> TemplateContext<T>.divAppearanceSetTransition(
    items: Property<List<DivAppearanceTransition>>? = null,
): LiteralProperty<DivAppearanceSetTransition> {
    return value(DivAppearanceSetTransition(
        items = items,
    ))
}

fun <T> TemplateContext<T>.divAppearanceSetTransition(
    items: List<DivAppearanceTransition>? = null,
): LiteralProperty<DivAppearanceSetTransition> {
    return value(DivAppearanceSetTransition(
        items = optionalValue(items),
    ))
}

fun CardContext.divAppearanceSetTransition(
    items: ValueProperty<List<DivAppearanceTransition>>,
): DivAppearanceSetTransition {
    return DivAppearanceSetTransition(
        items = items,
    )
}

fun CardContext.divAppearanceSetTransition(
    items: List<DivAppearanceTransition>,
): DivAppearanceSetTransition {
    return DivAppearanceSetTransition(
        items = value(items),
    )
}
