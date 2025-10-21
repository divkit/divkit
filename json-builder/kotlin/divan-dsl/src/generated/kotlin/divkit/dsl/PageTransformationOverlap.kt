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
 * Pages are stacked during animation overlapping one another.
 * 
 * Can be created using the method [pageTransformationOverlap].
 * 
 * Required parameters: `type`.
 */
@Generated
@ExposedCopyVisibility
data class PageTransformationOverlap internal constructor(
    @JsonIgnore
    val properties: Properties,
) : PageTransformation {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "overlap")
    )

    operator fun plus(additive: Properties): PageTransformationOverlap = PageTransformationOverlap(
        Properties(
            interpolator = additive.interpolator ?: properties.interpolator,
            nextPageAlpha = additive.nextPageAlpha ?: properties.nextPageAlpha,
            nextPageScale = additive.nextPageScale ?: properties.nextPageScale,
            previousPageAlpha = additive.previousPageAlpha ?: properties.previousPageAlpha,
            previousPageScale = additive.previousPageScale ?: properties.previousPageScale,
            reversedStackingOrder = additive.reversedStackingOrder ?: properties.reversedStackingOrder,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Animation speed adjustment. When the value is set to `spring`, it’s a damped oscillation animation truncated to 0.7, with the `damping=1` parameter. Other values correspond to the Bezier curve:<li>`linear` — cubic-bezier(0, 0, 1, 1)</li><li>`ease` — cubic-bezier(0.25, 0.1, 0.25, 1)</li><li>`ease_in` — cubic-bezier(0.42, 0, 1, 1)</li><li>`ease_out` — cubic-bezier(0, 0, 0.58, 1)</li><li>`ease_in_out` — cubic-bezier(0.42, 0, 0.58, 1)</li>
         * Default value: `ease_in_out`.
         */
        val interpolator: Property<AnimationInterpolator>?,
        /**
         * Minimum transparency of the next page, within the range [0, 1], when scrolling through the pager. The following page is always the page with a larger ordinal number in the `items` list, regardless of the scrolling direction.
         * Default value: `1.0`.
         */
        val nextPageAlpha: Property<Double>?,
        /**
         * Scaling the next page during pager scrolling. The following page is always the page with a larger ordinal number in the `items` list, regardless of the scrolling direction.
         * Default value: `1.0`.
         */
        val nextPageScale: Property<Double>?,
        /**
         * Minimum transparency of the previous page, in the range [0, 1], during pager scrolling. The previous page is always the page with a lower ordinal number in the `items` list, regardless of the scrolling direction.
         * Default value: `1.0`.
         */
        val previousPageAlpha: Property<Double>?,
        /**
         * Scaling the previous page during pager scrolling. The previous page is always the page with a lower ordinal number in the `items` list, regardless of the scrolling direction.
         * Default value: `1.0`.
         */
        val previousPageScale: Property<Double>?,
        /**
         * If the value is `false`, the following pages will be stacked on top of the previous ones. If the value is `true`, the reverse happens. The following page is always the page with a larger ordinal number in the `items` list, regardless of the scrolling direction.
         * Default value: `false`.
         */
        val reversedStackingOrder: Property<Boolean>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("interpolator", interpolator)
            result.tryPutProperty("next_page_alpha", nextPageAlpha)
            result.tryPutProperty("next_page_scale", nextPageScale)
            result.tryPutProperty("previous_page_alpha", previousPageAlpha)
            result.tryPutProperty("previous_page_scale", previousPageScale)
            result.tryPutProperty("reversed_stacking_order", reversedStackingOrder)
            return result
        }
    }
}

