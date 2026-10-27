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
 * Mask for entering phone numbers with dynamic regional format identification.
 * 
 * Can be created using the method [phoneInputMask].
 * 
 * Required parameters: `type, raw_text_variable`.
 */
@Generated
@JsonSerialize(using = PhoneInputMaskSerializer::class)
@ExposedCopyVisibility
data class PhoneInputMask internal constructor(
    val properties: Properties,
) : InputMask {
    operator fun plus(additive: Properties): PhoneInputMask = PhoneInputMask(
        Properties(
            rawTextVariable = additive.rawTextVariable ?: properties.rawTextVariable,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Name of the variable to store the unprocessed value.
         */
        val rawTextVariable: Property<String>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("raw_text_variable", rawTextVariable)
            return result
        }
    }
}

/**
 * @param rawTextVariable Name of the variable to store the unprocessed value.
 */
@Generated
fun DivScope.phoneInputMask(
    `use named arguments`: Guard = Guard.instance,
    rawTextVariable: String? = null,
): PhoneInputMask = PhoneInputMask(
    PhoneInputMask.Properties(
        rawTextVariable = valueOrNull(rawTextVariable),
    )
)

/**
 * @param rawTextVariable Name of the variable to store the unprocessed value.
 */
@Generated
fun DivScope.phoneInputMaskProps(
    `use named arguments`: Guard = Guard.instance,
    rawTextVariable: String? = null,
) = PhoneInputMask.Properties(
    rawTextVariable = valueOrNull(rawTextVariable),
)

/**
 * @param rawTextVariable Name of the variable to store the unprocessed value.
 */
@Generated
fun TemplateScope.phoneInputMaskRefs(
    `use named arguments`: Guard = Guard.instance,
    rawTextVariable: ReferenceProperty<String>? = null,
) = PhoneInputMask.Properties(
    rawTextVariable = rawTextVariable,
)

/**
 * @param rawTextVariable Name of the variable to store the unprocessed value.
 */
@Generated
fun PhoneInputMask.override(
    `use named arguments`: Guard = Guard.instance,
    rawTextVariable: String? = null,
): PhoneInputMask = PhoneInputMask(
    PhoneInputMask.Properties(
        rawTextVariable = valueOrNull(rawTextVariable) ?: properties.rawTextVariable,
    )
)

/**
 * @param rawTextVariable Name of the variable to store the unprocessed value.
 */
@Generated
fun PhoneInputMask.defer(
    `use named arguments`: Guard = Guard.instance,
    rawTextVariable: ReferenceProperty<String>? = null,
): PhoneInputMask = PhoneInputMask(
    PhoneInputMask.Properties(
        rawTextVariable = rawTextVariable ?: properties.rawTextVariable,
    )
)

/**
 * @param rawTextVariable Name of the variable to store the unprocessed value.
 */
@Generated
fun PhoneInputMask.modify(
    `use named arguments`: Guard = Guard.instance,
    rawTextVariable: Property<String>? = null,
): PhoneInputMask = PhoneInputMask(
    PhoneInputMask.Properties(
        rawTextVariable = rawTextVariable ?: properties.rawTextVariable,
    )
)

@Generated
fun PhoneInputMask.asList() = listOf(this)
