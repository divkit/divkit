package com.yandex.div.dsl.context

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.fasterxml.jackson.databind.node.ObjectNode
import com.yandex.div.dsl.Root
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Deprecated("Use divkit.dsl framework")
class CardContext {

    @JvmField
    @JsonUnwrapped
    internal var root: Root? = null

    @JvmField
    @JsonIgnore
    internal var asString: String? = null

    @JvmField
    @JsonIgnore
    internal var asNode: ObjectNode? = null
}

@OptIn(ExperimentalContracts::class)
@Deprecated("Use divkit.dsl framework")
fun card(
    initialize: CardContext.() -> Root
): CardContext {
    contract {
        callsInPlace(initialize, InvocationKind.EXACTLY_ONCE)
    }
    val context = CardContext()
    context.root = context.initialize()
    return context
}
