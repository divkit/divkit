@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divkit.dsl

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue
import divkit.dsl.annotation.*
import divkit.dsl.core.*
import divkit.dsl.scope.*
import kotlin.Any
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map

/**
 * A floating-point variable.
 * 
 * Can be created using the method [numberVariable].
 * 
 * Required properties: `value, name`.
 */
@Generated
class NumberVariable internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Variable {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "number")
    )

    operator fun plus(additive: Properties): NumberVariable = NumberVariable(
        Properties(
            name = additive.name ?: properties.name,
            value = additive.value ?: properties.value,
        )
    )

    class Properties internal constructor(
        /**
         * Variable name.
         */
        val name: Property<String>?,
        /**
         * Value.
         */
        val value: Property<Double>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("name", name)
            result.tryPutProperty("value", value)
            return result
        }
    }
}

/**
 * @param name Variable name.
 * @param value Value.
 */
@Generated
fun DivScope.numberVariable(
    `use named arguments`: Guard = Guard.instance,
    name: String,
    value: Double,
): NumberVariable = NumberVariable(
    NumberVariable.Properties(
        name = valueOrNull(name),
        value = valueOrNull(value),
    )
)

/**
 * @param name Variable name.
 * @param value Value.
 */
@Generated
fun DivScope.numberVariableProps(
    `use named arguments`: Guard = Guard.instance,
    name: String? = null,
    value: Double? = null,
) = NumberVariable.Properties(
    name = valueOrNull(name),
    value = valueOrNull(value),
)

/**
 * @param name Variable name.
 * @param value Value.
 */
@Generated
fun TemplateScope.numberVariableRefs(
    `use named arguments`: Guard = Guard.instance,
    name: ReferenceProperty<String>? = null,
    value: ReferenceProperty<Double>? = null,
) = NumberVariable.Properties(
    name = name,
    value = value,
)

/**
 * @param name Variable name.
 * @param value Value.
 */
@Generated
fun NumberVariable.override(
    `use named arguments`: Guard = Guard.instance,
    name: String? = null,
    value: Double? = null,
): NumberVariable = NumberVariable(
    NumberVariable.Properties(
        name = valueOrNull(name) ?: properties.name,
        value = valueOrNull(value) ?: properties.value,
    )
)

/**
 * @param name Variable name.
 * @param value Value.
 */
@Generated
fun NumberVariable.defer(
    `use named arguments`: Guard = Guard.instance,
    name: ReferenceProperty<String>? = null,
    value: ReferenceProperty<Double>? = null,
): NumberVariable = NumberVariable(
    NumberVariable.Properties(
        name = name ?: properties.name,
        value = value ?: properties.value,
    )
)

@Generated
fun NumberVariable.asList() = listOf(this)
