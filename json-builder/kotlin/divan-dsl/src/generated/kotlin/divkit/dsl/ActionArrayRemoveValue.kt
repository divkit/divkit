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
 * Deletes a value from the array
 * 
 * Can be created using the method [actionArrayRemoveValue].
 * 
 * Required parameters: `variable_name, type, index`.
 */
@Generated
@ExposedCopyVisibility
data class ActionArrayRemoveValue internal constructor(
    @JsonIgnore
    val properties: Properties,
) : ActionTyped {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "array_remove_value")
    )

    operator fun plus(additive: Properties): ActionArrayRemoveValue = ActionArrayRemoveValue(
        Properties(
            index = additive.index ?: properties.index,
            variableName = additive.variableName ?: properties.variableName,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        val index: Property<Int>?,
        val variableName: Property<String>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("index", index)
            result.tryPutProperty("variable_name", variableName)
            return result
        }
    }
}

@Generated
fun DivScope.actionArrayRemoveValue(
    `use named arguments`: Guard = Guard.instance,
    index: Int? = null,
    variableName: String? = null,
): ActionArrayRemoveValue = ActionArrayRemoveValue(
    ActionArrayRemoveValue.Properties(
        index = valueOrNull(index),
        variableName = valueOrNull(variableName),
    )
)

@Generated
fun DivScope.actionArrayRemoveValueProps(
    `use named arguments`: Guard = Guard.instance,
    index: Int? = null,
    variableName: String? = null,
) = ActionArrayRemoveValue.Properties(
    index = valueOrNull(index),
    variableName = valueOrNull(variableName),
)

@Generated
fun TemplateScope.actionArrayRemoveValueRefs(
    `use named arguments`: Guard = Guard.instance,
    index: ReferenceProperty<Int>? = null,
    variableName: ReferenceProperty<String>? = null,
) = ActionArrayRemoveValue.Properties(
    index = index,
    variableName = variableName,
)

@Generated
fun ActionArrayRemoveValue.override(
    `use named arguments`: Guard = Guard.instance,
    index: Int? = null,
    variableName: String? = null,
): ActionArrayRemoveValue = ActionArrayRemoveValue(
    ActionArrayRemoveValue.Properties(
        index = valueOrNull(index) ?: properties.index,
        variableName = valueOrNull(variableName) ?: properties.variableName,
    )
)

@Generated
fun ActionArrayRemoveValue.defer(
    `use named arguments`: Guard = Guard.instance,
    index: ReferenceProperty<Int>? = null,
    variableName: ReferenceProperty<String>? = null,
): ActionArrayRemoveValue = ActionArrayRemoveValue(
    ActionArrayRemoveValue.Properties(
        index = index ?: properties.index,
        variableName = variableName ?: properties.variableName,
    )
)

@Generated
fun ActionArrayRemoveValue.modify(
    `use named arguments`: Guard = Guard.instance,
    index: Property<Int>? = null,
    variableName: Property<String>? = null,
): ActionArrayRemoveValue = ActionArrayRemoveValue(
    ActionArrayRemoveValue.Properties(
        index = index ?: properties.index,
        variableName = variableName ?: properties.variableName,
    )
)

@Generated
fun ActionArrayRemoveValue.evaluate(
    `use named arguments`: Guard = Guard.instance,
    index: ExpressionProperty<Int>? = null,
    variableName: ExpressionProperty<String>? = null,
): ActionArrayRemoveValue = ActionArrayRemoveValue(
    ActionArrayRemoveValue.Properties(
        index = index ?: properties.index,
        variableName = variableName ?: properties.variableName,
    )
)

@Generated
fun ActionArrayRemoveValue.asList() = listOf(this)
