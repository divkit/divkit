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
 * Fixed coordinates of the central point of the gradient.
 * 
 * Can be created using the method [radialGradientFixedCenter].
 * 
 * Required parameters: `value, type`.
 */
@Generated
data class RadialGradientFixedCenter internal constructor(
    @JsonIgnore
    val properties: Properties,
) : RadialGradientCenter {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "fixed")
    )

    operator fun plus(additive: Properties): RadialGradientFixedCenter = RadialGradientFixedCenter(
        Properties(
            unit = additive.unit ?: properties.unit,
            value = additive.value ?: properties.value,
        )
    )

    data class Properties internal constructor(
        /**
         * Unit of measurement. To learn more about units of size measurement, see [Layout inside the card](../../layout).
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
 * @param unit Unit of measurement. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param value Coordinate value.
 */
@Generated
fun DivScope.radialGradientFixedCenter(
    `use named arguments`: Guard = Guard.instance,
    unit: SizeUnit? = null,
    value: Int? = null,
): RadialGradientFixedCenter = RadialGradientFixedCenter(
    RadialGradientFixedCenter.Properties(
        unit = valueOrNull(unit),
        value = valueOrNull(value),
    )
)

/**
 * @param unit Unit of measurement. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param value Coordinate value.
 */
@Generated
fun DivScope.radialGradientFixedCenterProps(
    `use named arguments`: Guard = Guard.instance,
    unit: SizeUnit? = null,
    value: Int? = null,
) = RadialGradientFixedCenter.Properties(
    unit = valueOrNull(unit),
    value = valueOrNull(value),
)

/**
 * @param unit Unit of measurement. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param value Coordinate value.
 */
@Generated
fun TemplateScope.radialGradientFixedCenterRefs(
    `use named arguments`: Guard = Guard.instance,
    unit: ReferenceProperty<SizeUnit>? = null,
    value: ReferenceProperty<Int>? = null,
) = RadialGradientFixedCenter.Properties(
    unit = unit,
    value = value,
)

/**
 * @param unit Unit of measurement. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param value Coordinate value.
 */
@Generated
fun RadialGradientFixedCenter.override(
    `use named arguments`: Guard = Guard.instance,
    unit: SizeUnit? = null,
    value: Int? = null,
): RadialGradientFixedCenter = RadialGradientFixedCenter(
    RadialGradientFixedCenter.Properties(
        unit = valueOrNull(unit) ?: properties.unit,
        value = valueOrNull(value) ?: properties.value,
    )
)

/**
 * @param unit Unit of measurement. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param value Coordinate value.
 */
@Generated
fun RadialGradientFixedCenter.defer(
    `use named arguments`: Guard = Guard.instance,
    unit: ReferenceProperty<SizeUnit>? = null,
    value: ReferenceProperty<Int>? = null,
): RadialGradientFixedCenter = RadialGradientFixedCenter(
    RadialGradientFixedCenter.Properties(
        unit = unit ?: properties.unit,
        value = value ?: properties.value,
    )
)

/**
 * @param unit Unit of measurement. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param value Coordinate value.
 */
@Generated
fun RadialGradientFixedCenter.modify(
    `use named arguments`: Guard = Guard.instance,
    unit: Property<SizeUnit>? = null,
    value: Property<Int>? = null,
): RadialGradientFixedCenter = RadialGradientFixedCenter(
    RadialGradientFixedCenter.Properties(
        unit = unit ?: properties.unit,
        value = value ?: properties.value,
    )
)

/**
 * @param unit Unit of measurement. To learn more about units of size measurement, see [Layout inside the card](../../layout).
 * @param value Coordinate value.
 */
@Generated
fun RadialGradientFixedCenter.evaluate(
    `use named arguments`: Guard = Guard.instance,
    unit: ExpressionProperty<SizeUnit>? = null,
    value: ExpressionProperty<Int>? = null,
): RadialGradientFixedCenter = RadialGradientFixedCenter(
    RadialGradientFixedCenter.Properties(
        unit = unit ?: properties.unit,
        value = value ?: properties.value,
    )
)

@Generated
fun RadialGradientFixedCenter.asList() = listOf(this)
