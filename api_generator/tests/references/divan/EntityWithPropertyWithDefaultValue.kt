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
 * Can be created using the method [`entityWithPropertyWithDefaultValue`].
 * 
 * Required properties: `type`.
 */
@Generated
class EntityWithPropertyWithDefaultValue internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity() {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_property_with_default_value")
    )

    operator fun plus(additive: Properties): EntityWithPropertyWithDefaultValue = EntityWithPropertyWithDefaultValue(
        Properties(
            int = additive.int ?: properties.int,
            nested = additive.nested ?: properties.nested,
            url = additive.url ?: properties.url,
        )
    )

    class Properties internal constructor(
        /**
         * Default value: `0`.
         */
        val int: Property<Int>?,
        /**
         * non_optional is used to suppress auto-generation of default value for object with all-optional fields.
         */
        val nested: Property<Nested>?,
        /**
         * Default value: `https://yandex.ru`.
         */
        val url: Property<URI>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("int", int)
            result.tryPutProperty("nested", nested)
            result.tryPutProperty("url", url)
            return result
        }
    }

    /**
     * non_optional is used to suppress auto-generation of default value for object with all-optional fields.
     * 
     * Can be created using the method [`entityWithPropertyWithDefaultValueNested`].
     * 
     * Required properties: `non_optional`.
     */
    @Generated
    class Nested internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

        operator fun plus(additive: Properties): Nested = Nested(
            Properties(
                int = additive.int ?: properties.int,
                nonOptional = additive.nonOptional ?: properties.nonOptional,
                url = additive.url ?: properties.url,
            )
        )

        class Properties internal constructor(
            /**
             * Default value: `0`.
             */
            val int: Property<Int>?,
            val nonOptional: Property<String>?,
            /**
             * Default value: `https://yandex.ru`.
             */
            val url: Property<URI>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("int", int)
                result.tryPutProperty("non_optional", nonOptional)
                result.tryPutProperty("url", url)
                return result
            }
        }
    }

}

/**
 * @param nested non_optional is used to suppress auto-generation of default value for object with all-optional fields.
 */
@Generated
fun DivScope.entityWithPropertyWithDefaultValue(
    `use named arguments`: Guard = Guard.instance,
    int: Int? = null,
    nested: EntityWithPropertyWithDefaultValue.Nested? = null,
    url: URI? = null,
): EntityWithPropertyWithDefaultValue = EntityWithPropertyWithDefaultValue(
    EntityWithPropertyWithDefaultValue.Properties(
        int = valueOrNull(int),
        nested = valueOrNull(nested),
        url = valueOrNull(url),
    )
)

/**
 * @param nested non_optional is used to suppress auto-generation of default value for object with all-optional fields.
 */
@Generated
fun DivScope.entityWithPropertyWithDefaultValueProps(
    `use named arguments`: Guard = Guard.instance,
    int: Int? = null,
    nested: EntityWithPropertyWithDefaultValue.Nested? = null,
    url: URI? = null,
) = EntityWithPropertyWithDefaultValue.Properties(
    int = valueOrNull(int),
    nested = valueOrNull(nested),
    url = valueOrNull(url),
)

/**
 * @param nested non_optional is used to suppress auto-generation of default value for object with all-optional fields.
 */
@Generated
fun TemplateScope.entityWithPropertyWithDefaultValueRefs(
    `use named arguments`: Guard = Guard.instance,
    int: ReferenceProperty<Int>? = null,
    nested: ReferenceProperty<EntityWithPropertyWithDefaultValue.Nested>? = null,
    url: ReferenceProperty<URI>? = null,
) = EntityWithPropertyWithDefaultValue.Properties(
    int = int,
    nested = nested,
    url = url,
)

/**
 * @param nested non_optional is used to suppress auto-generation of default value for object with all-optional fields.
 */
@Generated
fun EntityWithPropertyWithDefaultValue.override(
    `use named arguments`: Guard = Guard.instance,
    int: Int? = null,
    nested: EntityWithPropertyWithDefaultValue.Nested? = null,
    url: URI? = null,
): EntityWithPropertyWithDefaultValue = EntityWithPropertyWithDefaultValue(
    EntityWithPropertyWithDefaultValue.Properties(
        int = valueOrNull(int) ?: properties.int,
        nested = valueOrNull(nested) ?: properties.nested,
        url = valueOrNull(url) ?: properties.url,
    )
)

