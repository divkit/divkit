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
 * Can be created using the method [`entityWithStringEnumPropertyWithDefaultValue`].
 * 
 * Required properties: `type`.
 */
@Generated
class EntityWithStringEnumPropertyWithDefaultValue internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity() {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_string_enum_property_with_default_value")
    )

    operator fun plus(additive: Properties): EntityWithStringEnumPropertyWithDefaultValue = EntityWithStringEnumPropertyWithDefaultValue(
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
     * Possible values: [first, second, third].
     */
    @Generated
    sealed interface Value
}

@Generated
fun DivScope.entityWithStringEnumPropertyWithDefaultValue(
    `use named arguments`: Guard = Guard.instance,
    value: EntityWithStringEnumPropertyWithDefaultValue.Value? = null,
): EntityWithStringEnumPropertyWithDefaultValue = EntityWithStringEnumPropertyWithDefaultValue(
    EntityWithStringEnumPropertyWithDefaultValue.Properties(
        value = valueOrNull(value),
    )
)

@Generated
fun DivScope.entityWithStringEnumPropertyWithDefaultValueProps(
    `use named arguments`: Guard = Guard.instance,
    value: EntityWithStringEnumPropertyWithDefaultValue.Value? = null,
) = EntityWithStringEnumPropertyWithDefaultValue.Properties(
    value = valueOrNull(value),
)

@Generated
fun TemplateScope.entityWithStringEnumPropertyWithDefaultValueRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<EntityWithStringEnumPropertyWithDefaultValue.Value>? = null,
) = EntityWithStringEnumPropertyWithDefaultValue.Properties(
    value = value,
)

@Generated
fun EntityWithStringEnumPropertyWithDefaultValue.override(
    `use named arguments`: Guard = Guard.instance,
    value: EntityWithStringEnumPropertyWithDefaultValue.Value? = null,
): EntityWithStringEnumPropertyWithDefaultValue = EntityWithStringEnumPropertyWithDefaultValue(
    EntityWithStringEnumPropertyWithDefaultValue.Properties(
        value = valueOrNull(value) ?: properties.value,
    )
)

@Generated
fun Component<EntityWithStringEnumPropertyWithDefaultValue>.override(
    `use named arguments`: Guard = Guard.instance,
    value: EntityWithStringEnumPropertyWithDefaultValue.Value? = null,
): Component<EntityWithStringEnumPropertyWithDefaultValue> = Component(
    template = template,
    properties = EntityWithStringEnumPropertyWithDefaultValue.Properties(
        value = valueOrNull(value),
    ).mergeWith(properties)
)

@Generated
fun EntityWithStringEnumPropertyWithDefaultValue.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<EntityWithStringEnumPropertyWithDefaultValue.Value>? = null,
): EntityWithStringEnumPropertyWithDefaultValue = EntityWithStringEnumPropertyWithDefaultValue(
    EntityWithStringEnumPropertyWithDefaultValue.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun Component<EntityWithStringEnumPropertyWithDefaultValue>.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<EntityWithStringEnumPropertyWithDefaultValue.Value>? = null,
): Component<EntityWithStringEnumPropertyWithDefaultValue> = Component(
    template = template,
    properties = EntityWithStringEnumPropertyWithDefaultValue.Properties(
        value = value,
    ).mergeWith(properties)
)

@Generated
operator fun Component<EntityWithStringEnumPropertyWithDefaultValue>.plus(additive: Properties): Component<EntityWithStringEnumPropertyWithDefaultValue> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun EntityWithStringEnumPropertyWithDefaultValue.asList() = listOf(this)
