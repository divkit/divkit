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
 * Can be created using the method [textRangeMaskBase].
 */
@Generated
@ExposedCopyVisibility
data class TextRangeMaskBase internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): TextRangeMaskBase = TextRangeMaskBase(
        Properties(
            isEnabled = additive.isEnabled ?: properties.isEnabled,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Controls the mask state. If set to `true`, the mask will hide the specified part of text. Otherwise, the text will be shown.
         * Default value: `true`.
         */
        val isEnabled: Property<Boolean>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("is_enabled", isEnabled)
            return result
        }
    }
}

/**
 * @param isEnabled Controls the mask state. If set to `true`, the mask will hide the specified part of text. Otherwise, the text will be shown.
 */
@Generated
fun DivScope.textRangeMaskBase(
    `use named arguments`: Guard = Guard.instance,
    isEnabled: Boolean? = null,
): TextRangeMaskBase = TextRangeMaskBase(
    TextRangeMaskBase.Properties(
        isEnabled = valueOrNull(isEnabled),
    )
)

/**
 * @param isEnabled Controls the mask state. If set to `true`, the mask will hide the specified part of text. Otherwise, the text will be shown.
 */
@Generated
fun DivScope.textRangeMaskBaseProps(
    `use named arguments`: Guard = Guard.instance,
    isEnabled: Boolean? = null,
) = TextRangeMaskBase.Properties(
    isEnabled = valueOrNull(isEnabled),
)

/**
 * @param isEnabled Controls the mask state. If set to `true`, the mask will hide the specified part of text. Otherwise, the text will be shown.
 */
@Generated
fun TemplateScope.textRangeMaskBaseRefs(
    `use named arguments`: Guard = Guard.instance,
    isEnabled: ReferenceProperty<Boolean>? = null,
) = TextRangeMaskBase.Properties(
    isEnabled = isEnabled,
)

/**
 * @param isEnabled Controls the mask state. If set to `true`, the mask will hide the specified part of text. Otherwise, the text will be shown.
 */
@Generated
fun TextRangeMaskBase.override(
    `use named arguments`: Guard = Guard.instance,
    isEnabled: Boolean? = null,
): TextRangeMaskBase = TextRangeMaskBase(
    TextRangeMaskBase.Properties(
        isEnabled = valueOrNull(isEnabled) ?: properties.isEnabled,
    )
)

/**
 * @param isEnabled Controls the mask state. If set to `true`, the mask will hide the specified part of text. Otherwise, the text will be shown.
 */
@Generated
fun TextRangeMaskBase.defer(
    `use named arguments`: Guard = Guard.instance,
    isEnabled: ReferenceProperty<Boolean>? = null,
): TextRangeMaskBase = TextRangeMaskBase(
    TextRangeMaskBase.Properties(
        isEnabled = isEnabled ?: properties.isEnabled,
    )
)

/**
 * @param isEnabled Controls the mask state. If set to `true`, the mask will hide the specified part of text. Otherwise, the text will be shown.
 */
@Generated
fun TextRangeMaskBase.modify(
    `use named arguments`: Guard = Guard.instance,
    isEnabled: Property<Boolean>? = null,
): TextRangeMaskBase = TextRangeMaskBase(
    TextRangeMaskBase.Properties(
        isEnabled = isEnabled ?: properties.isEnabled,
    )
)

/**
 * @param isEnabled Controls the mask state. If set to `true`, the mask will hide the specified part of text. Otherwise, the text will be shown.
 */
@Generated
fun TextRangeMaskBase.evaluate(
    `use named arguments`: Guard = Guard.instance,
    isEnabled: ExpressionProperty<Boolean>? = null,
): TextRangeMaskBase = TextRangeMaskBase(
    TextRangeMaskBase.Properties(
        isEnabled = isEnabled ?: properties.isEnabled,
    )
)

@Generated
fun TextRangeMaskBase.asList() = listOf(this)
