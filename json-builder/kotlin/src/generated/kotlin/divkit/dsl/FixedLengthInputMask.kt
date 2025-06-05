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
 * Mask for entering text with a fixed number of characters.
 * 
 * Can be created using the method [fixedLengthInputMask].
 * 
 * Required parameters: `type, raw_text_variable, pattern_elements, pattern`.
 */
@Generated
data class FixedLengthInputMask internal constructor(
    @JsonIgnore
    val properties: Properties,
) : InputMask {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "fixed_length")
    )

    operator fun plus(additive: Properties): FixedLengthInputMask = FixedLengthInputMask(
        Properties(
            alwaysVisible = additive.alwaysVisible ?: properties.alwaysVisible,
            pattern = additive.pattern ?: properties.pattern,
            patternElements = additive.patternElements ?: properties.patternElements,
            rawTextVariable = additive.rawTextVariable ?: properties.rawTextVariable,
        )
    )

    data class Properties internal constructor(
        /**
         * If this option is enabled, the text field contains the mask before being filled in.
         * Default value: `false`.
         */
        val alwaysVisible: Property<Boolean>?,
        /**
         * String that sets the text input template. For example, the `+7 (###) ###-##-## ` template for a phone number.
         */
        val pattern: Property<String>?,
        /**
         * Template decoding is a description of the characters that will be replaced with user input.
         */
        val patternElements: Property<List<PatternElement>>?,
        /**
         * Name of the variable to store the unprocessed value.
         */
        val rawTextVariable: Property<String>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("always_visible", alwaysVisible)
            result.tryPutProperty("pattern", pattern)
            result.tryPutProperty("pattern_elements", patternElements)
            result.tryPutProperty("raw_text_variable", rawTextVariable)
            return result
        }
    }

    /**
     * Template decoding is a description of the characters that will be replaced with user input.
     * 
     * Can be created using the method [fixedLengthInputMaskPatternElement].
     * 
     * Required parameters: `key`.
     */
    @Generated
    data class PatternElement internal constructor(
        @JsonIgnore
        val properties: Properties,
    ) {
        @JsonAnyGetter
        internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

        operator fun plus(additive: Properties): PatternElement = PatternElement(
            Properties(
                key = additive.key ?: properties.key,
                placeholder = additive.placeholder ?: properties.placeholder,
                regex = additive.regex ?: properties.regex,
            )
        )

        data class Properties internal constructor(
            /**
             * A character in the template that will be replaced with a user-defined character.
             */
            val key: Property<String>?,
            /**
             * The character that's displayed in the input field where the user is expected to enter text. This is used if mask display is enabled.
             * Default value: `_`.
             */
            val placeholder: Property<String>?,
            /**
             * Regular expression for validating character inputs. For example, when a mask is digit-only.
             */
            val regex: Property<String>?,
        ) {
            internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
                val result = mutableMapOf<String, Any>()
                result.putAll(properties)
                result.tryPutProperty("key", key)
                result.tryPutProperty("placeholder", placeholder)
                result.tryPutProperty("regex", regex)
                return result
            }
        }
    }

}

/**
 * @param alwaysVisible If this option is enabled, the text field contains the mask before being filled in.
 * @param pattern String that sets the text input template. For example, the `+7 (###) ###-##-## ` template for a phone number.
 * @param patternElements Template decoding is a description of the characters that will be replaced with user input.
 * @param rawTextVariable Name of the variable to store the unprocessed value.
 */
@Generated
fun DivScope.fixedLengthInputMask(
    `use named arguments`: Guard = Guard.instance,
    alwaysVisible: Boolean? = null,
    pattern: String? = null,
    patternElements: List<FixedLengthInputMask.PatternElement>? = null,
    rawTextVariable: String? = null,
): FixedLengthInputMask = FixedLengthInputMask(
    FixedLengthInputMask.Properties(
        alwaysVisible = valueOrNull(alwaysVisible),
        pattern = valueOrNull(pattern),
        patternElements = valueOrNull(patternElements),
        rawTextVariable = valueOrNull(rawTextVariable),
    )
)

