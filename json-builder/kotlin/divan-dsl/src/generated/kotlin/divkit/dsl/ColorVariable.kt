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
 * Variable — HEX color as a string.
 * 
 * Can be created using the method [colorVariable].
 * 
 * Required parameters: `value, type, name`.
 */
@Generated
@JsonSerialize(using = ColorVariableSerializer::class)
@ExposedCopyVisibility
data class ColorVariable internal constructor(
    val properties: Properties,
) : Variable {
    operator fun plus(additive: Properties): ColorVariable = ColorVariable(
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
        val value: Property<Color>?,
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
fun DivScope.colorVariable(
    `use named arguments`: Guard = Guard.instance,
    name: String? = null,
    value: Color? = null,
): ColorVariable = ColorVariable(
    ColorVariable.Properties(
        name = valueOrNull(name),
        value = valueOrNull(value),
    )
)

/**
 * @param name Variable name.
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun DivScope.colorVariableProps(
    `use named arguments`: Guard = Guard.instance,
    name: String? = null,
    value: Color? = null,
) = ColorVariable.Properties(
    name = valueOrNull(name),
    value = valueOrNull(value),
)

/**
 * @param name Variable name.
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun TemplateScope.colorVariableRefs(
    `use named arguments`: Guard = Guard.instance,
    name: ReferenceProperty<String>? = null,
    value: ReferenceProperty<Color>? = null,
) = ColorVariable.Properties(
    name = name,
    value = value,
)

/**
 * @param name Variable name.
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun ColorVariable.override(
    `use named arguments`: Guard = Guard.instance,
    name: String? = null,
    value: Color? = null,
): ColorVariable = ColorVariable(
    ColorVariable.Properties(
        name = valueOrNull(name) ?: properties.name,
        value = valueOrNull(value) ?: properties.value,
    )
)

/**
 * @param name Variable name.
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun ColorVariable.defer(
    `use named arguments`: Guard = Guard.instance,
    name: ReferenceProperty<String>? = null,
    value: ReferenceProperty<Color>? = null,
): ColorVariable = ColorVariable(
    ColorVariable.Properties(
        name = name ?: properties.name,
        value = value ?: properties.value,
    )
)

/**
 * @param name Variable name.
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun ColorVariable.modify(
    `use named arguments`: Guard = Guard.instance,
    name: Property<String>? = null,
    value: Property<Color>? = null,
): ColorVariable = ColorVariable(
    ColorVariable.Properties(
        name = name ?: properties.name,
        value = value ?: properties.value,
    )
)

/**
 * @param value Value. Supports expressions for variable initialization.
 */
@Generated
fun ColorVariable.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<Color>? = null,
): ColorVariable = ColorVariable(
    ColorVariable.Properties(
        name = properties.name,
        value = value ?: properties.value,
    )
)

@Generated
fun ColorVariable.asList() = listOf(this)
