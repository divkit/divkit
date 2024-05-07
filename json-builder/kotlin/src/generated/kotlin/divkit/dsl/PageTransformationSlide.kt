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
 * The pages are moving when the pager is flipping without overlaping each other.
 * 
 * Can be created using the method [pageTransformationSlide].
 * 
 * Required parameters: `type`.
 */
@Generated
class PageTransformationSlide internal constructor(
    @JsonIgnore
    val properties: Properties,
) : PageTransformation {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "slide")
    )

    operator fun plus(additive: Properties): PageTransformationSlide = PageTransformationSlide(
        Properties(
            interpolator = additive.interpolator ?: properties.interpolator,
            nextPageAlpha = additive.nextPageAlpha ?: properties.nextPageAlpha,
            nextPageScale = additive.nextPageScale ?: properties.nextPageScale,
            previousPageAlpha = additive.previousPageAlpha ?: properties.previousPageAlpha,
            previousPageScale = additive.previousPageScale ?: properties.previousPageScale,
        )
    )

    class Properties internal constructor(
        /**
         * Tranformation speed nature. When the value is set to `spring` — animation of damping fluctuations cut to 0.7 with the `damping=1` parameter. Other options correspond to the Bezier curve:<li>`linear` — cubic-bezier(0, 0, 1, 1);</li><li>`ease` — cubic-bezier(0.25, 0.1, 0.25, 1);</li><li>`ease_in` — cubic-bezier(0.42, 0, 1, 1);</li><li>`ease_out` — cubic-bezier(0, 0, 0.58, 1);</li><li>`ease_in_out` — cubic-bezier(0.42, 0, 0.58, 1).</li>
         * Default value: `ease_in_out`.
         */
        val interpolator: Property<AnimationInterpolator>?,
        /**
         * Minimum alpha of the next page during pager scrolling in bounds [0, 1]. The next page is always a page with a large sequential number in the list of `items`, regardless of the direction of scrolling.
         * Default value: `1.0`.
         */
        val nextPageAlpha: Property<Double>?,
        /**
         * Scale of the next page during pager scrolling. The next page is always a page with a large sequential number in the list of `items`, regardless of the direction of scrolling.
         * Default value: `1.0`.
         */
        val nextPageScale: Property<Double>?,
        /**
         * Minimum alpha of the previous page during pager scrolling in bounds [0, 1]. The previous page is always a page with a lower sequential number in the list of `items`, regardless of the direction of scrolling.
         * Default value: `1.0`.
         */
        val previousPageAlpha: Property<Double>?,
        /**
         * Scale of the previous page during pager scrolling. The previous page is always a page with a lower sequential number in the list of `items`, regardless of the direction of scrolling.
         * Default value: `1.0`.
         */
        val previousPageScale: Property<Double>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("interpolator", interpolator)
            result.tryPutProperty("next_page_alpha", nextPageAlpha)
            result.tryPutProperty("next_page_scale", nextPageScale)
            result.tryPutProperty("previous_page_alpha", previousPageAlpha)
            result.tryPutProperty("previous_page_scale", previousPageScale)
            return result
        }
    }
}

/**
 * @param interpolator Tranformation speed nature. When the value is set to `spring` — animation of damping fluctuations cut to 0.7 with the `damping=1` parameter. Other options correspond to the Bezier curve:<li>`linear` — cubic-bezier(0, 0, 1, 1);</li><li>`ease` — cubic-bezier(0.25, 0.1, 0.25, 1);</li><li>`ease_in` — cubic-bezier(0.42, 0, 1, 1);</li><li>`ease_out` — cubic-bezier(0, 0, 0.58, 1);</li><li>`ease_in_out` — cubic-bezier(0.42, 0, 0.58, 1).</li>
 * @param nextPageAlpha Minimum alpha of the next page during pager scrolling in bounds [0, 1]. The next page is always a page with a large sequential number in the list of `items`, regardless of the direction of scrolling.
 * @param nextPageScale Scale of the next page during pager scrolling. The next page is always a page with a large sequential number in the list of `items`, regardless of the direction of scrolling.
 * @param previousPageAlpha Minimum alpha of the previous page during pager scrolling in bounds [0, 1]. The previous page is always a page with a lower sequential number in the list of `items`, regardless of the direction of scrolling.
 * @param previousPageScale Scale of the previous page during pager scrolling. The previous page is always a page with a lower sequential number in the list of `items`, regardless of the direction of scrolling.
 */
