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
 * Assigns a value to the variable
 * 
 * Can be created using the method [actionSetVariable].
 * 
 * Required parameters: `variable_name, value, type`.
 */
@Generated
@ExposedCopyVisibility
data class ActionSetVariable internal constructor(
    @JsonIgnore
    val properties: Properties,
) : ActionTyped {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "set_variable")
    )

    operator fun plus(additive: Properties): ActionSetVariable = ActionSetVariable(
        Properties(
            value = additive.value ?: properties.value,
            variableName = additive.variableName ?: properties.variableName,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        val value: Property<TypedValue>?,
        val variableName: Property<String>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("value", value)
            result.tryPutProperty("variable_name", variableName)
            return result
        }
    }
}

@Generated
fun DivScope.actionSetVariable(
    `use named arguments`: Guard = Guard.instance,
    value: TypedValue? = null,
    variableName: String? = null,
): ActionSetVariable = ActionSetVariable(
    ActionSetVariable.Properties(
        value = valueOrNull(value),
        variableName = valueOrNull(variableName),
    )
)

@Generated
fun DivScope.actionSetVariableProps(
    `use named arguments`: Guard = Guard.instance,
    value: TypedValue? = null,
    variableName: String? = null,
) = ActionSetVariable.Properties(
    value = valueOrNull(value),
    variableName = valueOrNull(variableName),
)

@Generated
fun TemplateScope.actionSetVariableRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<TypedValue>? = null,
    variableName: ReferenceProperty<String>? = null,
) = ActionSetVariable.Properties(
    value = value,
    variableName = variableName,
)

@Generated
fun ActionSetVariable.override(
    `use named arguments`: Guard = Guard.instance,
    value: TypedValue? = null,
    variableName: String? = null,
): ActionSetVariable = ActionSetVariable(
    ActionSetVariable.Properties(
        value = valueOrNull(value) ?: properties.value,
        variableName = valueOrNull(variableName) ?: properties.variableName,
    )
)

@Generated
fun ActionSetVariable.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<TypedValue>? = null,
    variableName: ReferenceProperty<String>? = null,
): ActionSetVariable = ActionSetVariable(
    ActionSetVariable.Properties(
        value = value ?: properties.value,
        variableName = variableName ?: properties.variableName,
    )
)

@Generated
fun ActionSetVariable.modify(
    `use named arguments`: Guard = Guard.instance,
    value: Property<TypedValue>? = null,
    variableName: Property<String>? = null,
): ActionSetVariable = ActionSetVariable(
    ActionSetVariable.Properties(
        value = value ?: properties.value,
        variableName = variableName ?: properties.variableName,
    )
)

@Generated
fun ActionSetVariable.evaluate(
    `use named arguments`: Guard = Guard.instance,
    variableName: ExpressionProperty<String>? = null,
): ActionSetVariable = ActionSetVariable(
    ActionSetVariable.Properties(
        value = properties.value,
        variableName = variableName ?: properties.variableName,
    )
)

@Generated
fun ActionSetVariable.asList() = listOf(this)
