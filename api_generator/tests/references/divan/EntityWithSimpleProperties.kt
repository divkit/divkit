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
 * Entity with simple properties.
 * 
 * Can be created using the method [`entityWithSimpleProperties`].
 * 
 * Required properties: `type`.
 */
@Generated
class EntityWithSimpleProperties internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity() {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_simple_properties")
    )

    operator fun plus(additive: Properties): EntityWithSimpleProperties = EntityWithSimpleProperties(
        Properties(
            boolean = additive.boolean ?: properties.boolean,
            booleanInt = additive.booleanInt ?: properties.booleanInt,
            color = additive.color ?: properties.color,
            double = additive.double ?: properties.double,
            id = additive.id ?: properties.id,
            integer = additive.integer ?: properties.integer,
            positiveInteger = additive.positiveInteger ?: properties.positiveInteger,
            string = additive.string ?: properties.string,
            url = additive.url ?: properties.url,
        )
    )

    class Properties internal constructor(
        /**
         * Boolean property.
         */
        val boolean: Property<Boolean>?,
        /**
         * Boolean value in numeric format.
         */
        val booleanInt: Property<BoolInt>?,
        /**
         * Color.
         */
        val color: Property<Color>?,
        /**
         * Floating point number.
         */
        val double: Property<Double>?,
        /**
         * ID. Can't contain expressions.
         */
        val id: Property<Int>?,
        /**
         * Integer.
         */
        val integer: Property<Int>?,
        /**
         * Positive integer.
         */
        val positiveInteger: Property<Int>?,
        /**
         * String.
         */
        val string: Property<String>?,
        val url: Property<URI>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("boolean", boolean)
            result.tryPutProperty("boolean_int", booleanInt)
            result.tryPutProperty("color", color)
            result.tryPutProperty("double", double)
            result.tryPutProperty("id", id)
            result.tryPutProperty("integer", integer)
            result.tryPutProperty("positive_integer", positiveInteger)
            result.tryPutProperty("string", string)
            result.tryPutProperty("url", url)
            return result
        }
    }
}

/**
 * @param boolean Boolean property.
 * @param boolean_int Boolean value in numeric format.
 * @param color Color.
 * @param double Floating point number.
 * @param id ID. Can't contain expressions.
 * @param integer Integer.
 * @param positive_integer Positive integer.
 * @param string String.
 */
@Generated
fun DivScope.entityWithSimpleProperties(
    `use named arguments`: Guard = Guard.instance,
    boolean: Boolean? = null,
    booleanInt: BoolInt? = null,
    color: Color? = null,
    double: Double? = null,
    id: Int? = null,
    integer: Int? = null,
    positiveInteger: Int? = null,
    string: String? = null,
    url: URI? = null,
): EntityWithSimpleProperties = EntityWithSimpleProperties(
    EntityWithSimpleProperties.Properties(
        boolean = valueOrNull(boolean),
        booleanInt = valueOrNull(booleanInt),
        color = valueOrNull(color),
        double = valueOrNull(double),
        id = valueOrNull(id),
        integer = valueOrNull(integer),
        positiveInteger = valueOrNull(positiveInteger),
        string = valueOrNull(string),
        url = valueOrNull(url),
    )
)

/**
 * @param boolean Boolean property.
 * @param boolean_int Boolean value in numeric format.
 * @param color Color.
 * @param double Floating point number.
 * @param id ID. Can't contain expressions.
 * @param integer Integer.
 * @param positive_integer Positive integer.
 * @param string String.
 */
@Generated
fun DivScope.entityWithSimplePropertiesProps(
    `use named arguments`: Guard = Guard.instance,
    boolean: Boolean? = null,
    booleanInt: BoolInt? = null,
    color: Color? = null,
    double: Double? = null,
    id: Int? = null,
    integer: Int? = null,
    positiveInteger: Int? = null,
    string: String? = null,
    url: URI? = null,
) = EntityWithSimpleProperties.Properties(
    boolean = valueOrNull(boolean),
    booleanInt = valueOrNull(booleanInt),
    color = valueOrNull(color),
    double = valueOrNull(double),
    id = valueOrNull(id),
    integer = valueOrNull(integer),
    positiveInteger = valueOrNull(positiveInteger),
    string = valueOrNull(string),
    url = valueOrNull(url),
)