/**
 * @param interpolator Animation speed adjustment. When the value is set to `spring`, it’s a damped oscillation animation truncated to 0.7, with the `damping=1` parameter. Other values correspond to the Bezier curve:<li>`linear` — cubic-bezier(0, 0, 1, 1)</li><li>`ease` — cubic-bezier(0.25, 0.1, 0.25, 1)</li><li>`ease_in` — cubic-bezier(0.42, 0, 1, 1)</li><li>`ease_out` — cubic-bezier(0, 0, 0.58, 1)</li><li>`ease_in_out` — cubic-bezier(0.42, 0, 0.58, 1)</li>
 * @param nextPageAlpha Minimum transparency of the next page, within the range [0, 1], when scrolling through the pager. The following page is always the page with a larger ordinal number in the `items` list, regardless of the scrolling direction.
 * @param nextPageScale Scaling the next page during pager scrolling. The following page is always the page with a larger ordinal number in the `items` list, regardless of the scrolling direction.
 * @param previousPageAlpha Minimum transparency of the previous page, in the range [0, 1], during pager scrolling. The previous page is always the page with a lower ordinal number in the `items` list, regardless of the scrolling direction.
 * @param previousPageScale Scaling the previous page during pager scrolling. The previous page is always the page with a lower ordinal number in the `items` list, regardless of the scrolling direction.
 * @param reversedStackingOrder If the value is `false`, the following pages will be stacked on top of the previous ones. If the value is `true`, the reverse happens. The following page is always the page with a larger ordinal number in the `items` list, regardless of the scrolling direction.
 */
@Generated
fun DivScope.pageTransformationOverlap(
    `use named arguments`: Guard = Guard.instance,
    interpolator: AnimationInterpolator? = null,
    nextPageAlpha: Double? = null,
    nextPageScale: Double? = null,
    previousPageAlpha: Double? = null,
    previousPageScale: Double? = null,
    reversedStackingOrder: Boolean? = null,
): PageTransformationOverlap = PageTransformationOverlap(
    PageTransformationOverlap.Properties(
        interpolator = valueOrNull(interpolator),
        nextPageAlpha = valueOrNull(nextPageAlpha),
        nextPageScale = valueOrNull(nextPageScale),
        previousPageAlpha = valueOrNull(previousPageAlpha),
        previousPageScale = valueOrNull(previousPageScale),
        reversedStackingOrder = valueOrNull(reversedStackingOrder),
    )
)

/**
 * @param interpolator Animation speed adjustment. When the value is set to `spring`, it’s a damped oscillation animation truncated to 0.7, with the `damping=1` parameter. Other values correspond to the Bezier curve:<li>`linear` — cubic-bezier(0, 0, 1, 1)</li><li>`ease` — cubic-bezier(0.25, 0.1, 0.25, 1)</li><li>`ease_in` — cubic-bezier(0.42, 0, 1, 1)</li><li>`ease_out` — cubic-bezier(0, 0, 0.58, 1)</li><li>`ease_in_out` — cubic-bezier(0.42, 0, 0.58, 1)</li>
 * @param nextPageAlpha Minimum transparency of the next page, within the range [0, 1], when scrolling through the pager. The following page is always the page with a larger ordinal number in the `items` list, regardless of the scrolling direction.
 * @param nextPageScale Scaling the next page during pager scrolling. The following page is always the page with a larger ordinal number in the `items` list, regardless of the scrolling direction.
 * @param previousPageAlpha Minimum transparency of the previous page, in the range [0, 1], during pager scrolling. The previous page is always the page with a lower ordinal number in the `items` list, regardless of the scrolling direction.
 * @param previousPageScale Scaling the previous page during pager scrolling. The previous page is always the page with a lower ordinal number in the `items` list, regardless of the scrolling direction.
 * @param reversedStackingOrder If the value is `false`, the following pages will be stacked on top of the previous ones. If the value is `true`, the reverse happens. The following page is always the page with a larger ordinal number in the `items` list, regardless of the scrolling direction.
 */
@Generated
fun DivScope.pageTransformationOverlapProps(
    `use named arguments`: Guard = Guard.instance,
    interpolator: AnimationInterpolator? = null,
    nextPageAlpha: Double? = null,
    nextPageScale: Double? = null,
    previousPageAlpha: Double? = null,
    previousPageScale: Double? = null,
    reversedStackingOrder: Boolean? = null,
) = PageTransformationOverlap.Properties(
    interpolator = valueOrNull(interpolator),
    nextPageAlpha = valueOrNull(nextPageAlpha),
    nextPageScale = valueOrNull(nextPageScale),
    previousPageAlpha = valueOrNull(previousPageAlpha),
    previousPageScale = valueOrNull(previousPageScale),
    reversedStackingOrder = valueOrNull(reversedStackingOrder),
)

