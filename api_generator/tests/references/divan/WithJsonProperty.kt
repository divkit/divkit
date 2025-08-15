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
 * Can be created using the method [withJsonProperty].
 * 
 * Required parameters: `type`.
 */
@Generated
data class WithJsonProperty internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_json_property")
    )

    operator fun plus(additive: Properties): WithJsonProperty = WithJsonProperty(
        Properties(
            jsonProperty = additive.jsonProperty ?: properties.jsonProperty,
        )
    )

    data class Properties internal constructor(
        /**
         * Default value: `{ "key": "value", "items": [ "value" ] }`.
         */
        val jsonProperty: Property<Map<String, Any>>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("json_property", jsonProperty)
            return result
        }
    }
}

@Generated
fun DivScope.withJsonProperty(
    `use named arguments`: Guard = Guard.instance,
    jsonProperty: Map<String, Any>? = null,
): WithJsonProperty = WithJsonProperty(
    WithJsonProperty.Properties(
        jsonProperty = valueOrNull(jsonProperty),
    )
)

@Generated
fun DivScope.withJsonPropertyProps(
    `use named arguments`: Guard = Guard.instance,
    jsonProperty: Map<String, Any>? = null,
) = WithJsonProperty.Properties(
    jsonProperty = valueOrNull(jsonProperty),
)

@Generated
fun TemplateScope.withJsonPropertyRefs(
    `use named arguments`: Guard = Guard.instance,
    jsonProperty: ReferenceProperty<Map<String, Any>>? = null,
) = WithJsonProperty.Properties(
    jsonProperty = jsonProperty,
)

@Generated
fun WithJsonProperty.override(
    `use named arguments`: Guard = Guard.instance,
    jsonProperty: Map<String, Any>? = null,
): WithJsonProperty = WithJsonProperty(
    WithJsonProperty.Properties(
        jsonProperty = valueOrNull(jsonProperty) ?: properties.jsonProperty,
    )
)

@Generated
fun WithJsonProperty.defer(
    `use named arguments`: Guard = Guard.instance,
    jsonProperty: ReferenceProperty<Map<String, Any>>? = null,
): WithJsonProperty = WithJsonProperty(
    WithJsonProperty.Properties(
        jsonProperty = jsonProperty ?: properties.jsonProperty,
    )
)

@Generated
fun WithJsonProperty.modify(
    `use named arguments`: Guard = Guard.instance,
    jsonProperty: Property<Map<String, Any>>? = null,
): WithJsonProperty = WithJsonProperty(
    WithJsonProperty.Properties(
        jsonProperty = jsonProperty ?: properties.jsonProperty,
    )
)

@Generated
fun WithJsonProperty.asList() = listOf(this)
