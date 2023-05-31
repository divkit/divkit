package com.yandex.div.dsl.context

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.node.ObjectNode
import com.yandex.div.dsl.LiteralProperty
import com.yandex.div.dsl.Property
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Deprecated("Use divkit.dsl framework")
class TemplateContext<T> {

    @JvmField
    @JsonAnyGetter
    internal val templates = mutableMapOf<String, T>()

    @JvmField
    @JsonIgnore
    internal var asString: String? = null

    @JvmField
    @JsonIgnore
    internal var asNode: ObjectNode? = null

    fun copyFrom(source: TemplateContext<T>) {
        clearCache()
        templates.putAll(source.templates)
    }

    fun <T> listOf(vararg items: LiteralProperty<out T>?): Property<List<T>> {
        return LiteralProperty(items.mapNotNull { property -> property?.value })
    }

    internal fun clearCache() {
        asString = null
        asNode = null
    }
}

@OptIn(ExperimentalContracts::class)
@Deprecated("Use divkit.dsl framework")
fun <T> templates(initialize: TemplateContext<T>.() -> Unit): TemplateContext<T> {
    contract {
        callsInPlace(initialize, InvocationKind.EXACTLY_ONCE)
    }
    val context = TemplateContext<T>()
    context.initialize()
    return context
}

fun <T, P : LiteralProperty<out T>> TemplateContext<T>.define(
    templateName: String,
    templateProperty: P
) {
    clearCache()
    templates[templateName] = templateProperty.value
}