/**
 * @param interpolator Animation speed adjustment. When the value is set to `spring`, it’s a damped oscillation animation truncated to 0.7, with the `damping=1` parameter. Other values correspond to the Bezier curve:<li>`linear` — cubic-bezier(0, 0, 1, 1)</li><li>`ease` — cubic-bezier(0.25, 0.1, 0.25, 1)</li><li>`ease_in` — cubic-bezier(0.42, 0, 1, 1)</li><li>`ease_out` — cubic-bezier(0, 0, 0.58, 1)</li><li>`ease_in_out` — cubic-bezier(0.42, 0, 0.58, 1)</li>
 * @param nextPageAlpha Minimum transparency of the next page, within the range [0, 1], when scrolling through the pager. The following page is always the page with a larger ordinal number in the `items` list, regardless of the scrolling direction.
 * @param nextPageScale Scaling the next page during pager scrolling. The following page is always the page with a larger ordinal number in the `items` list, regardless of the scrolling direction.
 * @param previousPageAlpha Minimum transparency of the previous page, in the range [0, 1], during pager scrolling. The previous page is always the page with a lower ordinal number in the `items` list, regardless of the scrolling direction.
 * @param previousPageScale Scaling the previous page during pager scrolling. The previous page is always the page with a lower ordinal number in the `items` list, regardless of the scrolling direction.
 * @param reversedStackingOrder If the value is `false`, the following pages will be stacked on top of the previous ones. If the value is `true`, the reverse happens. The following page is always the page with a larger ordinal number in the `items` list, regardless of the scrolling direction.
 */
@Generated
fun TemplateScope.pageTransformationOverlapRefs(
    `use named arguments`: Guard = Guard.instance,
    interpolator: ReferenceProperty<AnimationInterpolator>? = null,
    nextPageAlpha: ReferenceProperty<Double>? = null,
    nextPageScale: ReferenceProperty<Double>? = null,
    previousPageAlpha: ReferenceProperty<Double>? = null,
    previousPageScale: ReferenceProperty<Double>? = null,
    reversedStackingOrder: ReferenceProperty<Boolean>? = null,
) = PageTransformationOverlap.Properties(
    interpolator = interpolator,
    nextPageAlpha = nextPageAlpha,
    nextPageScale = nextPageScale,
    previousPageAlpha = previousPageAlpha,
    previousPageScale = previousPageScale,
    reversedStackingOrder = reversedStackingOrder,
)

/**
 * @param interpolator Animation speed adjustment. When the value is set to `spring`, it’s a damped oscillation animation truncated to 0.7, with the `damping=1` parameter. Other values correspond to the Bezier curve:<li>`linear` — cubic-bezier(0, 0, 1, 1)</li><li>`ease` — cubic-bezier(0.25, 0.1, 0.25, 1)</li><li>`ease_in` — cubic-bezier(0.42, 0, 1, 1)</li><li>`ease_out` — cubic-bezier(0, 0, 0.58, 1)</li><li>`ease_in_out` — cubic-bezier(0.42, 0, 0.58, 1)</li>
 * @param nextPageAlpha Minimum transparency of the next page, within the range [0, 1], when scrolling through the pager. The following page is always the page with a larger ordinal number in the `items` list, regardless of the scrolling direction.
 * @param nextPageScale Scaling the next page during pager scrolling. The following page is always the page with a larger ordinal number in the `items` list, regardless of the scrolling direction.
 * @param previousPageAlpha Minimum transparency of the previous page, in the range [0, 1], during pager scrolling. The previous page is always the page with a lower ordinal number in the `items` list, regardless of the scrolling direction.
 * @param previousPageScale Scaling the previous page during pager scrolling. The previous page is always the page with a lower ordinal number in the `items` list, regardless of the scrolling direction.
 * @param reversedStackingOrder If the value is `false`, the following pages will be stacked on top of the previous ones. If the value is `true`, the reverse happens. The following page is always the page with a larger ordinal number in the `items` list, regardless of the scrolling direction.
 */
