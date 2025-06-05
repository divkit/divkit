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
 * An arbitrary object in JSON format.
 * 
 * Can be created using the method [dictVariable].
 * 
 * Required parameters: `value, type, name`.
 */
@Generated
data class DictVariable internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Variable {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "dict")
    )

    operator fun plus(additive: Properties): DictVariable = DictVariable(
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

@Generated
fun DictVariable.asList() = listOf(this)
