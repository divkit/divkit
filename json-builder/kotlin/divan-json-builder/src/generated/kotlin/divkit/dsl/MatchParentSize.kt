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
 * Can be created using the method [matchParentSize].
 * 
 * Required parameters: `type`.
 */
@Generated
@ExposedCopyVisibility
data class MatchParentSize internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Size {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "match_parent")
    )

    operator fun plus(additive: Properties): MatchParentSize = MatchParentSize(
        Properties(
            maxSize = additive.maxSize ?: properties.maxSize,
            minSize = additive.minSize ?: properties.minSize,
            weight = additive.weight ?: properties.weight,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Maximum size of an element.
         */
        val maxSize: Property<SizeUnitValue>?,
        /**
         * Minimum size of an element.
         */
        val minSize: Property<SizeUnitValue>?,
        /**
         * Weight when distributing free space between elements with the size type `match_parent` inside an element. If the weight isn't specified, the elements will divide the place equally.
         */
        val weight: Property<Double>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("max_size", maxSize)
            result.tryPutProperty("min_size", minSize)
            result.tryPutProperty("weight", weight)
            return result
        }
    }
}

/**
 * @param maxSize Maximum size of an element.
 * @param minSize Minimum size of an element.
 * @param weight Weight when distributing free space between elements with the size type `match_parent` inside an element. If the weight isn't specified, the elements will divide the place equally.
 */
@Generated
fun DivScope.matchParentSize(
    `use named arguments`: Guard = Guard.instance,
    maxSize: SizeUnitValue? = null,
    minSize: SizeUnitValue? = null,
    weight: Double? = null,
): MatchParentSize = MatchParentSize(
    MatchParentSize.Properties(
        maxSize = valueOrNull(maxSize),
        minSize = valueOrNull(minSize),
        weight = valueOrNull(weight),
    )
)

/**
 * @param maxSize Maximum size of an element.
 * @param minSize Minimum size of an element.
 * @param weight Weight when distributing free space between elements with the size type `match_parent` inside an element. If the weight isn't specified, the elements will divide the place equally.
 */
@Generated
fun DivScope.matchParentSizeProps(
    `use named arguments`: Guard = Guard.instance,
    maxSize: SizeUnitValue? = null,
    minSize: SizeUnitValue? = null,
    weight: Double? = null,
) = MatchParentSize.Properties(
    maxSize = valueOrNull(maxSize),
    minSize = valueOrNull(minSize),
    weight = valueOrNull(weight),
)

/**
 * @param maxSize Maximum size of an element.
 * @param minSize Minimum size of an element.
 * @param weight Weight when distributing free space between elements with the size type `match_parent` inside an element. If the weight isn't specified, the elements will divide the place equally.
 */
@Generated
fun TemplateScope.matchParentSizeRefs(
    `use named arguments`: Guard = Guard.instance,
    maxSize: ReferenceProperty<SizeUnitValue>? = null,
    minSize: ReferenceProperty<SizeUnitValue>? = null,
    weight: ReferenceProperty<Double>? = null,
) = MatchParentSize.Properties(
    maxSize = maxSize,
    minSize = minSize,
    weight = weight,
)

/**
 * @param maxSize Maximum size of an element.
 * @param minSize Minimum size of an element.
 * @param weight Weight when distributing free space between elements with the size type `match_parent` inside an element. If the weight isn't specified, the elements will divide the place equally.
 */
@Generated
fun MatchParentSize.override(
    `use named arguments`: Guard = Guard.instance,
    maxSize: SizeUnitValue? = null,
    minSize: SizeUnitValue? = null,
    weight: Double? = null,
): MatchParentSize = MatchParentSize(
    MatchParentSize.Properties(
        maxSize = valueOrNull(maxSize) ?: properties.maxSize,
        minSize = valueOrNull(minSize) ?: properties.minSize,
        weight = valueOrNull(weight) ?: properties.weight,
    )
)

/**
 * @param maxSize Maximum size of an element.
 * @param minSize Minimum size of an element.
 * @param weight Weight when distributing free space between elements with the size type `match_parent` inside an element. If the weight isn't specified, the elements will divide the place equally.
 */
@Generated
fun MatchParentSize.defer(
    `use named arguments`: Guard = Guard.instance,
    maxSize: ReferenceProperty<SizeUnitValue>? = null,
    minSize: ReferenceProperty<SizeUnitValue>? = null,
    weight: ReferenceProperty<Double>? = null,
): MatchParentSize = MatchParentSize(
    MatchParentSize.Properties(
        maxSize = maxSize ?: properties.maxSize,
        minSize = minSize ?: properties.minSize,
        weight = weight ?: properties.weight,
    )
)

/**
 * @param maxSize Maximum size of an element.
 * @param minSize Minimum size of an element.
 * @param weight Weight when distributing free space between elements with the size type `match_parent` inside an element. If the weight isn't specified, the elements will divide the place equally.
 */
@Generated
fun MatchParentSize.modify(
    `use named arguments`: Guard = Guard.instance,
    maxSize: Property<SizeUnitValue>? = null,
    minSize: Property<SizeUnitValue>? = null,
    weight: Property<Double>? = null,
): MatchParentSize = MatchParentSize(
    MatchParentSize.Properties(
        maxSize = maxSize ?: properties.maxSize,
        minSize = minSize ?: properties.minSize,
        weight = weight ?: properties.weight,
    )
)

/**
 * @param weight Weight when distributing free space between elements with the size type `match_parent` inside an element. If the weight isn't specified, the elements will divide the place equally.
 */
@Generated
fun MatchParentSize.evaluate(
    `use named arguments`: Guard = Guard.instance,
    weight: ExpressionProperty<Double>? = null,
): MatchParentSize = MatchParentSize(
    MatchParentSize.Properties(
        maxSize = properties.maxSize,
        minSize = properties.minSize,
        weight = weight ?: properties.weight,
    )
)

@Generated
fun MatchParentSize.asList() = listOf(this)
