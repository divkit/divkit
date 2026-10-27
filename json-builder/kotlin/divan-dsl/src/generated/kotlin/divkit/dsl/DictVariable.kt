@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divkit.dsl

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import divkit.dsl.annotation.*
import divkit.dsl.core.*
import divkit.dsl.scope.*
import divkit.dsl.serialization.*
import kotlin.Any
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map

/**
 * An arbitrary object in JSON format.
 * 
 * Can be created using the method [dictVariable].
 * 
 * Required parameters: `value, type, name`.
 */
@Generated
@JsonSerialize(using = DictVariableSerializer::class)
@ExposedCopyVisibility
data class DictVariable internal constructor(
    val properties: Properties,
) : Variable {
    operator fun plus(additive: Properties): DictVariable = DictVariable(
        Properties(
            name = additive.name ?: properties.name,
            value = additive.value ?: properties.value,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Variable name.
         */
        val name: Property<String>?,
        /**
         * Value. Supports expressions for variable initialization.
         */
        val value: Property<Map<String, Any>>?,
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
fun DivScope.dictVariable(
    `use named arguments`: Guard = Guard.instance,
    name: String? = null,
    value: Map<String, Any>? = null,
): DictVariable = DictVariable(
    DictVariable.Properties(
        name = valueOrNull(name),
        value = valueOrNull(value),
    )
)

/**
 * @param name Variable name.
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun DivScope.dictVariableProps(
    `use named arguments`: Guard = Guard.instance,
    name: String? = null,
    value: Map<String, Any>? = null,
) = DictVariable.Properties(
    name = valueOrNull(name),
    value = valueOrNull(value),
)

/**
 * @param name Variable name.
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun TemplateScope.dictVariableRefs(
    `use named arguments`: Guard = Guard.instance,
    name: ReferenceProperty<String>? = null,
    value: ReferenceProperty<Map<String, Any>>? = null,
) = DictVariable.Properties(
    name = name,
    value = value,
)

/**
 * @param name Variable name.
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun DictVariable.override(
    `use named arguments`: Guard = Guard.instance,
    name: String? = null,
    value: Map<String, Any>? = null,
): DictVariable = DictVariable(
    DictVariable.Properties(
        name = valueOrNull(name) ?: properties.name,
        value = valueOrNull(value) ?: properties.value,
    )
)

/**
 * @param name Variable name.
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun DictVariable.defer(
    `use named arguments`: Guard = Guard.instance,
    name: ReferenceProperty<String>? = null,
    value: ReferenceProperty<Map<String, Any>>? = null,
): DictVariable = DictVariable(
    DictVariable.Properties(
        name = name ?: properties.name,
        value = value ?: properties.value,
    )
)

/**
 * @param name Variable name.
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun DictVariable.modify(
    `use named arguments`: Guard = Guard.instance,
    name: Property<String>? = null,
    value: Property<Map<String, Any>>? = null,
): DictVariable = DictVariable(
    DictVariable.Properties(
        name = name ?: properties.name,
        value = value ?: properties.value,
    )
)

/**
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun DictVariable.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<Map<String, Any>>? = null,
): DictVariable = DictVariable(
    DictVariable.Properties(
        name = properties.name,
        value = value ?: properties.value,
    )
)

@Generated
fun DictVariable.asList() = listOf(this)
