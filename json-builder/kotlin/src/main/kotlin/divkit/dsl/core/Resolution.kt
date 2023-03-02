package divkit.dsl.core

import kotlin.Any

sealed interface Resolution<T>

class FinalResolution<T : Any> internal constructor(
    val reference: ReferenceProperty<T>,
    val value: Any,
) : Resolution<T>

class ProxyResolution<T> internal constructor(
    val reference: ReferenceProperty<T>,
    val proxy: ReferenceProperty<T>,
) : Resolution<T>

infix fun <T : Any> ReferenceProperty<T>.bind(value: T): Resolution<T> = FinalResolution(this, value)

infix fun <T : Any> ReferenceProperty<T>.bind(property: Property<T>): Resolution<T> = when (property) {
    is LiteralProperty -> FinalResolution(this, property.value)
    is ExpressionProperty -> FinalResolution(this, property.expression)
    is ReferenceProperty -> ProxyResolution(this, property)
}
