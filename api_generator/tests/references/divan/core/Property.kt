package divan.core

import divan.annotation.Generated
import kotlin.Any
import kotlin.String
import kotlin.Unit
import kotlin.collections.MutableMap

@Generated
sealed interface Property<T>

@Generated
class ReferenceProperty<T> internal constructor(
    val name: String,
) : Property<T>

@Generated
class LiteralProperty<T> internal constructor(
    val value: T,
) : Property<T>

@Generated
class ExpressionProperty<T> internal constructor(
    val expression: String,
) : Property<T>

@Generated
fun <T> reference(name: String): ReferenceProperty<T> = ReferenceProperty(name)

@Generated
fun <T : Any> value(value: T): LiteralProperty<T> = LiteralProperty(value)

@Generated
fun <T : Any> valueOrNull(value: T?): LiteralProperty<T>? = if (value != null)
        LiteralProperty(value) else null

@Generated
fun <T : Any> expression(expression: String): ExpressionProperty<T> =
        ExpressionProperty(expression)

@Generated
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
