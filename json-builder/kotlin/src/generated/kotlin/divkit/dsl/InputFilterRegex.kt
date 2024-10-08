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
 * Regex filter.
 * 
 * Can be created using the method [inputFilterRegex].
 * 
 * Required parameters: `type, pattern`.
 */
@Generated
data class InputFilterRegex internal constructor(
    @JsonIgnore
    val properties: Properties,
) : InputFilter {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "regex")
    )

    operator fun plus(additive: Properties): InputFilterRegex = InputFilterRegex(
        Properties(
            pattern = additive.pattern ?: properties.pattern,
        )
    )

    data class Properties internal constructor(
        /**
         * A regular expression (pattern) that the input value must match.
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
 * @param pattern A regular expression (pattern) that the input value must match.
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
 * @param pattern A regular expression (pattern) that the input value must match.
 */
@Generated
fun DivScope.inputFilterRegexProps(
    `use named arguments`: Guard = Guard.instance,
    pattern: String? = null,
) = InputFilterRegex.Properties(
    pattern = valueOrNull(pattern),
)

/**
 * @param pattern A regular expression (pattern) that the input value must match.
 */
@Generated
fun TemplateScope.inputFilterRegexRefs(
    `use named arguments`: Guard = Guard.instance,
    pattern: ReferenceProperty<String>? = null,
) = InputFilterRegex.Properties(
    pattern = pattern,
)

/**
 * @param pattern A regular expression (pattern) that the input value must match.
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
 * @param pattern A regular expression (pattern) that the input value must match.
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
 * @param pattern A regular expression (pattern) that the input value must match.
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
