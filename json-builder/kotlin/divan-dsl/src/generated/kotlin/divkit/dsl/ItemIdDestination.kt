@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divkit.dsl

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import divkit.dsl.annotation.*
import divkit.dsl.core.*
import divkit.dsl.scope.*
import divkit.dsl.serialization.*
import kotlin.Any
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map

/**
 * Specifies the element with the given `id` as the scrolling end position.
 * 
 * Can be created using the method [itemIdDestination].
 * 
 * Required parameters: `value, type`.
 */
@Generated
@JsonSerialize(using = ItemIdDestinationSerializer::class)
@ExposedCopyVisibility
data class ItemIdDestination internal constructor(
    val properties: Properties,
) : ActionScrollDestination {
    operator fun plus(additive: Properties): ItemIdDestination = ItemIdDestination(
        Properties(
            value = additive.value ?: properties.value,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Identifier of the container child element to scroll to.
         */
        val value: Property<String>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("value", value)
            return result
        }
    }
}

/**
 * @param value Identifier of the container child element to scroll to.
 */
@Generated
fun DivScope.itemIdDestination(
    `use named arguments`: Guard = Guard.instance,
    value: String? = null,
): ItemIdDestination = ItemIdDestination(
    ItemIdDestination.Properties(
        value = valueOrNull(value),
    )
)

/**
 * @param value Identifier of the container child element to scroll to.
 */
@Generated
fun DivScope.itemIdDestinationProps(
    `use named arguments`: Guard = Guard.instance,
    value: String? = null,
) = ItemIdDestination.Properties(
    value = valueOrNull(value),
)

/**
 * @param value Identifier of the container child element to scroll to.
 */
@Generated
fun TemplateScope.itemIdDestinationRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<String>? = null,
) = ItemIdDestination.Properties(
    value = value,
)

/**
 * @param value Identifier of the container child element to scroll to.
 */
@Generated
fun ItemIdDestination.override(
    `use named arguments`: Guard = Guard.instance,
    value: String? = null,
): ItemIdDestination = ItemIdDestination(
    ItemIdDestination.Properties(
        value = valueOrNull(value) ?: properties.value,
    )
)

/**
 * @param value Identifier of the container child element to scroll to.
 */
@Generated
fun ItemIdDestination.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<String>? = null,
): ItemIdDestination = ItemIdDestination(
    ItemIdDestination.Properties(
        value = value ?: properties.value,
    )
)

/**
 * @param value Identifier of the container child element to scroll to.
 */
@Generated
fun ItemIdDestination.modify(
    `use named arguments`: Guard = Guard.instance,
    value: Property<String>? = null,
): ItemIdDestination = ItemIdDestination(
    ItemIdDestination.Properties(
        value = value ?: properties.value,
    )
)

/**
 * @param value Identifier of the container child element to scroll to.
 */
@Generated
fun ItemIdDestination.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<String>? = null,
): ItemIdDestination = ItemIdDestination(
    ItemIdDestination.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun ItemIdDestination.asList() = listOf(this)
