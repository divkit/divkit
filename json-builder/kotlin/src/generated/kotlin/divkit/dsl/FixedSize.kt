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
 * Fixed size of an element.
 * 
 * Can be created using the method [fixedSize].
 * 
 * Required properties: `value, type`.
 */
@Generated
class FixedSize internal constructor(
    @JsonIgnore
    val properties: Properties,
) : Size, RadialGradientRadius {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "fixed")
    )

    operator fun plus(additive: Properties): FixedSize = FixedSize(
        Properties(
            unit = additive.unit ?: properties.unit,
            value = additive.value ?: properties.value,
        )
    )

    class Properties internal constructor(
        /**
         * Unit of measurement. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
         * Default value: `dp`.
         */
        val unit: Property<SizeUnit>?,
        /**
         * Element size.
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
 * @param unit Unit of measurement. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param value Element size.
 */
@Generated
fun DivScope.fixedSize(
    `use named arguments`: Guard = Guard.instance,
    unit: SizeUnit? = null,
    value: Int,
): FixedSize = FixedSize(
    FixedSize.Properties(
        unit = valueOrNull(unit),
        value = valueOrNull(value),
    )
)

/**
 * @param unit Unit of measurement. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param value Element size.
 */
@Generated
fun DivScope.fixedSizeProps(
    `use named arguments`: Guard = Guard.instance,
    unit: SizeUnit? = null,
    value: Int? = null,
) = FixedSize.Properties(
    unit = valueOrNull(unit),
    value = valueOrNull(value),
)

/**
 * @param unit Unit of measurement. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param value Element size.
 */
@Generated
fun TemplateScope.fixedSizeRefs(
    `use named arguments`: Guard = Guard.instance,
    unit: ReferenceProperty<SizeUnit>? = null,
    value: ReferenceProperty<Int>? = null,
) = FixedSize.Properties(
    unit = unit,
    value = value,
)

/**
 * @param unit Unit of measurement. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param value Element size.
 */
@Generated
fun FixedSize.override(
    `use named arguments`: Guard = Guard.instance,
    unit: SizeUnit? = null,
    value: Int? = null,
): FixedSize = FixedSize(
    FixedSize.Properties(
        unit = valueOrNull(unit) ?: properties.unit,
        value = valueOrNull(value) ?: properties.value,
    )
)

/**
 * @param unit Unit of measurement. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 * @param value Element size.
 */
@Generated
fun FixedSize.defer(
    `use named arguments`: Guard = Guard.instance,
    unit: ReferenceProperty<SizeUnit>? = null,
    value: ReferenceProperty<Int>? = null,
): FixedSize = FixedSize(
    FixedSize.Properties(
        unit = unit ?: properties.unit,
        value = value ?: properties.value,
    )
)

/**
 * @param value Element size.
 */
@Generated
fun FixedSize.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<Int>? = null,
): FixedSize = FixedSize(
    FixedSize.Properties(
        unit = properties.unit,
        value = value ?: properties.value,
    )
)

@Generated
fun FixedSize.asList() = listOf(this)