/**
 * @param alwaysVisible If this option is enabled, the text field contains the mask before being filled in.
 * @param pattern String that sets the text input template. For example, the `+7 (###) ###-##-## ` template for a phone number.
 * @param patternElements Template decoding is a description of the characters that will be replaced with user input.
 * @param rawTextVariable Name of the variable to store the unprocessed value.
 */
@Generated
fun DivScope.fixedLengthInputMaskProps(
    `use named arguments`: Guard = Guard.instance,
    alwaysVisible: Boolean? = null,
    pattern: String? = null,
    patternElements: List<FixedLengthInputMask.PatternElement>? = null,
    rawTextVariable: String? = null,
) = FixedLengthInputMask.Properties(
    alwaysVisible = valueOrNull(alwaysVisible),
    pattern = valueOrNull(pattern),
    patternElements = valueOrNull(patternElements),
    rawTextVariable = valueOrNull(rawTextVariable),
)

/**
 * @param alwaysVisible If this option is enabled, the text field contains the mask before being filled in.
 * @param pattern String that sets the text input template. For example, the `+7 (###) ###-##-## ` template for a phone number.
 * @param patternElements Template decoding is a description of the characters that will be replaced with user input.
 * @param rawTextVariable Name of the variable to store the unprocessed value.
 */
@Generated
fun TemplateScope.fixedLengthInputMaskRefs(
    `use named arguments`: Guard = Guard.instance,
    alwaysVisible: ReferenceProperty<Boolean>? = null,
    pattern: ReferenceProperty<String>? = null,
    patternElements: ReferenceProperty<List<FixedLengthInputMask.PatternElement>>? = null,
    rawTextVariable: ReferenceProperty<String>? = null,
) = FixedLengthInputMask.Properties(
    alwaysVisible = alwaysVisible,
    pattern = pattern,
    patternElements = patternElements,
    rawTextVariable = rawTextVariable,
)

/**
 * @param alwaysVisible If this option is enabled, the text field contains the mask before being filled in.
 * @param pattern String that sets the text input template. For example, the `+7 (###) ###-##-## ` template for a phone number.
 * @param patternElements Template decoding is a description of the characters that will be replaced with user input.
 * @param rawTextVariable Name of the variable to store the unprocessed value.
 */
@Generated
fun FixedLengthInputMask.override(
    `use named arguments`: Guard = Guard.instance,
    alwaysVisible: Boolean? = null,
    pattern: String? = null,
    patternElements: List<FixedLengthInputMask.PatternElement>? = null,
    rawTextVariable: String? = null,
): FixedLengthInputMask = FixedLengthInputMask(
    FixedLengthInputMask.Properties(
        alwaysVisible = valueOrNull(alwaysVisible) ?: properties.alwaysVisible,
        pattern = valueOrNull(pattern) ?: properties.pattern,
        patternElements = valueOrNull(patternElements) ?: properties.patternElements,
        rawTextVariable = valueOrNull(rawTextVariable) ?: properties.rawTextVariable,
    )
)

/**
 * @param alwaysVisible If this option is enabled, the text field contains the mask before being filled in.
 * @param pattern String that sets the text input template. For example, the `+7 (###) ###-##-## ` template for a phone number.
 * @param patternElements Template decoding is a description of the characters that will be replaced with user input.
 * @param rawTextVariable Name of the variable to store the unprocessed value.
 */
