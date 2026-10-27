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
 * Fixed coordinates of the rotation axis.
 * 
 * Can be created using the method [pivotFixed].
 * 
 * Required parameters: `value, type`.
 */
@Generated
@JsonSerialize(using = PivotFixedSerializer::class)
@ExposedCopyVisibility
data class PivotFixed internal constructor(
    val properties: Properties,
) : Pivot {
    operator fun plus(additive: Properties): PivotFixed = PivotFixed(
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
fun DivScope.pivotFixed(
    `use named arguments`: Guard = Guard.instance,
    unit: SizeUnit? = null,
    value: Int? = null,
): PivotFixed = PivotFixed(
    PivotFixed.Properties(
        unit = valueOrNull(unit),
        value = valueOrNull(value),
    )
)

/**
 * @param unit Measurement unit. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param value Coordinate value.
 */
@Generated
fun DivScope.pivotFixedProps(
    `use named arguments`: Guard = Guard.instance,
    unit: SizeUnit? = null,
    value: Int? = null,
) = PivotFixed.Properties(
    unit = valueOrNull(unit),
    value = valueOrNull(value),
)

/**
 * @param unit Measurement unit. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param value Coordinate value.
 */
@Generated
fun TemplateScope.pivotFixedRefs(
    `use named arguments`: Guard = Guard.instance,
    unit: ReferenceProperty<SizeUnit>? = null,
    value: ReferenceProperty<Int>? = null,
) = PivotFixed.Properties(
    unit = unit,
    value = value,
)

/**
 * @param unit Measurement unit. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param value Coordinate value.
 */
@Generated
fun PivotFixed.override(
    `use named arguments`: Guard = Guard.instance,
    unit: SizeUnit? = null,
    value: Int? = null,
): PivotFixed = PivotFixed(
    PivotFixed.Properties(
        unit = valueOrNull(unit) ?: properties.unit,
        value = valueOrNull(value) ?: properties.value,
    )
)

/**
 * @param unit Measurement unit. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param value Coordinate value.
 */
@Generated
fun PivotFixed.defer(
    `use named arguments`: Guard = Guard.instance,
    unit: ReferenceProperty<SizeUnit>? = null,
    value: ReferenceProperty<Int>? = null,
): PivotFixed = PivotFixed(
    PivotFixed.Properties(
        unit = unit ?: properties.unit,
        value = value ?: properties.value,
    )
)

/**
 * @param unit Measurement unit. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param value Coordinate value.
 */
@Generated
fun PivotFixed.modify(
    `use named arguments`: Guard = Guard.instance,
    unit: Property<SizeUnit>? = null,
    value: Property<Int>? = null,
): PivotFixed = PivotFixed(
    PivotFixed.Properties(
        unit = unit ?: properties.unit,
        value = value ?: properties.value,
    )
)

/**
 * @param unit Measurement unit. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param value Coordinate value.
 */
@Generated
fun PivotFixed.evaluate(
    `use named arguments`: Guard = Guard.instance,
    unit: ExpressionProperty<SizeUnit>? = null,
    value: ExpressionProperty<Int>? = null,
): PivotFixed = PivotFixed(
    PivotFixed.Properties(
        unit = unit ?: properties.unit,
        value = value ?: properties.value,
    )
)

@Generated
fun PivotFixed.asList() = listOf(this)
