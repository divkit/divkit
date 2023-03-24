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
 * Background image.
 * 
 * Can be created using the method [imageBackground].
 * 
 * Required parameters: `type, image_url`.
 */
@Generated
class ImageBackground internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Background {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "image")
    )

    operator fun plus(additive: Properties): ImageBackground = ImageBackground(
        Properties(
            alpha = additive.alpha ?: properties.alpha,
            contentAlignmentHorizontal = additive.contentAlignmentHorizontal ?: properties.contentAlignmentHorizontal,
            contentAlignmentVertical = additive.contentAlignmentVertical ?: properties.contentAlignmentVertical,
            filters = additive.filters ?: properties.filters,
            imageUrl = additive.imageUrl ?: properties.imageUrl,
            preloadRequired = additive.preloadRequired ?: properties.preloadRequired,
            scale = additive.scale ?: properties.scale,
        )
    )

    class Properties internal constructor(
        /**
         * Image transparency.
         * Default value: `1.0`.
         */
        val alpha: Property<Double>?,
        /**
         * Horizontal image alignment.
         * Default value: `center`.
         */
        val contentAlignmentHorizontal: Property<AlignmentHorizontal>?,
        /**
         * Vertical image alignment.
         * Default value: `center`.
         */
        val contentAlignmentVertical: Property<AlignmentVertical>?,
        /**
         * Image filters.
         */
        val filters: Property<List<Filter>>?,
        /**
         * Image URL.
         */
        val imageUrl: Property<Url>?,
        /**
         * Background image must be loaded before the display.
         * Default value: `false`.
         */
        val preloadRequired: Property<Boolean>?,
        /**
         * Image scaling.
         * Default value: `fill`.
         */
        val scale: Property<ImageScale>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("alpha", alpha)
            result.tryPutProperty("content_alignment_horizontal", contentAlignmentHorizontal)
            result.tryPutProperty("content_alignment_vertical", contentAlignmentVertical)
            result.tryPutProperty("filters", filters)
            result.tryPutProperty("image_url", imageUrl)
            result.tryPutProperty("preload_required", preloadRequired)
            result.tryPutProperty("scale", scale)
            return result
        }
    }
}

/**
 * @param alpha Image transparency.
 * @param contentAlignmentHorizontal Horizontal image alignment.
 * @param contentAlignmentVertical Vertical image alignment.
 * @param filters Image filters.
 * @param imageUrl Image URL.
 * @param preloadRequired Background image must be loaded before the display.
 * @param scale Image scaling.
 */
@Generated
fun DivScope.imageBackground(
    `use named arguments`: Guard = Guard.instance,
    alpha: Double? = null,
    contentAlignmentHorizontal: AlignmentHorizontal? = null,
    contentAlignmentVertical: AlignmentVertical? = null,
    filters: List<Filter>? = null,
    imageUrl: Url? = null,
    preloadRequired: Boolean? = null,
    scale: ImageScale? = null,
): ImageBackground = ImageBackground(
    ImageBackground.Properties(
        alpha = valueOrNull(alpha),
        contentAlignmentHorizontal = valueOrNull(contentAlignmentHorizontal),
        contentAlignmentVertical = valueOrNull(contentAlignmentVertical),
        filters = valueOrNull(filters),
        imageUrl = valueOrNull(imageUrl),
        preloadRequired = valueOrNull(preloadRequired),
        scale = valueOrNull(scale),
    )
)

/**
 * @param alpha Image transparency.
 * @param contentAlignmentHorizontal Horizontal image alignment.
 * @param contentAlignmentVertical Vertical image alignment.
 * @param filters Image filters.
 * @param imageUrl Image URL.
 * @param preloadRequired Background image must be loaded before the display.
 * @param scale Image scaling.
 */
@Generated
fun DivScope.imageBackgroundProps(
    `use named arguments`: Guard = Guard.instance,
    alpha: Double? = null,
    contentAlignmentHorizontal: AlignmentHorizontal? = null,
    contentAlignmentVertical: AlignmentVertical? = null,
    filters: List<Filter>? = null,
    imageUrl: Url? = null,
    preloadRequired: Boolean? = null,
    scale: ImageScale? = null,
) = ImageBackground.Properties(
    alpha = valueOrNull(alpha),
    contentAlignmentHorizontal = valueOrNull(contentAlignmentHorizontal),
    contentAlignmentVertical = valueOrNull(contentAlignmentVertical),
    filters = valueOrNull(filters),
    imageUrl = valueOrNull(imageUrl),
    preloadRequired = valueOrNull(preloadRequired),
    scale = valueOrNull(scale),
)

/**
 * @param alpha Image transparency.
 * @param contentAlignmentHorizontal Horizontal image alignment.
 * @param contentAlignmentVertical Vertical image alignment.
 * @param filters Image filters.
 * @param imageUrl Image URL.
 * @param preloadRequired Background image must be loaded before the display.
 * @param scale Image scaling.
 */
