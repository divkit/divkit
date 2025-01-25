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
 * A mask to hide text (spoiler) that looks like a rectangle filled with color specified by `color` parameter.
 * 
 * Can be created using the method [textRangeMaskSolid].
 * 
 * Required parameters: `type, color`.
 */
@Generated
data class TextRangeMaskSolid internal constructor(
    @JsonIgnore
    val properties: Properties,
) : TextRangeMask {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "solid")
    )

    operator fun plus(additive: Properties): TextRangeMaskSolid = TextRangeMaskSolid(
        Properties(
            color = additive.color ?: properties.color,
            isEnabled = additive.isEnabled ?: properties.isEnabled,
        )
    )

    data class Properties internal constructor(
        /**
         * Color.
         */
        val color: Property<Color>?,
        /**
         * Controls mask state: if set to `true` mask will hide specified part of the text, otherwise the text will be shown.
         * Default value: `true`.
         */
        val isEnabled: Property<Boolean>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("color", color)
            result.tryPutProperty("is_enabled", isEnabled)
            return result
        }
    }
}

/**
 * @param color Color.
 * @param isEnabled Controls mask state: if set to `true` mask will hide specified part of the text, otherwise the text will be shown.
 */
@Generated
fun DivScope.textRangeMaskSolid(
    `use named arguments`: Guard = Guard.instance,
    color: Color? = null,
    isEnabled: Boolean? = null,
): TextRangeMaskSolid = TextRangeMaskSolid(
    TextRangeMaskSolid.Properties(
        color = valueOrNull(color),
        isEnabled = valueOrNull(isEnabled),
    )
)

/**
 * @param color Color.
 * @param isEnabled Controls mask state: if set to `true` mask will hide specified part of the text, otherwise the text will be shown.
 */
@Generated
fun DivScope.textRangeMaskSolidProps(
    `use named arguments`: Guard = Guard.instance,
    color: Color? = null,
    isEnabled: Boolean? = null,
) = TextRangeMaskSolid.Properties(
    color = valueOrNull(color),
    isEnabled = valueOrNull(isEnabled),
)

/**
 * @param color Color.
 * @param isEnabled Controls mask state: if set to `true` mask will hide specified part of the text, otherwise the text will be shown.
 */
@Generated
fun TemplateScope.textRangeMaskSolidRefs(
    `use named arguments`: Guard = Guard.instance,
    color: ReferenceProperty<Color>? = null,
    isEnabled: ReferenceProperty<Boolean>? = null,
) = TextRangeMaskSolid.Properties(
    color = color,
    isEnabled = isEnabled,
)

/**
 * @param color Color.
 * @param isEnabled Controls mask state: if set to `true` mask will hide specified part of the text, otherwise the text will be shown.
 */
@Generated
fun TextRangeMaskSolid.override(
    `use named arguments`: Guard = Guard.instance,
    color: Color? = null,
    isEnabled: Boolean? = null,
): TextRangeMaskSolid = TextRangeMaskSolid(
    TextRangeMaskSolid.Properties(
        color = valueOrNull(color) ?: properties.color,
        isEnabled = valueOrNull(isEnabled) ?: properties.isEnabled,
    )
)

/**
 * @param color Color.
 * @param isEnabled Controls mask state: if set to `true` mask will hide specified part of the text, otherwise the text will be shown.
 */
@Generated
fun TextRangeMaskSolid.defer(
    `use named arguments`: Guard = Guard.instance,
    color: ReferenceProperty<Color>? = null,
    isEnabled: ReferenceProperty<Boolean>? = null,
): TextRangeMaskSolid = TextRangeMaskSolid(
    TextRangeMaskSolid.Properties(
        color = color ?: properties.color,
        isEnabled = isEnabled ?: properties.isEnabled,
    )
)

/**
 * @param color Color.
 * @param isEnabled Controls mask state: if set to `true` mask will hide specified part of the text, otherwise the text will be shown.
 */
@Generated
fun TextRangeMaskSolid.evaluate(
    `use named arguments`: Guard = Guard.instance,
    color: ExpressionProperty<Color>? = null,
    isEnabled: ExpressionProperty<Boolean>? = null,
): TextRangeMaskSolid = TextRangeMaskSolid(
    TextRangeMaskSolid.Properties(
        color = color ?: properties.color,
        isEnabled = isEnabled ?: properties.isEnabled,
    )
)

@Generated
fun TextRangeMaskSolid.asList() = listOf(this)
