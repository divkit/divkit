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
 * Keyboard input method.
 * 
 * Can be created using the method [keyboardInput].
 * 
 * Required parameters: `type`.
 */
@Generated
class KeyboardInput internal constructor(
    @JsonIgnore
    val properties: Properties,
) : InputMethod {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "keyboard")
    )

    operator fun plus(additive: Properties): KeyboardInput = KeyboardInput(
        Properties(
            keyboardType = additive.keyboardType ?: properties.keyboardType,
        )
    )

    class Properties internal constructor(
        /**
         * Keyboard type.
         * Default value: `multi_line_text`.
         */
        val keyboardType: Property<KeyboardType>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("keyboard_type", keyboardType)
            return result
        }
    }

    /**
     * Keyboard type.
     * 
     * Possible values: [single_line_text, multi_line_text, phone, number, email, uri, decimal].
     */
    @Generated
    sealed interface KeyboardType
}

/**
 * @param keyboardType Keyboard type.
 */
@Generated
fun DivScope.keyboardInput(
    `use named arguments`: Guard = Guard.instance,
    keyboardType: KeyboardInput.KeyboardType? = null,
): KeyboardInput = KeyboardInput(
    KeyboardInput.Properties(
        keyboardType = valueOrNull(keyboardType),
    )
)

/**
 * @param keyboardType Keyboard type.
 */
@Generated
fun DivScope.keyboardInputProps(
    `use named arguments`: Guard = Guard.instance,
    keyboardType: KeyboardInput.KeyboardType? = null,
) = KeyboardInput.Properties(
    keyboardType = valueOrNull(keyboardType),
)

/**
 * @param keyboardType Keyboard type.
 */
@Generated
fun TemplateScope.keyboardInputRefs(
    `use named arguments`: Guard = Guard.instance,
    keyboardType: ReferenceProperty<KeyboardInput.KeyboardType>? = null,
) = KeyboardInput.Properties(
    keyboardType = keyboardType,
)

/**
 * @param keyboardType Keyboard type.
 */
@Generated
fun KeyboardInput.override(
    `use named arguments`: Guard = Guard.instance,
    keyboardType: KeyboardInput.KeyboardType? = null,
): KeyboardInput = KeyboardInput(
    KeyboardInput.Properties(
        keyboardType = valueOrNull(keyboardType) ?: properties.keyboardType,
    )
)

/**
 * @param keyboardType Keyboard type.
 */
@Generated
fun KeyboardInput.defer(
    `use named arguments`: Guard = Guard.instance,
    keyboardType: ReferenceProperty<KeyboardInput.KeyboardType>? = null,
): KeyboardInput = KeyboardInput(
    KeyboardInput.Properties(
        keyboardType = keyboardType ?: properties.keyboardType,
    )
)

/**
 * @param keyboardType Keyboard type.
 */
@Generated
fun KeyboardInput.evaluate(
    `use named arguments`: Guard = Guard.instance,
    keyboardType: ExpressionProperty<KeyboardInput.KeyboardType>? = null,
): KeyboardInput = KeyboardInput(
    KeyboardInput.Properties(
        keyboardType = keyboardType ?: properties.keyboardType,
    )
)

@Generated
fun KeyboardInput.asList() = listOf(this)

@Generated
fun KeyboardInput.KeyboardType.asList() = listOf(this)