/**
 * @param nested non_optional is used to suppress auto-generation of default value for object with all-optional fields.
 */
@Generated
fun Component<EntityWithPropertyWithDefaultValue>.override(
    `use named arguments`: Guard = Guard.instance,
    int: Int? = null,
    nested: EntityWithPropertyWithDefaultValue.Nested? = null,
    url: URI? = null,
): Component<EntityWithPropertyWithDefaultValue> = Component(
    template = template,
    properties = EntityWithPropertyWithDefaultValue.Properties(
        int = valueOrNull(int),
        nested = valueOrNull(nested),
        url = valueOrNull(url),
    ).mergeWith(properties)
)

/**
 * @param nested non_optional is used to suppress auto-generation of default value for object with all-optional fields.
 */
@Generated
fun EntityWithPropertyWithDefaultValue.defer(
    `use named arguments`: Guard = Guard.instance,
    int: ReferenceProperty<Int>? = null,
    nested: ReferenceProperty<EntityWithPropertyWithDefaultValue.Nested>? = null,
    url: ReferenceProperty<URI>? = null,
): EntityWithPropertyWithDefaultValue = EntityWithPropertyWithDefaultValue(
    EntityWithPropertyWithDefaultValue.Properties(
        int = int ?: properties.int,
        nested = nested ?: properties.nested,
        url = url ?: properties.url,
    )
)

/**
 * @param nested non_optional is used to suppress auto-generation of default value for object with all-optional fields.
 */
@Generated
fun Component<EntityWithPropertyWithDefaultValue>.defer(
    `use named arguments`: Guard = Guard.instance,
    int: ReferenceProperty<Int>? = null,
    nested: ReferenceProperty<EntityWithPropertyWithDefaultValue.Nested>? = null,
    url: ReferenceProperty<URI>? = null,
): Component<EntityWithPropertyWithDefaultValue> = Component(
    template = template,
    properties = EntityWithPropertyWithDefaultValue.Properties(
        int = int,
        nested = nested,
        url = url,
    ).mergeWith(properties)
)

@Generated
fun EntityWithPropertyWithDefaultValue.evaluate(
    `use named arguments`: Guard = Guard.instance,
    int: ExpressionProperty<Int>? = null,
    url: ExpressionProperty<URI>? = null,
): EntityWithPropertyWithDefaultValue = EntityWithPropertyWithDefaultValue(
    EntityWithPropertyWithDefaultValue.Properties(
        int = int ?: properties.int,
        nested = properties.nested,
        url = url ?: properties.url,
    )
)

@Generated
fun Component<EntityWithPropertyWithDefaultValue>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    int: ExpressionProperty<Int>? = null,
    url: ExpressionProperty<URI>? = null,
): Component<EntityWithPropertyWithDefaultValue> = Component(
    template = template,
    properties = EntityWithPropertyWithDefaultValue.Properties(
        int = int,
        nested = null,
        url = url,
    ).mergeWith(properties)
)

@Generated
operator fun Component<EntityWithPropertyWithDefaultValue>.plus(additive: Properties): Component<EntityWithPropertyWithDefaultValue> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun EntityWithPropertyWithDefaultValue.asList() = listOf(this)

@Generated
fun DivScope.entityWithPropertyWithDefaultValueNested(
    `use named arguments`: Guard = Guard.instance,
    int: Int? = null,
    nonOptional: String,
    url: URI? = null,
): EntityWithPropertyWithDefaultValue.Nested = EntityWithPropertyWithDefaultValue.Nested(
    EntityWithPropertyWithDefaultValue.Nested.Properties(
        int = valueOrNull(int),
        nonOptional = valueOrNull(nonOptional),
        url = valueOrNull(url),
    )
)

@Generated
fun DivScope.entityWithPropertyWithDefaultValueNestedProps(
    `use named arguments`: Guard = Guard.instance,
    int: Int? = null,
    nonOptional: String? = null,
    url: URI? = null,
) = EntityWithPropertyWithDefaultValue.Nested.Properties(
    int = valueOrNull(int),
    nonOptional = valueOrNull(nonOptional),
    url = valueOrNull(url),
)