@Generated
fun DivScope.pageTransformationSlide(
    `use named arguments`: Guard = Guard.instance,
    interpolator: AnimationInterpolator? = null,
    nextPageAlpha: Double? = null,
    nextPageScale: Double? = null,
    previousPageAlpha: Double? = null,
    previousPageScale: Double? = null,
): PageTransformationSlide = PageTransformationSlide(
    PageTransformationSlide.Properties(
        interpolator = valueOrNull(interpolator),
        nextPageAlpha = valueOrNull(nextPageAlpha),
        nextPageScale = valueOrNull(nextPageScale),
        previousPageAlpha = valueOrNull(previousPageAlpha),
        previousPageScale = valueOrNull(previousPageScale),
    )
)

/**
 * @param interpolator Tranformation speed nature. When the value is set to `spring` — animation of damping fluctuations cut to 0.7 with the `damping=1` parameter. Other options correspond to the Bezier curve:<li>`linear` — cubic-bezier(0, 0, 1, 1);</li><li>`ease` — cubic-bezier(0.25, 0.1, 0.25, 1);</li><li>`ease_in` — cubic-bezier(0.42, 0, 1, 1);</li><li>`ease_out` — cubic-bezier(0, 0, 0.58, 1);</li><li>`ease_in_out` — cubic-bezier(0.42, 0, 0.58, 1).</li>
 * @param nextPageAlpha Minimum alpha of the next page during pager scrolling in bounds [0, 1]. The next page is always a page with a large sequential number in the list of `items`, regardless of the direction of scrolling.
 * @param nextPageScale Scale of the next page during pager scrolling. The next page is always a page with a large sequential number in the list of `items`, regardless of the direction of scrolling.
 * @param previousPageAlpha Minimum alpha of the previous page during pager scrolling in bounds [0, 1]. The previous page is always a page with a lower sequential number in the list of `items`, regardless of the direction of scrolling.
 * @param previousPageScale Scale of the previous page during pager scrolling. The previous page is always a page with a lower sequential number in the list of `items`, regardless of the direction of scrolling.
 */
@Generated
fun DivScope.pageTransformationSlideProps(
    `use named arguments`: Guard = Guard.instance,
    interpolator: AnimationInterpolator? = null,
    nextPageAlpha: Double? = null,
    nextPageScale: Double? = null,
    previousPageAlpha: Double? = null,
    previousPageScale: Double? = null,
) = PageTransformationSlide.Properties(
    interpolator = valueOrNull(interpolator),
    nextPageAlpha = valueOrNull(nextPageAlpha),
    nextPageScale = valueOrNull(nextPageScale),
    previousPageAlpha = valueOrNull(previousPageAlpha),
    previousPageScale = valueOrNull(previousPageScale),
)

/**
 * @param interpolator Tranformation speed nature. When the value is set to `spring` — animation of damping fluctuations cut to 0.7 with the `damping=1` parameter. Other options correspond to the Bezier curve:<li>`linear` — cubic-bezier(0, 0, 1, 1);</li><li>`ease` — cubic-bezier(0.25, 0.1, 0.25, 1);</li><li>`ease_in` — cubic-bezier(0.42, 0, 1, 1);</li><li>`ease_out` — cubic-bezier(0, 0, 0.58, 1);</li><li>`ease_in_out` — cubic-bezier(0.42, 0, 0.58, 1).</li>
 * @param nextPageAlpha Minimum alpha of the next page during pager scrolling in bounds [0, 1]. The next page is always a page with a large sequential number in the list of `items`, regardless of the direction of scrolling.
 * @param nextPageScale Scale of the next page during pager scrolling. The next page is always a page with a large sequential number in the list of `items`, regardless of the direction of scrolling.
 * @param previousPageAlpha Minimum alpha of the previous page during pager scrolling in bounds [0, 1]. The previous page is always a page with a lower sequential number in the list of `items`, regardless of the direction of scrolling.
 * @param previousPageScale Scale of the previous page during pager scrolling. The previous page is always a page with a lower sequential number in the list of `items`, regardless of the direction of scrolling.
 */
