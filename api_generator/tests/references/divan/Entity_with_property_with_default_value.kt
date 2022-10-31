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
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.collections.Map

/**
 *
 * Можно создать при помощи метода [entity_with_property_with_default_value].
 *
 * Обязательные поля: type
 */
@Generated
class Entity_with_property_with_default_value internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
    	mapOf("type" to "entity_with_property_with_default_value")
    )

    operator fun plus(additive: Properties): Entity_with_property_with_default_value =
            Entity_with_property_with_default_value(
    	Properties(
    		int = additive.int ?: properties.int,
    		url = additive.url ?: properties.url,
    		nested = additive.nested ?: properties.nested,
    	)
    )

    class Properties internal constructor(
        val int: Property<Int>?,
        val url: Property<URI>?,
        val nested: Property<Nested>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("int", int)
            result.tryPutProperty("url", url)
            result.tryPutProperty("nested", nested)
            return result
        }
    }

    /**
     * non_optional используется, чтобы запретить автогенерацию дефолтного значения для объекта, все
     * свойства которого опциональны.
     *
     * Можно создать при помощи метода [entity_with_property_with_default_valueNested].
     *
     * Обязательные поля: nonOptional
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
        		url = additive.url ?: properties.url,
        		nonOptional = additive.nonOptional ?: properties.nonOptional,
        	)
        )

        class Properties internal constructor(
            val int: Property<Int>?,
            val url: Property<URI>?,
            val nonOptional: Property<String>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("int", int)
                result.tryPutProperty("url", url)
                result.tryPutProperty("non_optional", nonOptional)
                return result
            }
        }
    }
}

@Generated
fun DivScope.entity_with_property_with_default_valueNested(
    `use named arguments`: Guard = Guard.instance,
    int: Int? = null,
    url: URI? = null,
    nonOptional: String? = null,
): Entity_with_property_with_default_value.Nested = Entity_with_property_with_default_value.Nested(
	Entity_with_property_with_default_value.Nested.Properties(
		int = valueOrNull(int),
		url = valueOrNull(url),
		nonOptional = valueOrNull(nonOptional),
	)
)

@Generated
fun DivScope.entity_with_property_with_default_valueNestedProps(
    `use named arguments`: Guard = Guard.instance,
    int: Int? = null,
    url: URI? = null,
    nonOptional: String? = null,
) = Entity_with_property_with_default_value.Nested.Properties(
	int = valueOrNull(int),
	url = valueOrNull(url),
	nonOptional = valueOrNull(nonOptional),
)

@Generated
fun TemplateScope.entity_with_property_with_default_valueNestedRefs(
    `use named arguments`: Guard = Guard.instance,
    int: ReferenceProperty<Int>? = null,
    url: ReferenceProperty<URI>? = null,
    nonOptional: ReferenceProperty<String>? = null,
) = Entity_with_property_with_default_value.Nested.Properties(
	int = int,
	url = url,
	nonOptional = nonOptional,
)

@Generated
fun Entity_with_property_with_default_value.Nested.override(
    `use named arguments`: Guard = Guard.instance,
    int: Int? = null,
    url: URI? = null,
    nonOptional: String? = null,
): Entity_with_property_with_default_value.Nested = Entity_with_property_with_default_value.Nested(
	Entity_with_property_with_default_value.Nested.Properties(
		int = valueOrNull(int) ?: properties.int,
		url = valueOrNull(url) ?: properties.url,
		nonOptional = valueOrNull(nonOptional) ?: properties.nonOptional,
	)
)

@Generated
fun Entity_with_property_with_default_value.Nested.defer(
    `use named arguments`: Guard = Guard.instance,
    int: ReferenceProperty<Int>? = null,
    url: ReferenceProperty<URI>? = null,
    nonOptional: ReferenceProperty<String>? = null,
): Entity_with_property_with_default_value.Nested = Entity_with_property_with_default_value.Nested(
	Entity_with_property_with_default_value.Nested.Properties(
		int = int ?: properties.int,
		url = url ?: properties.url,
		nonOptional = nonOptional ?: properties.nonOptional,
	)
)

@Generated
fun Entity_with_property_with_default_value.Nested.evaluate(
    `use named arguments`: Guard = Guard.instance,
    int: ExpressionProperty<Int>? = null,
    url: ExpressionProperty<URI>? = null,
    nonOptional: ExpressionProperty<String>? = null,
): Entity_with_property_with_default_value.Nested = Entity_with_property_with_default_value.Nested(
	Entity_with_property_with_default_value.Nested.Properties(
		int = int ?: properties.int,
		url = url ?: properties.url,
		nonOptional = nonOptional ?: properties.nonOptional,
	)
)

@Generated
fun DivScope.entity_with_property_with_default_value(
    `use named arguments`: Guard = Guard.instance,
    int: Int? = null,
    url: URI? = null,
    nested: Entity_with_property_with_default_value.Nested? = null,
): Entity_with_property_with_default_value = Entity_with_property_with_default_value(
	Entity_with_property_with_default_value.Properties(
		int = valueOrNull(int),
		url = valueOrNull(url),
		nested = valueOrNull(nested),
	)
)

@Generated
fun DivScope.entity_with_property_with_default_valueProps(
    `use named arguments`: Guard = Guard.instance,
    int: Int? = null,
    url: URI? = null,
    nested: Entity_with_property_with_default_value.Nested? = null,
) = Entity_with_property_with_default_value.Properties(
	int = valueOrNull(int),
	url = valueOrNull(url),
	nested = valueOrNull(nested),
)

@Generated
fun TemplateScope.entity_with_property_with_default_valueRefs(
    `use named arguments`: Guard = Guard.instance,
    int: ReferenceProperty<Int>? = null,
    url: ReferenceProperty<URI>? = null,
    nested: ReferenceProperty<Entity_with_property_with_default_value.Nested>? = null,
) = Entity_with_property_with_default_value.Properties(
	int = int,
	url = url,
	nested = nested,
)

@Generated
fun Entity_with_property_with_default_value.override(
    `use named arguments`: Guard = Guard.instance,
    int: Int? = null,
    url: URI? = null,
    nested: Entity_with_property_with_default_value.Nested? = null,
): Entity_with_property_with_default_value = Entity_with_property_with_default_value(
	Entity_with_property_with_default_value.Properties(
		int = valueOrNull(int) ?: properties.int,
		url = valueOrNull(url) ?: properties.url,
		nested = valueOrNull(nested) ?: properties.nested,
	)
)

@Generated
fun Entity_with_property_with_default_value.defer(
    `use named arguments`: Guard = Guard.instance,
    int: ReferenceProperty<Int>? = null,
    url: ReferenceProperty<URI>? = null,
    nested: ReferenceProperty<Entity_with_property_with_default_value.Nested>? = null,
): Entity_with_property_with_default_value = Entity_with_property_with_default_value(
	Entity_with_property_with_default_value.Properties(
		int = int ?: properties.int,
		url = url ?: properties.url,
		nested = nested ?: properties.nested,
	)
)

@Generated
fun Entity_with_property_with_default_value.evaluate(
    `use named arguments`: Guard = Guard.instance,
    int: ExpressionProperty<Int>? = null,
    url: ExpressionProperty<URI>? = null,
): Entity_with_property_with_default_value = Entity_with_property_with_default_value(
	Entity_with_property_with_default_value.Properties(
		int = int ?: properties.int,
		url = url ?: properties.url,
		nested = properties.nested,
	)
)
