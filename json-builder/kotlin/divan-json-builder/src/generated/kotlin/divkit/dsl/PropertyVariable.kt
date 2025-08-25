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
 * A property that is handeled with get and set methods.
 * 
 * Can be created using the method [propertyVariable].
 * 
 * Required parameters: `value_type, type, name, get`.
 */
@Generated
@ExposedCopyVisibility
data class PropertyVariable internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Variable {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "property")
    )

    operator fun plus(additive: Properties): PropertyVariable = PropertyVariable(
        Properties(
            get = additive.get ?: properties.get,
            name = additive.name ?: properties.name,
            newValueVariableName = additive.newValueVariableName ?: properties.newValueVariableName,
            set = additive.set ?: properties.set,
            valueType = additive.valueType ?: properties.valueType,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Value. Supports expressions for property initialization.
         */
        val get: Property<String>?,
        /**
         * Property name.
         */
        val name: Property<String>?,
        /**
         * Name for accessing the data passed to the setter.
         * Default value: `new_value`.
         */
        val newValueVariableName: Property<String>?,
        /**
         * Action when setting a property.
         */
        val set: Property<List<Action>>?,
        val valueType: Property<EvaluableType>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("get", get)
            result.tryPutProperty("name", name)
            result.tryPutProperty("new_value_variable_name", newValueVariableName)
            result.tryPutProperty("set", set)
            result.tryPutProperty("value_type", valueType)
            return result
        }
    }
}

/**
 * @param get Value. Supports expressions for property initialization.
 * @param name Property name.
 * @param newValueVariableName Name for accessing the data passed to the setter.
 * @param set Action when setting a property.
 */
@Generated
fun DivScope.propertyVariable(
    `use named arguments`: Guard = Guard.instance,
    get: String? = null,
    name: String? = null,
    newValueVariableName: String? = null,
    set: List<Action>? = null,
    valueType: EvaluableType? = null,
): PropertyVariable = PropertyVariable(
    PropertyVariable.Properties(
        get = valueOrNull(get),
        name = valueOrNull(name),
        newValueVariableName = valueOrNull(newValueVariableName),
        set = valueOrNull(set),
        valueType = valueOrNull(valueType),
    )
)

/**
 * @param get Value. Supports expressions for property initialization.
 * @param name Property name.
 * @param newValueVariableName Name for accessing the data passed to the setter.
 * @param set Action when setting a property.
 */
@Generated
fun DivScope.propertyVariableProps(
    `use named arguments`: Guard = Guard.instance,
    get: String? = null,
    name: String? = null,
    newValueVariableName: String? = null,
    set: List<Action>? = null,
    valueType: EvaluableType? = null,
) = PropertyVariable.Properties(
    get = valueOrNull(get),
    name = valueOrNull(name),
    newValueVariableName = valueOrNull(newValueVariableName),
    set = valueOrNull(set),
    valueType = valueOrNull(valueType),
)

/**
 * @param get Value. Supports expressions for property initialization.
 * @param name Property name.
 * @param newValueVariableName Name for accessing the data passed to the setter.
 * @param set Action when setting a property.
 */
@Generated
fun TemplateScope.propertyVariableRefs(
    `use named arguments`: Guard = Guard.instance,
    get: ReferenceProperty<String>? = null,
    name: ReferenceProperty<String>? = null,
    newValueVariableName: ReferenceProperty<String>? = null,
    set: ReferenceProperty<List<Action>>? = null,
    valueType: ReferenceProperty<EvaluableType>? = null,
) = PropertyVariable.Properties(
    get = get,
    name = name,
    newValueVariableName = newValueVariableName,
    set = set,
    valueType = valueType,
)

/**
 * @param get Value. Supports expressions for property initialization.
 * @param name Property name.
 * @param newValueVariableName Name for accessing the data passed to the setter.
 * @param set Action when setting a property.
 */
@Generated
fun PropertyVariable.override(
    `use named arguments`: Guard = Guard.instance,
    get: String? = null,
    name: String? = null,
    newValueVariableName: String? = null,
    set: List<Action>? = null,
    valueType: EvaluableType? = null,
): PropertyVariable = PropertyVariable(
    PropertyVariable.Properties(
        get = valueOrNull(get) ?: properties.get,
        name = valueOrNull(name) ?: properties.name,
        newValueVariableName = valueOrNull(newValueVariableName) ?: properties.newValueVariableName,
        set = valueOrNull(set) ?: properties.set,
        valueType = valueOrNull(valueType) ?: properties.valueType,
    )
)

/**
 * @param get Value. Supports expressions for property initialization.
 * @param name Property name.
 * @param newValueVariableName Name for accessing the data passed to the setter.
 * @param set Action when setting a property.
 */
@Generated
fun PropertyVariable.defer(
    `use named arguments`: Guard = Guard.instance,
    get: ReferenceProperty<String>? = null,
    name: ReferenceProperty<String>? = null,
    newValueVariableName: ReferenceProperty<String>? = null,
    set: ReferenceProperty<List<Action>>? = null,
    valueType: ReferenceProperty<EvaluableType>? = null,
): PropertyVariable = PropertyVariable(
    PropertyVariable.Properties(
        get = get ?: properties.get,
        name = name ?: properties.name,
        newValueVariableName = newValueVariableName ?: properties.newValueVariableName,
        set = set ?: properties.set,
        valueType = valueType ?: properties.valueType,
    )
)

/**
 * @param get Value. Supports expressions for property initialization.
 * @param name Property name.
 * @param newValueVariableName Name for accessing the data passed to the setter.
 * @param set Action when setting a property.
 */
@Generated
fun PropertyVariable.modify(
    `use named arguments`: Guard = Guard.instance,
    get: Property<String>? = null,
    name: Property<String>? = null,
    newValueVariableName: Property<String>? = null,
    set: Property<List<Action>>? = null,
    valueType: Property<EvaluableType>? = null,
): PropertyVariable = PropertyVariable(
    PropertyVariable.Properties(
        get = get ?: properties.get,
        name = name ?: properties.name,
        newValueVariableName = newValueVariableName ?: properties.newValueVariableName,
        set = set ?: properties.set,
        valueType = valueType ?: properties.valueType,
    )
)

/**
 * @param get Value. Supports expressions for property initialization.
 */
@Generated
fun PropertyVariable.evaluate(
    `use named arguments`: Guard = Guard.instance,
    get: ExpressionProperty<String>? = null,
    valueType: ExpressionProperty<EvaluableType>? = null,
): PropertyVariable = PropertyVariable(
    PropertyVariable.Properties(
        get = get ?: properties.get,
        name = properties.name,
        newValueVariableName = properties.newValueVariableName,
        set = properties.set,
        valueType = valueType ?: properties.valueType,
    )
)

@Generated
fun PropertyVariable.asList() = listOf(this)
