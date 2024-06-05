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
 * Sets value in the array
 * 
 * Can be created using the method [actionArraySetValue].
 * 
 * Required parameters: `variable_name, value, type, index`.
 */
@Generated
class ActionArraySetValue internal constructor(
    @JsonIgnore
    val properties: Properties,
) : ActionTyped {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "array_set_value")
    )

    operator fun plus(additive: Properties): ActionArraySetValue = ActionArraySetValue(
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
fun DivScope.actionArraySetValue(
    `use named arguments`: Guard = Guard.instance,
    index: Int? = null,
    value: TypedValue? = null,
    variableName: String? = null,
): ActionArraySetValue = ActionArraySetValue(
    ActionArraySetValue.Properties(
        index = valueOrNull(index),
        value = valueOrNull(value),
        variableName = valueOrNull(variableName),
    )
)

@Generated
fun DivScope.actionArraySetValueProps(
    `use named arguments`: Guard = Guard.instance,
    index: Int? = null,
    value: TypedValue? = null,
    variableName: String? = null,
) = ActionArraySetValue.Properties(
    index = valueOrNull(index),
    value = valueOrNull(value),
    variableName = valueOrNull(variableName),
)

@Generated
fun TemplateScope.actionArraySetValueRefs(
    `use named arguments`: Guard = Guard.instance,
    index: ReferenceProperty<Int>? = null,
    value: ReferenceProperty<TypedValue>? = null,
    variableName: ReferenceProperty<String>? = null,
) = ActionArraySetValue.Properties(
    index = index,
    value = value,
    variableName = variableName,
)

@Generated
fun ActionArraySetValue.override(
    `use named arguments`: Guard = Guard.instance,
    index: Int? = null,
    value: TypedValue? = null,
    variableName: String? = null,
): ActionArraySetValue = ActionArraySetValue(
    ActionArraySetValue.Properties(
        index = valueOrNull(index) ?: properties.index,
        value = valueOrNull(value) ?: properties.value,
        variableName = valueOrNull(variableName) ?: properties.variableName,
    )
)

@Generated
fun ActionArraySetValue.defer(
    `use named arguments`: Guard = Guard.instance,
    index: ReferenceProperty<Int>? = null,
    value: ReferenceProperty<TypedValue>? = null,
    variableName: ReferenceProperty<String>? = null,
): ActionArraySetValue = ActionArraySetValue(
    ActionArraySetValue.Properties(
        index = index ?: properties.index,
        value = value ?: properties.value,
        variableName = variableName ?: properties.variableName,
    )
)

@Generated
fun ActionArraySetValue.evaluate(
    `use named arguments`: Guard = Guard.instance,
    index: ExpressionProperty<Int>? = null,
    variableName: ExpressionProperty<String>? = null,
): ActionArraySetValue = ActionArraySetValue(
    ActionArraySetValue.Properties(
        index = index ?: properties.index,
        value = properties.value,
        variableName = variableName ?: properties.variableName,
    )
)

@Generated
fun ActionArraySetValue.asList() = listOf(this)
