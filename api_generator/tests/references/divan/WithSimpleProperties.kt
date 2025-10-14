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
 * Can be created using the method [withSimpleProperties].
 * 
 * Required parameters: `type`.
 */
@Generated
@ExposedCopyVisibility
data class WithSimpleProperties internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_simple_properties")
    )

    operator fun plus(additive: Properties): WithSimpleProperties = WithSimpleProperties(
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

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Boolean property.
         */
        val boolean: Property<Boolean>?,
        /**
         * Boolean value in numeric format.
         */
        @DeprecatedApi("Marked as deprecated in the JSON schema")
        val booleanInt: Property<Boolean>?,
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
         * Default value: `0`.
         */
        val id: Property<Int>?,
        /**
         * Integer.
         * Default value: `0`.
         */
        val integer: Property<Long>?,
        /**
         * Positive integer.
         */
        val positiveInteger: Property<Int>?,
        /**
         * String.
         */
        val string: Property<String>?,
        val url: Property<Url>?,
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
 * @param booleanInt Boolean value in numeric format.
 * @param color Color.
 * @param double Floating point number.
 * @param id ID. Can't contain expressions.
 * @param integer Integer.
 * @param positiveInteger Positive integer.
 * @param string String.
 */
@Generated
fun DivScope.withSimpleProperties(
    `use named arguments`: Guard = Guard.instance,
    boolean: Boolean? = null,
    @DeprecatedApi("Marked as deprecated in the JSON schema") booleanInt: Boolean? = null,
    color: Color? = null,
    double: Double? = null,
    id: Int? = null,
    integer: Long? = null,
    positiveInteger: Int? = null,
    string: String? = null,
    url: Url? = null,
): WithSimpleProperties = WithSimpleProperties(
    WithSimpleProperties.Properties(
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
 * @param booleanInt Boolean value in numeric format.
 * @param color Color.
 * @param double Floating point number.
 * @param id ID. Can't contain expressions.
 * @param integer Integer.
 * @param positiveInteger Positive integer.
 * @param string String.
 */
@Generated
fun DivScope.withSimplePropertiesProps(
    `use named arguments`: Guard = Guard.instance,
    boolean: Boolean? = null,
    @DeprecatedApi("Marked as deprecated in the JSON schema") booleanInt: Boolean? = null,
    color: Color? = null,
    double: Double? = null,
    id: Int? = null,
    integer: Long? = null,
    positiveInteger: Int? = null,
    string: String? = null,
    url: Url? = null,
) = WithSimpleProperties.Properties(
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
 * @param booleanInt Boolean value in numeric format.
 * @param color Color.
 * @param double Floating point number.
 * @param id ID. Can't contain expressions.
 * @param integer Integer.
 * @param positiveInteger Positive integer.
 * @param string String.
 */
@Generated
fun TemplateScope.withSimplePropertiesRefs(
    `use named arguments`: Guard = Guard.instance,
    boolean: ReferenceProperty<Boolean>? = null,
    @DeprecatedApi("Marked as deprecated in the JSON schema") booleanInt: ReferenceProperty<Boolean>? = null,
    color: ReferenceProperty<Color>? = null,
    double: ReferenceProperty<Double>? = null,
    id: ReferenceProperty<Int>? = null,
    integer: ReferenceProperty<Long>? = null,
    positiveInteger: ReferenceProperty<Int>? = null,
    string: ReferenceProperty<String>? = null,
    url: ReferenceProperty<Url>? = null,
) = WithSimpleProperties.Properties(
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
 * @param booleanInt Boolean value in numeric format.
 * @param color Color.
 * @param double Floating point number.
 * @param id ID. Can't contain expressions.
 * @param integer Integer.
 * @param positiveInteger Positive integer.
 * @param string String.
 */
@Generated
fun WithSimpleProperties.override(
    `use named arguments`: Guard = Guard.instance,
    boolean: Boolean? = null,
    @DeprecatedApi("Marked as deprecated in the JSON schema") booleanInt: Boolean? = null,
    color: Color? = null,
    double: Double? = null,
    id: Int? = null,
    integer: Long? = null,
    positiveInteger: Int? = null,
    string: String? = null,
    url: Url? = null,
): WithSimpleProperties = WithSimpleProperties(
    WithSimpleProperties.Properties(
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
 * @param booleanInt Boolean value in numeric format.
 * @param color Color.
 * @param double Floating point number.
 * @param id ID. Can't contain expressions.
 * @param integer Integer.
 * @param positiveInteger Positive integer.
 * @param string String.
 */
@Generated
fun WithSimpleProperties.defer(
    `use named arguments`: Guard = Guard.instance,
    boolean: ReferenceProperty<Boolean>? = null,
    @DeprecatedApi("Marked as deprecated in the JSON schema") booleanInt: ReferenceProperty<Boolean>? = null,
    color: ReferenceProperty<Color>? = null,
    double: ReferenceProperty<Double>? = null,
    id: ReferenceProperty<Int>? = null,
    integer: ReferenceProperty<Long>? = null,
    positiveInteger: ReferenceProperty<Int>? = null,
    string: ReferenceProperty<String>? = null,
    url: ReferenceProperty<Url>? = null,
): WithSimpleProperties = WithSimpleProperties(
    WithSimpleProperties.Properties(
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
 * @param booleanInt Boolean value in numeric format.
 * @param color Color.
 * @param double Floating point number.
 * @param id ID. Can't contain expressions.
 * @param integer Integer.
 * @param positiveInteger Positive integer.
 * @param string String.
 */
@Generated
fun WithSimpleProperties.modify(
    `use named arguments`: Guard = Guard.instance,
    boolean: Property<Boolean>? = null,
    @DeprecatedApi("Marked as deprecated in the JSON schema") booleanInt: Property<Boolean>? = null,
    color: Property<Color>? = null,
    double: Property<Double>? = null,
    id: Property<Int>? = null,
    integer: Property<Long>? = null,
    positiveInteger: Property<Int>? = null,
    string: Property<String>? = null,
    url: Property<Url>? = null,
): WithSimpleProperties = WithSimpleProperties(
    WithSimpleProperties.Properties(
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
 * @param booleanInt Boolean value in numeric format.
 * @param color Color.
 * @param double Floating point number.
 * @param integer Integer.
 * @param positiveInteger Positive integer.
 * @param string String.
 */
@Generated
fun WithSimpleProperties.evaluate(
    `use named arguments`: Guard = Guard.instance,
    boolean: ExpressionProperty<Boolean>? = null,
    @DeprecatedApi("Marked as deprecated in the JSON schema") booleanInt: ExpressionProperty<Boolean>? = null,
    color: ExpressionProperty<Color>? = null,
    double: ExpressionProperty<Double>? = null,
    integer: ExpressionProperty<Long>? = null,
    positiveInteger: ExpressionProperty<Int>? = null,
    string: ExpressionProperty<String>? = null,
    url: ExpressionProperty<Url>? = null,
): WithSimpleProperties = WithSimpleProperties(
    WithSimpleProperties.Properties(
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
 * @param booleanInt Boolean value in numeric format.
 * @param color Color.
 * @param double Floating point number.
 * @param id ID. Can't contain expressions.
 * @param integer Integer.
 * @param positiveInteger Positive integer.
 * @param string String.
 */
@Generated
fun Component<WithSimpleProperties>.override(
    `use named arguments`: Guard = Guard.instance,
    boolean: Boolean? = null,
    @DeprecatedApi("Marked as deprecated in the JSON schema") booleanInt: Boolean? = null,
    color: Color? = null,
    double: Double? = null,
    id: Int? = null,
    integer: Long? = null,
    positiveInteger: Int? = null,
    string: String? = null,
    url: Url? = null,
): Component<WithSimpleProperties> = Component(
    template = template,
    properties = WithSimpleProperties.Properties(
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
 * @param booleanInt Boolean value in numeric format.
 * @param color Color.
 * @param double Floating point number.
 * @param id ID. Can't contain expressions.
 * @param integer Integer.
 * @param positiveInteger Positive integer.
 * @param string String.
 */
@Generated
fun Component<WithSimpleProperties>.defer(
    `use named arguments`: Guard = Guard.instance,
    boolean: ReferenceProperty<Boolean>? = null,
    @DeprecatedApi("Marked as deprecated in the JSON schema") booleanInt: ReferenceProperty<Boolean>? = null,
    color: ReferenceProperty<Color>? = null,
    double: ReferenceProperty<Double>? = null,
    id: ReferenceProperty<Int>? = null,
    integer: ReferenceProperty<Long>? = null,
    positiveInteger: ReferenceProperty<Int>? = null,
    string: ReferenceProperty<String>? = null,
    url: ReferenceProperty<Url>? = null,
): Component<WithSimpleProperties> = Component(
    template = template,
    properties = WithSimpleProperties.Properties(
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
 * @param booleanInt Boolean value in numeric format.
 * @param color Color.
 * @param double Floating point number.
 * @param integer Integer.
 * @param positiveInteger Positive integer.
 * @param string String.
 */
@Generated
fun Component<WithSimpleProperties>.evaluate(
    `use named arguments`: Guard = Guard.instance,
    boolean: ExpressionProperty<Boolean>? = null,
    @DeprecatedApi("Marked as deprecated in the JSON schema") booleanInt: ExpressionProperty<Boolean>? = null,
    color: ExpressionProperty<Color>? = null,
    double: ExpressionProperty<Double>? = null,
    integer: ExpressionProperty<Long>? = null,
    positiveInteger: ExpressionProperty<Int>? = null,
    string: ExpressionProperty<String>? = null,
    url: ExpressionProperty<Url>? = null,
): Component<WithSimpleProperties> = Component(
    template = template,
    properties = WithSimpleProperties.Properties(
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

/**
 * @param boolean Boolean property.
 * @param booleanInt Boolean value in numeric format.
 * @param color Color.
 * @param double Floating point number.
 * @param id ID. Can't contain expressions.
 * @param integer Integer.
 * @param positiveInteger Positive integer.
 * @param string String.
 */
@Generated
fun Component<WithSimpleProperties>.modify(
    `use named arguments`: Guard = Guard.instance,
    boolean: Property<Boolean>? = null,
    @DeprecatedApi("Marked as deprecated in the JSON schema") booleanInt: Property<Boolean>? = null,
    color: Property<Color>? = null,
    double: Property<Double>? = null,
    id: Property<Int>? = null,
    integer: Property<Long>? = null,
    positiveInteger: Property<Int>? = null,
    string: Property<String>? = null,
    url: Property<Url>? = null,
): Component<WithSimpleProperties> = Component(
    template = template,
    properties = WithSimpleProperties.Properties(
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

@Generated
operator fun Component<WithSimpleProperties>.plus(additive: WithSimpleProperties.Properties): Component<WithSimpleProperties> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun WithSimpleProperties.asList() = listOf(this)
