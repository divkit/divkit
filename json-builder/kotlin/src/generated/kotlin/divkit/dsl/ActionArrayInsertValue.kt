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
 * Adds a value to the array
 * 
 * Can be created using the method [actionArrayInsertValue].
 * 
 * Required parameters: `variable_name, value, type`.
 */
@Generated
class ActionArrayInsertValue internal constructor(
    @JsonIgnore
    val properties: Properties,
) : ActionTyped {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "array_insert_value")
    )

    operator fun plus(additive: Properties): ActionArrayInsertValue = ActionArrayInsertValue(
        Properties(
            index = additive.index ?: properties.index,
            value = additive.value ?: properties.value,
            variableName = additive.variableName ?: properties.variableName,
        )
    )

    class Properties internal constructor(
        val index: Property<Int>?,
        val value: Property<TypedValue>?,
        val variableName: Property<String>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("index", index)
            result.tryPutProperty("value", value)
            result.tryPutProperty("variable_name", variableName)
            return result
        }
    }
}

@Generated
fun DivScope.actionArrayInsertValue(
    `use named arguments`: Guard = Guard.instance,
    index: Int? = null,
    value: TypedValue? = null,
    variableName: String? = null,
): ActionArrayInsertValue = ActionArrayInsertValue(
    ActionArrayInsertValue.Properties(
        index = valueOrNull(index),
        value = valueOrNull(value),
        variableName = valueOrNull(variableName),
    )
)

@Generated
fun DivScope.actionArrayInsertValueProps(
    `use named arguments`: Guard = Guard.instance,
    index: Int? = null,
    value: TypedValue? = null,
    variableName: String? = null,
) = ActionArrayInsertValue.Properties(
    index = valueOrNull(index),
    value = valueOrNull(value),
    variableName = valueOrNull(variableName),
)

@Generated
fun TemplateScope.actionArrayInsertValueRefs(
    `use named arguments`: Guard = Guard.instance,
    index: ReferenceProperty<Int>? = null,
    value: ReferenceProperty<TypedValue>? = null,
    variableName: ReferenceProperty<String>? = null,
) = ActionArrayInsertValue.Properties(
    index = index,
    value = value,
    variableName = variableName,
)

@Generated
fun ActionArrayInsertValue.override(
    `use named arguments`: Guard = Guard.instance,
    index: Int? = null,
    value: TypedValue? = null,
    variableName: String? = null,
): ActionArrayInsertValue = ActionArrayInsertValue(
    ActionArrayInsertValue.Properties(
        index = valueOrNull(index) ?: properties.index,
        value = valueOrNull(value) ?: properties.value,
        variableName = valueOrNull(variableName) ?: properties.variableName,
    )
)

@Generated
fun ActionArrayInsertValue.defer(
    `use named arguments`: Guard = Guard.instance,
    index: ReferenceProperty<Int>? = null,
    value: ReferenceProperty<TypedValue>? = null,
    variableName: ReferenceProperty<String>? = null,
): ActionArrayInsertValue = ActionArrayInsertValue(
    ActionArrayInsertValue.Properties(
        index = index ?: properties.index,
        value = value ?: properties.value,
        variableName = variableName ?: properties.variableName,
    )
)

@Generated
fun ActionArrayInsertValue.evaluate(
    `use named arguments`: Guard = Guard.instance,
    index: ExpressionProperty<Int>? = null,
    variableName: ExpressionProperty<String>? = null,
): ActionArrayInsertValue = ActionArrayInsertValue(
    ActionArrayInsertValue.Properties(
        index = index ?: properties.index,
        value = properties.value,
        variableName = variableName ?: properties.variableName,
    )
)

@Generated
fun ActionArrayInsertValue.asList() = listOf(this)
