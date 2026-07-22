// Generated code. Do not modify.

package com.yandex.api_generator.test

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class EntityWithPropertyWithDefaultValue internal constructor(
    @JsonIgnore val colorAarrggbb: Property<Color>?,
    @JsonIgnore val colorRrggbb: Property<Color>?,
    @JsonIgnore val int: Property<Int>?,
    @JsonIgnore val nested: Property<Nested>?,
    @JsonIgnore val url: Property<URI>?,
) : Entity {

    @JsonProperty("type") override val type = "entity_with_property_with_default_value"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "color_aarrggbb" to colorAarrggbb,
            "color_rrggbb" to colorRrggbb,
            "int" to int,
            "nested" to nested,
            "url" to url,
        )
    }

    class Nested internal constructor(
        @JsonIgnore val int: Property<Int>?,
        @JsonIgnore val nonOptional: Property<String>?,
        @JsonIgnore val url: Property<URI>?,
    ) {

        @JsonAnyGetter
        internal fun properties(): Map<String, Any> {
            return propertyMapOf(
                "int" to int,
                "non_optional" to nonOptional,
                "url" to url,
            )
        }
    }
}

fun <T> TemplateContext<T>.entityWithPropertyWithDefaultValue(): LiteralProperty<EntityWithPropertyWithDefaultValue> {
    return value(EntityWithPropertyWithDefaultValue(
        colorAarrggbb = null,
        colorRrggbb = null,
        int = null,
        nested = null,
        url = null,
    ))
}

fun <T> TemplateContext<T>.entityWithPropertyWithDefaultValue(
    colorAarrggbb: Property<Color>? = null,
    colorRrggbb: Property<Color>? = null,
    int: Property<Int>? = null,
    nested: Property<EntityWithPropertyWithDefaultValue.Nested>? = null,
    url: Property<URI>? = null,
): LiteralProperty<EntityWithPropertyWithDefaultValue> {
    return value(EntityWithPropertyWithDefaultValue(
        colorAarrggbb = colorAarrggbb,
        colorRrggbb = colorRrggbb,
        int = int,
        nested = nested,
        url = url,
    ))
}

fun <T> TemplateContext<T>.entityWithPropertyWithDefaultValue(
    colorAarrggbb: Color? = null,
    colorRrggbb: Color? = null,
    int: Int? = null,
    nested: EntityWithPropertyWithDefaultValue.Nested? = null,
    url: URI? = null,
): LiteralProperty<EntityWithPropertyWithDefaultValue> {
    return value(EntityWithPropertyWithDefaultValue(
        colorAarrggbb = optionalValue(colorAarrggbb),
        colorRrggbb = optionalValue(colorRrggbb),
        int = optionalValue(int),
        nested = optionalValue(nested),
        url = optionalValue(url),
    ))
}

fun <T> TemplateContext<T>.nested(): LiteralProperty<EntityWithPropertyWithDefaultValue.Nested> {
    return value(EntityWithPropertyWithDefaultValue.Nested(
        int = null,
        nonOptional = null,
        url = null,
    ))
}

fun <T> TemplateContext<T>.nested(
    nonOptional: Property<String>? = null,
    int: Property<Int>? = null,
    url: Property<URI>? = null,
): LiteralProperty<EntityWithPropertyWithDefaultValue.Nested> {
    return value(EntityWithPropertyWithDefaultValue.Nested(
        int = int,
        nonOptional = nonOptional,
        url = url,
    ))
}

fun <T> TemplateContext<T>.nested(
    nonOptional: String? = null,
    int: Int? = null,
    url: URI? = null,
): LiteralProperty<EntityWithPropertyWithDefaultValue.Nested> {
    return value(EntityWithPropertyWithDefaultValue.Nested(
        int = optionalValue(int),
        nonOptional = optionalValue(nonOptional),
        url = optionalValue(url),
    ))
}

fun CardContext.entityWithPropertyWithDefaultValue(): EntityWithPropertyWithDefaultValue {
    return EntityWithPropertyWithDefaultValue(
        colorAarrggbb = null,
        colorRrggbb = null,
        int = null,
        nested = null,
        url = null,
    )
}

fun CardContext.entityWithPropertyWithDefaultValue(
    colorAarrggbb: ValueProperty<Color>? = null,
    colorRrggbb: ValueProperty<Color>? = null,
    int: ValueProperty<Int>? = null,
    nested: ValueProperty<EntityWithPropertyWithDefaultValue.Nested>? = null,
    url: ValueProperty<URI>? = null,
): EntityWithPropertyWithDefaultValue {
    return EntityWithPropertyWithDefaultValue(
        colorAarrggbb = colorAarrggbb,
        colorRrggbb = colorRrggbb,
        int = int,
        nested = nested,
        url = url,
    )
}

fun CardContext.entityWithPropertyWithDefaultValue(
    colorAarrggbb: Color? = null,
    colorRrggbb: Color? = null,
    int: Int? = null,
    nested: EntityWithPropertyWithDefaultValue.Nested? = null,
    url: URI? = null,
): EntityWithPropertyWithDefaultValue {
    return EntityWithPropertyWithDefaultValue(
        colorAarrggbb = optionalValue(colorAarrggbb),
        colorRrggbb = optionalValue(colorRrggbb),
        int = optionalValue(int),
        nested = optionalValue(nested),
        url = optionalValue(url),
    )
}

fun CardContext.nested(
    nonOptional: ValueProperty<String>,
    int: ValueProperty<Int>? = null,
    url: ValueProperty<URI>? = null,
): EntityWithPropertyWithDefaultValue.Nested {
    return EntityWithPropertyWithDefaultValue.Nested(
        int = int,
        nonOptional = nonOptional,
        url = url,
    )
}

fun CardContext.nested(
    nonOptional: String,
    int: Int? = null,
    url: URI? = null,
): EntityWithPropertyWithDefaultValue.Nested {
    return EntityWithPropertyWithDefaultValue.Nested(
        int = optionalValue(int),
        nonOptional = value(nonOptional),
        url = optionalValue(url),
    )
}
