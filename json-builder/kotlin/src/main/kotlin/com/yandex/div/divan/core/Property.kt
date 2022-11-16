package com.yandex.div.divan.core

import kotlin.Any
import kotlin.String
import kotlin.Unit
import kotlin.collections.MutableMap

sealed interface Property<T>

class ReferenceProperty<T> internal constructor(
    val name: String,
) : Property<T>

class LiteralProperty<T> internal constructor(
    val value: T,
) : Property<T>

class ExpressionProperty<T> internal constructor(
    val expression: String,
) : Property<T>

fun <T> reference(name: String): ReferenceProperty<T> = ReferenceProperty(name)

fun <T : Any> value(value: T): LiteralProperty<T> = LiteralProperty(value)

fun <T : Any> valueOrNull(value: T?): LiteralProperty<T>? = if (value != null)
    LiteralProperty(value) else null

fun <T : Any> expression(expression: String): ExpressionProperty<T> =
    ExpressionProperty(expression)

internal fun MutableMap<String, Any>.tryPutProperty(name: String, property: Property<out Any>?):
        Unit {
    if (property != null) {
        val exhaustive = when (property) {
            is LiteralProperty -> if (property.value is Boolean) {
                put(name, if (property.value) 1 else 0)
            } else {
                put(name, property.value)
            }

            is ReferenceProperty -> put("$$name", property.name)
            is ExpressionProperty -> put(name, property.expression)
        }
    }
}
