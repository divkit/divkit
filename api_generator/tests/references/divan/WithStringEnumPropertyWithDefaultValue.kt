@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divan

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import divan.annotation.Generated
import divan.core.Guard
import divan.core.Property
import divan.core.ReferenceProperty
import divan.core.tryPutProperty
import divan.core.valueOrNull
import divan.scope.DivScope
import divan.scope.TemplateScope
import kotlin.Any
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map

/**
 * Can be created using the method [withStringEnumPropertyWithDefaultValue].
 * 
 * Required parameters: `type`.
 */
@Generated
class WithStringEnumPropertyWithDefaultValue internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_string_enum_property_with_default_value")
    )

    operator fun plus(additive: Properties): WithStringEnumPropertyWithDefaultValue = WithStringEnumPropertyWithDefaultValue(
        Properties(
            value = additive.value ?: properties.value,
        )
    )

    class Properties internal constructor(
        /**
         * Default value: `second`.
         */
        val value: Property<Value>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("value", value)
            return result
        }
    }

    /**
     * Possible values: [first], [second], [third].
     */
    @Generated
    sealed interface Value
}

@Generated
fun DivScope.withStringEnumPropertyWithDefaultValue(
    `use named arguments`: Guard = Guard.instance,
    value: WithStringEnumPropertyWithDefaultValue.Value? = null,
): WithStringEnumPropertyWithDefaultValue = WithStringEnumPropertyWithDefaultValue(
    WithStringEnumPropertyWithDefaultValue.Properties(
        value = valueOrNull(value),
    )
)

@Generated
fun DivScope.withStringEnumPropertyWithDefaultValueProps(
    `use named arguments`: Guard = Guard.instance,
    value: WithStringEnumPropertyWithDefaultValue.Value? = null,
) = WithStringEnumPropertyWithDefaultValue.Properties(
    value = valueOrNull(value),
)

@Generated
fun TemplateScope.withStringEnumPropertyWithDefaultValueRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<WithStringEnumPropertyWithDefaultValue.Value>? = null,
) = WithStringEnumPropertyWithDefaultValue.Properties(
    value = value,
)

@Generated
fun WithStringEnumPropertyWithDefaultValue.override(
    `use named arguments`: Guard = Guard.instance,
    value: WithStringEnumPropertyWithDefaultValue.Value? = null,
): WithStringEnumPropertyWithDefaultValue = WithStringEnumPropertyWithDefaultValue(
    WithStringEnumPropertyWithDefaultValue.Properties(
        value = valueOrNull(value) ?: properties.value,
    )
)

@Generated
fun WithStringEnumPropertyWithDefaultValue.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<WithStringEnumPropertyWithDefaultValue.Value>? = null,
): WithStringEnumPropertyWithDefaultValue = WithStringEnumPropertyWithDefaultValue(
    WithStringEnumPropertyWithDefaultValue.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun WithStringEnumPropertyWithDefaultValue.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<WithStringEnumPropertyWithDefaultValue.Value>? = null,
): WithStringEnumPropertyWithDefaultValue = WithStringEnumPropertyWithDefaultValue(
    WithStringEnumPropertyWithDefaultValue.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun Component<WithStringEnumPropertyWithDefaultValue>.override(
    `use named arguments`: Guard = Guard.instance,
    value: WithStringEnumPropertyWithDefaultValue.Value? = null,
): Component<WithStringEnumPropertyWithDefaultValue> = Component(
    template = template,
    properties = WithStringEnumPropertyWithDefaultValue.Properties(
        value = valueOrNull(value),
    ).mergeWith(properties)
)

@Generated
fun Component<WithStringEnumPropertyWithDefaultValue>.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<WithStringEnumPropertyWithDefaultValue.Value>? = null,
): Component<WithStringEnumPropertyWithDefaultValue> = Component(
    template = template,
    properties = WithStringEnumPropertyWithDefaultValue.Properties(
        value = value,
    ).mergeWith(properties)
)

@Generated
fun Component<WithStringEnumPropertyWithDefaultValue>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<WithStringEnumPropertyWithDefaultValue.Value>? = null,
): Component<WithStringEnumPropertyWithDefaultValue> = Component(
    template = template,
    properties = WithStringEnumPropertyWithDefaultValue.Properties(
        value = value,
    ).mergeWith(properties)
)

@Generated
operator fun Component<WithStringEnumPropertyWithDefaultValue>.plus(additive: WithStringEnumPropertyWithDefaultValue.Properties): Component<WithStringEnumPropertyWithDefaultValue> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun WithStringEnumPropertyWithDefaultValue.asList() = listOf(this)

@Generated
fun WithStringEnumPropertyWithDefaultValue.Value.asList() = listOf(this)