@Generated
fun PageTransformationOverlap.override(
    `use named arguments`: Guard = Guard.instance,
    interpolator: AnimationInterpolator? = null,
    nextPageAlpha: Double? = null,
    nextPageScale: Double? = null,
    previousPageAlpha: Double? = null,
    previousPageScale: Double? = null,
    reversedStackingOrder: Boolean? = null,
): PageTransformationOverlap = PageTransformationOverlap(
    PageTransformationOverlap.Properties(
        interpolator = valueOrNull(interpolator) ?: properties.interpolator,
        nextPageAlpha = valueOrNull(nextPageAlpha) ?: properties.nextPageAlpha,
        nextPageScale = valueOrNull(nextPageScale) ?: properties.nextPageScale,
        previousPageAlpha = valueOrNull(previousPageAlpha) ?: properties.previousPageAlpha,
        previousPageScale = valueOrNull(previousPageScale) ?: properties.previousPageScale,
        reversedStackingOrder = valueOrNull(reversedStackingOrder) ?: properties.reversedStackingOrder,
    )
)

/**
 * @param interpolator Animation speed adjustment. When the value is set to `spring`, it’s a damped oscillation animation truncated to 0.7, with the `damping=1` parameter. Other values correspond to the Bezier curve:<li>`linear` — cubic-bezier(0, 0, 1, 1)</li><li>`ease` — cubic-bezier(0.25, 0.1, 0.25, 1)</li><li>`ease_in` — cubic-bezier(0.42, 0, 1, 1)</li><li>`ease_out` — cubic-bezier(0, 0, 0.58, 1)</li><li>`ease_in_out` — cubic-bezier(0.42, 0, 0.58, 1)</li>
 * @param nextPageAlpha Minimum transparency of the next page, within the range [0, 1], when scrolling through the pager. The following page is always the page with a larger ordinal number in the `items` list, regardless of the scrolling direction.
 * @param nextPageScale Scaling the next page during pager scrolling. The following page is always the page with a larger ordinal number in the `items` list, regardless of the scrolling direction.
 * @param previousPageAlpha Minimum transparency of the previous page, in the range [0, 1], during pager scrolling. The previous page is always the page with a lower ordinal number in the `items` list, regardless of the scrolling direction.
 * @param previousPageScale Scaling the previous page during pager scrolling. The previous page is always the page with a lower ordinal number in the `items` list, regardless of the scrolling direction.
 * @param reversedStackingOrder If the value is `false`, the following pages will be stacked on top of the previous ones. If the value is `true`, the reverse happens. The following page is always the page with a larger ordinal number in the `items` list, regardless of the scrolling direction.
 */
@Generated
fun PageTransformationOverlap.defer(
    `use named arguments`: Guard = Guard.instance,
    interpolator: ReferenceProperty<AnimationInterpolator>? = null,
    nextPageAlpha: ReferenceProperty<Double>? = null,
    nextPageScale: ReferenceProperty<Double>? = null,
    previousPageAlpha: ReferenceProperty<Double>? = null,
    previousPageScale: ReferenceProperty<Double>? = null,
    reversedStackingOrder: ReferenceProperty<Boolean>? = null,
): PageTransformationOverlap = PageTransformationOverlap(
    PageTransformationOverlap.Properties(
        interpolator = interpolator ?: properties.interpolator,
        nextPageAlpha = nextPageAlpha ?: properties.nextPageAlpha,
        nextPageScale = nextPageScale ?: properties.nextPageScale,
        previousPageAlpha = previousPageAlpha ?: properties.previousPageAlpha,
        previousPageScale = previousPageScale ?: properties.previousPageScale,
        reversedStackingOrder = reversedStackingOrder ?: properties.reversedStackingOrder,
    )
)

/**
 * @param interpolator Animation speed adjustment. When the value is set to `spring`, it’s a damped oscillation animation truncated to 0.7, with the `damping=1` parameter. Other values correspond to the Bezier curve:<li>`linear` — cubic-bezier(0, 0, 1, 1)</li><li>`ease` — cubic-bezier(0.25, 0.1, 0.25, 1)</li><li>`ease_in` — cubic-bezier(0.42, 0, 1, 1)</li><li>`ease_out` — cubic-bezier(0, 0, 0.58, 1)</li><li>`ease_in_out` — cubic-bezier(0.42, 0, 0.58, 1)</li>
 * @param nextPageAlpha Minimum transparency of the next page, within the range [0, 1], when scrolling through the pager. The following page is always the page with a larger ordinal number in the `items` list, regardless of the scrolling direction.
 * @param nextPageScale Scaling the next page during pager scrolling. The following page is always the page with a larger ordinal number in the `items` list, regardless of the scrolling direction.
 * @param previousPageAlpha Minimum transparency of the previous page, in the range [0, 1], during pager scrolling. The previous page is always the page with a lower ordinal number in the `items` list, regardless of the scrolling direction.
 * @param previousPageScale Scaling the previous page during pager scrolling. The previous page is always the page with a lower ordinal number in the `items` list, regardless of the scrolling direction.
 * @param reversedStackingOrder If the value is `false`, the following pages will be stacked on top of the previous ones. If the value is `true`, the reverse happens. The following page is always the page with a larger ordinal number in the `items` list, regardless of the scrolling direction.
 */
