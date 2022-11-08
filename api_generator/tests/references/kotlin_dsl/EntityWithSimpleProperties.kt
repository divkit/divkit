// Generated code. Do not modify.

package com.yandex.api_generator.test

import java.net.URI
import com.fasterxml.jackson.annotation.*
import com.yandex.div.dsl.*
import com.yandex.div.dsl.context.*
import com.yandex.div.dsl.type.*
import com.yandex.div.dsl.util.*

class EntityWithSimpleProperties internal constructor(
    @JsonIgnore val boolean: Property<Boolean>?,
    @JsonIgnore val booleanInt: Property<BoolInt>?,
    @JsonIgnore val color: Property<Color>?,
    @JsonIgnore val double: Property<Double>?,
    @JsonIgnore val id: Property<Int>?,
    @JsonIgnore val integer: Property<Int>?,
    @JsonIgnore val positiveInteger: Property<Int>?,
    @JsonIgnore val string: Property<String>?,
    @JsonIgnore val url: Property<URI>?,
) : Entity {

    @JsonProperty("type") override val type = "entity_with_simple_properties"

    @JsonAnyGetter
    internal fun properties(): Map<String, Any> {
        return propertyMapOf(
            "boolean" to boolean,
            "boolean_int" to booleanInt,
            "color" to color,
            "double" to double,
            "id" to id,
            "integer" to integer,
            "positive_integer" to positiveInteger,
            "string" to string,
            "url" to url,
        )
    }
}

fun <T> TemplateContext<T>.entityWithSimpleProperties(): LiteralProperty<EntityWithSimpleProperties> {
    return value(EntityWithSimpleProperties(
        boolean = null,
        booleanInt = null,
        color = null,
        double = null,
        id = null,
        integer = null,
        positiveInteger = null,
        string = null,
        url = null,
    ))
}

fun <T> TemplateContext<T>.entityWithSimpleProperties(
    boolean: Property<Boolean>? = null,
    booleanInt: Property<BoolInt>? = null,
    color: Property<Color>? = null,
    double: Property<Double>? = null,
    id: Property<Int>? = null,
    integer: Property<Int>? = null,
    positiveInteger: Property<Int>? = null,
    string: Property<String>? = null,
    url: Property<URI>? = null,
): LiteralProperty<EntityWithSimpleProperties> {
    return value(EntityWithSimpleProperties(
        boolean = boolean,
        booleanInt = booleanInt,
        color = color,
        double = double,
        id = id,
        integer = integer,
        positiveInteger = positiveInteger,
        string = string,
        url = url,
    ))
}

fun <T> TemplateContext<T>.entityWithSimpleProperties(
    boolean: Boolean? = null,
    booleanInt: BoolInt? = null,
    color: Color? = null,
    double: Double? = null,
    id: Int? = null,
    integer: Int? = null,
    positiveInteger: Int? = null,
    string: String? = null,
    url: URI? = null,
): LiteralProperty<EntityWithSimpleProperties> {
    return value(EntityWithSimpleProperties(
        boolean = optionalValue(boolean),
        booleanInt = optionalValue(booleanInt),
        color = optionalValue(color),
        double = optionalValue(double),
        id = optionalValue(id),
        integer = optionalValue(integer),
        positiveInteger = optionalValue(positiveInteger),
        string = optionalValue(string),
        url = optionalValue(url),
    ))
}

fun CardContext.entityWithSimpleProperties(): EntityWithSimpleProperties {
    return EntityWithSimpleProperties(
        boolean = null,
        booleanInt = null,
        color = null,
        double = null,
        id = null,
        integer = null,
        positiveInteger = null,
        string = null,
        url = null,
    )
}

fun CardContext.entityWithSimpleProperties(
    boolean: ValueProperty<Boolean>? = null,
    booleanInt: ValueProperty<BoolInt>? = null,
    color: ValueProperty<Color>? = null,
    double: ValueProperty<Double>? = null,
    id: ValueProperty<Int>? = null,
    integer: ValueProperty<Int>? = null,
    positiveInteger: ValueProperty<Int>? = null,
    string: ValueProperty<String>? = null,
    url: ValueProperty<URI>? = null,
): EntityWithSimpleProperties {
    return EntityWithSimpleProperties(
        boolean = boolean,
        booleanInt = booleanInt,
        color = color,
        double = double,
        id = id,
        integer = integer,
        positiveInteger = positiveInteger,
        string = string,
        url = url,
    )
}

fun CardContext.entityWithSimpleProperties(
    boolean: Boolean? = null,
    booleanInt: BoolInt? = null,
    color: Color? = null,
    double: Double? = null,
    id: Int? = null,
    integer: Int? = null,
    positiveInteger: Int? = null,
    string: String? = null,
    url: URI? = null,
): EntityWithSimpleProperties {
    return EntityWithSimpleProperties(
        boolean = optionalValue(boolean),
        booleanInt = optionalValue(booleanInt),
        color = optionalValue(color),
        double = optionalValue(double),
        id = optionalValue(id),
        integer = optionalValue(integer),
        positiveInteger = optionalValue(positiveInteger),
        string = optionalValue(string),
        url = optionalValue(url),
    )
}
