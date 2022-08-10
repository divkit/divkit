// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivAccessibility internal constructor(
    @JsonIgnore val description: Property<String>?,
    @JsonIgnore val hint: Property<String>?,
    @JsonIgnore val mode: Property<Mode>?,
    @JsonIgnore val stateDescription: Property<String>?,
    @JsonIgnore val type: Property<Type>?,
) {

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "description" to description,
            "hint" to hint,
            "mode" to mode,
            "state_description" to stateDescription,
            "type" to type,
        )
    }

    enum class Mode(@JsonValue val value: String) {
        DEFAULT("default"),
        MERGE("merge"),
        EXCLUDE("exclude"),
    }

    enum class Type(@JsonValue val value: String) {
        NONE("none"),
        BUTTON("button"),
        IMAGE("image"),
        TEXT("text"),
        EDIT_TEXT("edit_text"),
        HEADER("header"),
        TAB_BAR("tab_bar"),
    }
}

fun <T> TemplateContext<T>.divAccessibility(): LiteralProperty<DivAccessibility> {
    return value(DivAccessibility(
        description = null,
        hint = null,
        mode = null,
        stateDescription = null,
        type = null,
    ))
}

fun <T> TemplateContext<T>.divAccessibility(
    description: Property<String>? = null,
    hint: Property<String>? = null,
    mode: Property<DivAccessibility.Mode>? = null,
    stateDescription: Property<String>? = null,
    type: Property<DivAccessibility.Type>? = null,
): LiteralProperty<DivAccessibility> {
    return value(DivAccessibility(
        description = description,
        hint = hint,
        mode = mode,
        stateDescription = stateDescription,
        type = type,
    ))
}

fun <T> TemplateContext<T>.divAccessibility(
    description: String? = null,
    hint: String? = null,
    mode: DivAccessibility.Mode? = null,
    stateDescription: String? = null,
    type: DivAccessibility.Type? = null,
): LiteralProperty<DivAccessibility> {
    return value(DivAccessibility(
        description = optionalValue(description),
        hint = optionalValue(hint),
        mode = optionalValue(mode),
        stateDescription = optionalValue(stateDescription),
        type = optionalValue(type),
    ))
}

fun CardContext.divAccessibility(): DivAccessibility {
    return DivAccessibility(
        description = null,
        hint = null,
        mode = null,
        stateDescription = null,
        type = null,
    )
}

fun CardContext.divAccessibility(
    description: ValueProperty<String>? = null,
    hint: ValueProperty<String>? = null,
    mode: ValueProperty<DivAccessibility.Mode>? = null,
    stateDescription: ValueProperty<String>? = null,
    type: ValueProperty<DivAccessibility.Type>? = null,
): DivAccessibility {
    return DivAccessibility(
        description = description,
        hint = hint,
        mode = mode,
        stateDescription = stateDescription,
        type = type,
    )
}

fun CardContext.divAccessibility(
    description: String? = null,
    hint: String? = null,
    mode: DivAccessibility.Mode? = null,
    stateDescription: String? = null,
    type: DivAccessibility.Type? = null,
): DivAccessibility {
    return DivAccessibility(
        description = optionalValue(description),
        hint = optionalValue(hint),
        mode = optionalValue(mode),
        stateDescription = optionalValue(stateDescription),
        type = optionalValue(type),
    )
}
