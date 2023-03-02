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
 * Element dimension value.
 * 
 * Can be created using the method [dimension].
 * 
 * Required properties: `value`.
 */
@Generated
class Dimension internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): Dimension = Dimension(
        Properties(
            unit = additive.unit ?: properties.unit,
            value = additive.value ?: properties.value,
        )
    )

    class Properties internal constructor(
        /**
         * Default value: `dp`.
         */
        val unit: Property<SizeUnit>?,
        /**
         * Value.
         */
        val value: Property<Double>?,
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
 * @param value Value.
 */
@Generated
fun DivScope.dimension(
    `use named arguments`: Guard = Guard.instance,
    unit: SizeUnit? = null,
    value: Double,
): Dimension = Dimension(
    Dimension.Properties(
        unit = valueOrNull(unit),
        value = valueOrNull(value),
    )
)

/**
 * @param value Value.
 */
@Generated
fun DivScope.dimensionProps(
    `use named arguments`: Guard = Guard.instance,
    unit: SizeUnit? = null,
    value: Double? = null,
) = Dimension.Properties(
    unit = valueOrNull(unit),
    value = valueOrNull(value),
)

/**
 * @param value Value.
 */
@Generated
fun TemplateScope.dimensionRefs(
    `use named arguments`: Guard = Guard.instance,
    unit: ReferenceProperty<SizeUnit>? = null,
    value: ReferenceProperty<Double>? = null,
) = Dimension.Properties(
    unit = unit,
    value = value,
)

/**
 * @param value Value.
 */
@Generated
fun Dimension.override(
    `use named arguments`: Guard = Guard.instance,
    unit: SizeUnit? = null,
    value: Double? = null,
): Dimension = Dimension(
    Dimension.Properties(
        unit = valueOrNull(unit) ?: properties.unit,
        value = valueOrNull(value) ?: properties.value,
    )
)

/**
 * @param value Value.
 */
@Generated
fun Dimension.defer(
    `use named arguments`: Guard = Guard.instance,
    unit: ReferenceProperty<SizeUnit>? = null,
    value: ReferenceProperty<Double>? = null,
): Dimension = Dimension(
    Dimension.Properties(
        unit = unit ?: properties.unit,
        value = value ?: properties.value,
    )
)

/**
 * @param value Value.
 */
@Generated
fun Dimension.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<Double>? = null,
): Dimension = Dimension(
    Dimension.Properties(
        unit = properties.unit,
        value = value ?: properties.value,
    )
)

@Generated
fun Dimension.asList() = listOf(this)
