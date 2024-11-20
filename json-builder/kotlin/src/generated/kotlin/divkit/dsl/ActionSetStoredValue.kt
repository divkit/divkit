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
 * Temporarily saves the variable in storage.
 * 
 * Can be created using the method [actionSetStoredValue].
 * 
 * Required parameters: `value, type, name, lifetime`.
 */
@Generated
data class ActionSetStoredValue internal constructor(
    @JsonIgnore
    val properties: Properties,
) : ActionTyped {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "set_stored_value")
    )

    operator fun plus(additive: Properties): ActionSetStoredValue = ActionSetStoredValue(
        Properties(
            lifetime = additive.lifetime ?: properties.lifetime,
            name = additive.name ?: properties.name,
            value = additive.value ?: properties.value,
        )
    )

    data class Properties internal constructor(
        /**
         * Duration of storage in seconds.
         */
        val lifetime: Property<Int>?,
        /**
         * Name of the saved variable.
         */
        val name: Property<String>?,
        /**
         * Saved value.
         */
        val value: Property<TypedValue>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("lifetime", lifetime)
            result.tryPutProperty("name", name)
            result.tryPutProperty("value", value)
            return result
        }
    }
}

/**
 * @param lifetime Duration of storage in seconds.
 * @param name Name of the saved variable.
 * @param value Saved value.
 */
@Generated
fun DivScope.actionSetStoredValue(
    `use named arguments`: Guard = Guard.instance,
    lifetime: Int? = null,
    name: String? = null,
    value: TypedValue? = null,
): ActionSetStoredValue = ActionSetStoredValue(
    ActionSetStoredValue.Properties(
        lifetime = valueOrNull(lifetime),
        name = valueOrNull(name),
        value = valueOrNull(value),
    )
)

/**
 * @param lifetime Duration of storage in seconds.
 * @param name Name of the saved variable.
 * @param value Saved value.
 */
@Generated
fun DivScope.actionSetStoredValueProps(
    `use named arguments`: Guard = Guard.instance,
    lifetime: Int? = null,
    name: String? = null,
    value: TypedValue? = null,
) = ActionSetStoredValue.Properties(
    lifetime = valueOrNull(lifetime),
    name = valueOrNull(name),
    value = valueOrNull(value),
)

/**
 * @param lifetime Duration of storage in seconds.
 * @param name Name of the saved variable.
 * @param value Saved value.
 */
@Generated
fun TemplateScope.actionSetStoredValueRefs(
    `use named arguments`: Guard = Guard.instance,
    lifetime: ReferenceProperty<Int>? = null,
    name: ReferenceProperty<String>? = null,
    value: ReferenceProperty<TypedValue>? = null,
) = ActionSetStoredValue.Properties(
    lifetime = lifetime,
    name = name,
    value = value,
)

/**
 * @param lifetime Duration of storage in seconds.
 * @param name Name of the saved variable.
 * @param value Saved value.
 */
@Generated
fun ActionSetStoredValue.override(
    `use named arguments`: Guard = Guard.instance,
    lifetime: Int? = null,
    name: String? = null,
    value: TypedValue? = null,
): ActionSetStoredValue = ActionSetStoredValue(
    ActionSetStoredValue.Properties(
        lifetime = valueOrNull(lifetime) ?: properties.lifetime,
        name = valueOrNull(name) ?: properties.name,
        value = valueOrNull(value) ?: properties.value,
    )
)

/**
 * @param lifetime Duration of storage in seconds.
 * @param name Name of the saved variable.
 * @param value Saved value.
 */
@Generated
fun ActionSetStoredValue.defer(
    `use named arguments`: Guard = Guard.instance,
    lifetime: ReferenceProperty<Int>? = null,
    name: ReferenceProperty<String>? = null,
    value: ReferenceProperty<TypedValue>? = null,
): ActionSetStoredValue = ActionSetStoredValue(
    ActionSetStoredValue.Properties(
        lifetime = lifetime ?: properties.lifetime,
        name = name ?: properties.name,
        value = value ?: properties.value,
    )
)

/**
 * @param lifetime Duration of storage in seconds.
 * @param name Name of the saved variable.
 */
@Generated
fun ActionSetStoredValue.evaluate(
    `use named arguments`: Guard = Guard.instance,
    lifetime: ExpressionProperty<Int>? = null,
    name: ExpressionProperty<String>? = null,
): ActionSetStoredValue = ActionSetStoredValue(
    ActionSetStoredValue.Properties(
        lifetime = lifetime ?: properties.lifetime,
        name = name ?: properties.name,
        value = properties.value,
    )
)

@Generated
fun ActionSetStoredValue.asList() = listOf(this)
