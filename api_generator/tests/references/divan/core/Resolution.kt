package divan.core

import divan.annotation.Generated
import kotlin.Any

@Generated
sealed interface Resolution<T>

@Generated
class FinalResolution<T : Any> internal constructor(
    val reference: ReferenceProperty<T>,
    val value: Any,
) : Resolution<T>

@Generated
class ProxyResolution<T> internal constructor(
    val reference: ReferenceProperty<T>,
    val proxy: ReferenceProperty<T>,
) : Resolution<T>

@Generated
infix fun <T : Any> ReferenceProperty<T>.bind(value: T): Resolution<T> =
        FinalResolution(this, value)

@Generated
infix fun <T : Any> ReferenceProperty<T>.bind(property: Property<T>): Resolution<T> = when
        (property) {
    is LiteralProperty -> FinalResolution(this, property.value)
    is ExpressionProperty -> FinalResolution(this, property.expression)
    is ReferenceProperty -> ProxyResolution(this, property)
}
