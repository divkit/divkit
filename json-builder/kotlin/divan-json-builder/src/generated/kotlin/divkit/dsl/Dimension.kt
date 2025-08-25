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
 * Required parameters: `value`.
 */
@Generated
@ExposedCopyVisibility
data class Dimension internal constructor(
    @JsonIgnore
    val properties: Properties,
) {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(emptyMap())

    operator fun plus(additive: Properties): Dimension = Dimension(
        Properties(
            value = additive.value ?: properties.value,
            unit = additive.unit ?: properties.unit,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Value.
         */
        val value: Property<Double>?,
        /**
         * Default value: `dp`.
         */
        val unit: Property<SizeUnit>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("value", value)
            result.tryPutProperty("unit", unit)
            return result
        }
    }
}

/**
 * @param value Value.
 */
@Generated
fun DivScope.dimension(
    value: Double? = null,
    `use named arguments`: Guard = Guard.instance,
    unit: SizeUnit? = null,
): Dimension = Dimension(
    Dimension.Properties(
        value = valueOrNull(value),
        unit = valueOrNull(unit),
    )
)

/**
 * @param value Value.
 */
@Generated
fun DivScope.dimensionProps(
    `use named arguments`: Guard = Guard.instance,
    value: Double? = null,
    unit: SizeUnit? = null,
) = Dimension.Properties(
    value = valueOrNull(value),
    unit = valueOrNull(unit),
)

/**
 * @param value Value.
 */
@Generated
fun TemplateScope.dimensionRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Double>? = null,
    unit: ReferenceProperty<SizeUnit>? = null,
) = Dimension.Properties(
    value = value,
    unit = unit,
)

/**
 * @param value Value.
 */
@Generated
fun Dimension.override(
    `use named arguments`: Guard = Guard.instance,
    value: Double? = null,
    unit: SizeUnit? = null,
): Dimension = Dimension(
    Dimension.Properties(
        value = valueOrNull(value) ?: properties.value,
        unit = valueOrNull(unit) ?: properties.unit,
    )
)

/**
 * @param value Value.
 */
@Generated
fun Dimension.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Double>? = null,
    unit: ReferenceProperty<SizeUnit>? = null,
): Dimension = Dimension(
    Dimension.Properties(
        value = value ?: properties.value,
        unit = unit ?: properties.unit,
    )
)

/**
 * @param value Value.
 */
@Generated
fun Dimension.modify(
    `use named arguments`: Guard = Guard.instance,
    value: Property<Double>? = null,
    unit: Property<SizeUnit>? = null,
): Dimension = Dimension(
    Dimension.Properties(
        value = value ?: properties.value,
        unit = unit ?: properties.unit,
    )
)

/**
 * @param value Value.
 */
@Generated
fun Dimension.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<Double>? = null,
    unit: ExpressionProperty<SizeUnit>? = null,
): Dimension = Dimension(
    Dimension.Properties(
        value = value ?: properties.value,
        unit = unit ?: properties.unit,
    )
)

@Generated
fun Dimension.asList() = listOf(this)