@Generated
fun TemplateScope.pageTransformationSlideRefs(
    `use named arguments`: Guard = Guard.instance,
    interpolator: ReferenceProperty<AnimationInterpolator>? = null,
    nextPageAlpha: ReferenceProperty<Double>? = null,
    nextPageScale: ReferenceProperty<Double>? = null,
    previousPageAlpha: ReferenceProperty<Double>? = null,
    previousPageScale: ReferenceProperty<Double>? = null,
) = PageTransformationSlide.Properties(
    interpolator = interpolator,
    nextPageAlpha = nextPageAlpha,
    nextPageScale = nextPageScale,
    previousPageAlpha = previousPageAlpha,
    previousPageScale = previousPageScale,
)

/**
 * @param interpolator Tranformation speed nature. When the value is set to `spring` — animation of damping fluctuations cut to 0.7 with the `damping=1` parameter. Other options correspond to the Bezier curve:<li>`linear` — cubic-bezier(0, 0, 1, 1);</li><li>`ease` — cubic-bezier(0.25, 0.1, 0.25, 1);</li><li>`ease_in` — cubic-bezier(0.42, 0, 1, 1);</li><li>`ease_out` — cubic-bezier(0, 0, 0.58, 1);</li><li>`ease_in_out` — cubic-bezier(0.42, 0, 0.58, 1).</li>
 * @param nextPageAlpha Minimum alpha of the next page during pager scrolling in bounds [0, 1]. The next page is always a page with a large sequential number in the list of `items`, regardless of the direction of scrolling.
 * @param nextPageScale Scale of the next page during pager scrolling. The next page is always a page with a large sequential number in the list of `items`, regardless of the direction of scrolling.
 * @param previousPageAlpha Minimum alpha of the previous page during pager scrolling in bounds [0, 1]. The previous page is always a page with a lower sequential number in the list of `items`, regardless of the direction of scrolling.
 * @param previousPageScale Scale of the previous page during pager scrolling. The previous page is always a page with a lower sequential number in the list of `items`, regardless of the direction of scrolling.
 */
@Generated
fun PageTransformationSlide.override(
    `use named arguments`: Guard = Guard.instance,
    interpolator: AnimationInterpolator? = null,
    nextPageAlpha: Double? = null,
    nextPageScale: Double? = null,
    previousPageAlpha: Double? = null,
    previousPageScale: Double? = null,
): PageTransformationSlide = PageTransformationSlide(
    PageTransformationSlide.Properties(
        interpolator = valueOrNull(interpolator) ?: properties.interpolator,
        nextPageAlpha = valueOrNull(nextPageAlpha) ?: properties.nextPageAlpha,
        nextPageScale = valueOrNull(nextPageScale) ?: properties.nextPageScale,
        previousPageAlpha = valueOrNull(previousPageAlpha) ?: properties.previousPageAlpha,
        previousPageScale = valueOrNull(previousPageScale) ?: properties.previousPageScale,
    )
)

