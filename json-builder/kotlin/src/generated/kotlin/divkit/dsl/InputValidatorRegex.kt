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
 * Regex validator.
 * 
 * Can be created using the method [inputValidatorRegex].
 * 
 * Required parameters: `variable, type, pattern, label_id`.
 */
@Generated
class InputValidatorRegex internal constructor(
    @JsonIgnore
    val properties: Properties,
) : InputValidator {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "regex")
    )

    operator fun plus(additive: Properties): InputValidatorRegex = InputValidatorRegex(
        Properties(
            allowEmpty = additive.allowEmpty ?: properties.allowEmpty,
            labelId = additive.labelId ?: properties.labelId,
            pattern = additive.pattern ?: properties.pattern,
            variable = additive.variable ?: properties.variable,
        )
    )

    class Properties internal constructor(
        /**
         * Whether an empty value is correct. By default, false.
         * Default value: `false`.
         */
        val allowEmpty: Property<Boolean>?,
        /**
         * ID of the text div containing the error message, which will also be used for accessibility.
         */
        val labelId: Property<String>?,
        /**
         * Regex pattern for matching.
         */
        val pattern: Property<String>?,
        /**
         * Name of validation storage variable.
         */
        val variable: Property<String>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("allow_empty", allowEmpty)
            result.tryPutProperty("label_id", labelId)
            result.tryPutProperty("pattern", pattern)
            result.tryPutProperty("variable", variable)
            return result
        }
    }
}

/**
 * @param allowEmpty Whether an empty value is correct. By default, false.
 * @param labelId ID of the text div containing the error message, which will also be used for accessibility.
 * @param pattern Regex pattern for matching.
 * @param variable Name of validation storage variable.
 */
@Generated
fun DivScope.inputValidatorRegex(
    `use named arguments`: Guard = Guard.instance,
    allowEmpty: Boolean? = null,
    labelId: String? = null,
    pattern: String? = null,
    variable: String? = null,
): InputValidatorRegex = InputValidatorRegex(
    InputValidatorRegex.Properties(
        allowEmpty = valueOrNull(allowEmpty),
        labelId = valueOrNull(labelId),
        pattern = valueOrNull(pattern),
        variable = valueOrNull(variable),
    )
)

/**
 * @param allowEmpty Whether an empty value is correct. By default, false.
 * @param labelId ID of the text div containing the error message, which will also be used for accessibility.
 * @param pattern Regex pattern for matching.
 * @param variable Name of validation storage variable.
 */
@Generated
fun DivScope.inputValidatorRegexProps(
    `use named arguments`: Guard = Guard.instance,
    allowEmpty: Boolean? = null,
    labelId: String? = null,
    pattern: String? = null,
    variable: String? = null,
) = InputValidatorRegex.Properties(
    allowEmpty = valueOrNull(allowEmpty),
    labelId = valueOrNull(labelId),
    pattern = valueOrNull(pattern),
    variable = valueOrNull(variable),
)

/**
 * @param allowEmpty Whether an empty value is correct. By default, false.
 * @param labelId ID of the text div containing the error message, which will also be used for accessibility.
 * @param pattern Regex pattern for matching.
 * @param variable Name of validation storage variable.
 */
@Generated
fun TemplateScope.inputValidatorRegexRefs(
    `use named arguments`: Guard = Guard.instance,
    allowEmpty: ReferenceProperty<Boolean>? = null,
    labelId: ReferenceProperty<String>? = null,
    pattern: ReferenceProperty<String>? = null,
    variable: ReferenceProperty<String>? = null,
) = InputValidatorRegex.Properties(
    allowEmpty = allowEmpty,
    labelId = labelId,
    pattern = pattern,
    variable = variable,
)

/**
 * @param allowEmpty Whether an empty value is correct. By default, false.
 * @param labelId ID of the text div containing the error message, which will also be used for accessibility.
 * @param pattern Regex pattern for matching.
 * @param variable Name of validation storage variable.
 */
@Generated
fun InputValidatorRegex.override(
    `use named arguments`: Guard = Guard.instance,
    allowEmpty: Boolean? = null,
    labelId: String? = null,
    pattern: String? = null,
    variable: String? = null,
): InputValidatorRegex = InputValidatorRegex(
    InputValidatorRegex.Properties(
        allowEmpty = valueOrNull(allowEmpty) ?: properties.allowEmpty,
        labelId = valueOrNull(labelId) ?: properties.labelId,
        pattern = valueOrNull(pattern) ?: properties.pattern,
        variable = valueOrNull(variable) ?: properties.variable,
    )
)

/**
 * @param allowEmpty Whether an empty value is correct. By default, false.
 * @param labelId ID of the text div containing the error message, which will also be used for accessibility.
 * @param pattern Regex pattern for matching.
 * @param variable Name of validation storage variable.
 */
@Generated
fun InputValidatorRegex.defer(
    `use named arguments`: Guard = Guard.instance,
    allowEmpty: ReferenceProperty<Boolean>? = null,
    labelId: ReferenceProperty<String>? = null,
    pattern: ReferenceProperty<String>? = null,
    variable: ReferenceProperty<String>? = null,
): InputValidatorRegex = InputValidatorRegex(
    InputValidatorRegex.Properties(
        allowEmpty = allowEmpty ?: properties.allowEmpty,
        labelId = labelId ?: properties.labelId,
        pattern = pattern ?: properties.pattern,
        variable = variable ?: properties.variable,
    )
)

/**
 * @param allowEmpty Whether an empty value is correct. By default, false.
 * @param labelId ID of the text div containing the error message, which will also be used for accessibility.
 * @param pattern Regex pattern for matching.
 */
@Generated
fun InputValidatorRegex.evaluate(
    `use named arguments`: Guard = Guard.instance,
    allowEmpty: ExpressionProperty<Boolean>? = null,
    labelId: ExpressionProperty<String>? = null,
    pattern: ExpressionProperty<String>? = null,
): InputValidatorRegex = InputValidatorRegex(
    InputValidatorRegex.Properties(
        allowEmpty = allowEmpty ?: properties.allowEmpty,
        labelId = labelId ?: properties.labelId,
        pattern = pattern ?: properties.pattern,
        variable = properties.variable,
    )
)

@Generated
fun InputValidatorRegex.asList() = listOf(this)
