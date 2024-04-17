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
 * Fixed coordinates of the rotation axis.
 * 
 * Can be created using the method [pivotFixed].
 */
@Generated
class PivotFixed internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Pivot {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "pivot-fixed")
    )

    operator fun plus(additive: Properties): PivotFixed = PivotFixed(
        Properties(
            unit = additive.unit ?: properties.unit,
            value = additive.value ?: properties.value,
        )
    )

    class Properties internal constructor(
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
