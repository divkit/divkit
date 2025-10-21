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
 * Location of the coordinate of the rotation axis as a percentage relative to the element size.
 * 
 * Can be created using the method [pivotPercentage].
 * 
 * Required parameters: `value, type`.
 */
@Generated
@ExposedCopyVisibility
data class PivotPercentage internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Pivot {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "pivot-percentage")
    )

    operator fun plus(additive: Properties): PivotPercentage = PivotPercentage(
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
fun DivScope.pivotPercentage(
    `use named arguments`: Guard = Guard.instance,
    value: Double? = null,
): PivotPercentage = PivotPercentage(
    PivotPercentage.Properties(
        value = valueOrNull(value),
    )
)

/**
 * @param value Coordinate value as a percentage.
 */
@Generated
fun DivScope.pivotPercentageProps(
    `use named arguments`: Guard = Guard.instance,
    value: Double? = null,
) = PivotPercentage.Properties(
    value = valueOrNull(value),
)

/**
 * @param value Coordinate value as a percentage.
 */
@Generated
fun TemplateScope.pivotPercentageRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Double>? = null,
) = PivotPercentage.Properties(
    value = value,
)

/**
 * @param value Coordinate value as a percentage.
 */
@Generated
fun PivotPercentage.override(
    `use named arguments`: Guard = Guard.instance,
    value: Double? = null,
): PivotPercentage = PivotPercentage(
    PivotPercentage.Properties(
        value = valueOrNull(value) ?: properties.value,
    )
)

/**
 * @param value Coordinate value as a percentage.
 */
@Generated
fun PivotPercentage.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Double>? = null,
): PivotPercentage = PivotPercentage(
    PivotPercentage.Properties(
        value = value ?: properties.value,
    )
)

/**
 * @param value Coordinate value as a percentage.
 */
@Generated
fun PivotPercentage.modify(
    `use named arguments`: Guard = Guard.instance,
    value: Property<Double>? = null,
): PivotPercentage = PivotPercentage(
    PivotPercentage.Properties(
        value = value ?: properties.value,
    )
)

/**
 * @param value Coordinate value as a percentage.
 */
@Generated
fun PivotPercentage.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<Double>? = null,
): PivotPercentage = PivotPercentage(
    PivotPercentage.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun PivotPercentage.asList() = listOf(this)
