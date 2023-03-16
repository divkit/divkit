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
 * Can be created using the method [withArrayOfExpressions].
 * 
 * Required parameters: `type, items`.
 */
@Generated
class WithArrayOfExpressions internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_array_of_expressions")
    )

    operator fun plus(additive: Properties): WithArrayOfExpressions = WithArrayOfExpressions(
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
fun DivScope.withArrayOfExpressions(
    `use named arguments`: Guard = Guard.instance,
    items: List<String>? = null,
): WithArrayOfExpressions = WithArrayOfExpressions(
    WithArrayOfExpressions.Properties(
        items = valueOrNull(items),
    )
)

@Generated
fun DivScope.withArrayOfExpressionsProps(
    `use named arguments`: Guard = Guard.instance,
    items: List<String>? = null,
) = WithArrayOfExpressions.Properties(
    items = valueOrNull(items),
)

@Generated
fun TemplateScope.withArrayOfExpressionsRefs(
    `use named arguments`: Guard = Guard.instance,
    items: ReferenceProperty<List<String>>? = null,
) = WithArrayOfExpressions.Properties(
    items = items,
)

@Generated
fun WithArrayOfExpressions.override(
    `use named arguments`: Guard = Guard.instance,
    items: List<String>? = null,
): WithArrayOfExpressions = WithArrayOfExpressions(
    WithArrayOfExpressions.Properties(
        items = valueOrNull(items) ?: properties.items,
    )
)

@Generated
fun WithArrayOfExpressions.defer(
    `use named arguments`: Guard = Guard.instance,
    items: ReferenceProperty<List<String>>? = null,
): WithArrayOfExpressions = WithArrayOfExpressions(
    WithArrayOfExpressions.Properties(
        items = items ?: properties.items,
    )
)

@Generated
fun Component<WithArrayOfExpressions>.override(
    `use named arguments`: Guard = Guard.instance,
    items: List<String>? = null,
): Component<WithArrayOfExpressions> = Component(
    template = template,
    properties = WithArrayOfExpressions.Properties(
        items = valueOrNull(items),
    ).mergeWith(properties)
)

@Generated
fun Component<WithArrayOfExpressions>.defer(
    `use named arguments`: Guard = Guard.instance,
    items: ReferenceProperty<List<String>>? = null,
): Component<WithArrayOfExpressions> = Component(
    template = template,
    properties = WithArrayOfExpressions.Properties(
        items = items,
    ).mergeWith(properties)
)

@Generated
operator fun Component<WithArrayOfExpressions>.plus(additive: WithArrayOfExpressions.Properties): Component<WithArrayOfExpressions> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun WithArrayOfExpressions.asList() = listOf(this)
