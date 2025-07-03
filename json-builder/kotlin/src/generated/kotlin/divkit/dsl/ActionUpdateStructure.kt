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
 * Set values ​​in a variable of type array or dictionary with different nesting.
 * 
 * Can be created using the method [actionUpdateStructure].
 * 
 * Required parameters: `variable_name, value, type, path`.
 */
@Generated
data class ActionUpdateStructure internal constructor(
    @JsonIgnore
    val properties: Properties,
) : ActionTyped {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "update_structure")
    )

    operator fun plus(additive: Properties): ActionUpdateStructure = ActionUpdateStructure(
        Properties(
            path = additive.path ?: properties.path,
            value = additive.value ?: properties.value,
            variableName = additive.variableName ?: properties.variableName,
        )
    )

    data class Properties internal constructor(
        /**
         * Path within an array/dictionary where a value needs to be set. Path format: <li>Each path element is separated by a '/' symbol.</li><li>Path elements can be of two types: an index of an element in an array, starting from 0 or dictionary keys in the form of arbitrary strings.</li><li>The path is read from left to right, each element determines the transition to the next level of nesting.</li><li>The path cannot be empty, start or end with the '/' character.</li>Example path: `key/0/inner_key/1`.
         */
        val path: Property<String>?,
        /**
         * Value set into dictionary/array.
         */
        val value: Property<TypedValue>?,
        /**
         * Variable name of array or dictionary type.
         */
        val variableName: Property<String>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("path", path)
            result.tryPutProperty("value", value)
            result.tryPutProperty("variable_name", variableName)
            return result
        }
    }
}

/**
 * @param path Path within an array/dictionary where a value needs to be set. Path format: <li>Each path element is separated by a '/' symbol.</li><li>Path elements can be of two types: an index of an element in an array, starting from 0 or dictionary keys in the form of arbitrary strings.</li><li>The path is read from left to right, each element determines the transition to the next level of nesting.</li><li>The path cannot be empty, start or end with the '/' character.</li>Example path: `key/0/inner_key/1`.
 * @param value Value set into dictionary/array.
 * @param variableName Variable name of array or dictionary type.
 */
@Generated
fun DivScope.actionUpdateStructure(
    `use named arguments`: Guard = Guard.instance,
    path: String? = null,
    value: TypedValue? = null,
    variableName: String? = null,
): ActionUpdateStructure = ActionUpdateStructure(
    ActionUpdateStructure.Properties(
        path = valueOrNull(path),
        value = valueOrNull(value),
        variableName = valueOrNull(variableName),
    )
)

/**
 * @param path Path within an array/dictionary where a value needs to be set. Path format: <li>Each path element is separated by a '/' symbol.</li><li>Path elements can be of two types: an index of an element in an array, starting from 0 or dictionary keys in the form of arbitrary strings.</li><li>The path is read from left to right, each element determines the transition to the next level of nesting.</li><li>The path cannot be empty, start or end with the '/' character.</li>Example path: `key/0/inner_key/1`.
 * @param value Value set into dictionary/array.
 * @param variableName Variable name of array or dictionary type.
 */
@Generated
fun DivScope.actionUpdateStructureProps(
    `use named arguments`: Guard = Guard.instance,
    path: String? = null,
    value: TypedValue? = null,
    variableName: String? = null,
) = ActionUpdateStructure.Properties(
    path = valueOrNull(path),
    value = valueOrNull(value),
    variableName = valueOrNull(variableName),
)

/**
 * @param path Path within an array/dictionary where a value needs to be set. Path format: <li>Each path element is separated by a '/' symbol.</li><li>Path elements can be of two types: an index of an element in an array, starting from 0 or dictionary keys in the form of arbitrary strings.</li><li>The path is read from left to right, each element determines the transition to the next level of nesting.</li><li>The path cannot be empty, start or end with the '/' character.</li>Example path: `key/0/inner_key/1`.
 * @param value Value set into dictionary/array.
 * @param variableName Variable name of array or dictionary type.
 */
@Generated
fun TemplateScope.actionUpdateStructureRefs(
    `use named arguments`: Guard = Guard.instance,
    path: ReferenceProperty<String>? = null,
    value: ReferenceProperty<TypedValue>? = null,
    variableName: ReferenceProperty<String>? = null,
) = ActionUpdateStructure.Properties(
    path = path,
    value = value,
    variableName = variableName,
)

