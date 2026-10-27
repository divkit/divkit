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
 * Element size (%).
 * 
 * Can be created using the method [percentageSize].
 * 
 * Required parameters: `value, type`.
 */
@Generated
@JsonSerialize(using = PercentageSizeSerializer::class)
@ExposedCopyVisibility
data class PercentageSize internal constructor(
    val properties: Properties,
) {
    operator fun plus(additive: Properties): PercentageSize = PercentageSize(
        Properties(
            value = additive.value ?: properties.value,
        )
    )

    @ExposedCopyVisibility
    data class Properties internal constructor(
        /**
         * Element size value.
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
 * @param value Element size value.
 */
@Generated
fun DivScope.percentageSize(
    `use named arguments`: Guard = Guard.instance,
    value: Double? = null,
): PercentageSize = PercentageSize(
    PercentageSize.Properties(
        value = valueOrNull(value),
    )
)

/**
 * @param value Element size value.
 */
@Generated
fun DivScope.percentageSizeProps(
    `use named arguments`: Guard = Guard.instance,
    value: Double? = null,
) = PercentageSize.Properties(
    value = valueOrNull(value),
)

/**
 * @param value Element size value.
 */
@Generated
fun TemplateScope.percentageSizeRefs(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Double>? = null,
) = PercentageSize.Properties(
    value = value,
)

/**
 * @param value Element size value.
 */
@Generated
fun PercentageSize.override(
    `use named arguments`: Guard = Guard.instance,
    value: Double? = null,
): PercentageSize = PercentageSize(
    PercentageSize.Properties(
        value = valueOrNull(value) ?: properties.value,
    )
)

/**
 * @param value Element size value.
 */
@Generated
fun PercentageSize.defer(
    `use named arguments`: Guard = Guard.instance,
    value: ReferenceProperty<Double>? = null,
): PercentageSize = PercentageSize(
    PercentageSize.Properties(
        value = value ?: properties.value,
    )
)

/**
 * @param value Element size value.
 */
@Generated
fun PercentageSize.modify(
    `use named arguments`: Guard = Guard.instance,
    value: Property<Double>? = null,
): PercentageSize = PercentageSize(
    PercentageSize.Properties(
        value = value ?: properties.value,
    )
)

/**
 * @param value Element size value.
 */
@Generated
fun PercentageSize.evaluate(
    `use named arguments`: Guard = Guard.instance,
    value: ExpressionProperty<Double>? = null,
): PercentageSize = PercentageSize(
    PercentageSize.Properties(
        value = value ?: properties.value,
    )
)

@Generated
fun PercentageSize.asList() = listOf(this)
