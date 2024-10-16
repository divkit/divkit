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
 * Sets the value in the dictionary by the specified key. Deletes the key if the value is not set.
 * 
 * Can be created using the method [actionDictSetValue].
 * 
 * Required parameters: `variable_name, type, key`.
 */
@Generated
data class ActionDictSetValue internal constructor(
    @JsonIgnore
    val properties: Properties,
) : ActionTyped {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "dict_set_value")
    )

    operator fun plus(additive: Properties): ActionDictSetValue = ActionDictSetValue(
        Properties(
            key = additive.key ?: properties.key,
            value = additive.value ?: properties.value,
            variableName = additive.variableName ?: properties.variableName,
        )
    )

    data class Properties internal constructor(
        val key: Property<String>?,
        val value: Property<TypedValue>?,
        val variableName: Property<String>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("key", key)
            result.tryPutProperty("value", value)
            result.tryPutProperty("variable_name", variableName)
            return result
        }
    }
}

@Generated
fun DivScope.actionDictSetValue(
    `use named arguments`: Guard = Guard.instance,
    key: String? = null,
    value: TypedValue? = null,
    variableName: String? = null,
): ActionDictSetValue = ActionDictSetValue(
    ActionDictSetValue.Properties(
        key = valueOrNull(key),
        value = valueOrNull(value),
        variableName = valueOrNull(variableName),
    )
)

@Generated
fun DivScope.actionDictSetValueProps(
    `use named arguments`: Guard = Guard.instance,
    key: String? = null,
    value: TypedValue? = null,
    variableName: String? = null,
) = ActionDictSetValue.Properties(
    key = valueOrNull(key),
    value = valueOrNull(value),
    variableName = valueOrNull(variableName),
)

@Generated
fun TemplateScope.actionDictSetValueRefs(
    `use named arguments`: Guard = Guard.instance,
    key: ReferenceProperty<String>? = null,
    value: ReferenceProperty<TypedValue>? = null,
    variableName: ReferenceProperty<String>? = null,
) = ActionDictSetValue.Properties(
    key = key,
    value = value,
    variableName = variableName,
)

@Generated
fun ActionDictSetValue.override(
    `use named arguments`: Guard = Guard.instance,
    key: String? = null,
    value: TypedValue? = null,
    variableName: String? = null,
): ActionDictSetValue = ActionDictSetValue(
    ActionDictSetValue.Properties(
        key = valueOrNull(key) ?: properties.key,
        value = valueOrNull(value) ?: properties.value,
        variableName = valueOrNull(variableName) ?: properties.variableName,
    )
)

@Generated
fun ActionDictSetValue.defer(
    `use named arguments`: Guard = Guard.instance,
    key: ReferenceProperty<String>? = null,
    value: ReferenceProperty<TypedValue>? = null,
    variableName: ReferenceProperty<String>? = null,
): ActionDictSetValue = ActionDictSetValue(
    ActionDictSetValue.Properties(
        key = key ?: properties.key,
        value = value ?: properties.value,
        variableName = variableName ?: properties.variableName,
    )
)

@Generated
fun ActionDictSetValue.evaluate(
    `use named arguments`: Guard = Guard.instance,
    key: ExpressionProperty<String>? = null,
    variableName: ExpressionProperty<String>? = null,
): ActionDictSetValue = ActionDictSetValue(
    ActionDictSetValue.Properties(
        key = key ?: properties.key,
        value = properties.value,
        variableName = variableName ?: properties.variableName,
    )
)

@Generated
fun ActionDictSetValue.asList() = listOf(this)
