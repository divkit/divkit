@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divkit.dsl

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue
import divkit.dsl.annotation.*
import divkit.dsl.core.*
import divkit.dsl.scope.*
import kotlin.Any
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map

/**
 * Element size adjusts to a parent element.
 * 
 * Can be created using the method [stretchIndicatorItemPlacement].
 * 
 * Required parameters: `type`.
 */
@Generated
data class StretchIndicatorItemPlacement internal constructor(
    @JsonIgnore
    val properties: Properties,
) : IndicatorItemPlacement {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "stretch")
    )

    operator fun plus(additive: Properties): StretchIndicatorItemPlacement = StretchIndicatorItemPlacement(
        Properties(
            itemSpacing = additive.itemSpacing ?: properties.itemSpacing,
            maxVisibleItems = additive.maxVisibleItems ?: properties.maxVisibleItems,
        )
    )

    data class Properties internal constructor(
        /**
         * Spacing between indicator centers.
         * Default value: `{"type": "fixed","value":5}`.
         */
        val itemSpacing: Property<FixedSize>?,
        /**
         * Maximum number of visible indicators.
         * Default value: `10`.
         */
        val maxVisibleItems: Property<Int>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("item_spacing", itemSpacing)
            result.tryPutProperty("max_visible_items", maxVisibleItems)
            return result
        }
    }
}

/**
 * @param itemSpacing Spacing between indicator centers.
 * @param maxVisibleItems Maximum number of visible indicators.
 */
@Generated
fun DivScope.stretchIndicatorItemPlacement(
    `use named arguments`: Guard = Guard.instance,
    itemSpacing: FixedSize? = null,
    maxVisibleItems: Int? = null,
): StretchIndicatorItemPlacement = StretchIndicatorItemPlacement(
    StretchIndicatorItemPlacement.Properties(
        itemSpacing = valueOrNull(itemSpacing),
        maxVisibleItems = valueOrNull(maxVisibleItems),
    )
)

/**
 * @param itemSpacing Spacing between indicator centers.
 * @param maxVisibleItems Maximum number of visible indicators.
 */
@Generated
fun DivScope.stretchIndicatorItemPlacementProps(
    `use named arguments`: Guard = Guard.instance,
    itemSpacing: FixedSize? = null,
    maxVisibleItems: Int? = null,
) = StretchIndicatorItemPlacement.Properties(
    itemSpacing = valueOrNull(itemSpacing),
    maxVisibleItems = valueOrNull(maxVisibleItems),
)

/**
 * @param itemSpacing Spacing between indicator centers.
 * @param maxVisibleItems Maximum number of visible indicators.
 */
@Generated
fun TemplateScope.stretchIndicatorItemPlacementRefs(
    `use named arguments`: Guard = Guard.instance,
    itemSpacing: ReferenceProperty<FixedSize>? = null,
    maxVisibleItems: ReferenceProperty<Int>? = null,
) = StretchIndicatorItemPlacement.Properties(
    itemSpacing = itemSpacing,
    maxVisibleItems = maxVisibleItems,
)

/**
 * @param itemSpacing Spacing between indicator centers.
 * @param maxVisibleItems Maximum number of visible indicators.
 */
@Generated
fun StretchIndicatorItemPlacement.override(
    `use named arguments`: Guard = Guard.instance,
    itemSpacing: FixedSize? = null,
    maxVisibleItems: Int? = null,
): StretchIndicatorItemPlacement = StretchIndicatorItemPlacement(
    StretchIndicatorItemPlacement.Properties(
        itemSpacing = valueOrNull(itemSpacing) ?: properties.itemSpacing,
        maxVisibleItems = valueOrNull(maxVisibleItems) ?: properties.maxVisibleItems,
    )
)

/**
 * @param itemSpacing Spacing between indicator centers.
 * @param maxVisibleItems Maximum number of visible indicators.
 */
@Generated
fun StretchIndicatorItemPlacement.defer(
    `use named arguments`: Guard = Guard.instance,
    itemSpacing: ReferenceProperty<FixedSize>? = null,
    maxVisibleItems: ReferenceProperty<Int>? = null,
): StretchIndicatorItemPlacement = StretchIndicatorItemPlacement(
    StretchIndicatorItemPlacement.Properties(
        itemSpacing = itemSpacing ?: properties.itemSpacing,
        maxVisibleItems = maxVisibleItems ?: properties.maxVisibleItems,
    )
)

/**
 * @param itemSpacing Spacing between indicator centers.
 * @param maxVisibleItems Maximum number of visible indicators.
 */
@Generated
fun StretchIndicatorItemPlacement.modify(
    `use named arguments`: Guard = Guard.instance,
    itemSpacing: Property<FixedSize>? = null,
    maxVisibleItems: Property<Int>? = null,
): StretchIndicatorItemPlacement = StretchIndicatorItemPlacement(
    StretchIndicatorItemPlacement.Properties(
        itemSpacing = itemSpacing ?: properties.itemSpacing,
        maxVisibleItems = maxVisibleItems ?: properties.maxVisibleItems,
    )
)

/**
 * @param maxVisibleItems Maximum number of visible indicators.
 */
@Generated
fun StretchIndicatorItemPlacement.evaluate(
    `use named arguments`: Guard = Guard.instance,
    maxVisibleItems: ExpressionProperty<Int>? = null,
): StretchIndicatorItemPlacement = StretchIndicatorItemPlacement(
    StretchIndicatorItemPlacement.Properties(
        itemSpacing = properties.itemSpacing,
        maxVisibleItems = maxVisibleItems ?: properties.maxVisibleItems,
    )
)

@Generated
fun StretchIndicatorItemPlacement.asList() = listOf(this)
