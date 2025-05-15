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
 * A Boolean variable in binary format.
 * 
 * Can be created using the method [booleanVariable].
 * 
 * Required parameters: `value, type, name`.
 */
@Generated
data class BooleanVariable internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Variable {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "boolean")
    )

    operator fun plus(additive: Properties): BooleanVariable = BooleanVariable(
        Properties(
            name = additive.name ?: properties.name,
            value = additive.value ?: properties.value,
        )
    )

    data class Properties internal constructor(
        /**
         * Variable name.
         */
        val name: Property<String>?,
        /**
         * Value.
         */
        val value: Property<Boolean>?,
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
fun DivScope.booleanVariable(
    `use named arguments`: Guard = Guard.instance,
    name: String? = null,
    value: Boolean? = null,
): BooleanVariable = BooleanVariable(
    BooleanVariable.Properties(
        name = valueOrNull(name),
        value = valueOrNull(value),
    )
)

/**
 * @param name Variable name.
 * @param value Value.
 */
@Generated
fun DivScope.booleanVariableProps(
    `use named arguments`: Guard = Guard.instance,
    name: String? = null,
    value: Boolean? = null,
) = BooleanVariable.Properties(
    name = valueOrNull(name),
    value = valueOrNull(value),
)

/**
 * @param name Variable name.
 * @param value Value.
 */
@Generated
fun TemplateScope.booleanVariableRefs(
    `use named arguments`: Guard = Guard.instance,
    name: ReferenceProperty<String>? = null,
    value: ReferenceProperty<Boolean>? = null,
) = BooleanVariable.Properties(
    name = name,
    value = value,
)

/**
 * @param name Variable name.
 * @param value Value.
 */
@Generated
fun BooleanVariable.override(
    `use named arguments`: Guard = Guard.instance,
    name: String? = null,
    value: Boolean? = null,
): BooleanVariable = BooleanVariable(
    BooleanVariable.Properties(
        name = valueOrNull(name) ?: properties.name,
        value = valueOrNull(value) ?: properties.value,
    )
)

/**
 * @param name Variable name.
 * @param value Value.
 */
@Generated
fun BooleanVariable.defer(
    `use named arguments`: Guard = Guard.instance,
    name: ReferenceProperty<String>? = null,
    value: ReferenceProperty<Boolean>? = null,
): BooleanVariable = BooleanVariable(
    BooleanVariable.Properties(
        name = name ?: properties.name,
        value = value ?: properties.value,
    )
)

/**
 * @param value Value.
 */
@Generated
fun BooleanVariable.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<Boolean>? = null,
): BooleanVariable = BooleanVariable(
    BooleanVariable.Properties(
        name = properties.name,
        value = value ?: properties.value,
    )
)

@Generated
fun BooleanVariable.asList() = listOf(this)