@Generated
fun PageTransformationOverlap.modify(
    `use named arguments`: Guard = Guard.instance,
    interpolator: Property<AnimationInterpolator>? = null,
    nextPageAlpha: Property<Double>? = null,
    nextPageScale: Property<Double>? = null,
    previousPageAlpha: Property<Double>? = null,
    previousPageScale: Property<Double>? = null,
    reversedStackingOrder: Property<Boolean>? = null,
): PageTransformationOverlap = PageTransformationOverlap(
    PageTransformationOverlap.Properties(
        interpolator = interpolator ?: properties.interpolator,
        nextPageAlpha = nextPageAlpha ?: properties.nextPageAlpha,
        nextPageScale = nextPageScale ?: properties.nextPageScale,
        previousPageAlpha = previousPageAlpha ?: properties.previousPageAlpha,
        previousPageScale = previousPageScale ?: properties.previousPageScale,
        reversedStackingOrder = reversedStackingOrder ?: properties.reversedStackingOrder,
    )
)

/**
 * @param interpolator Animation speed adjustment. When the value is set to `spring`, it’s a damped oscillation animation truncated to 0.7, with the `damping=1` parameter. Other values correspond to the Bezier curve:<li>`linear` — cubic-bezier(0, 0, 1, 1)</li><li>`ease` — cubic-bezier(0.25, 0.1, 0.25, 1)</li><li>`ease_in` — cubic-bezier(0.42, 0, 1, 1)</li><li>`ease_out` — cubic-bezier(0, 0, 0.58, 1)</li><li>`ease_in_out` — cubic-bezier(0.42, 0, 0.58, 1)</li>
 * @param nextPageAlpha Minimum transparency of the next page, within the range [0, 1], when scrolling through the pager. The following page is always the page with a larger ordinal number in the `items` list, regardless of the scrolling direction.
 * @param nextPageScale Scaling the next page during pager scrolling. The following page is always the page with a larger ordinal number in the `items` list, regardless of the scrolling direction.
 * @param previousPageAlpha Minimum transparency of the previous page, in the range [0, 1], during pager scrolling. The previous page is always the page with a lower ordinal number in the `items` list, regardless of the scrolling direction.
 * @param previousPageScale Scaling the previous page during pager scrolling. The previous page is always the page with a lower ordinal number in the `items` list, regardless of the scrolling direction.
 * @param reversedStackingOrder If the value is `false`, the following pages will be stacked on top of the previous ones. If the value is `true`, the reverse happens. The following page is always the page with a larger ordinal number in the `items` list, regardless of the scrolling direction.
 */
@Generated
fun PageTransformationOverlap.evaluate(
    `use named arguments`: Guard = Guard.instance,
    interpolator: ExpressionProperty<AnimationInterpolator>? = null,
    nextPageAlpha: ExpressionProperty<Double>? = null,
    nextPageScale: ExpressionProperty<Double>? = null,
    previousPageAlpha: ExpressionProperty<Double>? = null,
    previousPageScale: ExpressionProperty<Double>? = null,
    reversedStackingOrder: ExpressionProperty<Boolean>? = null,
): PageTransformationOverlap = PageTransformationOverlap(
    PageTransformationOverlap.Properties(
        interpolator = interpolator ?: properties.interpolator,
        nextPageAlpha = nextPageAlpha ?: properties.nextPageAlpha,
        nextPageScale = nextPageScale ?: properties.nextPageScale,
        previousPageAlpha = previousPageAlpha ?: properties.previousPageAlpha,
        previousPageScale = previousPageScale ?: properties.previousPageScale,
        reversedStackingOrder = reversedStackingOrder ?: properties.reversedStackingOrder,
    )
)

@Generated
fun PageTransformationOverlap.asList() = listOf(this)
