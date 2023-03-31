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
 * Fixed width value of the visible part of a neighbouring page.
 * 
 * Can be created using the method [neighbourPageSize].
 * 
 * Required properties: `type, neighbour_page_width`.
 */
@Generated
class NeighbourPageSize internal constructor(
    @JsonIgnore
    val properties: Properties,
) : PagerLayoutMode {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "fixed")
    )

    operator fun plus(additive: Properties): NeighbourPageSize = NeighbourPageSize(
        Properties(
            neighbourPageWidth = additive.neighbourPageWidth ?: properties.neighbourPageWidth,
        )
    )

    class Properties internal constructor(
        /**
         * Width of the visible part of a neighbouring page.
         */
        val neighbourPageWidth: Property<FixedSize>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("neighbour_page_width", neighbourPageWidth)
            return result
        }
    }
}

/**
 * @param neighbourPageWidth Width of the visible part of a neighbouring page.
 */
@Generated
fun DivScope.neighbourPageSize(
    `use named arguments`: Guard = Guard.instance,
    neighbourPageWidth: FixedSize? = null,
): NeighbourPageSize = NeighbourPageSize(
    NeighbourPageSize.Properties(
        neighbourPageWidth = valueOrNull(neighbourPageWidth),
    )
)

/**
 * @param neighbourPageWidth Width of the visible part of a neighbouring page.
 */
@Generated
fun DivScope.neighbourPageSizeProps(
    `use named arguments`: Guard = Guard.instance,
    neighbourPageWidth: FixedSize? = null,
) = NeighbourPageSize.Properties(
    neighbourPageWidth = valueOrNull(neighbourPageWidth),
)

/**
 * @param neighbourPageWidth Width of the visible part of a neighbouring page.
 */
@Generated
fun TemplateScope.neighbourPageSizeRefs(
    `use named arguments`: Guard = Guard.instance,
    neighbourPageWidth: ReferenceProperty<FixedSize>? = null,
) = NeighbourPageSize.Properties(
    neighbourPageWidth = neighbourPageWidth,
)

/**
 * @param neighbourPageWidth Width of the visible part of a neighbouring page.
 */
@Generated
fun NeighbourPageSize.override(
    `use named arguments`: Guard = Guard.instance,
    neighbourPageWidth: FixedSize? = null,
): NeighbourPageSize = NeighbourPageSize(
    NeighbourPageSize.Properties(
        neighbourPageWidth = valueOrNull(neighbourPageWidth) ?: properties.neighbourPageWidth,
    )
)

/**
 * @param neighbourPageWidth Width of the visible part of a neighbouring page.
 */
@Generated
fun NeighbourPageSize.defer(
    `use named arguments`: Guard = Guard.instance,
    neighbourPageWidth: ReferenceProperty<FixedSize>? = null,
): NeighbourPageSize = NeighbourPageSize(
    NeighbourPageSize.Properties(
        neighbourPageWidth = neighbourPageWidth ?: properties.neighbourPageWidth,
    )
)

@Generated
fun NeighbourPageSize.asList() = listOf(this)