@Generated
fun FixedLengthInputMask.defer(
    `use named arguments`: Guard = Guard.instance,
    alwaysVisible: ReferenceProperty<Boolean>? = null,
    pattern: ReferenceProperty<String>? = null,
    patternElements: ReferenceProperty<List<FixedLengthInputMask.PatternElement>>? = null,
    rawTextVariable: ReferenceProperty<String>? = null,
): FixedLengthInputMask = FixedLengthInputMask(
    FixedLengthInputMask.Properties(
        alwaysVisible = alwaysVisible ?: properties.alwaysVisible,
        pattern = pattern ?: properties.pattern,
        patternElements = patternElements ?: properties.patternElements,
        rawTextVariable = rawTextVariable ?: properties.rawTextVariable,
    )
)

/**
 * @param alwaysVisible If this option is enabled, the text field contains the mask before being filled in.
 * @param pattern String that sets the text input template. For example, the `+7 (###) ###-##-## ` template for a phone number.
 * @param patternElements Template decoding is a description of the characters that will be replaced with user input.
 * @param rawTextVariable Name of the variable to store the unprocessed value.
 */
@Generated
fun FixedLengthInputMask.modify(
    `use named arguments`: Guard = Guard.instance,
    alwaysVisible: Property<Boolean>? = null,
    pattern: Property<String>? = null,
    patternElements: Property<List<FixedLengthInputMask.PatternElement>>? = null,
    rawTextVariable: Property<String>? = null,
): FixedLengthInputMask = FixedLengthInputMask(
    FixedLengthInputMask.Properties(
        alwaysVisible = alwaysVisible ?: properties.alwaysVisible,
        pattern = pattern ?: properties.pattern,
        patternElements = patternElements ?: properties.patternElements,
        rawTextVariable = rawTextVariable ?: properties.rawTextVariable,
    )
)

/**
 * @param alwaysVisible If this option is enabled, the text field contains the mask before being filled in.
 * @param pattern String that sets the text input template. For example, the `+7 (###) ###-##-## ` template for a phone number.
 */
@Generated
fun FixedLengthInputMask.evaluate(
    `use named arguments`: Guard = Guard.instance,
    alwaysVisible: ExpressionProperty<Boolean>? = null,
    pattern: ExpressionProperty<String>? = null,
): FixedLengthInputMask = FixedLengthInputMask(
    FixedLengthInputMask.Properties(
        alwaysVisible = alwaysVisible ?: properties.alwaysVisible,
        pattern = pattern ?: properties.pattern,
        patternElements = properties.patternElements,
        rawTextVariable = properties.rawTextVariable,
    )
)

@Generated
fun FixedLengthInputMask.asList() = listOf(this)

/**
 * @param key A character in the template that will be replaced with a user-defined character.
 * @param placeholder The character that's displayed in the input field where the user is expected to enter text. This is used if mask display is enabled.
 * @param regex Regular expression for validating character inputs. For example, when a mask is digit-only.
 */
@Generated
fun DivScope.fixedLengthInputMaskPatternElement(
    `use named arguments`: Guard = Guard.instance,
    key: String? = null,
    placeholder: String? = null,
    regex: String? = null,
): FixedLengthInputMask.PatternElement = FixedLengthInputMask.PatternElement(
    FixedLengthInputMask.PatternElement.Properties(
        key = valueOrNull(key),
        placeholder = valueOrNull(placeholder),
        regex = valueOrNull(regex),
    )
)

/**
 * @param key A character in the template that will be replaced with a user-defined character.
 * @param placeholder The character that's displayed in the input field where the user is expected to enter text. This is used if mask display is enabled.
 * @param regex Regular expression for validating character inputs. For example, when a mask is digit-only.
 */
@Generated
fun DivScope.fixedLengthInputMaskPatternElementProps(
    `use named arguments`: Guard = Guard.instance,
    key: String? = null,
    placeholder: String? = null,
    regex: String? = null,
) = FixedLengthInputMask.PatternElement.Properties(
    key = valueOrNull(key),
    placeholder = valueOrNull(placeholder),
    regex = valueOrNull(regex),
)

/**
 * @param key A character in the template that will be replaced with a user-defined character.
 * @param placeholder The character that's displayed in the input field where the user is expected to enter text. This is used if mask display is enabled.
 * @param regex Regular expression for validating character inputs. For example, when a mask is digit-only.
 */
