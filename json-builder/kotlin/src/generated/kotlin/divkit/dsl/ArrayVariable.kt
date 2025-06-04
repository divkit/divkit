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
 * An arbitrary array in JSON format.
 * 
 * Can be created using the method [arrayVariable].
 * 
 * Required parameters: `value, type, name`.
 */
@Generated
data class ArrayVariable internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Variable {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "array")
    )

    operator fun plus(additive: Properties): ArrayVariable = ArrayVariable(
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
         * Value. Supports expressions for variable initialization.
         */
        val value: Property<List<Any>>?,
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
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun DivScope.arrayVariable(
    `use named arguments`: Guard = Guard.instance,
    name: String? = null,
    value: List<Any>? = null,
): ArrayVariable = ArrayVariable(
    ArrayVariable.Properties(
        name = valueOrNull(name),
        value = valueOrNull(value),
    )
)

/**
 * @param name Variable name.
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun DivScope.arrayVariableProps(
    `use named arguments`: Guard = Guard.instance,
    name: String? = null,
    value: List<Any>? = null,
) = ArrayVariable.Properties(
    name = valueOrNull(name),
    value = valueOrNull(value),
)

/**
 * @param name Variable name.
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun TemplateScope.arrayVariableRefs(
    `use named arguments`: Guard = Guard.instance,
    name: ReferenceProperty<String>? = null,
    value: ReferenceProperty<List<Any>>? = null,
) = ArrayVariable.Properties(
    name = name,
    value = value,
)

/**
 * @param name Variable name.
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun ArrayVariable.override(
    `use named arguments`: Guard = Guard.instance,
    name: String? = null,
    value: List<Any>? = null,
): ArrayVariable = ArrayVariable(
    ArrayVariable.Properties(
        name = valueOrNull(name) ?: properties.name,
        value = valueOrNull(value) ?: properties.value,
    )
)

/**
 * @param name Variable name.
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun ArrayVariable.defer(
    `use named arguments`: Guard = Guard.instance,
    name: ReferenceProperty<String>? = null,
    value: ReferenceProperty<List<Any>>? = null,
): ArrayVariable = ArrayVariable(
    ArrayVariable.Properties(
        name = name ?: properties.name,
        value = value ?: properties.value,
    )
)

/**
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun ArrayVariable.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<List<Any>>? = null,
): ArrayVariable = ArrayVariable(
    ArrayVariable.Properties(
        name = properties.name,
        value = value ?: properties.value,
    )
)

@Generated
fun ArrayVariable.asList() = listOf(this)
