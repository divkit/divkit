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
 * Fixed coordinates of the translation.
 * 
 * Can be created using the method [fixedTranslation].
 * 
 * Required parameters: `value, type`.
 */
@Generated
@ExposedCopyVisibility
data class FixedTranslation internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Translation {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "translation-fixed")
    )

    operator fun plus(additive: Properties): FixedTranslation = FixedTranslation(
        Properties(
            unit = additive.unit ?: properties.unit,
            value = additive.value ?: properties.value,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Measurement unit. To learn more about units of size measurement, see [Layout inside the card](../../layout).
         * Default value: `dp`.
         */
        val unit: Property<SizeUnit>?,
        /**
         * Coordinate value.
         */
        val value: Property<Int>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("unit", unit)
            result.tryPutProperty("value", value)
            return result
        }
    }
}

/**
 * @param unit Measurement unit. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param value Coordinate value.
 */
@Generated
fun DivScope.fixedTranslation(
    `use named arguments`: Guard = Guard.instance,
    unit: SizeUnit? = null,
    value: Int? = null,
): FixedTranslation = FixedTranslation(
    FixedTranslation.Properties(
        unit = valueOrNull(unit),
        value = valueOrNull(value),
    )
)

/**
 * @param unit Measurement unit. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param value Coordinate value.
 */
@Generated
fun DivScope.fixedTranslationProps(
    `use named arguments`: Guard = Guard.instance,
    unit: SizeUnit? = null,
    value: Int? = null,
) = FixedTranslation.Properties(
    unit = valueOrNull(unit),
    value = valueOrNull(value),
)

/**
 * @param unit Measurement unit. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param value Coordinate value.
 */
@Generated
fun TemplateScope.fixedTranslationRefs(
    `use named arguments`: Guard = Guard.instance,
    unit: ReferenceProperty<SizeUnit>? = null,
    value: ReferenceProperty<Int>? = null,
) = FixedTranslation.Properties(
    unit = unit,
    value = value,
)

/**
 * @param unit Measurement unit. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param value Coordinate value.
 */
@Generated
fun FixedTranslation.override(
    `use named arguments`: Guard = Guard.instance,
    unit: SizeUnit? = null,
    value: Int? = null,
): FixedTranslation = FixedTranslation(
    FixedTranslation.Properties(
        unit = valueOrNull(unit) ?: properties.unit,
        value = valueOrNull(value) ?: properties.value,
    )
)

/**
 * @param unit Measurement unit. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param value Coordinate value.
 */
@Generated
fun FixedTranslation.defer(
    `use named arguments`: Guard = Guard.instance,
    unit: ReferenceProperty<SizeUnit>? = null,
    value: ReferenceProperty<Int>? = null,
): FixedTranslation = FixedTranslation(
    FixedTranslation.Properties(
        unit = unit ?: properties.unit,
        value = value ?: properties.value,
    )
)

/**
 * @param unit Measurement unit. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param value Coordinate value.
 */
@Generated
fun FixedTranslation.modify(
    `use named arguments`: Guard = Guard.instance,
    unit: Property<SizeUnit>? = null,
    value: Property<Int>? = null,
): FixedTranslation = FixedTranslation(
    FixedTranslation.Properties(
        unit = unit ?: properties.unit,
        value = value ?: properties.value,
    )
)

/**
 * @param unit Measurement unit. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param value Coordinate value.
 */
@Generated
fun FixedTranslation.evaluate(
    `use named arguments`: Guard = Guard.instance,
    unit: ExpressionProperty<SizeUnit>? = null,
    value: ExpressionProperty<Int>? = null,
): FixedTranslation = FixedTranslation(
    FixedTranslation.Properties(
        unit = unit ?: properties.unit,
        value = value ?: properties.value,
    )
)

@Generated
fun FixedTranslation.asList() = listOf(this)
