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
 * Can be created using the method [withArrayOfEnums].
 * 
 * Required parameters: `type, items`.
 */
@Generated
class WithArrayOfEnums internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Entity {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "entity_with_array_of_enums")
    )

    operator fun plus(additive: Properties): WithArrayOfEnums = WithArrayOfEnums(
        Properties(
            items = additive.items ?: properties.items,
        )
    )

    class Properties internal constructor(
        val items: Property<List<Item>>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("items", items)
            return result
        }
    }

    /**
     * Possible values: [first, second].
     */
    @Generated
    sealed interface Item
}

@Generated
fun DivScope.withArrayOfEnums(
    `use named arguments`: Guard = Guard.instance,
    items: List<WithArrayOfEnums.Item>? = null,
): WithArrayOfEnums = WithArrayOfEnums(
    WithArrayOfEnums.Properties(
        items = valueOrNull(items),
    )
)

@Generated
fun DivScope.withArrayOfEnumsProps(
    `use named arguments`: Guard = Guard.instance,
    items: List<WithArrayOfEnums.Item>? = null,
) = WithArrayOfEnums.Properties(
    items = valueOrNull(items),
)

@Generated
fun TemplateScope.withArrayOfEnumsRefs(
    `use named arguments`: Guard = Guard.instance,
    items: ReferenceProperty<List<WithArrayOfEnums.Item>>? = null,
) = WithArrayOfEnums.Properties(
    items = items,
)

@Generated
fun WithArrayOfEnums.override(
    `use named arguments`: Guard = Guard.instance,
    items: List<WithArrayOfEnums.Item>? = null,
): WithArrayOfEnums = WithArrayOfEnums(
    WithArrayOfEnums.Properties(
        items = valueOrNull(items) ?: properties.items,
    )
)

@Generated
fun WithArrayOfEnums.defer(
    `use named arguments`: Guard = Guard.instance,
    items: ReferenceProperty<List<WithArrayOfEnums.Item>>? = null,
): WithArrayOfEnums = WithArrayOfEnums(
    WithArrayOfEnums.Properties(
        items = items ?: properties.items,
    )
)

@Generated
fun Component<WithArrayOfEnums>.override(
    `use named arguments`: Guard = Guard.instance,
    items: List<WithArrayOfEnums.Item>? = null,
): Component<WithArrayOfEnums> = Component(
    template = template,
    properties = WithArrayOfEnums.Properties(
        items = valueOrNull(items),
    ).mergeWith(properties)
)

@Generated
fun Component<WithArrayOfEnums>.defer(
    `use named arguments`: Guard = Guard.instance,
    items: ReferenceProperty<List<WithArrayOfEnums.Item>>? = null,
): Component<WithArrayOfEnums> = Component(
    template = template,
    properties = WithArrayOfEnums.Properties(
        items = items,
    ).mergeWith(properties)
)

@Generated
operator fun Component<WithArrayOfEnums>.plus(additive: WithArrayOfEnums.Properties): Component<WithArrayOfEnums> = Component(
    template = template,
    properties = additive.mergeWith(properties)
)

@Generated
fun WithArrayOfEnums.asList() = listOf(this)

@Generated
fun WithArrayOfEnums.Item.asList() = listOf(this)
