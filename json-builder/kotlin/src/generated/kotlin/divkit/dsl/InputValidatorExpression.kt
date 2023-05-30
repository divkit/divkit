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
 * Expression validator.
 * 
 * Can be created using the method [inputValidatorExpression].
 * 
 * Required parameters: `variable, type, label_id, condition`.
 */
@Generated
class InputValidatorExpression internal constructor(
    @JsonIgnore
    val properties: Properties,
) : InputValidator {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "expression")
    )

    operator fun plus(additive: Properties): InputValidatorExpression = InputValidatorExpression(
        Properties(
            allowEmpty = additive.allowEmpty ?: properties.allowEmpty,
            condition = additive.condition ?: properties.condition,
            labelId = additive.labelId ?: properties.labelId,
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
         * Expression condition for evaluating.
         */
        val condition: Property<String>?,
        /**
         * ID of the text div containing the error message, which will also be used for accessibility.
         */
        val labelId: Property<String>?,
        /**
         * Name of validation storage variable.
         */
        val variable: Property<String>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("allow_empty", allowEmpty)
            result.tryPutProperty("condition", condition)
            result.tryPutProperty("label_id", labelId)
            result.tryPutProperty("variable", variable)
            return result
        }
    }
}

/**
 * @param allowEmpty Whether an empty value is correct. By default, false.
 * @param condition Expression condition for evaluating.
 * @param labelId ID of the text div containing the error message, which will also be used for accessibility.
 * @param variable Name of validation storage variable.
 */
@Generated
fun DivScope.inputValidatorExpression(
    `use named arguments`: Guard = Guard.instance,
    allowEmpty: Boolean? = null,
    condition: String? = null,
    labelId: String? = null,
    variable: String? = null,
): InputValidatorExpression = InputValidatorExpression(
    InputValidatorExpression.Properties(
        allowEmpty = valueOrNull(allowEmpty),
        condition = valueOrNull(condition),
        labelId = valueOrNull(labelId),
        variable = valueOrNull(variable),
    )
)

/**
 * @param allowEmpty Whether an empty value is correct. By default, false.
 * @param condition Expression condition for evaluating.
 * @param labelId ID of the text div containing the error message, which will also be used for accessibility.
 * @param variable Name of validation storage variable.
 */
@Generated
fun DivScope.inputValidatorExpressionProps(
    `use named arguments`: Guard = Guard.instance,
    allowEmpty: Boolean? = null,
    condition: String? = null,
    labelId: String? = null,
    variable: String? = null,
) = InputValidatorExpression.Properties(
    allowEmpty = valueOrNull(allowEmpty),
    condition = valueOrNull(condition),
    labelId = valueOrNull(labelId),
    variable = valueOrNull(variable),
)

/**
 * @param allowEmpty Whether an empty value is correct. By default, false.
 * @param condition Expression condition for evaluating.
 * @param labelId ID of the text div containing the error message, which will also be used for accessibility.
 * @param variable Name of validation storage variable.
 */
@Generated
fun TemplateScope.inputValidatorExpressionRefs(
    `use named arguments`: Guard = Guard.instance,
    allowEmpty: ReferenceProperty<Boolean>? = null,
    condition: ReferenceProperty<String>? = null,
    labelId: ReferenceProperty<String>? = null,
    variable: ReferenceProperty<String>? = null,
) = InputValidatorExpression.Properties(
    allowEmpty = allowEmpty,
    condition = condition,
    labelId = labelId,
    variable = variable,
)

/**
 * @param allowEmpty Whether an empty value is correct. By default, false.
 * @param condition Expression condition for evaluating.
 * @param labelId ID of the text div containing the error message, which will also be used for accessibility.
 * @param variable Name of validation storage variable.
 */
@Generated
fun InputValidatorExpression.override(
    `use named arguments`: Guard = Guard.instance,
    allowEmpty: Boolean? = null,
    condition: String? = null,
    labelId: String? = null,
    variable: String? = null,
): InputValidatorExpression = InputValidatorExpression(
    InputValidatorExpression.Properties(
        allowEmpty = valueOrNull(allowEmpty) ?: properties.allowEmpty,
        condition = valueOrNull(condition) ?: properties.condition,
        labelId = valueOrNull(labelId) ?: properties.labelId,
        variable = valueOrNull(variable) ?: properties.variable,
    )
)

/**
 * @param allowEmpty Whether an empty value is correct. By default, false.
 * @param condition Expression condition for evaluating.
 * @param labelId ID of the text div containing the error message, which will also be used for accessibility.
 * @param variable Name of validation storage variable.
 */
@Generated
fun InputValidatorExpression.defer(
    `use named arguments`: Guard = Guard.instance,
    allowEmpty: ReferenceProperty<Boolean>? = null,
    condition: ReferenceProperty<String>? = null,
    labelId: ReferenceProperty<String>? = null,
    variable: ReferenceProperty<String>? = null,
): InputValidatorExpression = InputValidatorExpression(
    InputValidatorExpression.Properties(
        allowEmpty = allowEmpty ?: properties.allowEmpty,
        condition = condition ?: properties.condition,
        labelId = labelId ?: properties.labelId,
        variable = variable ?: properties.variable,
    )
)

/**
 * @param allowEmpty Whether an empty value is correct. By default, false.
 * @param condition Expression condition for evaluating.
 * @param labelId ID of the text div containing the error message, which will also be used for accessibility.
 */
@Generated
fun InputValidatorExpression.evaluate(
    `use named arguments`: Guard = Guard.instance,
    allowEmpty: ExpressionProperty<Boolean>? = null,
    condition: ExpressionProperty<String>? = null,
    labelId: ExpressionProperty<String>? = null,
): InputValidatorExpression = InputValidatorExpression(
    InputValidatorExpression.Properties(
        allowEmpty = allowEmpty ?: properties.allowEmpty,
        condition = condition ?: properties.condition,
        labelId = labelId ?: properties.labelId,
        variable = properties.variable,
    )
)

@Generated
fun InputValidatorExpression.asList() = listOf(this)
