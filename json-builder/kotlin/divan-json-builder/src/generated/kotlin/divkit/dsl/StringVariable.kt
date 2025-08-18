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
 * A string variable.
 * 
 * Can be created using the method [stringVariable].
 * 
 * Required parameters: `value, type, name`.
 */
@Generated
data class StringVariable internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Variable {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "string")
    )

    operator fun plus(additive: Properties): StringVariable = StringVariable(
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
        val value: Property<String>?,
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
fun DivScope.stringVariable(
    `use named arguments`: Guard = Guard.instance,
    name: String? = null,
    value: String? = null,
): StringVariable = StringVariable(
    StringVariable.Properties(
        name = valueOrNull(name),
        value = valueOrNull(value),
    )
)

/**
 * @param name Variable name.
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun DivScope.stringVariableProps(
    `use named arguments`: Guard = Guard.instance,
    name: String? = null,
    value: String? = null,
) = StringVariable.Properties(
    name = valueOrNull(name),
    value = valueOrNull(value),
)

/**
 * @param name Variable name.
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun TemplateScope.stringVariableRefs(
    `use named arguments`: Guard = Guard.instance,
    name: ReferenceProperty<String>? = null,
    value: ReferenceProperty<String>? = null,
) = StringVariable.Properties(
    name = name,
    value = value,
)

/**
 * @param name Variable name.
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun StringVariable.override(
    `use named arguments`: Guard = Guard.instance,
    name: String? = null,
    value: String? = null,
): StringVariable = StringVariable(
    StringVariable.Properties(
        name = valueOrNull(name) ?: properties.name,
        value = valueOrNull(value) ?: properties.value,
    )
)

/**
 * @param name Variable name.
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun StringVariable.defer(
    `use named arguments`: Guard = Guard.instance,
    name: ReferenceProperty<String>? = null,
    value: ReferenceProperty<String>? = null,
): StringVariable = StringVariable(
    StringVariable.Properties(
        name = name ?: properties.name,
        value = value ?: properties.value,
    )
)

/**
 * @param name Variable name.
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun StringVariable.modify(
    `use named arguments`: Guard = Guard.instance,
    name: Property<String>? = null,
    value: Property<String>? = null,
): StringVariable = StringVariable(
    StringVariable.Properties(
        name = name ?: properties.name,
        value = value ?: properties.value,
    )
)

/**
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun StringVariable.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<String>? = null,
): StringVariable = StringVariable(
    StringVariable.Properties(
        name = properties.name,
        value = value ?: properties.value,
    )
)

@Generated
fun StringVariable.asList() = listOf(this)
