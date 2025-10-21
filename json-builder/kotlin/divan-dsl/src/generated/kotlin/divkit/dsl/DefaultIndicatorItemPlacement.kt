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
 * Can be created using the method [defaultIndicatorItemPlacement].
 * 
 * Required parameters: `type`.
 */
@Generated
@ExposedCopyVisibility
data class DefaultIndicatorItemPlacement internal constructor(
    @JsonIgnore
    val properties: Properties,
) : IndicatorItemPlacement {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "default")
    )

    operator fun plus(additive: Properties): DefaultIndicatorItemPlacement = DefaultIndicatorItemPlacement(
        Properties(
            spaceBetweenCenters = additive.spaceBetweenCenters ?: properties.spaceBetweenCenters,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Spacing between indicator centers.
         * Default value: `{"type": "fixed","value":15}`.
         */
        val spaceBetweenCenters: Property<FixedSize>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("space_between_centers", spaceBetweenCenters)
            return result
        }
    }
}

/**
 * @param spaceBetweenCenters Spacing between indicator centers.
 */
@Generated
fun DivScope.defaultIndicatorItemPlacement(
    `use named arguments`: Guard = Guard.instance,
    spaceBetweenCenters: FixedSize? = null,
): DefaultIndicatorItemPlacement = DefaultIndicatorItemPlacement(
    DefaultIndicatorItemPlacement.Properties(
        spaceBetweenCenters = valueOrNull(spaceBetweenCenters),
    )
)

/**
 * @param spaceBetweenCenters Spacing between indicator centers.
 */
@Generated
fun DivScope.defaultIndicatorItemPlacementProps(
    `use named arguments`: Guard = Guard.instance,
    spaceBetweenCenters: FixedSize? = null,
) = DefaultIndicatorItemPlacement.Properties(
    spaceBetweenCenters = valueOrNull(spaceBetweenCenters),
)

/**
 * @param spaceBetweenCenters Spacing between indicator centers.
 */
@Generated
fun TemplateScope.defaultIndicatorItemPlacementRefs(
    `use named arguments`: Guard = Guard.instance,
    spaceBetweenCenters: ReferenceProperty<FixedSize>? = null,
) = DefaultIndicatorItemPlacement.Properties(
    spaceBetweenCenters = spaceBetweenCenters,
)

/**
 * @param spaceBetweenCenters Spacing between indicator centers.
 */
@Generated
fun DefaultIndicatorItemPlacement.override(
    `use named arguments`: Guard = Guard.instance,
    spaceBetweenCenters: FixedSize? = null,
): DefaultIndicatorItemPlacement = DefaultIndicatorItemPlacement(
    DefaultIndicatorItemPlacement.Properties(
        spaceBetweenCenters = valueOrNull(spaceBetweenCenters) ?: properties.spaceBetweenCenters,
    )
)

/**
 * @param spaceBetweenCenters Spacing between indicator centers.
 */
@Generated
fun DefaultIndicatorItemPlacement.defer(
    `use named arguments`: Guard = Guard.instance,
    spaceBetweenCenters: ReferenceProperty<FixedSize>? = null,
): DefaultIndicatorItemPlacement = DefaultIndicatorItemPlacement(
    DefaultIndicatorItemPlacement.Properties(
        spaceBetweenCenters = spaceBetweenCenters ?: properties.spaceBetweenCenters,
    )
)

/**
 * @param spaceBetweenCenters Spacing between indicator centers.
 */
@Generated
fun DefaultIndicatorItemPlacement.modify(
    `use named arguments`: Guard = Guard.instance,
    spaceBetweenCenters: Property<FixedSize>? = null,
): DefaultIndicatorItemPlacement = DefaultIndicatorItemPlacement(
    DefaultIndicatorItemPlacement.Properties(
        spaceBetweenCenters = spaceBetweenCenters ?: properties.spaceBetweenCenters,
    )
)

@Generated
fun DefaultIndicatorItemPlacement.asList() = listOf(this)