/**
 * @param path Path within an array/dictionary where a value needs to be set. Path format: <li>Each path element is separated by a '/' symbol.</li><li>Path elements can be of two types: an index of an element in an array, starting from 0 or dictionary keys in the form of arbitrary strings.</li><li>The path is read from left to right, each element determines the transition to the next level of nesting.</li><li>The path cannot be empty, start or end with the '/' character.</li>Example path: `key/0/inner_key/1`.
 * @param value Value set into dictionary/array.
 * @param variableName Variable name of array or dictionary type.
 */
@Generated
fun ActionUpdateStructure.override(
    `use named arguments`: Guard = Guard.instance,
    path: String? = null,
    value: TypedValue? = null,
    variableName: String? = null,
): ActionUpdateStructure = ActionUpdateStructure(
    ActionUpdateStructure.Properties(
        path = valueOrNull(path) ?: properties.path,
        value = valueOrNull(value) ?: properties.value,
        variableName = valueOrNull(variableName) ?: properties.variableName,
    )
)

/**
 * @param path Path within an array/dictionary where a value needs to be set. Path format: <li>Each path element is separated by a '/' symbol.</li><li>Path elements can be of two types: an index of an element in an array, starting from 0 or dictionary keys in the form of arbitrary strings.</li><li>The path is read from left to right, each element determines the transition to the next level of nesting.</li><li>The path cannot be empty, start or end with the '/' character.</li>Example path: `key/0/inner_key/1`.
 * @param value Value set into dictionary/array.
 * @param variableName Variable name of array or dictionary type.
 */
@Generated
fun ActionUpdateStructure.defer(
    `use named arguments`: Guard = Guard.instance,
    path: ReferenceProperty<String>? = null,
    value: ReferenceProperty<TypedValue>? = null,
    variableName: ReferenceProperty<String>? = null,
): ActionUpdateStructure = ActionUpdateStructure(
    ActionUpdateStructure.Properties(
        path = path ?: properties.path,
        value = value ?: properties.value,
        variableName = variableName ?: properties.variableName,
    )
)

/**
 * @param path Path within an array/dictionary where a value needs to be set. Path format: <li>Each path element is separated by a '/' symbol.</li><li>Path elements can be of two types: an index of an element in an array, starting from 0 or dictionary keys in the form of arbitrary strings.</li><li>The path is read from left to right, each element determines the transition to the next level of nesting.</li><li>The path cannot be empty, start or end with the '/' character.</li>Example path: `key/0/inner_key/1`.
 * @param value Value set into dictionary/array.
 * @param variableName Variable name of array or dictionary type.
 */
@Generated
fun ActionUpdateStructure.modify(
    `use named arguments`: Guard = Guard.instance,
    path: Property<String>? = null,
    value: Property<TypedValue>? = null,
    variableName: Property<String>? = null,
): ActionUpdateStructure = ActionUpdateStructure(
    ActionUpdateStructure.Properties(
        path = path ?: properties.path,
        value = value ?: properties.value,
        variableName = variableName ?: properties.variableName,
    )
)

/**
 * @param path Path within an array/dictionary where a value needs to be set. Path format: <li>Each path element is separated by a '/' symbol.</li><li>Path elements can be of two types: an index of an element in an array, starting from 0 or dictionary keys in the form of arbitrary strings.</li><li>The path is read from left to right, each element determines the transition to the next level of nesting.</li><li>The path cannot be empty, start or end with the '/' character.</li>Example path: `key/0/inner_key/1`.
 * @param variableName Variable name of array or dictionary type.
 */
@Generated
fun ActionUpdateStructure.evaluate(
    `use named arguments`: Guard = Guard.instance,
    path: ExpressionProperty<String>? = null,
    variableName: ExpressionProperty<String>? = null,
): ActionUpdateStructure = ActionUpdateStructure(
    ActionUpdateStructure.Properties(
        path = path ?: properties.path,
        value = properties.value,
        variableName = variableName ?: properties.variableName,
    )
)

@Generated
fun ActionUpdateStructure.asList() = listOf(this)
