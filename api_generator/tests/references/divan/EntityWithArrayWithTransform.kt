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
 * Can be created using the method [`entityWithArrayWithTransform`].
 * 
 * Required properties: `type, array`.
 */
@Generated
class EntityWithArrayWithTransform internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity() {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_array_with_transform")
    )

    operator fun plus(additive: Properties): EntityWithArrayWithTransform = EntityWithArrayWithTransform(
        Properties(
            array = additive.array ?: properties.array,
        )
    )

    class Properties internal constructor(
        val array: Property<List<Color>>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("array", array)
            return result
        }
    }
}

@Generated
fun DivScope.entityWithArrayWithTransform(
    `use named arguments`: Guard = Guard.instance,
    array: List<Color>,
): EntityWithArrayWithTransform = EntityWithArrayWithTransform(
    EntityWithArrayWithTransform.Properties(
        array = valueOrNull(array),
    )
)

@Generated
fun DivScope.entityWithArrayWithTransformProps(
    `use named arguments`: Guard = Guard.instance,
    array: List<Color>? = null,
) = EntityWithArrayWithTransform.Properties(
    array = valueOrNull(array),
)

@Generated
fun TemplateScope.entityWithArrayWithTransformRefs(
    `use named arguments`: Guard = Guard.instance,
    array: ReferenceProperty<List<Color>>? = null,
) = EntityWithArrayWithTransform.Properties(
    array = array,
)

@Generated
fun EntityWithArrayWithTransform.override(
    `use named arguments`: Guard = Guard.instance,
    array: List<Color>? = null,
): EntityWithArrayWithTransform = EntityWithArrayWithTransform(
    EntityWithArrayWithTransform.Properties(
        array = valueOrNull(array) ?: properties.array,
    )
)

@Generated
fun Component<EntityWithArrayWithTransform>.override(
    `use named arguments`: Guard = Guard.instance,
    array: List<Color>? = null,
): Component<EntityWithArrayWithTransform> = Component(
    template = template,
    properties = EntityWithArrayWithTransform.Properties(
        array = valueOrNull(array),
    ).mergeWith(properties)
)

@Generated
fun EntityWithArrayWithTransform.defer(
    `use named arguments`: Guard = Guard.instance,
    array: ReferenceProperty<List<Color>>? = null,
): EntityWithArrayWithTransform = EntityWithArrayWithTransform(
    EntityWithArrayWithTransform.Properties(
        array = array ?: properties.array,
    )
)

@Generated
fun Component<EntityWithArrayWithTransform>.defer(
    `use named arguments`: Guard = Guard.instance,
    array: ReferenceProperty<List<Color>>? = null,
): Component<EntityWithArrayWithTransform> = Component(
    template = template,
    properties = EntityWithArrayWithTransform.Properties(
        array = array,
    ).mergeWith(properties)
)

@Generated
operator fun Component<EntityWithArrayWithTransform>.plus(additive: Properties): Component<EntityWithArrayWithTransform> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun EntityWithArrayWithTransform.asList() = listOf(this)