@Generated
fun TemplateScope.imageBackgroundRefs(
    `use named arguments`: Guard = Guard.instance,
    alpha: ReferenceProperty<Double>? = null,
    contentAlignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    contentAlignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    filters: ReferenceProperty<List<Filter>>? = null,
    imageUrl: ReferenceProperty<Url>? = null,
    preloadRequired: ReferenceProperty<Boolean>? = null,
    scale: ReferenceProperty<ImageScale>? = null,
) = ImageBackground.Properties(
    alpha = alpha,
    contentAlignmentHorizontal = contentAlignmentHorizontal,
    contentAlignmentVertical = contentAlignmentVertical,
    filters = filters,
    imageUrl = imageUrl,
    preloadRequired = preloadRequired,
    scale = scale,
)

/**
 * @param alpha Image transparency.
 * @param contentAlignmentHorizontal Horizontal image alignment.
 * @param contentAlignmentVertical Vertical image alignment.
 * @param filters Image filters.
 * @param imageUrl Image URL.
 * @param preloadRequired Background image must be loaded before the display.
 * @param scale Image scaling.
 */
@Generated
fun ImageBackground.override(
    `use named arguments`: Guard = Guard.instance,
    alpha: Double? = null,
    contentAlignmentHorizontal: AlignmentHorizontal? = null,
    contentAlignmentVertical: AlignmentVertical? = null,
    filters: List<Filter>? = null,
    imageUrl: Url? = null,
    preloadRequired: Boolean? = null,
    scale: ImageScale? = null,
): ImageBackground = ImageBackground(
    ImageBackground.Properties(
        alpha = valueOrNull(alpha) ?: properties.alpha,
        contentAlignmentHorizontal = valueOrNull(contentAlignmentHorizontal) ?: properties.contentAlignmentHorizontal,
        contentAlignmentVertical = valueOrNull(contentAlignmentVertical) ?: properties.contentAlignmentVertical,
        filters = valueOrNull(filters) ?: properties.filters,
        imageUrl = valueOrNull(imageUrl) ?: properties.imageUrl,
        preloadRequired = valueOrNull(preloadRequired) ?: properties.preloadRequired,
        scale = valueOrNull(scale) ?: properties.scale,
    )
)

/**
 * @param alpha Image transparency.
 * @param contentAlignmentHorizontal Horizontal image alignment.
 * @param contentAlignmentVertical Vertical image alignment.
 * @param filters Image filters.
 * @param imageUrl Image URL.
 * @param preloadRequired Background image must be loaded before the display.
 * @param scale Image scaling.
 */
@Generated
fun ImageBackground.defer(
    `use named arguments`: Guard = Guard.instance,
    alpha: ReferenceProperty<Double>? = null,
    contentAlignmentHorizontal: ReferenceProperty<AlignmentHorizontal>? = null,
    contentAlignmentVertical: ReferenceProperty<AlignmentVertical>? = null,
    filters: ReferenceProperty<List<Filter>>? = null,
    imageUrl: ReferenceProperty<Url>? = null,
    preloadRequired: ReferenceProperty<Boolean>? = null,
    scale: ReferenceProperty<ImageScale>? = null,
): ImageBackground = ImageBackground(
    ImageBackground.Properties(
        alpha = alpha ?: properties.alpha,
        contentAlignmentHorizontal = contentAlignmentHorizontal ?: properties.contentAlignmentHorizontal,
        contentAlignmentVertical = contentAlignmentVertical ?: properties.contentAlignmentVertical,
        filters = filters ?: properties.filters,
        imageUrl = imageUrl ?: properties.imageUrl,
        preloadRequired = preloadRequired ?: properties.preloadRequired,
        scale = scale ?: properties.scale,
    )
)

/**
 * @param alpha Image transparency.
 * @param contentAlignmentHorizontal Horizontal image alignment.
 * @param contentAlignmentVertical Vertical image alignment.
 * @param imageUrl Image URL.
 * @param preloadRequired Background image must be loaded before the display.
 * @param scale Image scaling.
 */
@Generated
fun ImageBackground.evaluate(
    `use named arguments`: Guard = Guard.instance,
    alpha: ExpressionProperty<Double>? = null,
    contentAlignmentHorizontal: ExpressionProperty<AlignmentHorizontal>? = null,
    contentAlignmentVertical: ExpressionProperty<AlignmentVertical>? = null,
    imageUrl: ExpressionProperty<Url>? = null,
    preloadRequired: ExpressionProperty<Boolean>? = null,
    scale: ExpressionProperty<ImageScale>? = null,
): ImageBackground = ImageBackground(
    ImageBackground.Properties(
        alpha = alpha ?: properties.alpha,
        contentAlignmentHorizontal = contentAlignmentHorizontal ?: properties.contentAlignmentHorizontal,
        contentAlignmentVertical = contentAlignmentVertical ?: properties.contentAlignmentVertical,
        filters = properties.filters,
        imageUrl = imageUrl ?: properties.imageUrl,
        preloadRequired = preloadRequired ?: properties.preloadRequired,
        scale = scale ?: properties.scale,
    )
)

@Generated
fun ImageBackground.asList() = listOf(this)
