@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divkit.dsl

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import divkit.dsl.annotation.*
import divkit.dsl.core.*
import divkit.dsl.scope.*
import divkit.dsl.serialization.*
import kotlin.Any
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map

/**
 * Filter based on regular expressions.
 * 
 * Can be created using the method [inputFilterRegex].
 * 
 * Required parameters: `type, pattern`.
 */
@Generated
@JsonSerialize(using = InputFilterRegexSerializer::class)
@ExposedCopyVisibility
data class InputFilterRegex internal constructor(
    val properties: Properties,
) : InputFilter {
    operator fun plus(additive: Properties): InputFilterRegex = InputFilterRegex(
        Properties(
            pattern = additive.pattern ?: properties.pattern,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Regular expression (pattern) that the entered value must match.
         */
        val pattern: Property<String>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("pattern", pattern)
            return result
        }
    }
}

/**
 * @param pattern Regular expression (pattern) that the entered value must match.
 */
@Generated
fun DivScope.inputFilterRegex(
    `use named arguments`: Guard = Guard.instance,
    pattern: String? = null,
): InputFilterRegex = InputFilterRegex(
    InputFilterRegex.Properties(
        pattern = valueOrNull(pattern),
    )
)

/**
 * @param pattern Regular expression (pattern) that the entered value must match.
 */
@Generated
fun DivScope.inputFilterRegexProps(
    `use named arguments`: Guard = Guard.instance,
    pattern: String? = null,
) = InputFilterRegex.Properties(
    pattern = valueOrNull(pattern),
)

/**
 * @param pattern Regular expression (pattern) that the entered value must match.
 */
@Generated
fun TemplateScope.inputFilterRegexRefs(
    `use named arguments`: Guard = Guard.instance,
    pattern: ReferenceProperty<String>? = null,
) = InputFilterRegex.Properties(
    pattern = pattern,
)

/**
 * @param pattern Regular expression (pattern) that the entered value must match.
 */
@Generated
fun InputFilterRegex.override(
    `use named arguments`: Guard = Guard.instance,
    pattern: String? = null,
): InputFilterRegex = InputFilterRegex(
    InputFilterRegex.Properties(
        pattern = valueOrNull(pattern) ?: properties.pattern,
    )
)

/**
 * @param pattern Regular expression (pattern) that the entered value must match.
 */
@Generated
fun InputFilterRegex.defer(
    `use named arguments`: Guard = Guard.instance,
    pattern: ReferenceProperty<String>? = null,
): InputFilterRegex = InputFilterRegex(
    InputFilterRegex.Properties(
        pattern = pattern ?: properties.pattern,
    )
)

/**
 * @param pattern Regular expression (pattern) that the entered value must match.
 */
@Generated
fun InputFilterRegex.modify(
    `use named arguments`: Guard = Guard.instance,
    pattern: Property<String>? = null,
): InputFilterRegex = InputFilterRegex(
    InputFilterRegex.Properties(
        pattern = pattern ?: properties.pattern,
    )
)

/**
 * @param pattern Regular expression (pattern) that the entered value must match.
 */
@Generated
fun InputFilterRegex.evaluate(
    `use named arguments`: Guard = Guard.instance,
    pattern: ExpressionProperty<String>? = null,
): InputFilterRegex = InputFilterRegex(
    InputFilterRegex.Properties(
        pattern = pattern ?: properties.pattern,
    )
)

@Generated
fun InputFilterRegex.asList() = listOf(this)
