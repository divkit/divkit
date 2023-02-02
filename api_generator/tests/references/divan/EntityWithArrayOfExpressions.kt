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
 * Can be created using the method [`entityWithArrayOfExpressions`].
 * 
 * Required properties: `type, items`.
 */
@Generated
class EntityWithArrayOfExpressions internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity() {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_array_of_expressions")
    )

    operator fun plus(additive: Properties): EntityWithArrayOfExpressions = EntityWithArrayOfExpressions(
        Properties(
            items = additive.items ?: properties.items,
        )
    )

    class Properties internal constructor(
        val items: Property<List<String>>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("items", items)
            return result
        }
    }
}

@Generated
fun DivScope.entityWithArrayOfExpressions(
    `use named arguments`: Guard = Guard.instance,
    items: List<String>,
): EntityWithArrayOfExpressions = EntityWithArrayOfExpressions(
    EntityWithArrayOfExpressions.Properties(
        items = valueOrNull(items),
    )
)

@Generated
fun DivScope.entityWithArrayOfExpressionsProps(
    `use named arguments`: Guard = Guard.instance,
    items: List<String>? = null,
) = EntityWithArrayOfExpressions.Properties(
    items = valueOrNull(items),
)

@Generated
fun TemplateScope.entityWithArrayOfExpressionsRefs(
    `use named arguments`: Guard = Guard.instance,
    items: ReferenceProperty<List<String>>? = null,
) = EntityWithArrayOfExpressions.Properties(
    items = items,
)

@Generated
fun EntityWithArrayOfExpressions.override(
    `use named arguments`: Guard = Guard.instance,
    items: List<String>? = null,
): EntityWithArrayOfExpressions = EntityWithArrayOfExpressions(
    EntityWithArrayOfExpressions.Properties(
        items = valueOrNull(items) ?: properties.items,
    )
)

@Generated
fun Component<EntityWithArrayOfExpressions>.override(
    `use named arguments`: Guard = Guard.instance,
    items: List<String>? = null,
): Component<EntityWithArrayOfExpressions> = Component(
    template = template,
    properties = EntityWithArrayOfExpressions.Properties(
        items = valueOrNull(items),
    ).mergeWith(properties)
)

@Generated
fun EntityWithArrayOfExpressions.defer(
    `use named arguments`: Guard = Guard.instance,
    items: ReferenceProperty<List<String>>? = null,
): EntityWithArrayOfExpressions = EntityWithArrayOfExpressions(
    EntityWithArrayOfExpressions.Properties(
        items = items ?: properties.items,
    )
)

@Generated
fun Component<EntityWithArrayOfExpressions>.defer(
    `use named arguments`: Guard = Guard.instance,
    items: ReferenceProperty<List<String>>? = null,
): Component<EntityWithArrayOfExpressions> = Component(
    template = template,
    properties = EntityWithArrayOfExpressions.Properties(
        items = items,
    ).mergeWith(properties)
)

@Generated
operator fun Component<EntityWithArrayOfExpressions>.plus(additive: Properties): Component<EntityWithArrayOfExpressions> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun EntityWithArrayOfExpressions.asList() = listOf(this)
