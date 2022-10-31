@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divan

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import divan.annotation.Generated
import divan.core.ExpressionProperty
import divan.core.Guard
import divan.core.Property
import divan.core.ReferenceProperty
import divan.core.tryPutProperty
import divan.core.valueOrNull
import divan.scope.DivScope
import divan.scope.TemplateScope
import java.net.URI
import kotlin.Any
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Double
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.collections.Map

/**
 * Объект с простыми свойствами.
 *
 * Можно создать при помощи метода [entity_with_simple_properties].
 *
 * Обязательные поля: type
 */
@Generated
class Entity_with_simple_properties internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
    	mapOf("type" to "entity_with_simple_properties")
    )

    operator fun plus(additive: Properties): Entity_with_simple_properties =
            Entity_with_simple_properties(
    	Properties(
    		id = additive.id ?: properties.id,
    		string = additive.string ?: properties.string,
    		integer = additive.integer ?: properties.integer,
    		double = additive.double ?: properties.double,
    		boolean = additive.boolean ?: properties.boolean,
    		booleanInt = additive.booleanInt ?: properties.booleanInt,
    		positiveInteger = additive.positiveInteger ?: properties.positiveInteger,
    		color = additive.color ?: properties.color,
    		url = additive.url ?: properties.url,
    	)
    )

    class Properties internal constructor(
        /**
         * Идентификатор. Не может содержать выражение.
         */
        val id: Property<Int>?,
        /**
         * Строка.
         */
        val string: Property<String>?,
        /**
         * Целое число.
         */
        val integer: Property<Int>?,
        /**
         * Число с плавающей точкой.
         */
        val double: Property<Double>?,
        /**
         * Логическое значение.
         */
        val boolean: Property<Boolean>?,
        /**
         * Логическое значение в числовом формате.
         */
        @Deprecated("Marked as deprecated in json schema")
        val booleanInt: Property<Boolean>?,
        /**
         * Положительное целое число.
         */
        val positiveInteger: Property<Int>?,
        /**
         * Цвет.
         */
        val color: Property<Color>?,
        val url: Property<URI>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("id", id)
            result.tryPutProperty("string", string)
            result.tryPutProperty("integer", integer)
            result.tryPutProperty("double", double)
            result.tryPutProperty("boolean", boolean)
            result.tryPutProperty("boolean_int", booleanInt)
            result.tryPutProperty("positive_integer", positiveInteger)
            result.tryPutProperty("color", color)
            result.tryPutProperty("url", url)
            return result
        }
    }
}

/**
 * @param id Идентификатор. Не может содержать выражение.
 * @param string Строка.
 * @param integer Целое число.
 * @param double Число с плавающей точкой.
 * @param boolean Логическое значение.
 * @param booleanInt Логическое значение в числовом формате.
 * @param positiveInteger Положительное целое число.
 * @param color Цвет.
 */
@Generated
fun DivScope.entity_with_simple_properties(
    `use named arguments`: Guard = Guard.instance,
    id: Int? = null,
    string: String? = null,
    integer: Int? = null,
    double: Double? = null,
    boolean: Boolean? = null,
    booleanInt: Boolean? = null,
    positiveInteger: Int? = null,
    color: Color? = null,
    url: URI? = null,
): Entity_with_simple_properties = Entity_with_simple_properties(
	Entity_with_simple_properties.Properties(
		id = valueOrNull(id),
		string = valueOrNull(string),
		integer = valueOrNull(integer),
		double = valueOrNull(double),
		boolean = valueOrNull(boolean),
		booleanInt = valueOrNull(booleanInt),
		positiveInteger = valueOrNull(positiveInteger),
		color = valueOrNull(color),
		url = valueOrNull(url),
	)
)

/**
 * @param id Идентификатор. Не может содержать выражение.
 * @param string Строка.
 * @param integer Целое число.
 * @param double Число с плавающей точкой.
 * @param boolean Логическое значение.
 * @param booleanInt Логическое значение в числовом формате.
 * @param positiveInteger Положительное целое число.
 * @param color Цвет.
 */
@Generated
fun DivScope.entity_with_simple_propertiesProps(
    `use named arguments`: Guard = Guard.instance,
    id: Int? = null,
    string: String? = null,
    integer: Int? = null,
    double: Double? = null,
    boolean: Boolean? = null,
    booleanInt: Boolean? = null,
    positiveInteger: Int? = null,
    color: Color? = null,
    url: URI? = null,
) = Entity_with_simple_properties.Properties(
	id = valueOrNull(id),
	string = valueOrNull(string),
	integer = valueOrNull(integer),
	double = valueOrNull(double),
	boolean = valueOrNull(boolean),
	booleanInt = valueOrNull(booleanInt),
	positiveInteger = valueOrNull(positiveInteger),
	color = valueOrNull(color),
	url = valueOrNull(url),
)

/**
 * @param id Идентификатор. Не может содержать выражение.
 * @param string Строка.
 * @param integer Целое число.
 * @param double Число с плавающей точкой.
 * @param boolean Логическое значение.
 * @param booleanInt Логическое значение в числовом формате.
 * @param positiveInteger Положительное целое число.
 * @param color Цвет.
 */
