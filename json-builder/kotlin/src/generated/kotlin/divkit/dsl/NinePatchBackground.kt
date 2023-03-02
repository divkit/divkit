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
 * Image in 9-patch format (https://developer.android.com/studio/write/draw9patch).
 * 
 * Can be created using the method [ninePatchBackground].
 * 
 * Required properties: `type, insets, image_url`.
 */
@Generated
class NinePatchBackground internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Background {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "nine_patch_image")
    )

    operator fun plus(additive: Properties): NinePatchBackground = NinePatchBackground(
        Properties(
            imageUrl = additive.imageUrl ?: properties.imageUrl,
            insets = additive.insets ?: properties.insets,
        )
    )

    class Properties internal constructor(
        /**
         * Image URL.
         */
        val imageUrl: Property<Url>?,
        /**
         * Margins that break the image into parts using the same rules as the CSS `border-image-slice` property (https://developer.mozilla.org/en-US/docs/Web/CSS/border-image-slice).
         */
        val insets: Property<AbsoluteEdgeInsets>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("image_url", imageUrl)
            result.tryPutProperty("insets", insets)
            return result
        }
    }
}

/**
 * @param imageUrl Image URL.
 * @param insets Margins that break the image into parts using the same rules as the CSS `border-image-slice` property (https://developer.mozilla.org/en-US/docs/Web/CSS/border-image-slice).
 */
@Generated
fun DivScope.ninePatchBackground(
    `use named arguments`: Guard = Guard.instance,
    imageUrl: Url,
    insets: AbsoluteEdgeInsets,
): NinePatchBackground = NinePatchBackground(
    NinePatchBackground.Properties(
        imageUrl = valueOrNull(imageUrl),
        insets = valueOrNull(insets),
    )
)

/**
 * @param imageUrl Image URL.
 * @param insets Margins that break the image into parts using the same rules as the CSS `border-image-slice` property (https://developer.mozilla.org/en-US/docs/Web/CSS/border-image-slice).
 */
@Generated
fun DivScope.ninePatchBackgroundProps(
    `use named arguments`: Guard = Guard.instance,
    imageUrl: Url? = null,
    insets: AbsoluteEdgeInsets? = null,
) = NinePatchBackground.Properties(
    imageUrl = valueOrNull(imageUrl),
    insets = valueOrNull(insets),
)

/**
 * @param imageUrl Image URL.
 * @param insets Margins that break the image into parts using the same rules as the CSS `border-image-slice` property (https://developer.mozilla.org/en-US/docs/Web/CSS/border-image-slice).
 */
@Generated
fun TemplateScope.ninePatchBackgroundRefs(
    `use named arguments`: Guard = Guard.instance,
    imageUrl: ReferenceProperty<Url>? = null,
    insets: ReferenceProperty<AbsoluteEdgeInsets>? = null,
) = NinePatchBackground.Properties(
    imageUrl = imageUrl,
    insets = insets,
)

/**
 * @param imageUrl Image URL.
 * @param insets Margins that break the image into parts using the same rules as the CSS `border-image-slice` property (https://developer.mozilla.org/en-US/docs/Web/CSS/border-image-slice).
 */
@Generated
fun NinePatchBackground.override(
    `use named arguments`: Guard = Guard.instance,
    imageUrl: Url? = null,
    insets: AbsoluteEdgeInsets? = null,
): NinePatchBackground = NinePatchBackground(
    NinePatchBackground.Properties(
        imageUrl = valueOrNull(imageUrl) ?: properties.imageUrl,
        insets = valueOrNull(insets) ?: properties.insets,
    )
)

/**
 * @param imageUrl Image URL.
 * @param insets Margins that break the image into parts using the same rules as the CSS `border-image-slice` property (https://developer.mozilla.org/en-US/docs/Web/CSS/border-image-slice).
 */
@Generated
fun NinePatchBackground.defer(
    `use named arguments`: Guard = Guard.instance,
    imageUrl: ReferenceProperty<Url>? = null,
    insets: ReferenceProperty<AbsoluteEdgeInsets>? = null,
): NinePatchBackground = NinePatchBackground(
    NinePatchBackground.Properties(
        imageUrl = imageUrl ?: properties.imageUrl,
        insets = insets ?: properties.insets,
    )
)

/**
 * @param imageUrl Image URL.
 */
@Generated
fun NinePatchBackground.evaluate(
    `use named arguments`: Guard = Guard.instance,
    imageUrl: ExpressionProperty<Url>? = null,
): NinePatchBackground = NinePatchBackground(
    NinePatchBackground.Properties(
        imageUrl = imageUrl ?: properties.imageUrl,
        insets = properties.insets,
    )
)

@Generated
fun NinePatchBackground.asList() = listOf(this)
