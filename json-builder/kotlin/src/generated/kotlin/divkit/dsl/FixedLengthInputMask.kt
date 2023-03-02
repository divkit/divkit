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
 * Text input mask with a fixed amount of characters.
 * 
 * Can be created using the method [fixedLengthInputMask].
 * 
 * Required properties: `type, pattern_elements, pattern`.
 */
@Generated
class FixedLengthInputMask internal constructor(
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
        )
    )

    class Properties internal constructor(
        /**
         * Option to show mask before completion.
         * Default value: `false`.
         */
        val alwaysVisible: Property<Boolean>?,
        /**
         * Text input mask's format.
         */
        val pattern: Property<String>?,
        /**
         * Decoding of characters from pattern that are replaceable with user input.
         */
        val patternElements: Property<List<PatternElement>>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("always_visible", alwaysVisible)
            result.tryPutProperty("pattern", pattern)
            result.tryPutProperty("pattern_elements", patternElements)
            return result
        }
    }

    /**
     * Decoding of characters from pattern that are replaceable with user input.
     * 
     * Can be created using the method [fixedLengthInputMaskPatternElement].
     * 
     * Required properties: `key`.
     */
    @Generated
    class PatternElement internal constructor(
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

        class Properties internal constructor(
            /**
             * Special character replaceable with user input.
             */
            val key: Property<String>?,
            /**
             * Character that is shown in the place of the special character if mask should be always visible.
             * Default value: `_`.
             */
            val placeholder: Property<String>?,
            /**
             * Regular expression with which the character will be validated.
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
 * @param alwaysVisible Option to show mask before completion.
 * @param pattern Text input mask's format.
 * @param patternElements Decoding of characters from pattern that are replaceable with user input.
 */
@Generated
fun DivScope.fixedLengthInputMask(
    `use named arguments`: Guard = Guard.instance,
    alwaysVisible: Boolean? = null,
    pattern: String,
    patternElements: List<FixedLengthInputMask.PatternElement>,
): FixedLengthInputMask = FixedLengthInputMask(
    FixedLengthInputMask.Properties(
        alwaysVisible = valueOrNull(alwaysVisible),
        pattern = valueOrNull(pattern),
        patternElements = valueOrNull(patternElements),
    )
)

/**
 * @param alwaysVisible Option to show mask before completion.
 * @param pattern Text input mask's format.
 * @param patternElements Decoding of characters from pattern that are replaceable with user input.
 */
@Generated
fun DivScope.fixedLengthInputMaskProps(
    `use named arguments`: Guard = Guard.instance,
    alwaysVisible: Boolean? = null,
    pattern: String? = null,
    patternElements: List<FixedLengthInputMask.PatternElement>? = null,
) = FixedLengthInputMask.Properties(
    alwaysVisible = valueOrNull(alwaysVisible),
    pattern = valueOrNull(pattern),
    patternElements = valueOrNull(patternElements),
)

/**
 * @param alwaysVisible Option to show mask before completion.
 * @param pattern Text input mask's format.
 * @param patternElements Decoding of characters from pattern that are replaceable with user input.
 */
@Generated
fun TemplateScope.fixedLengthInputMaskRefs(
    `use named arguments`: Guard = Guard.instance,
    alwaysVisible: ReferenceProperty<Boolean>? = null,
    pattern: ReferenceProperty<String>? = null,
    patternElements: ReferenceProperty<List<FixedLengthInputMask.PatternElement>>? = null,
) = FixedLengthInputMask.Properties(
    alwaysVisible = alwaysVisible,
    pattern = pattern,
    patternElements = patternElements,
)

/**
 * @param alwaysVisible Option to show mask before completion.
 * @param pattern Text input mask's format.
 * @param patternElements Decoding of characters from pattern that are replaceable with user input.
 */
@Generated
fun FixedLengthInputMask.override(
    `use named arguments`: Guard = Guard.instance,
    alwaysVisible: Boolean? = null,
    pattern: String? = null,
    patternElements: List<FixedLengthInputMask.PatternElement>? = null,
): FixedLengthInputMask = FixedLengthInputMask(
    FixedLengthInputMask.Properties(
        alwaysVisible = valueOrNull(alwaysVisible) ?: properties.alwaysVisible,
        pattern = valueOrNull(pattern) ?: properties.pattern,
        patternElements = valueOrNull(patternElements) ?: properties.patternElements,
    )
)

/**
 * @param alwaysVisible Option to show mask before completion.
 * @param pattern Text input mask's format.
 * @param patternElements Decoding of characters from pattern that are replaceable with user input.
 */
@Generated
fun FixedLengthInputMask.defer(
    `use named arguments`: Guard = Guard.instance,
    alwaysVisible: ReferenceProperty<Boolean>? = null,
    pattern: ReferenceProperty<String>? = null,
    patternElements: ReferenceProperty<List<FixedLengthInputMask.PatternElement>>? = null,
): FixedLengthInputMask = FixedLengthInputMask(
    FixedLengthInputMask.Properties(
        alwaysVisible = alwaysVisible ?: properties.alwaysVisible,
        pattern = pattern ?: properties.pattern,
        patternElements = patternElements ?: properties.patternElements,
    )
)

/**
 * @param alwaysVisible Option to show mask before completion.
 * @param pattern Text input mask's format.
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
    )
)

@Generated
fun FixedLengthInputMask.asList() = listOf(this)

/**
 * @param key Special character replaceable with user input.
 * @param placeholder Character that is shown in the place of the special character if mask should be always visible.
 * @param regex Regular expression with which the character will be validated.
 */
@Generated
fun DivScope.fixedLengthInputMaskPatternElement(
    `use named arguments`: Guard = Guard.instance,
    key: String,
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
 * @param key Special character replaceable with user input.
 * @param placeholder Character that is shown in the place of the special character if mask should be always visible.
 * @param regex Regular expression with which the character will be validated.
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
 * @param key Special character replaceable with user input.
 * @param placeholder Character that is shown in the place of the special character if mask should be always visible.
 * @param regex Regular expression with which the character will be validated.
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
 * @param key Special character replaceable with user input.
 * @param placeholder Character that is shown in the place of the special character if mask should be always visible.
 * @param regex Regular expression with which the character will be validated.
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
 * @param key Special character replaceable with user input.
 * @param placeholder Character that is shown in the place of the special character if mask should be always visible.
 * @param regex Regular expression with which the character will be validated.
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
 * @param key Special character replaceable with user input.
 * @param placeholder Character that is shown in the place of the special character if mask should be always visible.
 * @param regex Regular expression with which the character will be validated.
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
