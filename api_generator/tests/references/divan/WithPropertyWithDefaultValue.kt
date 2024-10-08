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
 * Can be created using the method [withPropertyWithDefaultValue].
 * 
 * Required parameters: `type`.
 */
@Generated
data class WithPropertyWithDefaultValue internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_property_with_default_value")
    )

    operator fun plus(additive: Properties): WithPropertyWithDefaultValue = WithPropertyWithDefaultValue(
        Properties(
            int = additive.int ?: properties.int,
            nested = additive.nested ?: properties.nested,
            url = additive.url ?: properties.url,
        )
    )

    data class Properties internal constructor(
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
        val url: Property<Url>?,
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
     * Can be created using the method [withPropertyWithDefaultValueNested].
     * 
     * Required parameters: `non_optional`.
     */
    @Generated
    data class Nested internal constructor(
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

        data class Properties internal constructor(
            /**
             * Default value: `0`.
             */
            val int: Property<Int>?,
            val nonOptional: Property<String>?,
            /**
             * Default value: `https://yandex.ru`.
             */
            val url: Property<Url>?,
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
fun DivScope.withPropertyWithDefaultValue(
    `use named arguments`: Guard = Guard.instance,
    int: Int? = null,
    nested: WithPropertyWithDefaultValue.Nested? = null,
    url: Url? = null,
): WithPropertyWithDefaultValue = WithPropertyWithDefaultValue(
    WithPropertyWithDefaultValue.Properties(
        int = valueOrNull(int),
        nested = valueOrNull(nested),
        url = valueOrNull(url),
    )
)

/**
 * @param nested non_optional is used to suppress auto-generation of default value for object with all-optional fields.
 */
@Generated
fun DivScope.withPropertyWithDefaultValueProps(
    `use named arguments`: Guard = Guard.instance,
    int: Int? = null,
    nested: WithPropertyWithDefaultValue.Nested? = null,
    url: Url? = null,
) = WithPropertyWithDefaultValue.Properties(
    int = valueOrNull(int),
    nested = valueOrNull(nested),
    url = valueOrNull(url),
)

/**
 * @param nested non_optional is used to suppress auto-generation of default value for object with all-optional fields.
 */
@Generated
fun TemplateScope.withPropertyWithDefaultValueRefs(
    `use named arguments`: Guard = Guard.instance,
    int: ReferenceProperty<Int>? = null,
    nested: ReferenceProperty<WithPropertyWithDefaultValue.Nested>? = null,
    url: ReferenceProperty<Url>? = null,
) = WithPropertyWithDefaultValue.Properties(
    int = int,
    nested = nested,
    url = url,
)

/**
 * @param nested non_optional is used to suppress auto-generation of default value for object with all-optional fields.
 */
@Generated
fun WithPropertyWithDefaultValue.override(
    `use named arguments`: Guard = Guard.instance,
    int: Int? = null,
    nested: WithPropertyWithDefaultValue.Nested? = null,
    url: Url? = null,
): WithPropertyWithDefaultValue = WithPropertyWithDefaultValue(
    WithPropertyWithDefaultValue.Properties(
        int = valueOrNull(int) ?: properties.int,
        nested = valueOrNull(nested) ?: properties.nested,
        url = valueOrNull(url) ?: properties.url,
    )
)

/**
 * @param nested non_optional is used to suppress auto-generation of default value for object with all-optional fields.
 */
@Generated
fun WithPropertyWithDefaultValue.defer(
    `use named arguments`: Guard = Guard.instance,
    int: ReferenceProperty<Int>? = null,
    nested: ReferenceProperty<WithPropertyWithDefaultValue.Nested>? = null,
    url: ReferenceProperty<Url>? = null,
): WithPropertyWithDefaultValue = WithPropertyWithDefaultValue(
    WithPropertyWithDefaultValue.Properties(
        int = int ?: properties.int,
        nested = nested ?: properties.nested,
        url = url ?: properties.url,
    )
)

@Generated
fun WithPropertyWithDefaultValue.evaluate(
    `use named arguments`: Guard = Guard.instance,
    int: ExpressionProperty<Int>? = null,
    url: ExpressionProperty<Url>? = null,
): WithPropertyWithDefaultValue = WithPropertyWithDefaultValue(
    WithPropertyWithDefaultValue.Properties(
        int = int ?: properties.int,
        nested = properties.nested,
        url = url ?: properties.url,
    )
)

/**
 * @param nested non_optional is used to suppress auto-generation of default value for object with all-optional fields.
 */
@Generated
fun Component<WithPropertyWithDefaultValue>.override(
    `use named arguments`: Guard = Guard.instance,
    int: Int? = null,
    nested: WithPropertyWithDefaultValue.Nested? = null,
    url: Url? = null,
): Component<WithPropertyWithDefaultValue> = Component(
    template = template,
    properties = WithPropertyWithDefaultValue.Properties(
        int = valueOrNull(int),
        nested = valueOrNull(nested),
        url = valueOrNull(url),
    ).mergeWith(properties)
)

/**
 * @param nested non_optional is used to suppress auto-generation of default value for object with all-optional fields.
 */
@Generated
fun Component<WithPropertyWithDefaultValue>.defer(
    `use named arguments`: Guard = Guard.instance,
    int: ReferenceProperty<Int>? = null,
    nested: ReferenceProperty<WithPropertyWithDefaultValue.Nested>? = null,
    url: ReferenceProperty<Url>? = null,
): Component<WithPropertyWithDefaultValue> = Component(
    template = template,
    properties = WithPropertyWithDefaultValue.Properties(
        int = int,
        nested = nested,
        url = url,
    ).mergeWith(properties)
)

@Generated
fun Component<WithPropertyWithDefaultValue>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    int: ExpressionProperty<Int>? = null,
    url: ExpressionProperty<Url>? = null,
): Component<WithPropertyWithDefaultValue> = Component(
    template = template,
    properties = WithPropertyWithDefaultValue.Properties(
        int = int,
        nested = null,
        url = url,
    ).mergeWith(properties)
)

@Generated
operator fun Component<WithPropertyWithDefaultValue>.plus(additive: WithPropertyWithDefaultValue.Properties): Component<WithPropertyWithDefaultValue> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun WithPropertyWithDefaultValue.asList() = listOf(this)

@Generated
fun DivScope.withPropertyWithDefaultValueNested(
    `use named arguments`: Guard = Guard.instance,
    int: Int? = null,
    nonOptional: String? = null,
    url: Url? = null,
): WithPropertyWithDefaultValue.Nested = WithPropertyWithDefaultValue.Nested(
    WithPropertyWithDefaultValue.Nested.Properties(
        int = valueOrNull(int),
        nonOptional = valueOrNull(nonOptional),
        url = valueOrNull(url),
    )
)

@Generated
fun DivScope.withPropertyWithDefaultValueNestedProps(
    `use named arguments`: Guard = Guard.instance,
    int: Int? = null,
    nonOptional: String? = null,
    url: Url? = null,
) = WithPropertyWithDefaultValue.Nested.Properties(
    int = valueOrNull(int),
    nonOptional = valueOrNull(nonOptional),
    url = valueOrNull(url),
)

@Generated
fun TemplateScope.withPropertyWithDefaultValueNestedRefs(
    `use named arguments`: Guard = Guard.instance,
    int: ReferenceProperty<Int>? = null,
    nonOptional: ReferenceProperty<String>? = null,
    url: ReferenceProperty<Url>? = null,
) = WithPropertyWithDefaultValue.Nested.Properties(
    int = int,
    nonOptional = nonOptional,
    url = url,
)

@Generated
fun WithPropertyWithDefaultValue.Nested.override(
    `use named arguments`: Guard = Guard.instance,
    int: Int? = null,
    nonOptional: String? = null,
    url: Url? = null,
): WithPropertyWithDefaultValue.Nested = WithPropertyWithDefaultValue.Nested(
    WithPropertyWithDefaultValue.Nested.Properties(
        int = valueOrNull(int) ?: properties.int,
        nonOptional = valueOrNull(nonOptional) ?: properties.nonOptional,
        url = valueOrNull(url) ?: properties.url,
    )
)

@Generated
fun WithPropertyWithDefaultValue.Nested.defer(
    `use named arguments`: Guard = Guard.instance,
    int: ReferenceProperty<Int>? = null,
    nonOptional: ReferenceProperty<String>? = null,
    url: ReferenceProperty<Url>? = null,
): WithPropertyWithDefaultValue.Nested = WithPropertyWithDefaultValue.Nested(
    WithPropertyWithDefaultValue.Nested.Properties(
        int = int ?: properties.int,
        nonOptional = nonOptional ?: properties.nonOptional,
        url = url ?: properties.url,
    )
)

@Generated
fun WithPropertyWithDefaultValue.Nested.evaluate(
    `use named arguments`: Guard = Guard.instance,
    int: ExpressionProperty<Int>? = null,
    nonOptional: ExpressionProperty<String>? = null,
    url: ExpressionProperty<Url>? = null,
): WithPropertyWithDefaultValue.Nested = WithPropertyWithDefaultValue.Nested(
    WithPropertyWithDefaultValue.Nested.Properties(
        int = int ?: properties.int,
        nonOptional = nonOptional ?: properties.nonOptional,
        url = url ?: properties.url,
    )
)

@Generated
fun WithPropertyWithDefaultValue.Nested.asList() = listOf(this)