@Generated
fun TemplateScope.fixedLengthInputMaskPatternElementRefs(
    `use named arguments`: Guard = Guard.instance,
    key: ReferenceProperty<String>? = null,
    placeholder: ReferenceProperty<String>? = null,
    regex: ReferenceProperty<String>? = null,
) = FixedLengthInputMask.PatternElement.Properties(
    key = key,
    placeholder = placeholder,
    regex = regex,
)

/**
 * @param key A character in the template that will be replaced with a user-defined character.
 * @param placeholder The character that's displayed in the input field where the user is expected to enter text. This is used if mask display is enabled.
 * @param regex Regular expression for validating character inputs. For example, when a mask is digit-only.
 */
@Generated
fun FixedLengthInputMask.PatternElement.override(
    `use named arguments`: Guard = Guard.instance,
    key: String? = null,
    placeholder: String? = null,
    regex: String? = null,
): FixedLengthInputMask.PatternElement = FixedLengthInputMask.PatternElement(
    FixedLengthInputMask.PatternElement.Properties(
        key = valueOrNull(key) ?: properties.key,
        placeholder = valueOrNull(placeholder) ?: properties.placeholder,
        regex = valueOrNull(regex) ?: properties.regex,
    )
)

/**
 * @param key A character in the template that will be replaced with a user-defined character.
 * @param placeholder The character that's displayed in the input field where the user is expected to enter text. This is used if mask display is enabled.
 * @param regex Regular expression for validating character inputs. For example, when a mask is digit-only.
 */
@Generated
fun FixedLengthInputMask.PatternElement.defer(
    `use named arguments`: Guard = Guard.instance,
    key: ReferenceProperty<String>? = null,
    placeholder: ReferenceProperty<String>? = null,
    regex: ReferenceProperty<String>? = null,
): FixedLengthInputMask.PatternElement = FixedLengthInputMask.PatternElement(
    FixedLengthInputMask.PatternElement.Properties(
        key = key ?: properties.key,
        placeholder = placeholder ?: properties.placeholder,
        regex = regex ?: properties.regex,
    )
)

/**
 * @param key A character in the template that will be replaced with a user-defined character.
 * @param placeholder The character that's displayed in the input field where the user is expected to enter text. This is used if mask display is enabled.
 * @param regex Regular expression for validating character inputs. For example, when a mask is digit-only.
 */
@Generated
fun FixedLengthInputMask.PatternElement.modify(
    `use named arguments`: Guard = Guard.instance,
    key: Property<String>? = null,
    placeholder: Property<String>? = null,
    regex: Property<String>? = null,
): FixedLengthInputMask.PatternElement = FixedLengthInputMask.PatternElement(
    FixedLengthInputMask.PatternElement.Properties(
        key = key ?: properties.key,
        placeholder = placeholder ?: properties.placeholder,
        regex = regex ?: properties.regex,
    )
)

/**
 * @param key A character in the template that will be replaced with a user-defined character.
 * @param placeholder The character that's displayed in the input field where the user is expected to enter text. This is used if mask display is enabled.
 * @param regex Regular expression for validating character inputs. For example, when a mask is digit-only.
 */
@Generated
fun FixedLengthInputMask.PatternElement.evaluate(
    `use named arguments`: Guard = Guard.instance,
    key: ExpressionProperty<String>? = null,
    placeholder: ExpressionProperty<String>? = null,
    regex: ExpressionProperty<String>? = null,
): FixedLengthInputMask.PatternElement = FixedLengthInputMask.PatternElement(
    FixedLengthInputMask.PatternElement.Properties(
        key = key ?: properties.key,
        placeholder = placeholder ?: properties.placeholder,
        regex = regex ?: properties.regex,
    )
)

@Generated
fun FixedLengthInputMask.PatternElement.asList() = listOf(this)
