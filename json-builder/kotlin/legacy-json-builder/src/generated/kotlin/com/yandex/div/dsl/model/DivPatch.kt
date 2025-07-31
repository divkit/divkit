// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivPatch internal constructor(
    @JsonIgnore val changes: Property<List<Change>>?,
    @JsonIgnore val mode: Property<Mode>?,
) {

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "changes" to changes,
            "mode" to mode,
        )
    }

    enum class Mode(@JsonValue val value: String) {
        TRANSACTIONAL("transactional"),
        PARTIAL("partial"),
    }

    class Change internal constructor(
        @JsonIgnore val id: Property<String>?,
        @JsonIgnore val items: Property<List<Div>>?,
    ) {

        @JsonAnyGetter
        internal fun properties(): Map<String, Any> {
            return propertyMapOf(
                "id" to id,
                "items" to items,
            )
        }
    }
}

fun <T> TemplateContext<T>.divPatch(): LiteralProperty<DivPatch> {
    return value(DivPatch(
        changes = null,
        mode = null,
    ))
}

fun <T> TemplateContext<T>.divPatch(
    changes: Property<List<DivPatch.Change>>? = null,
    mode: Property<DivPatch.Mode>? = null,
): LiteralProperty<DivPatch> {
    return value(DivPatch(
        changes = changes,
        mode = mode,
    ))
}

fun <T> TemplateContext<T>.divPatch(
    changes: List<DivPatch.Change>? = null,
    mode: DivPatch.Mode? = null,
): LiteralProperty<DivPatch> {
    return value(DivPatch(
        changes = optionalValue(changes),
        mode = optionalValue(mode),
    ))
}

fun <T> TemplateContext<T>.change(): LiteralProperty<DivPatch.Change> {
    return value(DivPatch.Change(
        id = null,
        items = null,
    ))
}

fun <T> TemplateContext<T>.change(
    id: Property<String>? = null,
    items: Property<List<Div>>? = null,
): LiteralProperty<DivPatch.Change> {
    return value(DivPatch.Change(
        id = id,
        items = items,
    ))
}

fun <T> TemplateContext<T>.change(
    id: String? = null,
    items: List<Div>? = null,
): LiteralProperty<DivPatch.Change> {
    return value(DivPatch.Change(
        id = optionalValue(id),
        items = optionalValue(items),
    ))
}

fun CardContext.divPatch(
    changes: ValueProperty<List<DivPatch.Change>>,
    mode: ValueProperty<DivPatch.Mode>? = null,
): DivPatch {
    return DivPatch(
        changes = changes,
        mode = mode,
    )
}

fun CardContext.divPatch(
    changes: List<DivPatch.Change>,
    mode: DivPatch.Mode? = null,
): DivPatch {
    return DivPatch(
        changes = value(changes),
        mode = optionalValue(mode),
    )
}

fun CardContext.change(
    id: ValueProperty<String>,
    items: ValueProperty<List<Div>>? = null,
): DivPatch.Change {
    return DivPatch.Change(
        id = id,
        items = items,
    )
}

fun CardContext.change(
    id: String,
    items: List<Div>? = null,
): DivPatch.Change {
    return DivPatch.Change(
        id = value(id),
        items = optionalValue(items),
    )
}
