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
 * Location of the translation coordinates as a percentage relative to the element size.
 * 
 * Can be created using the method [percentageTranslation].
 * 
 * Required parameters: `value, type`.
 */
@Generated
@ExposedCopyVisibility
data class PercentageTranslation internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Translation {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "translation-percentage")
    )

    operator fun plus(additive: Properties): PercentageTranslation = PercentageTranslation(
        Properties(
            value = additive.value ?: properties.value,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Coordinate value as a percentage.
         */
        val value: Property<Double>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("value", value)
            return result
        }
    }
}

/**
 * @param value Coordinate value as a percentage.
 */
@Generated
fun DivScope.percentageTranslation(
    `use named arguments`: Guard = Guard.instance,
    value: Double? = null,
): PercentageTranslation = PercentageTranslation(
    PercentageTranslation.Properties(
        value = valueOrNull(value),
    )
)

/**
 * @param value Coordinate value as a percentage.
 */
@Generated
fun DivScope.percentageTranslationProps(
    `use named arguments`: Guard = Guard.instance,
    value: Double? = null,
) = PercentageTranslation.Properties(
    value = valueOrNull(value),
)

/**
 * @param value Coordinate value as a percentage.
 */
@Generated
fun TemplateScope.percentageTranslationRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Double>? = null,
) = PercentageTranslation.Properties(
    value = value,
)

/**
 * @param value Coordinate value as a percentage.
 */
@Generated
fun PercentageTranslation.override(
    `use named arguments`: Guard = Guard.instance,
    value: Double? = null,
): PercentageTranslation = PercentageTranslation(
    PercentageTranslation.Properties(
        value = valueOrNull(value) ?: properties.value,
    )
)

/**
 * @param value Coordinate value as a percentage.
 */
@Generated
fun PercentageTranslation.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Double>? = null,
): PercentageTranslation = PercentageTranslation(
    PercentageTranslation.Properties(
        value = value ?: properties.value,
    )
)

/**
 * @param value Coordinate value as a percentage.
 */
@Generated
fun PercentageTranslation.modify(
    `use named arguments`: Guard = Guard.instance,
    value: Property<Double>? = null,
): PercentageTranslation = PercentageTranslation(
    PercentageTranslation.Properties(
        value = value ?: properties.value,
    )
)

/**
 * @param value Coordinate value as a percentage.
 */
@Generated
fun PercentageTranslation.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<Double>? = null,
): PercentageTranslation = PercentageTranslation(
    PercentageTranslation.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun PercentageTranslation.asList() = listOf(this)
