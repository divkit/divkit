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
 * Translation transformation.
 * 
 * Can be created using the method [translationTransformation].
 * 
 * Required parameters: `type`.
 */
@Generated
@ExposedCopyVisibility
data class TranslationTransformation internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Transformation {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "translation")
    )

    operator fun plus(additive: Properties): TranslationTransformation = TranslationTransformation(
        Properties(
            x = additive.x ?: properties.x,
            y = additive.y ?: properties.y,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * X coordinate of the translation.
         */
        val x: Property<Translation>?,
        /**
         * Y coordinate of the translation.
         */
        val y: Property<Translation>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("x", x)
            result.tryPutProperty("y", y)
            return result
        }
    }
}

/**
 * @param x X coordinate of the translation.
 * @param y Y coordinate of the translation.
 */
@Generated
fun DivScope.translationTransformation(
    `use named arguments`: Guard = Guard.instance,
    x: Translation? = null,
    y: Translation? = null,
): TranslationTransformation = TranslationTransformation(
    TranslationTransformation.Properties(
        x = valueOrNull(x),
        y = valueOrNull(y),
    )
)

/**
 * @param x X coordinate of the translation.
 * @param y Y coordinate of the translation.
 */
@Generated
fun DivScope.translationTransformationProps(
    `use named arguments`: Guard = Guard.instance,
    x: Translation? = null,
    y: Translation? = null,
) = TranslationTransformation.Properties(
    x = valueOrNull(x),
    y = valueOrNull(y),
)

/**
 * @param x X coordinate of the translation.
 * @param y Y coordinate of the translation.
 */
@Generated
fun TemplateScope.translationTransformationRefs(
    `use named arguments`: Guard = Guard.instance,
    x: ReferenceProperty<Translation>? = null,
    y: ReferenceProperty<Translation>? = null,
) = TranslationTransformation.Properties(
    x = x,
    y = y,
)

/**
 * @param x X coordinate of the translation.
 * @param y Y coordinate of the translation.
 */
@Generated
fun TranslationTransformation.override(
    `use named arguments`: Guard = Guard.instance,
    x: Translation? = null,
    y: Translation? = null,
): TranslationTransformation = TranslationTransformation(
    TranslationTransformation.Properties(
        x = valueOrNull(x) ?: properties.x,
        y = valueOrNull(y) ?: properties.y,
    )
)

/**
 * @param x X coordinate of the translation.
 * @param y Y coordinate of the translation.
 */
@Generated
fun TranslationTransformation.defer(
    `use named arguments`: Guard = Guard.instance,
    x: ReferenceProperty<Translation>? = null,
    y: ReferenceProperty<Translation>? = null,
): TranslationTransformation = TranslationTransformation(
    TranslationTransformation.Properties(
        x = x ?: properties.x,
        y = y ?: properties.y,
    )
)

/**
 * @param x X coordinate of the translation.
 * @param y Y coordinate of the translation.
 */
@Generated
fun TranslationTransformation.modify(
    `use named arguments`: Guard = Guard.instance,
    x: Property<Translation>? = null,
    y: Property<Translation>? = null,
): TranslationTransformation = TranslationTransformation(
    TranslationTransformation.Properties(
        x = x ?: properties.x,
        y = y ?: properties.y,
    )
)

@Generated
fun TranslationTransformation.asList() = listOf(this)