@Generated
fun TemplateScope.entityWithPropertyWithDefaultValueNestedRefs(
    `use named arguments`: Guard = Guard.instance,
    int: ReferenceProperty<Int>? = null,
    nonOptional: ReferenceProperty<String>? = null,
    url: ReferenceProperty<URI>? = null,
) = EntityWithPropertyWithDefaultValue.Nested.Properties(
    int = int,
    nonOptional = nonOptional,
    url = url,
)

@Generated
fun EntityWithPropertyWithDefaultValue.Nested.override(
    `use named arguments`: Guard = Guard.instance,
    int: Int? = null,
    nonOptional: String? = null,
    url: URI? = null,
): EntityWithPropertyWithDefaultValue.Nested = EntityWithPropertyWithDefaultValue.Nested(
    EntityWithPropertyWithDefaultValue.Nested.Properties(
        int = valueOrNull(int) ?: properties.int,
        nonOptional = valueOrNull(nonOptional) ?: properties.nonOptional,
        url = valueOrNull(url) ?: properties.url,
    )
)

@Generated
fun Component<EntityWithPropertyWithDefaultValue.Nested>.override(
    `use named arguments`: Guard = Guard.instance,
    int: Int? = null,
    nonOptional: String? = null,
    url: URI? = null,
): Component<EntityWithPropertyWithDefaultValue.Nested> = Component(
    template = template,
    properties = EntityWithPropertyWithDefaultValue.Nested.Properties(
        int = valueOrNull(int),
        nonOptional = valueOrNull(nonOptional),
        url = valueOrNull(url),
    ).mergeWith(properties)
)

@Generated
fun EntityWithPropertyWithDefaultValue.Nested.defer(
    `use named arguments`: Guard = Guard.instance,
    int: ReferenceProperty<Int>? = null,
    nonOptional: ReferenceProperty<String>? = null,
    url: ReferenceProperty<URI>? = null,
): EntityWithPropertyWithDefaultValue.Nested = EntityWithPropertyWithDefaultValue.Nested(
    EntityWithPropertyWithDefaultValue.Nested.Properties(
        int = int ?: properties.int,
        nonOptional = nonOptional ?: properties.nonOptional,
        url = url ?: properties.url,
    )
)

@Generated
fun Component<EntityWithPropertyWithDefaultValue.Nested>.defer(
    `use named arguments`: Guard = Guard.instance,
    int: ReferenceProperty<Int>? = null,
    nonOptional: ReferenceProperty<String>? = null,
    url: ReferenceProperty<URI>? = null,
): Component<EntityWithPropertyWithDefaultValue.Nested> = Component(
    template = template,
    properties = EntityWithPropertyWithDefaultValue.Nested.Properties(
        int = int,
        nonOptional = nonOptional,
        url = url,
    ).mergeWith(properties)
)

@Generated
fun EntityWithPropertyWithDefaultValue.Nested.evaluate(
    `use named arguments`: Guard = Guard.instance,
    int: ExpressionProperty<Int>? = null,
    nonOptional: ExpressionProperty<String>? = null,
    url: ExpressionProperty<URI>? = null,
): EntityWithPropertyWithDefaultValue.Nested = EntityWithPropertyWithDefaultValue.Nested(
    EntityWithPropertyWithDefaultValue.Nested.Properties(
        int = int ?: properties.int,
        nonOptional = nonOptional ?: properties.nonOptional,
        url = url ?: properties.url,
    )
)

@Generated
fun Component<EntityWithPropertyWithDefaultValue.Nested>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    int: ExpressionProperty<Int>? = null,
    nonOptional: ExpressionProperty<String>? = null,
    url: ExpressionProperty<URI>? = null,
): Component<EntityWithPropertyWithDefaultValue.Nested> = Component(
    template = template,
    properties = EntityWithPropertyWithDefaultValue.Nested.Properties(
        int = int,
        nonOptional = nonOptional,
        url = url,
    ).mergeWith(properties)
)

@Generated
operator fun Component<Nested>.plus(additive: Properties): Component<Nested> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun EntityWithPropertyWithDefaultValue.Nested.asList() = listOf(this)