/**
 * @param interpolator Tranformation speed nature. When the value is set to `spring` — animation of damping fluctuations cut to 0.7 with the `damping=1` parameter. Other options correspond to the Bezier curve:<li>`linear` — cubic-bezier(0, 0, 1, 1);</li><li>`ease` — cubic-bezier(0.25, 0.1, 0.25, 1);</li><li>`ease_in` — cubic-bezier(0.42, 0, 1, 1);</li><li>`ease_out` — cubic-bezier(0, 0, 0.58, 1);</li><li>`ease_in_out` — cubic-bezier(0.42, 0, 0.58, 1).</li>
 * @param nextPageAlpha Minimum alpha of the next page during pager scrolling in bounds [0, 1]. The next page is always a page with a large sequential number in the list of `items`, regardless of the direction of scrolling.
 * @param nextPageScale Scale of the next page during pager scrolling. The next page is always a page with a large sequential number in the list of `items`, regardless of the direction of scrolling.
 * @param previousPageAlpha Minimum alpha of the previous page during pager scrolling in bounds [0, 1]. The previous page is always a page with a lower sequential number in the list of `items`, regardless of the direction of scrolling.
 * @param previousPageScale Scale of the previous page during pager scrolling. The previous page is always a page with a lower sequential number in the list of `items`, regardless of the direction of scrolling.
 */
@Generated
fun PageTransformationSlide.defer(
    `use named arguments`: Guard = Guard.instance,
    interpolator: ReferenceProperty<AnimationInterpolator>? = null,
    nextPageAlpha: ReferenceProperty<Double>? = null,
    nextPageScale: ReferenceProperty<Double>? = null,
    previousPageAlpha: ReferenceProperty<Double>? = null,
    previousPageScale: ReferenceProperty<Double>? = null,
): PageTransformationSlide = PageTransformationSlide(
    PageTransformationSlide.Properties(
        interpolator = interpolator ?: properties.interpolator,
        nextPageAlpha = nextPageAlpha ?: properties.nextPageAlpha,
        nextPageScale = nextPageScale ?: properties.nextPageScale,
        previousPageAlpha = previousPageAlpha ?: properties.previousPageAlpha,
        previousPageScale = previousPageScale ?: properties.previousPageScale,
    )
)

/**
 * @param interpolator Tranformation speed nature. When the value is set to `spring` — animation of damping fluctuations cut to 0.7 with the `damping=1` parameter. Other options correspond to the Bezier curve:<li>`linear` — cubic-bezier(0, 0, 1, 1);</li><li>`ease` — cubic-bezier(0.25, 0.1, 0.25, 1);</li><li>`ease_in` — cubic-bezier(0.42, 0, 1, 1);</li><li>`ease_out` — cubic-bezier(0, 0, 0.58, 1);</li><li>`ease_in_out` — cubic-bezier(0.42, 0, 0.58, 1).</li>
 * @param nextPageAlpha Minimum alpha of the next page during pager scrolling in bounds [0, 1]. The next page is always a page with a large sequential number in the list of `items`, regardless of the direction of scrolling.
 * @param nextPageScale Scale of the next page during pager scrolling. The next page is always a page with a large sequential number in the list of `items`, regardless of the direction of scrolling.
 * @param previousPageAlpha Minimum alpha of the previous page during pager scrolling in bounds [0, 1]. The previous page is always a page with a lower sequential number in the list of `items`, regardless of the direction of scrolling.
 * @param previousPageScale Scale of the previous page during pager scrolling. The previous page is always a page with a lower sequential number in the list of `items`, regardless of the direction of scrolling.
 */
@Generated
fun PageTransformationSlide.evaluate(
    `use named arguments`: Guard = Guard.instance,
    interpolator: ExpressionProperty<AnimationInterpolator>? = null,
    nextPageAlpha: ExpressionProperty<Double>? = null,
    nextPageScale: ExpressionProperty<Double>? = null,
    previousPageAlpha: ExpressionProperty<Double>? = null,
    previousPageScale: ExpressionProperty<Double>? = null,
): PageTransformationSlide = PageTransformationSlide(
    PageTransformationSlide.Properties(
        interpolator = interpolator ?: properties.interpolator,
        nextPageAlpha = nextPageAlpha ?: properties.nextPageAlpha,
        nextPageScale = nextPageScale ?: properties.nextPageScale,
        previousPageAlpha = previousPageAlpha ?: properties.previousPageAlpha,
        previousPageScale = previousPageScale ?: properties.previousPageScale,
    )
)

@Generated
fun PageTransformationSlide.asList() = listOf(this)
