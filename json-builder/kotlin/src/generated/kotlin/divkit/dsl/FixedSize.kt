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
 * Required parameters: `value, type`.
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
            value = additive.value ?: properties.value,
            unit = additive.unit ?: properties.unit,
        )
    )

    class Properties internal constructor(
        /**
         * Element size.
         */
        val value: Property<Int>?,
        /**
         * Unit of measurement. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
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
 * @param value Element size.
 * @param unit Unit of measurement. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 */
@Generated
fun DivScope.fixedSize(
    value: Int? = null,
    `use named arguments`: Guard = Guard.instance,
    unit: SizeUnit? = null,
): FixedSize = FixedSize(
    FixedSize.Properties(
        value = valueOrNull(value),
        unit = valueOrNull(unit),
    )
)

/**
 * @param value Element size.
 * @param unit Unit of measurement. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 */
@Generated
fun DivScope.fixedSizeProps(
    `use named arguments`: Guard = Guard.instance,
    value: Int? = null,
    unit: SizeUnit? = null,
) = FixedSize.Properties(
    value = valueOrNull(value),
    unit = valueOrNull(unit),
)

/**
 * @param value Element size.
 * @param unit Unit of measurement. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 */
@Generated
fun TemplateScope.fixedSizeRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Int>? = null,
    unit: ReferenceProperty<SizeUnit>? = null,
) = FixedSize.Properties(
    value = value,
    unit = unit,
)

/**
 * @param value Element size.
 * @param unit Unit of measurement. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 */
@Generated
fun FixedSize.override(
    `use named arguments`: Guard = Guard.instance,
    value: Int? = null,
    unit: SizeUnit? = null,
): FixedSize = FixedSize(
    FixedSize.Properties(
        value = valueOrNull(value) ?: properties.value,
        unit = valueOrNull(unit) ?: properties.unit,
    )
)

/**
 * @param value Element size.
 * @param unit Unit of measurement. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 */
@Generated
fun FixedSize.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Int>? = null,
    unit: ReferenceProperty<SizeUnit>? = null,
): FixedSize = FixedSize(
    FixedSize.Properties(
        value = value ?: properties.value,
        unit = unit ?: properties.unit,
    )
)

/**
 * @param value Element size.
 * @param unit Unit of measurement. To learn more about units of size measurement, see [Layout inside the card](../../layout.dita).
 */
@Generated
fun FixedSize.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<Int>? = null,
    unit: ExpressionProperty<SizeUnit>? = null,
): FixedSize = FixedSize(
    FixedSize.Properties(
        value = value ?: properties.value,
        unit = unit ?: properties.unit,
    )
)

@Generated
fun FixedSize.asList() = listOf(this)