@Generated
fun TemplateScope.entity_with_simple_propertiesRefs(
    `use named arguments`: Guard = Guard.instance,
    id: ReferenceProperty<Int>? = null,
    string: ReferenceProperty<String>? = null,
    integer: ReferenceProperty<Int>? = null,
    double: ReferenceProperty<Double>? = null,
    boolean: ReferenceProperty<Boolean>? = null,
    booleanInt: ReferenceProperty<Boolean>? = null,
    positiveInteger: ReferenceProperty<Int>? = null,
    color: ReferenceProperty<Color>? = null,
    url: ReferenceProperty<URI>? = null,
) = Entity_with_simple_properties.Properties(
	id = id,
	string = string,
	integer = integer,
	double = double,
	boolean = boolean,
	booleanInt = booleanInt,
	positiveInteger = positiveInteger,
	color = color,
	url = url,
)

/**
 * @param id Идентификатор. Не может содержать выражение.
 * @param string Строка.
 * @param integer Целое число.
 * @param double Число с плавающей точкой.
 * @param boolean Логическое значение.
 * @param booleanInt Логическое значение в числовом формате.
 * @param positiveInteger Положительное целое число.
 * @param color Цвет.
 */
@Generated
fun Entity_with_simple_properties.override(
    `use named arguments`: Guard = Guard.instance,
    id: Int? = null,
    string: String? = null,
    integer: Int? = null,
    double: Double? = null,
    boolean: Boolean? = null,
    booleanInt: Boolean? = null,
    positiveInteger: Int? = null,
    color: Color? = null,
    url: URI? = null,
): Entity_with_simple_properties = Entity_with_simple_properties(
	Entity_with_simple_properties.Properties(
		id = valueOrNull(id) ?: properties.id,
		string = valueOrNull(string) ?: properties.string,
		integer = valueOrNull(integer) ?: properties.integer,
		double = valueOrNull(double) ?: properties.double,
		boolean = valueOrNull(boolean) ?: properties.boolean,
		booleanInt = valueOrNull(booleanInt) ?: properties.booleanInt,
		positiveInteger = valueOrNull(positiveInteger) ?: properties.positiveInteger,
		color = valueOrNull(color) ?: properties.color,
		url = valueOrNull(url) ?: properties.url,
	)
)

/**
 * @param id Идентификатор. Не может содержать выражение.
 * @param string Строка.
 * @param integer Целое число.
 * @param double Число с плавающей точкой.
 * @param boolean Логическое значение.
 * @param booleanInt Логическое значение в числовом формате.
 * @param positiveInteger Положительное целое число.
 * @param color Цвет.
 */
@Generated
fun Entity_with_simple_properties.defer(
    `use named arguments`: Guard = Guard.instance,
    id: ReferenceProperty<Int>? = null,
    string: ReferenceProperty<String>? = null,
    integer: ReferenceProperty<Int>? = null,
    double: ReferenceProperty<Double>? = null,
    boolean: ReferenceProperty<Boolean>? = null,
    booleanInt: ReferenceProperty<Boolean>? = null,
    positiveInteger: ReferenceProperty<Int>? = null,
    color: ReferenceProperty<Color>? = null,
    url: ReferenceProperty<URI>? = null,
): Entity_with_simple_properties = Entity_with_simple_properties(
	Entity_with_simple_properties.Properties(
		id = id ?: properties.id,
		string = string ?: properties.string,
		integer = integer ?: properties.integer,
		double = double ?: properties.double,
		boolean = boolean ?: properties.boolean,
		booleanInt = booleanInt ?: properties.booleanInt,
		positiveInteger = positiveInteger ?: properties.positiveInteger,
		color = color ?: properties.color,
		url = url ?: properties.url,
	)
)

/**
 * @param string Строка.
 * @param integer Целое число.
 * @param double Число с плавающей точкой.
 * @param boolean Логическое значение.
 * @param booleanInt Логическое значение в числовом формате.
 * @param positiveInteger Положительное целое число.
 * @param color Цвет.
 */
@Generated
fun Entity_with_simple_properties.evaluate(
    `use named arguments`: Guard = Guard.instance,
    string: ExpressionProperty<String>? = null,
    integer: ExpressionProperty<Int>? = null,
    double: ExpressionProperty<Double>? = null,
    boolean: ExpressionProperty<Boolean>? = null,
    booleanInt: ExpressionProperty<Boolean>? = null,
    positiveInteger: ExpressionProperty<Int>? = null,
    color: ExpressionProperty<Color>? = null,
    url: ExpressionProperty<URI>? = null,
): Entity_with_simple_properties = Entity_with_simple_properties(
	Entity_with_simple_properties.Properties(
		id = properties.id,
		string = string ?: properties.string,
		integer = integer ?: properties.integer,
		double = double ?: properties.double,
		boolean = boolean ?: properties.boolean,
		booleanInt = booleanInt ?: properties.booleanInt,
		positiveInteger = positiveInteger ?: properties.positiveInteger,
		color = color ?: properties.color,
		url = url ?: properties.url,
	)
)
