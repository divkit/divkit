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
 * Can be created using the method [inputValidatorBase].
 */
@Generated
class InputValidatorBase internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): InputValidatorBase = InputValidatorBase(
        Properties(
            allowEmpty = additive.allowEmpty ?: properties.allowEmpty,
            labelId = additive.labelId ?: properties.labelId,
            variable = additive.variable ?: properties.variable,
        )
    )

    class Properties internal constructor(
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
         * The name of the variable that stores the calculation results.
         */
        val variable: Property<String>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("allow_empty", allowEmpty)
            result.tryPutProperty("label_id", labelId)
            result.tryPutProperty("variable", variable)
            return result
        }
    }
}

/**
 * @param allowEmpty Determines whether the empty field value is valid.
 * @param labelId ID of the text element containing the error message. The message will also be used for providing access.
 * @param variable The name of the variable that stores the calculation results.
 */
@Generated
fun DivScope.inputValidatorBase(
    `use named arguments`: Guard = Guard.instance,
    allowEmpty: Boolean? = null,
    labelId: String? = null,
    variable: String? = null,
): InputValidatorBase = InputValidatorBase(
    InputValidatorBase.Properties(
        allowEmpty = valueOrNull(allowEmpty),
        labelId = valueOrNull(labelId),
        variable = valueOrNull(variable),
    )
)

/**
 * @param allowEmpty Determines whether the empty field value is valid.
 * @param labelId ID of the text element containing the error message. The message will also be used for providing access.
 * @param variable The name of the variable that stores the calculation results.
 */
@Generated
fun DivScope.inputValidatorBaseProps(
    `use named arguments`: Guard = Guard.instance,
    allowEmpty: Boolean? = null,
    labelId: String? = null,
    variable: String? = null,
) = InputValidatorBase.Properties(
    allowEmpty = valueOrNull(allowEmpty),
    labelId = valueOrNull(labelId),
    variable = valueOrNull(variable),
)

/**
 * @param allowEmpty Determines whether the empty field value is valid.
 * @param labelId ID of the text element containing the error message. The message will also be used for providing access.
 * @param variable The name of the variable that stores the calculation results.
 */
@Generated
fun TemplateScope.inputValidatorBaseRefs(
    `use named arguments`: Guard = Guard.instance,
    allowEmpty: ReferenceProperty<Boolean>? = null,
    labelId: ReferenceProperty<String>? = null,
    variable: ReferenceProperty<String>? = null,
) = InputValidatorBase.Properties(
    allowEmpty = allowEmpty,
    labelId = labelId,
    variable = variable,
)

/**
 * @param allowEmpty Determines whether the empty field value is valid.
 * @param labelId ID of the text element containing the error message. The message will also be used for providing access.
 * @param variable The name of the variable that stores the calculation results.
 */
@Generated
fun InputValidatorBase.override(
    `use named arguments`: Guard = Guard.instance,
    allowEmpty: Boolean? = null,
    labelId: String? = null,
    variable: String? = null,
): InputValidatorBase = InputValidatorBase(
    InputValidatorBase.Properties(
        allowEmpty = valueOrNull(allowEmpty) ?: properties.allowEmpty,
        labelId = valueOrNull(labelId) ?: properties.labelId,
        variable = valueOrNull(variable) ?: properties.variable,
    )
)

/**
 * @param allowEmpty Determines whether the empty field value is valid.
 * @param labelId ID of the text element containing the error message. The message will also be used for providing access.
 * @param variable The name of the variable that stores the calculation results.
 */
@Generated
fun InputValidatorBase.defer(
    `use named arguments`: Guard = Guard.instance,
    allowEmpty: ReferenceProperty<Boolean>? = null,
    labelId: ReferenceProperty<String>? = null,
    variable: ReferenceProperty<String>? = null,
): InputValidatorBase = InputValidatorBase(
    InputValidatorBase.Properties(
        allowEmpty = allowEmpty ?: properties.allowEmpty,
        labelId = labelId ?: properties.labelId,
        variable = variable ?: properties.variable,
    )
)

/**
 * @param allowEmpty Determines whether the empty field value is valid.
 * @param labelId ID of the text element containing the error message. The message will also be used for providing access.
 */
@Generated
fun InputValidatorBase.evaluate(
    `use named arguments`: Guard = Guard.instance,
    allowEmpty: ExpressionProperty<Boolean>? = null,
    labelId: ExpressionProperty<String>? = null,
): InputValidatorBase = InputValidatorBase(
    InputValidatorBase.Properties(
        allowEmpty = allowEmpty ?: properties.allowEmpty,
        labelId = labelId ?: properties.labelId,
        variable = properties.variable,
    )
)

@Generated
fun InputValidatorBase.asList() = listOf(this)
