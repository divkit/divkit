// Generated code. Do not modify.

package com.yandex.div.dsl.model

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class DivTrigger internal constructor(
    @JsonIgnore val actions: Property<List<DivAction>>?,
    @JsonIgnore val condition: Property<BoolInt>?,
    @JsonIgnore val mode: Property<Mode>?,
) {

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "actions" to actions,
            "condition" to condition,
            "mode" to mode,
        )
    }

    enum class Mode(@JsonValue val value: String) {
        ON_CONDITION("on_condition"),
        ON_VARIABLE("on_variable"),
    }
}

fun <T> TemplateContext<T>.divTrigger(): LiteralProperty<DivTrigger> {
    return value(DivTrigger(
        actions = null,
        condition = null,
        mode = null,
    ))
}

fun <T> TemplateContext<T>.divTrigger(
    actions: Property<List<DivAction>>? = null,
    condition: Property<BoolInt>? = null,
    mode: Property<DivTrigger.Mode>? = null,
): LiteralProperty<DivTrigger> {
    return value(DivTrigger(
        actions = actions,
        condition = condition,
        mode = mode,
    ))
}

fun <T> TemplateContext<T>.divTrigger(
    actions: List<DivAction>? = null,
    condition: BoolInt? = null,
    mode: DivTrigger.Mode? = null,
): LiteralProperty<DivTrigger> {
    return value(DivTrigger(
        actions = optionalValue(actions),
        condition = optionalValue(condition),
        mode = optionalValue(mode),
    ))
}

fun CardContext.divTrigger(
    actions: ValueProperty<List<DivAction>>,
    condition: ValueProperty<BoolInt>,
    mode: ValueProperty<DivTrigger.Mode>? = null,
): DivTrigger {
    return DivTrigger(
        actions = actions,
        condition = condition,
        mode = mode,
    )
}

fun CardContext.divTrigger(
    actions: List<DivAction>,
    condition: BoolInt,
    mode: DivTrigger.Mode? = null,
): DivTrigger {
    return DivTrigger(
        actions = value(actions),
        condition = value(condition),
        mode = optionalValue(mode),
    )
}
