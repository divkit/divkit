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
@ExposedCopyVisibility
data class InputValidatorRegex internal constructor(
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

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Determines whether the empty field value is valid.
         * Default value: `false`.
         */
        val allowEmpty: Property<Boolean>?,
        /**
         * ID of the text element containing the error message. The message will also be used for providing access.
         */
        val labelId: Property<String>?,
        /**
         * A regular expression (pattern) that the field value must match.
         */
        val pattern: Property<String>?,
        /**
         * The name of the variable that stores the calculation results.
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
 * @param allowEmpty Determines whether the empty field value is valid.
 * @param labelId ID of the text element containing the error message. The message will also be used for providing access.
 * @param pattern A regular expression (pattern) that the field value must match.
 * @param variable The name of the variable that stores the calculation results.
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
 * @param allowEmpty Determines whether the empty field value is valid.
 * @param labelId ID of the text element containing the error message. The message will also be used for providing access.
 * @param pattern A regular expression (pattern) that the field value must match.
 * @param variable The name of the variable that stores the calculation results.
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
 * @param allowEmpty Determines whether the empty field value is valid.
 * @param labelId ID of the text element containing the error message. The message will also be used for providing access.
 * @param pattern A regular expression (pattern) that the field value must match.
 * @param variable The name of the variable that stores the calculation results.
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
 * @param allowEmpty Determines whether the empty field value is valid.
 * @param labelId ID of the text element containing the error message. The message will also be used for providing access.
 * @param pattern A regular expression (pattern) that the field value must match.
 * @param variable The name of the variable that stores the calculation results.
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
 * @param allowEmpty Determines whether the empty field value is valid.
 * @param labelId ID of the text element containing the error message. The message will also be used for providing access.
 * @param pattern A regular expression (pattern) that the field value must match.
 * @param variable The name of the variable that stores the calculation results.
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
 * @param allowEmpty Determines whether the empty field value is valid.
 * @param labelId ID of the text element containing the error message. The message will also be used for providing access.
 * @param pattern A regular expression (pattern) that the field value must match.
 * @param variable The name of the variable that stores the calculation results.
 */
@Generated
fun InputValidatorRegex.modify(
    `use named arguments`: Guard = Guard.instance,
    allowEmpty: Property<Boolean>? = null,
    labelId: Property<String>? = null,
    pattern: Property<String>? = null,
    variable: Property<String>? = null,
): InputValidatorRegex = InputValidatorRegex(
    InputValidatorRegex.Properties(
        allowEmpty = allowEmpty ?: properties.allowEmpty,
        labelId = labelId ?: properties.labelId,
        pattern = pattern ?: properties.pattern,
        variable = variable ?: properties.variable,
    )
)

/**
 * @param allowEmpty Determines whether the empty field value is valid.
 * @param labelId ID of the text element containing the error message. The message will also be used for providing access.
 * @param pattern A regular expression (pattern) that the field value must match.
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