/**
 * @param boolean Boolean property.
 * @param boolean_int Boolean value in numeric format.
 * @param color Color.
 * @param double Floating point number.
 * @param id ID. Can't contain expressions.
 * @param integer Integer.
 * @param positive_integer Positive integer.
 * @param string String.
 */
@Generated
fun TemplateScope.entityWithSimplePropertiesRefs(
    `use named arguments`: Guard = Guard.instance,
    boolean: ReferenceProperty<Boolean>? = null,
    booleanInt: ReferenceProperty<BoolInt>? = null,
    color: ReferenceProperty<Color>? = null,
    double: ReferenceProperty<Double>? = null,
    id: ReferenceProperty<Int>? = null,
    integer: ReferenceProperty<Int>? = null,
    positiveInteger: ReferenceProperty<Int>? = null,
    string: ReferenceProperty<String>? = null,
    url: ReferenceProperty<URI>? = null,
) = EntityWithSimpleProperties.Properties(
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

/**
 * @param boolean Boolean property.
 * @param boolean_int Boolean value in numeric format.
 * @param color Color.
 * @param double Floating point number.
 * @param id ID. Can't contain expressions.
 * @param integer Integer.
 * @param positive_integer Positive integer.
 * @param string String.
 */
@Generated
fun EntityWithSimpleProperties.override(
    `use named arguments`: Guard = Guard.instance,
    boolean: Boolean? = null,
    booleanInt: BoolInt? = null,
    color: Color? = null,
    double: Double? = null,
    id: Int? = null,
    integer: Int? = null,
    positiveInteger: Int? = null,
    string: String? = null,
    url: URI? = null,
): EntityWithSimpleProperties = EntityWithSimpleProperties(
    EntityWithSimpleProperties.Properties(
        boolean = valueOrNull(boolean) ?: properties.boolean,
        booleanInt = valueOrNull(booleanInt) ?: properties.booleanInt,
        color = valueOrNull(color) ?: properties.color,
        double = valueOrNull(double) ?: properties.double,
        id = valueOrNull(id) ?: properties.id,
        integer = valueOrNull(integer) ?: properties.integer,
        positiveInteger = valueOrNull(positiveInteger) ?: properties.positiveInteger,
        string = valueOrNull(string) ?: properties.string,
        url = valueOrNull(url) ?: properties.url,
    )
)

/**
 * @param boolean Boolean property.
 * @param boolean_int Boolean value in numeric format.
 * @param color Color.
 * @param double Floating point number.
 * @param id ID. Can't contain expressions.
 * @param integer Integer.
 * @param positive_integer Positive integer.
 * @param string String.
 */
@Generated
fun Component<EntityWithSimpleProperties>.override(
    `use named arguments`: Guard = Guard.instance,
    boolean: Boolean? = null,
    booleanInt: BoolInt? = null,
    color: Color? = null,
    double: Double? = null,
    id: Int? = null,
    integer: Int? = null,
    positiveInteger: Int? = null,
    string: String? = null,
    url: URI? = null,
): Component<EntityWithSimpleProperties> = Component(
    template = template,
    properties = EntityWithSimpleProperties.Properties(
        boolean = valueOrNull(boolean),
        booleanInt = valueOrNull(booleanInt),
        color = valueOrNull(color),
        double = valueOrNull(double),
        id = valueOrNull(id),
        integer = valueOrNull(integer),
        positiveInteger = valueOrNull(positiveInteger),
        string = valueOrNull(string),
        url = valueOrNull(url),
    ).mergeWith(properties)
)

/**
 * @param boolean Boolean property.
 * @param boolean_int Boolean value in numeric format.
 * @param color Color.
 * @param double Floating point number.
 * @param id ID. Can't contain expressions.
 * @param integer Integer.
 * @param positive_integer Positive integer.
 * @param string String.
 */
@Generated
fun EntityWithSimpleProperties.defer(
    `use named arguments`: Guard = Guard.instance,
    boolean: ReferenceProperty<Boolean>? = null,
    booleanInt: ReferenceProperty<BoolInt>? = null,
    color: ReferenceProperty<Color>? = null,
    double: ReferenceProperty<Double>? = null,
    id: ReferenceProperty<Int>? = null,
    integer: ReferenceProperty<Int>? = null,
    positiveInteger: ReferenceProperty<Int>? = null,
    string: ReferenceProperty<String>? = null,
    url: ReferenceProperty<URI>? = null,
): EntityWithSimpleProperties = EntityWithSimpleProperties(
    EntityWithSimpleProperties.Properties(
        boolean = boolean ?: properties.boolean,
        booleanInt = booleanInt ?: properties.booleanInt,
        color = color ?: properties.color,
        double = double ?: properties.double,
        id = id ?: properties.id,
        integer = integer ?: properties.integer,
        positiveInteger = positiveInteger ?: properties.positiveInteger,
        string = string ?: properties.string,
        url = url ?: properties.url,
    )
)

/**
 * @param boolean Boolean property.
 * @param boolean_int Boolean value in numeric format.
 * @param color Color.
 * @param double Floating point number.
 * @param id ID. Can't contain expressions.
 * @param integer Integer.
 * @param positive_integer Positive integer.
 * @param string String.
 */
@Generated
fun Component<EntityWithSimpleProperties>.defer(
    `use named arguments`: Guard = Guard.instance,
    boolean: ReferenceProperty<Boolean>? = null,
    booleanInt: ReferenceProperty<BoolInt>? = null,
    color: ReferenceProperty<Color>? = null,
    double: ReferenceProperty<Double>? = null,
    id: ReferenceProperty<Int>? = null,
    integer: ReferenceProperty<Int>? = null,
    positiveInteger: ReferenceProperty<Int>? = null,
    string: ReferenceProperty<String>? = null,
    url: ReferenceProperty<URI>? = null,
): Component<EntityWithSimpleProperties> = Component(
    template = template,
    properties = EntityWithSimpleProperties.Properties(
        boolean = boolean,
        booleanInt = booleanInt,
        color = color,
        double = double,
        id = id,
        integer = integer,
        positiveInteger = positiveInteger,
        string = string,
        url = url,
    ).mergeWith(properties)
)

/**
 * @param boolean Boolean property.
 * @param boolean_int Boolean value in numeric format.
 * @param color Color.
 * @param double Floating point number.
 * @param integer Integer.
 * @param positive_integer Positive integer.
 * @param string String.
 */
@Generated
fun EntityWithSimpleProperties.evaluate(
    `use named arguments`: Guard = Guard.instance,
    boolean: ExpressionProperty<Boolean>? = null,
    booleanInt: ExpressionProperty<BoolInt>? = null,
    color: ExpressionProperty<Color>? = null,
    double: ExpressionProperty<Double>? = null,
    integer: ExpressionProperty<Int>? = null,
    positiveInteger: ExpressionProperty<Int>? = null,
    string: ExpressionProperty<String>? = null,
    url: ExpressionProperty<URI>? = null,
): EntityWithSimpleProperties = EntityWithSimpleProperties(
    EntityWithSimpleProperties.Properties(
        boolean = boolean ?: properties.boolean,
        booleanInt = booleanInt ?: properties.booleanInt,
        color = color ?: properties.color,
        double = double ?: properties.double,
        id = properties.id,
        integer = integer ?: properties.integer,
        positiveInteger = positiveInteger ?: properties.positiveInteger,
        string = string ?: properties.string,
        url = url ?: properties.url,
    )
)

/**
 * @param boolean Boolean property.
 * @param boolean_int Boolean value in numeric format.
 * @param color Color.
 * @param double Floating point number.
 * @param integer Integer.
 * @param positive_integer Positive integer.
 * @param string String.
 */
@Generated
fun Component<EntityWithSimpleProperties>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    boolean: ExpressionProperty<Boolean>? = null,
    booleanInt: ExpressionProperty<BoolInt>? = null,
    color: ExpressionProperty<Color>? = null,
    double: ExpressionProperty<Double>? = null,
    integer: ExpressionProperty<Int>? = null,
    positiveInteger: ExpressionProperty<Int>? = null,
    string: ExpressionProperty<String>? = null,
    url: ExpressionProperty<URI>? = null,
): Component<EntityWithSimpleProperties> = Component(
    template = template,
    properties = EntityWithSimpleProperties.Properties(
        boolean = boolean,
        booleanInt = booleanInt,
        color = color,
        double = double,
        id = null,
        integer = integer,
        positiveInteger = positiveInteger,
        string = string,
        url = url,
    ).mergeWith(properties)
)

@Generated
operator fun Component<EntityWithSimpleProperties>.plus(additive: Properties): Component<EntityWithSimpleProperties> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun EntityWithSimpleProperties.asList() = listOf(this)
