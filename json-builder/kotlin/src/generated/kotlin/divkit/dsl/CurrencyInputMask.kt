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
 * Mask for entering currency in the specified regional format.
 * 
 * Can be created using the method [currencyInputMask].
 * 
 * Required parameters: `type, raw_text_variable`.
 */
@Generated
data class CurrencyInputMask internal constructor(
    @JsonIgnore
    val properties: Properties,
) : InputMask {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "currency")
    )

    operator fun plus(additive: Properties): CurrencyInputMask = CurrencyInputMask(
        Properties(
            locale = additive.locale ?: properties.locale,
            rawTextVariable = additive.rawTextVariable ?: properties.rawTextVariable,
        )
    )

    data class Properties internal constructor(
        /**
         * Language tag that the currency format should match, as per [IETF BCP 47](https://en.wikipedia.org/wiki/IETF_language_tag). If the language is not set, it is defined automatically.
         */
        val locale: Property<String>?,
        /**
         * Name of the variable to store the unprocessed value.
         */
        val rawTextVariable: Property<String>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("locale", locale)
            result.tryPutProperty("raw_text_variable", rawTextVariable)
            return result
        }
    }
}

/**
 * @param locale Language tag that the currency format should match, as per [IETF BCP 47](https://en.wikipedia.org/wiki/IETF_language_tag). If the language is not set, it is defined automatically.
 * @param rawTextVariable Name of the variable to store the unprocessed value.
 */
@Generated
fun DivScope.currencyInputMask(
    `use named arguments`: Guard = Guard.instance,
    locale: String? = null,
    rawTextVariable: String? = null,
): CurrencyInputMask = CurrencyInputMask(
    CurrencyInputMask.Properties(
        locale = valueOrNull(locale),
        rawTextVariable = valueOrNull(rawTextVariable),
    )
)

/**
 * @param locale Language tag that the currency format should match, as per [IETF BCP 47](https://en.wikipedia.org/wiki/IETF_language_tag). If the language is not set, it is defined automatically.
 * @param rawTextVariable Name of the variable to store the unprocessed value.
 */
@Generated
fun DivScope.currencyInputMaskProps(
    `use named arguments`: Guard = Guard.instance,
    locale: String? = null,
    rawTextVariable: String? = null,
) = CurrencyInputMask.Properties(
    locale = valueOrNull(locale),
    rawTextVariable = valueOrNull(rawTextVariable),
)

/**
 * @param locale Language tag that the currency format should match, as per [IETF BCP 47](https://en.wikipedia.org/wiki/IETF_language_tag). If the language is not set, it is defined automatically.
 * @param rawTextVariable Name of the variable to store the unprocessed value.
 */
@Generated
fun TemplateScope.currencyInputMaskRefs(
    `use named arguments`: Guard = Guard.instance,
    locale: ReferenceProperty<String>? = null,
    rawTextVariable: ReferenceProperty<String>? = null,
) = CurrencyInputMask.Properties(
    locale = locale,
    rawTextVariable = rawTextVariable,
)

/**
 * @param locale Language tag that the currency format should match, as per [IETF BCP 47](https://en.wikipedia.org/wiki/IETF_language_tag). If the language is not set, it is defined automatically.
 * @param rawTextVariable Name of the variable to store the unprocessed value.
 */
@Generated
fun CurrencyInputMask.override(
    `use named arguments`: Guard = Guard.instance,
    locale: String? = null,
    rawTextVariable: String? = null,
): CurrencyInputMask = CurrencyInputMask(
    CurrencyInputMask.Properties(
        locale = valueOrNull(locale) ?: properties.locale,
        rawTextVariable = valueOrNull(rawTextVariable) ?: properties.rawTextVariable,
    )
)

/**
 * @param locale Language tag that the currency format should match, as per [IETF BCP 47](https://en.wikipedia.org/wiki/IETF_language_tag). If the language is not set, it is defined automatically.
 * @param rawTextVariable Name of the variable to store the unprocessed value.
 */
@Generated
fun CurrencyInputMask.defer(
    `use named arguments`: Guard = Guard.instance,
    locale: ReferenceProperty<String>? = null,
    rawTextVariable: ReferenceProperty<String>? = null,
): CurrencyInputMask = CurrencyInputMask(
    CurrencyInputMask.Properties(
        locale = locale ?: properties.locale,
        rawTextVariable = rawTextVariable ?: properties.rawTextVariable,
    )
)

/**
 * @param locale Language tag that the currency format should match, as per [IETF BCP 47](https://en.wikipedia.org/wiki/IETF_language_tag). If the language is not set, it is defined automatically.
 */
@Generated
fun CurrencyInputMask.evaluate(
    `use named arguments`: Guard = Guard.instance,
    locale: ExpressionProperty<String>? = null,
): CurrencyInputMask = CurrencyInputMask(
    CurrencyInputMask.Properties(
        locale = locale ?: properties.locale,
        rawTextVariable = properties.rawTextVariable,
    )
)

@Generated
fun CurrencyInputMask.asList() = listOf(this)
